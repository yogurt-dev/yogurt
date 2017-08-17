/**
 *    驴鱼社区后台管理UI框架
 *    基于XENON 封装
 *    SINCE：0.1.*
 *    VERSION:1.8.*
 *    ANCHOR: DENGGUANGYI
 **/
;
(function ($, window,template ,require,undefined) {
    //root path.js
    var Path = {
        'version': "0.8.4",
        'map': function (path) {
            if (Path.routes.defined.hasOwnProperty(path)) {
                return Path.routes.defined[path];
            } else {
                return new Path.core.route(path);
            }
        },
        'root': function (path) {
            Path.routes.root = path;
        },
        'rescue': function (fn) {
            Path.routes.rescue = fn;
        },
        'history': {
            'initial':{}, // Empty container for "Initial Popstate" checking variables.
            'pushState': function(state, title, path){
                if(Path.history.supported){
                    if(Path.dispatch(path)){
                        history.pushState(state, title, path);
                    }
                } else {
                    if(Path.history.fallback){
                        window.location.hash = "#" + path;
                    }
                }
            },
            'popState': function(event){
                var initialPop = !Path.history.initial.popped && location.href == Path.history.initial.URL;
                Path.history.initial.popped = true;
                if(initialPop) return;
                Path.dispatch(document.location.pathname);
            },
            'listen': function(fallback){
                Path.history.supported = !!(window.history && window.history.pushState);
                Path.history.fallback  = fallback;

                if(Path.history.supported){
                    Path.history.initial.popped = ('state' in window.history), Path.history.initial.URL = location.href;
                    window.onpopstate = Path.history.popState;
                } else {
                    if(Path.history.fallback){
                        for(route in Path.routes.defined){
                            if(route.charAt(0) != "#"){
                                Path.routes.defined["#"+route] = Path.routes.defined[route];
                                Path.routes.defined["#"+route].path = "#"+route;
                            }
                        }
                        Path.listen();
                    }
                }
            }
        },
        'match': function (path, parameterize) {
            var params = {}, route = null, possible_routes, slice, i, j, compare;
            for (route in Path.routes.defined) {
                if (route !== null && route !== undefined) {
                    route = Path.routes.defined[route];
                    possible_routes = route.partition();
                    for (j = 0; j < possible_routes.length; j++) {
                        slice = possible_routes[j];
                        compare = path;
                        if (slice.search(/:/) > 0) {
                            for (i = 0; i < slice.split("/").length; i++) {
                                if ((i < compare.split("/").length) && (slice.split("/")[i].charAt(0) === ":")) {
                                    params[slice.split('/')[i].replace(/:/, '')] = compare.split("/")[i];
                                    compare = compare.replace(compare.split("/")[i], slice.split("/")[i]);
                                }
                            }
                        }
                        if (slice === compare) {
                            if (parameterize) {
                                route.params = params;
                            }
                            return route;
                        }
                    }
                }
            }
            return null;
        },
        'dispatch': function (passed_route) {
            var previous_route, matched_route;
            if (Path.routes.current !== passed_route) {
                Path.routes.previous = Path.routes.current;
                Path.routes.current = passed_route;
                matched_route = Path.match(passed_route, true);

                if (Path.routes.previous) {
                    previous_route = Path.match(Path.routes.previous);
                    if (previous_route !== null && previous_route.do_exit !== null) {
                        previous_route.do_exit();
                    }
                }

                if (matched_route !== null) {
                    matched_route.run();
                    return true;
                } else {
                    if (Path.routes.rescue !== null) {
                        Path.routes.rescue();
                    }
                }
            }
        },
        'listen': function () {
            var fn = function(){ Path.dispatch(location.hash); }

            if (location.hash === "") {
                if (Path.routes.root !== null) {
                    location.hash = Path.routes.root;
                }
            }

            // The 'document.documentMode' checks below ensure that PathJS fires the right events
            // even in IE "Quirks Mode".
            if ("onhashchange" in window && (!document.documentMode || document.documentMode >= 8)) {
                window.onhashchange = fn;
            } else {
                setInterval(fn, 50);
            }

            if(location.hash !== "") {
                Path.dispatch(location.hash);
            }
        },
        'core': {
            'route': function (path) {
                this.path = path;
                this.action = null;
                this.do_enter = [];
                this.do_exit = null;
                this.params = {};
                Path.routes.defined[path] = this;
            }
        },
        'routes': {
            'current': null,
            'root': null,
            'rescue': null,
            'previous': null,
            'defined': {}
        }
    };
    Path.core.route.prototype = {
        'to': function (fn) {
            this.action = fn;
            return this;
        },
        'enter': function (fns) {
            if (fns instanceof Array) {
                this.do_enter = this.do_enter.concat(fns);
            } else {
                this.do_enter.push(fns);
            }
            return this;
        },
        'exit': function (fn) {
            this.do_exit = fn;
            return this;
        },
        'partition': function () {
            var parts = [], options = [], re = /\(([^}]+?)\)/g, text, i;
            while (text = re.exec(this.path)) {
                parts.push(text[1]);
            }
            options.push(this.path.split("(")[0]);
            for (i = 0; i < parts.length; i++) {
                options.push(options[options.length - 1] + parts[i]);
            }
            return options;
        },
        'run': function () {
            var halt_execution = false, i, result, previous;

            if (Path.routes.defined[this.path].hasOwnProperty("do_enter")) {
                if (Path.routes.defined[this.path].do_enter.length > 0) {
                    for (i = 0; i < Path.routes.defined[this.path].do_enter.length; i++) {
                        result = Path.routes.defined[this.path].do_enter[i].apply(this, null);
                        if (result === false) {
                            halt_execution = true;
                            break;
                        }
                    }
                }
            }
            if (!halt_execution) {
                Path.routes.defined[this.path].action();
            }
        }
    };
    //jq ajax扩展 截获
    //备份jquery的ajax方法
    var _ajax = $.ajax;
    //重写jquery的ajax方法
    $.ajax = function (opt) {
        //备份opt中error和success方法
        var fn = {
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                //alert('系统错误，请联系管理员！：' + textStatus + ',' + errorThrown);
            },
            success: function (data, textStatus) {
            }
        }
        if (opt.error) {
            fn.error = opt.error;
        }
        if (opt.success) {
            fn.success = opt.success;
        }
        //扩展增强处理
        var _opt = $.extend(opt, {
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                //错误方法增强处理
                hide_loading_bar();
                DF.toggleMainViewMask(false);
//                alert2('系统未知错误，请联系管理员！');
                fn.error(XMLHttpRequest, textStatus, errorThrown);
            },
            success: function (data, textStatus) {
                //去掉蒙板
                DF.toggleMainViewMask(false);
                //成功回调方法增强处理
                if (data.errorCode && data.errorCode == '-1') {
                    window.location.href = 'login.html';
                    return;
                }
                //业务异常
                if (data.errorCode && data.errorCode == '0') {
                    if(data.message == 'ERROR'){
                        DF.toast.error('网络繁忙');
                    }else{
                        DF.toast.error(data.message);
                    }
                }
                fn.success(data, textStatus);
            },
            beforeSend: function (XMLHttpRequest) {
                if($.ajaxSettings._opt && $.ajaxSettings._opt.beforeSend){
                    $.ajaxSettings._opt.beforeSend(XMLHttpRequest);
                }
            },
            complete: function (XMLHttpRequest, TS) {
                //请求完成后回调函数 (请求成功或失败之后均调用)。
            }
        });
        //_opt.cache = false;
        _ajax(_opt);
    };
    window.alert2 = alert;
    var DonkishFramework = function ($) {
        //版本号、开发模式 (true:开发模式，false:使用模式)
        this.version = '1.8.8.08mvvm';
        var devMode = true;
        //
        this.getDevMode = function () {
            return devMode;
        }
        //url参数
        var urlParam = {},
        //页面来回传递存储的url参数
            _urlParam = {},
        //高级查询的参数
            senior_param = null,
        //当前页面
            currentPage = '',
        //上一页
            lastPage = '',
        //权限数据
            ad,
        //初始化标示
            _FLAG = true;
        //authdata
        this.getAuthorityData = function () {
            return ad;
        }
        this.get111 = function () {
            return _urlParam;
        }
        /**
         * 获取浏览器中的url 去掉参数
         */
        function getCurrentUrl() {
            var url = location.hash ;
            if(url.indexOf('\?')==-1){
                url = url.replace('\#','');
            }else{
                var index = url.indexOf('\?');
                url = url.substring(1,index);
            }
            return url;
        }
        //检测全局对象的句柄
        var monitor = (function () {
            var _onDocStart = {};
            for (var p in window) {
                _onDocStart[p] = true;
            }
            /**
             * 检测当前页面上已经存在的所有全局变量
             * @return {Array} 返回全局变量列表
             * @private
             */
            var _detect = function () {
                var _globalVars = [];
                for (var p in window) {
                    if (!_onDocStart[p]) {
                        _globalVars.push(p);
                    }
                }
                return _globalVars
            };

            return {
                detect: _detect
            };
        })();
        //组件队列
        var components = [];
        //模板缓存
        var tmplCache = {};
        //基于require的模块加载
        this.load = function (arr,fn) {
            if(arguments.length!==2){
                console.error('参数错误');
                return
            }
            var dependence = [];
            if($.isArray(arr)){
                dependence = arr;
            }else if($.isPlainObject(arr)){
                //传递为一个对象的时候 还未想好如何扩展
                //TODO
            }else{
                dependence = [arr];
            }
            require(dependence,function () {
                var _exportsArs = arguments ;
                fn.apply(this,_exportsArs);
            });
        };
        //左侧菜单选择
        this.leftSelectMenuPin = function (id, first, second) {
            if (!id && !first && !second) {
                this.gotoPage();
            }
            var i, j;
            try {
                i = Number(first);
                j = Number(second);
            } catch (e) {
                console.log('请输入数字或字符串的数字');
                return
            }
            //$('#'+id+' li').removeClass('active');
            $('#' + id + '>li:eq(' + i + ')').addClass('active');
            $('#' + id + '>li:eq(' + i + ')>a').trigger('click');
            $('#' + id + '>li:eq(' + i + ') li:eq(' + j + ')').addClass('active');
            $('#' + id + '>li:eq(' + i + ') li:eq(' + j + ')>a').trigger('click');

        };
        //模拟点击到左侧菜单
        this.pinMenu = function (first, second) {
            if (first - 1 < 0 || second - 1 < 0) {
                this.gotoPage();
                return
            }
            this.leftSelectMenuPin('main-menu', first - 1, second - 1);
        }
        //解析获取浏览器参数
        this.analyticalUrlParams = function (url) {
            if(typeof url == 'string'){
                if (url && url.indexOf("?") != -1) {
                    var paramObj = {};
                    var str = url.substr(1);
                    var strs = url.substring(url.indexOf("?") + 1, url.length).split("&");
                    for (var i = 0; i < strs.length; i++) {
                        paramObj[strs[i].split("=")[0]] = (strs[i].split("=")[1]);
                    }
                    return paramObj;
                }
            }else if(typeof url == 'object'){
                var param = '';
                for(var k in url){
                    if(url[k] && url[k].toString()=='[object Object]'){
                        continue;
                    }
                    param += k+ '='+url[k] + '&';
                }
                if(param && param.substring(param.length-1,param.length) =='&'){
                    param = param.substring(0,param.length-1);
                }
                return param;
            }else{
                console.log('参数错误，正确格式为‘string’或‘object’');
                return '';
            }
        }
        this.getCtxPath = function () {
            var currentPath = window.document.location.href
                ,pathName = window.document.location.pathname
                ,pos = currentPath.indexOf(pathName)
                ,localhostPath = currentPath.substring(0,pos)
                ,projectName = pathName.substring(0,pathName.substr(1).indexOf('/')+1) || document.location.host;
            if(localhostPath.indexOf(projectName)!==-1){
                return localhostPath
            }else{
                return localhostPath + projectName
            }
        }
        //获取框架内的url参数 对象形式
        this.getUrlParam = function () {
            return urlParam;
        }
        this.getSeniorSearchParam = function () {
            return  senior_param;
        }
        this.clearSeniorSearchParam = function () {
            senior_param = null;
        }
        //获取页面信息
        var i = 1;
        this.getPage = function (url, callback) {
            if (!url) return;
            var self = this;
            var cache = tmplCache[url];
            if(cache && !self.getDevMode()){
                callback(cache);
                return
            }
            var ctx = self.getCtxPath();
            _mainViewIsOverlay(true);
            show_loading_bar && show_loading_bar({pct: 100, delay: 0.8});
            $.ajax({
                type: "get",
                url: ctx + '/module' + url + ".html?_=" + Math.random().toString().substring(2, 15),
                error: function (e) {
                    if (e.status == '404') {
                        if (i === 1) {
                            console.log("找不到该模板，加载模版失败！");
                            self.gotoPage('/404');
                        }
                        i++;
                    }
                },
                success: function (data) {
                    tmplCache[url] = data;
                    callback(data);
                }
            });
        };
        //获取页面模板 先走缓存 再走服务器
        this.getTmpl = function (url, callback) {
            if (!url) return;
            var cache = tmplCache[url];
            if(cache){
                callback(cache);
                return
            }
            var self = this;
            var ctx = self.getCtxPath();
            $.ajax({
                type: "get",
                url: ctx +'/tmpl' + url + ".html",
                error: function (e) {
                    if (e.status == '404') {
                        console.log("找不到该模板，加载模版失败！");
                    }
                },
                success: function (data) {
                    tmplCache[url] = data;
                    callback(data);
                }
            });
        };
        this.getTmplCache = function(){
            return tmplCache;
        }
        /**渲染页面
         * 4个参数
         * dom: dom模板 必须
         * container: 被渲染的容器 必须
         * fn:回调函数
         * data: 需要遍历的数据 不必须
         * fadeInOut: 不必须
         */
        this.renderPage = function (dom, container, fn, fadeInOut) {

            //TODO  多数据的情况
            if (dom && container) {
                var el, div = null, self = this;
                if (typeof container === 'object') {
                    div = container;
                } else {
                    if (typeof container === 'string') {
                        div = $('#' + container).length ? $('#' + container) : null || $('.' + container).length ? $('.' + container) : null;
                        if (!div) {
                            console.log('渲染目标为' + container + '的容器不存在');
                            return;
                        }
                    } else {
                        console.log('渲染目标必须为字符串或jq对象');
                        return;
                    }
                }
                el = $(dom);
                var tempView = [],tempScript = [],tempStyle = [],tempTemplate = [];
                $.map(el,function(e,i){
                    var nodeName = e.nodeName;
                    if(nodeName=='VIEW'||nodeName=='DIV'){
                        tempView.push(e)
                    }else if(nodeName=='STYLE'){
                        tempStyle.push(e)
                    }else if(nodeName=='SCRIPT'){
                        tempScript.push(e)
                    }else if(nodeName=='TEMPLATE'){
                        tempTemplate.push(e)
                    }
                });
                var tempDom = $(tempView);
                div.fadeOut('fast', function () {
                    var $this = $(this);
                    //清理dom 包括html 文本等
                    // $this.children().remove();
                    //清理注释
                    this.innerHTML = '';
                    //清除ueditor
                    window.UE && UE.ui.removeAll();
                    //清除 ayncSetF
                    ayncSetF = {};
                    function cp() {
                        try {
                            $this.html(tempDom);
                            $this.fadeIn('normal');
                        } catch (e) {
                            console.error(e.message);
                            console.error(e.stack);
                            $this.fadeIn('normal');
                            return
                        }
                        self.initCustomFormComponent(tempDom);
                        self.initCustomFormPlaceHolder(tempDom);
                        $this.append(tempScript);
                        self.initHelp();
                        //debug 全局对象
                        devMode && self.debug_globalVars();
                        fn && fn(tempDom);
                        tempDom = null;
                        tempScript = null;
                    };
                    setTimeout(function () {
                        if(!_FLAG){
                            cp();
                        }else{
                            var si = setInterval(function () {
                                if(!_FLAG){
                                    cp();
                                    clearInterval(si);
                                }
                            },100);
                        }
                    },1);

                });
            }
        };

        //页面切换
        this.gotoPage = function (url, param, fn, isMenu) {
            // console.log('isMenu:'+isMenu);
            var _url = '/Welcome',self = this,tempUrl = '',tar = 'main-content-view',routes = [];
            urlParam = ($.extend(param,_urlParam[url])) || {};
            if (!url) {
                url = _url;
                senior_param = null;
            }else{
                if(url == '-1' ){
                    _urlParam[lastPage] = param;
                    //self.gotoPage(lastPage,param,null,false);
                    history.go(-1);
                    return;
                }else if(url=='-2'){
                    //TODO 缓存的视图
                }else{

                }
            }

            //如果菜单中点击进来
            if(location.hash.indexOf(url) !==-1){

            }else{
                //非菜单 直接gotoPage进来
                //跳转之前判断此hash是否在Path中注册
                if (url.indexOf('?') != -1) {
                    param = self.analyticalUrlParams(url);
                    url = url.substring(0, url.indexOf("?"));
                }
                var flag = false,paths = self.path.routes.defined;
                for(var k in paths){
                    routes.push(k);
                }
                flag = routes.indexOf('#'+url) == -1;
                //如果没有注册
                if(flag){
                    location.hash = '#'+tempUrl;
                }
            }
            // if(location.hash !=='#'+ tempUrl){
            //     location.hash = '#'+tempUrl;
            //     return
            // }
            self.getPage(url, function (page) {
                if (param) {
                    urlParam = param;
                }
                _urlParam = {};
                //跳转页面的时候保存查询参数 如果是reloadPage（url==currentPage） 即刷新当前页时不需要保存
                if($('.senior_wrapper').length && location.hash ===""&& url !== currentPage){
                    var $form = $('.senior_wrapper').last();
                    senior_param = getFormValues($form);
                    senior_param['originalData'] = $form.serializeObject();
                    senior_param['_url_'] = currentPage;
                    if(senior_param['_save_status']===''){
                        senior_param = null;
                    }
                }
                if(self.event.beforePageSwitch(DF.path.routes.current,function (flag) {
                        if(flag){
                            _mainViewIsOverlay(false);
                            self.renderPage(page, tar, function () {
                                lastPage = currentPage;
                                tempUrl ? (currentPage = tempUrl) : (currentPage = url);
                                self.event.afterPageSwitch();
                                fn && fn()
                            });
                        }
                    })){
                    _mainViewIsOverlay(false);
                    self.renderPage(page, tar, function () {
                        lastPage = currentPage;
                        tempUrl ? (currentPage = tempUrl) : (currentPage = url);
                        self.event.afterPageSwitch();
                        fn && fn()
                    });
                }
            });
        }
        function getFormValues($form) {
            var o = {};
            $('input.form-control,select[name],input[type=radio]',$form).each(function () {
                var $this = $(this),name = $this.attr('name'),value = $this.val();
                if($this.attr('datetype')=='daterange'){
                    if(value){
                        value = value.split('\ - ');
                        o[name+'_start'] = value[0];
                        o[name+'_end'] = value[1];
                    }else{
                        o[name+'_start'] = '';
                    }
                }else if($this.attr('type')=='radio'){
                    var v = $('input[name='+name+']:checked',$form).val();
                    o[name] = v||'';
                }else{
                    o[name] = value||'';
                }
            });
            return o;
        }
        //重新加载当前页面 flag:true
        this.reloadPage = function () {
            var me = this, param = null,url = this.getCurrentPage();
            //如果是菜单进入 菜单传递参数
            if(location.hash.indexOf('\?')!==-1){
                param = me.analyticalUrlParams(location.href);
            }else{
                var obj = DF.getUrlParam();
                //非注册的页面重新reload情况 带着参数
                if(!$.isEmptyObject(obj) && location.hash==''){
                    param = obj;
                }
            }
            this.gotoPage(url,param,null,true);
        }
        //获取当前页面
        this.getCurrentPage = function () {
            return currentPage;
        }
        this.getLastPage = function () {
            return lastPage;
        }
        this.path = Path;
        //映射路由
        this.mapRoute = function (key,value,target) {
            var self = this,el = target ||'',hash=key,url=value;
            if(self.path && hash && url){
                var param = null;
                if(hash.indexOf('\?')!==-1){
                    param = self.analyticalUrlParams(key);
                    url = url.substring(0, url.indexOf("?"));
                }
                self.path.map(hash).to(function(){
                    // if(self.get111()[url]){;
                    //     self.gotoPage(url,param,null,false);
                    // }else{
                    //     self.gotoPage(url,param,null,true);
                    // }
                    self.gotoPage(url,param,null,!self.get111()[url]);
                });
            }
        }
        /**
         * 基于jq 封装的ajax
         * 提供基本功能
         */
        this.ajax = function (cfg) {
            var self = this;
            var deffer = $.Deferred();
            var df = {
                type: 'get',
                showLoadingBar: true,
                timeout: 20000,
                async: true,
                cache: true,
                dataType: 'json',
                contentType: 'application/json',
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    _errorFn(XMLHttpRequest, textStatus, errorThrown);
                },
                complete: function (xhr, textStatus) {
                    _finallyFn(xhr, textStatus)
                }
            };
            var df = $.extend(df, cfg);
            var ok = df.success;
            df.success = function (data, textStatus) {
                //正常
                ok && ok(data, textStatus);
                deffer.resolve(data, textStatus);
            };
            var _errorFn = function (XMLHttpRequest, textStatus, errorThrown) {
                //alert('系统错误，请联系管理员！：' + textStatus + ',' + errorThrown);
                hide_loading_bar();
                deffer.reject(XMLHttpRequest, textStatus, errorThrown);
            };
            var _finallyFn = function (xhr, textStatus, a) {
                //调用成功执行的函数
                // 获取相关Http Response header
                // var wpoInfo = {
                //     // 服务器端时间
                //     "date": xhr.getResponseHeader('Date'),
                //     // 如果开启了gzip，会返回这个东西
                //     "contentEncoding": xhr.getResponseHeader('Content-Encoding'),
                //     // keep-alive ？ close？
                //     "connection": xhr.getResponseHeader('Connection'),
                //     // 响应长度
                //     "contentLength": xhr.getResponseHeader('Content-Length'),
                //     // 服务器类型，apache？lighttpd？
                //     "server": xhr.getResponseHeader('Server'),
                //     "vary": xhr.getResponseHeader('Vary'),
                //     "transferEncoding": xhr.getResponseHeader('Transfer-Encoding'),
                //     // text/html ? text/xml?
                //     "contentType": xhr.getResponseHeader('Content-Type'),
                //     "cacheControl": xhr.getResponseHeader('Cache-Control'),
                //     // 生命周期？
                //     "exprires": xhr.getResponseHeader('Exprires'),
                //     "lastModified": xhr.getResponseHeader('Last-Modified')
                // };
                //console.log(xhr,textStatus);
            };
            var _filter = function (data, type) {
                //对返回的数据做第一手的处理
                return data;
            }
            if (df.url) {
                //兼容.do 请求  如果请求为.do 那么修改content-type application/x-www-form-urlencoded
                if (df.url.indexOf('.do') !== -1) {
                    df.contentType = 'application/x-www-form-urlencoded';
                }
                if(df.showLoadingBar){
                    show_loading_bar({pct: 100, delay: 0.8});
                    self.toggleMainViewMask(true);
                }
                $.ajax(df);
                return deffer.promise();
            } else {
                console.log("url为必须的参数");
                return;
            }
        };
        //判断是否为多选框
        function _isMultipleSelect (el){
            return el[0].tagName == 'DIV' && el.hasClass('select2-container');
        }
        //初始化表单的函数只初始化placeholder 接受jquery 对象的form
        this.initCustomFormPlaceHolder = function (forms,isShowRedPoint) {
            if (typeof forms == 'object' && forms.length) {
                var form,redPoint = isShowRedPoint===undefined || isShowRedPoint==true;
                function _initSingleForm ($form){
                    var _form = $form;
                    var elements = _form.find('div.form-group>label').next();
                    //控制form内元素的 placeholder位置 监听获得焦点与失去焦点事件
                    elements.each(function () {
                        var $this = $(this);
                        if (!$this.hasClass('no-placeholder')) {
                            var tagname = $this[0].tagName;
                            //除去placeholder
                            $this.removeAttr('placeholder');
                            //除去error样式类
                            $this.removeClass('error');
                            $this.parent().find('label.error').length && $this.parent().find('label.error').remove();
                            //获得字段名儿
                            var label = $this.prev().text();
                            //自动计算可以输入的标签长度 px
                            var paddingRight = 40;
                            if (label) {
                                paddingRight += 15 * label.length;
                            }
                            if(!$this.hasClass('uEditor') && !$this.hasClass('select2-container')){
                                $this.css({'padding-right': paddingRight + 'px'});
                            }
                            if($this.hasClass('select2-container')){
                                $this.find('a.select2-choice').css({'padding-right': paddingRight + 'px'});
                            }
                            var placeholder = $('<span class="DF-placeHolder pleft">' + label + '</span>');
                            //必填项* redstar
                            if(redPoint && $this.attr('required')=='required'){
                                placeholder = $('<span class="DF-placeHolder pleft">'+ '<span class="text-danger" style="vertical-align: middle;font-size: 15px;font-weight: bold;">*</span>'+ label +'</span>');
                            }
                            //当即placeholder时候模拟点击input框
                            if($this.is('input')){
                                placeholder.on('click',function () {
                                    if (_isMultipleSelect($this)) {
                                        $this.find('input').trigger('focus');
                                    } else {
                                        $this.trigger('focus');
                                    }
                                });
                            }else if($this.is('select')){
                                //非只读的时候
                                if(!$this.prop('readonly') && !$this.prop('disabled')){
                                    if($this.attr('searchable')=='true'){
                                        //如果是单选搜索
                                        placeholder.css('cursor','pointer').on('click',function () {
                                            $('a.select2-choice',$this.prev()).trigger('mousedown');
                                        });
                                    }else{
                                        //如果是单选非搜索
                                        placeholder.css('cursor','pointer').on('click',function () {
                                            $('.selectboxit',$this.next()).trigger('click');
                                        });
                                    }

                                }
                            }else if($this.is('div') && $this.hasClass('select2-container')){
                                placeholder.css('cursor','pointer').on('click',function () {
                                    $('a.select2-choice',$this.prev()).trigger('mousedown');
                                });
                            }
                            //如果有默认的值
                            if ($this.val() || _isMultipleSelect($this) && $this.next().val()) {
                                placeholder.addClass('pright');
                            }
                            $this.parent().find('.DF-placeHolder').remove();
                            $this.parent().append(placeholder);
                            //多选
                            if (_isMultipleSelect($this)) {
                                //$this.find('input').focus(function () {
                                //    placeholder.fadeOut(250);
                                //});
                                $this.next().bind('DF.propertychange', function () {
                                    if ($(this).val()) {
                                        $(this).parent().find('span.DF-placeHolder').addClass('pright').fadeIn(250);
                                    } else {
                                        $(this).parent().find('span.DF-placeHolder').removeClass('pright').fadeIn(250);
                                    }
                                });
                                //单选
                            } else if ('SELECT' == tagname) {
                                $this.off('DF.propertychange').on('DF.propertychange', function () {
                                    if ($(this).val()) {
                                        $(this).parent().find('span.DF-placeHolder').addClass('pright');
                                    }else{
                                        (function ($this) {
                                            setTimeout(function () {
                                                if($this.parent().find('.selectboxit-text').text()){
                                                    $this.parent().find('span.DF-placeHolder').addClass('pright');
                                                }
                                            },1);
                                        })($(this));
                                    }
                                });
                                //普通文本域
                            } else {
                                $this.blur(function () {
                                    if ($this.val()) {
                                        placeholder.addClass('pright').fadeIn(250);
                                    } else {
                                        placeholder.removeClass('pright').fadeIn(250);
                                    }
                                });
                                $this.bind('placeholder.focus',function(e){
                                    if(!$this.prop('readonly')){
                                        placeholder.fadeOut(250);
                                    }
                                });
                                $this.focus(function () {
                                    $this.trigger('placeholder.focus');
                                });
                            }
                        }
                    });
                    //在对表单赋值时  增加propertychange事件
                    elements.bind('DF.propertychange', function () {
                        var $this = $(this);
                        if($this.is('input')||$this.is('select')||$this.is('textarea')){
                            if ($this.val()) {
                                $this.parent().find('span.DF-placeHolder').addClass('pright');
                            }else{
                                $this.parent().find('span.DF-placeHolder').removeClass('pright');
                            }
                        }
                    });
                }
                forms.each(function(index){
                    form = $(this);
                    _initSingleForm(form);
                });

            } else {
                console.log('初始化表单失败!');
            }
        };
        /**
         * 模态窗口
         * @param
         * config 配置项 根据传递的 模板ID 判断是否缓存页面
         * fn 回调函数 里面包括 业务自定义的表单
         * type 是否为组件调用
         */
        //当前加载模态窗口的formId 决定是否清空里面的内容

        //TODO
        var lazyLoadFormId = '';
        this.showModal = function (config, fn, type) {
            //哪个窗体 默认业务窗体
            var modalId = type || '_LYModal';
            //获取句柄
            var self = this,
                element = $('#'+modalId),
                header = element.find('.modal-header'),
                body = element.find('.modal-body'),
                footer = element.find('.modal-footer');
            //sub_btn = $('#_validate_btn') || $('<button id="_validate_btn" type="submit" class="hidden"></button>');
            //默认配置
            var df = {
                title: 'Modal',
                cache: type ? false : true,
                onShow:function(){},
                beforeShow:function(){},
                onHide:function(){},
                width: '600px'
            };
            var df = $.extend(df, config);
            if(df.backdrop=='static'){
                //data-backdrop="static"
                element.attr('data-backdrop','static');
            }else{
                element.removeAttr('data-backdrop');
            }
            //如果有dom属性
            if (df.dom) {
                var dom, readOnly = false;
                //JQ对象
                if (df.dom instanceof jQuery) {
                    dom = df.dom;
                    //普通对象
                } else if (df.dom instanceof Object) {
                    dom = $(df.dom.form);
                    readOnly = df.dom.readOnly;
                }
                //初始化modal表单 根据ID 与缓存 决定是否每次都清空modal-body
                _init(dom.selector, df.cache);

                if (!df.cache || dom.selector != lazyLoadFormId) {
                    var result  = _resolveTmpl(dom.html(),df.data || {});
                    result.html && body.append(result.html);
                    result.script && body.append(result.script);
                    //解析标签绑定标签
                    result.html && _bindDFEvents(result.html,eventQueue);
                    //完全显示做的事件
                    element.off('shown.bs.modal').on('shown.bs.modal',function(){
                        $('.js-for-focus',element).trigger('focus');
                        df.onShow(modal);
                    });
                    //完全隐藏后的事件
                    element.off('hidden.bs.modal').on('hidden.bs.modal',function(){
                        df.onHide(modal);
                    });
                    element.off('show.bs.modal').on('show.bs.modal',function(event){
                        if($(event.target).hasClass('modal')){
                            setTimeout(function () {
                                if($('.modal.in').length){
                                    var backdrop = $('.modal-backdrop.in').last(),zindex;
                                    if(backdrop.prev().hasClass('modal-backdrop')){
                                        zindex = Number(backdrop.prev().css('z-index'));
                                    }else{
                                        zindex = Number(backdrop.css('z-index'));
                                    }
                                    backdrop.css('z-index',zindex+20);
                                }
                                if($('.modal-backdrop.in').length){
                                    $('body').css('overflow','hidden');
                                }else{
                                    $('body').css('overflow','auto');
                                }
                            },1)
                        };
                    });
                    element.off('hide.bs.modal').on('hide.bs.modal',function(event){
                        if($(event.target).hasClass('modal')){
                            element.css('z-index',1050);
                            setTimeout(function () {
                                if($('.modal.in').length){
                                    $('body').css('overflow','hidden').addClass('modal-open');
                                }else{
                                    $('body').css('overflow','auto');
                                }
                            },1);
                        }
                    });
                }
                //非组件情况
                if (!type) {
                    lazyLoadFormId = dom.selector;
                }
                /**
                 * TODO 根据表单 遍历一次form即可
                 */
                if (modalId != '_LYModal_alert') {
                    //判断是否为只读
                    self.setFormReadOnly(body.children(), readOnly);
                    //添加modal-body 第一次添加的body需要初始化所有组件
                    self.initCustomFormComponent(body.children());
                    //初始化表单
                    self.initCustomFormPlaceHolder(body.children());
                }
            } else {
                console.log('模态窗口必须提供展示内容且为字符串或者jqdom对象！');
                return
            }

            //添加modal的宽度
            $('#' + modalId + ' .modal-dialog').css({'width': df.width});
            //添加modal-header
            header.find('.modal-title').text(df.title);
            //添加modal-footer
            // var bdfs = [];
            if (config.btns && config.btns.length) {
                var items = config.btns;
                for (var i = 0; i < items.length; i++) {
                    var cfg = items[i];
                    if (!cfg.authCode || (ad && _isAuthCodePassByAuthorityData(ad,cfg.authCode))) {
                        var bdf = {
                            valid: true
                        };
                        bdf = $.extend(bdf, cfg);
                        // bdfs[i] = bdf;
                        var btn = $('<button type="button" class="btn btn-info"></button>');
                        // btn.attr('index', i);
                        btn.attr('id', bdf.id);
                        btn.text(bdf.text);
                        bdf.class && btn.removeClass('btn-info').addClass(bdf.class);
                        bdf.icon && btn.append('<i class="' + bdf.icon + '">');
                        (function (df) {
                            df.fn && btn.on('click',function () {
                                var $btn = $(this);
                                if (df.valid) {
                                    if(self.validate(form)){
                                        df.fn.call($btn, modal,df.param);
                                    }
                                } else {
                                    df.fn.call($btn, modal,df.param);
                                }
                            });
                        })(bdf);
                        footer.append(btn);
                    }
                }
            }
            //if the modal is open ,the modal which will be open should be high level 'z-index' than the last one
            if($('.modal.in').length){
                var currentModal = $('.modal.last').length? $('.modal.last'):$('.modal:visible');
                $('.modal').removeClass('last');
                element.addClass('last');
                var currentModalZIndex = Number(currentModal.css('z-index'));
                element.css('z-index',currentModalZIndex+20);

            }
            var modal = element.modal('show', df);
            //暴漏获取form的方法
            modal.getForm = function () {
                return body.children();
            };
            var form = modal.getForm();
            //表单校验
            //form.validate({
            //    debug: true
            //});
            //暴露销毁modal的方法
            modal.destroy = function () {
                header.find('h4').text('');
                body.children() && body.children().remove();
                footer.children() && footer.children().remove();
                header = null;
                body = null;
                footer = null;
            }
            fn && fn(modal);

            //初始化modal窗口 formId为懒加载的formid
            function _init(formId, cache) {
                footer.children().remove();
                //清空异步组件赋值缓存区
                ayncSetF = {};
                if (!cache || formId != lazyLoadFormId) {
                    body.text('');
                    body.children().remove();
                } else {
                    body.children().reset();
                    //模态窗口缓存 验证信息需要清空
                    //body.children().validate({
                    //    debug: true,
                    //    ignore:'input:hidden',
                    //    errorPlacement:selectErrorHandler
                    //}).init();
                }
            };
            return modal;
        };
        this.hideModal = function (modal) {
            modal.modal('hide');
        }

        /**
         * 设置表单只读 与否
         * @param flag
         */
        this.setFormReadOnly = function (form, flag) {
            if (form instanceof  jQuery && form.length) {
                ////添加只读样式
                flag?form.addClass("DF-form-readonly"):form.removeClass("DF-form-readonly");
                var elements = form.find('div.form-group label').next();
                elements.each(function () {
                    var $this = $(this);
                    //为父容器添加 只读类
                    var pc = $this.parent();
                    flag?pc.addClass('readonly'):pc.removeClass('readonly');
                    $this.setReadOnly(flag);
                });
            } else {
                console.log('入参为jquery对象的form');
            }
        }
        //为每一种html元素设置 只读 readonly
        $.fn.setReadOnly = function (flag) {
            var $this = $(this);
            _isMultipleSelect($this) && ($this = $this.next());
            /*若checkbox*/
            if ($this.hasClass('iswitch') && $this.attr('type') == 'checkbox') {
                if(flag){
                    $this.hide();
                    $this.next().show();
                    $this.parent().append('<span class="DF-placeHolder pleft pright">' + $this.parent().find('label').text() + ($this.val()?'是':'否')+ '</span>');
                    $this.parent().find('label').css("cssText", "display: hidden!important");
                }else{
                    $this.show();
                    $this.next().hide();
                    $this.parent().find('span').hide();
                    $this.parent().find('label').css("cssText", "display: block!important");
                }
            }
            //遍历form下的所有组件 置为只读 除了ckeditor
            else if ($this.hasClass('uEditor')) {
                if(flag){
                    UE.getEditor($this.attr('id')).ready(function () {
                        this.disable();
                    });
                }else{
                    UE.getEditor($this.attr('id')).ready(function () {
                        this.enable();
                        this.setContent("");
                    });
                }
            } else {
                if(flag){
                    $this.attr("readonly","readonly");
                    $this.attr("disabled","disabled");
                    //$this[0].disabled = true;
                    //$this.unbind('placeholder.focus');
                }else{
                    $this.removeAttr('readonly');
                    $this.removeAttr('disabled');
                    //$this.bind('placeholder.focus');
                }
            }
            //判断是否为只读显示的字段
            if ($this.attr('forView') == 'true') {
                if(flag){
                    $this.parent().removeClass('hidden');
                }else{
                    $this.parent().addClass('hidden');
                }
            }
        }
        /**
         * 为表单赋值
         * @param data 数据要是一个object
         * @param form 需要一个jquery对象
         * DIV与类 层级固定
         */
        //异步组件需要 加载完之后再进行赋值
        var ayncSetF = {};
        this.getAyncSetF = function () {
            return ayncSetF;
        }
        this.setFormValues = function (data, form) {
            if (form instanceof jQuery) {
                //每次赋值时 清空异步组件赋值缓存区
                ayncSetF = {};
                // var elements = form.find('div.form-group label').next();
                var elements = $('input[name],select[name],textarea[name],div[name]',form);
                elements.each(function () {
                    var $this = $(this);
                    //若该jquery对象存在 则为选择器特例 为多选对象
                    _isMultipleSelect($this) && ($this = $this.next());
                    var name = $this.attr('name');
                    var tagName = $this[0].tagName;
                    for (var k in data) {
                        if (k == name) {
                            //checkbox
                            if ($this.hasClass('iswitch') && $this.attr('type') == 'checkbox') {
                                /*显示值*/
                                var showValue = "";
                                //获取当前选择状态
                                var checked = $this.is(':checked');
                                /*显示值*/
                                var showValue = "";
                                if (data[k] == true || data[k] == "true" || data[k] == "1") {//应为选中状态
                                    showValue = "是";
                                    //根据当前状态判断是否点击
                                    if (!checked) {
                                        $this.trigger('click');
                                    }
                                } else {
                                    showValue = "否";
                                    if (checked) {
                                        $this.trigger('click');
                                    }
                                }
                                $this.val(data[k]);
                                $this.next().val(showValue);
                                break;
                            } else if ('INPUT' == tagName) {
                                //单选
                                if($this.attr('type')=='radio'){
                                    if($this.val() == data[k]){
                                        //btn-group的时候添加active
                                        if($this.parent().is('label')&&$this.parent().hasClass('btn')){
                                            $('input[name='+name+']:checked').parent().removeClass('active')
                                            $this.parent().addClass('active');
                                        }
                                        $this.prop('checked','checked');
                                        break;
                                    }
                                    //普通文本
                                }else {
                                    $this.val(data[k]);
                                    break;
                                }
                                //选择框（单选、多选）
                            } else if ('SELECT' == tagName) {
                                //下拉多选
                                if ($this.attr('multiple')) {
                                    //根据jsonListparseKey标签属性解析
                                    var dataArray = [];
                                    //TODO
                                    if ($this.attr('jsonListParseKey')) {
                                        for (var i in data[k]) {
                                            dataArray[i] = data[k][i][$this.attr('jsonListParseKey')]
                                        }
                                    } else {
                                        if($.isArray(data[k])){
                                            dataArray = data[k];
                                        }else{
                                            dataArray = data[k] && data[k].split(",") || [];
                                        }
                                    }
                                    //dataArray.length && $this.val(dataArray).trigger('change');
                                    if($this.find('option').length){
                                        setTimeout(function(){
                                            $this.val(dataArray).trigger('change');
                                        },100)
                                    }
                                    dataArray.length && (ayncSetF[name] = dataArray);
                                } else {
                                    //下拉单选 非下拉树的情况
                                    if(!$this.attr('selectTree')){
                                        //data[k] && $this.val(data[k]).trigger('change');
                                        //console.log($this.find('option').length);
                                        if($this.find('option').length){
                                            setTimeout(function(){
                                                var value  = data[k];
                                                if(value||value===false){
                                                    typeof value==='boolean' && (value=value.toString());
                                                    $this.val(value).trigger('change');
                                                }else{
                                                    $this.next().find('.selectboxit-text').text('');
                                                }
                                            },100)
                                        }
                                        if (!data[k]) {
                                            //如果没有值那么不显示默认值
                                            $this.val('');
                                            $this.next().find('.selectboxit-text').text('');
                                        }else{
                                            ayncSetF[name] = data[k]
                                        }
                                    }else{
                                        //下拉树 字典组件
                                        if($this.attr('dictitemcode')){
                                            var arr = $this.attr('levelDictItemCode'),
                                                dictCodeName = $this.attr('dictCodeName'),
                                                dictItemCode = data[dictCodeName],
                                                cascadeParentName = $this.attr('cascadeParentName')=='true';
                                            var dictValueCode = data[k];
                                            if(dictItemCode && dictValueCode){
                                                var url = 'dataDict/dataDictValueName/'+dictItemCode+'/'+dictValueCode +'/'+cascadeParentName;
                                                DF.ajax({
                                                    url:url
                                                    //url:'data/dictname.json'
                                                }).done(function(resp){
                                                    if(resp && resp.result){
                                                        var selectContainer = $this.next();
                                                        selectContainer.find('.selectboxit-text').text(resp.result);
                                                        $this.find('option').first().val(dictValueCode);
                                                        $this.val(dictValueCode);
                                                    }
                                                });
                                            }

                                        }else{
                                            if(data[k] && data[k].split('\,').length!==2){
                                                console.warn('下拉树的赋值格式为"id,text"');
                                                break;
                                            }
                                            setTimeout(function(){
                                                var arr = data[k].split('\,');
                                                var selectContainer = $this.next();
                                                selectContainer.find('.selectboxit-text').text(arr[1]);
                                                $this.find('option').first().val(arr[0]);
                                                $this.val(arr[0]);
                                            },100);
                                        }
                                    }
                                }
                                break;
                            } else if ($this.hasClass('uEditor')) {
                                setTimeout(function () {
                                    UE.getEditor($this.attr('id')).ready(function () {
                                        this.setContent(data[k]);
                                    })
                                }, 300);
                                break;
                            } else {
                                //TODO 其他标签
                                $this.val(data[k]);
                                break;
                            }
                        }
                    }
                });
            }
        };
        //提示框
        this.toast = function () {
            toastr.options = {
                "closeButton": true,
                "debug": true,
                "positionClass": "toast-bottom-right",
                "onclick": null,
                "showDuration": "300",
                "hideDuration": "1000",
                "timeOut": "3000",
                "extendedTimeOut": "1000",
                "showEasing": "swing",
                "hideEasing": "linear",
                "showMethod": "fadeIn",
                "hideMethod": "fadeOut"
            }
            return {
                success: function (content, title, option) {
                    if (!title) title = '';
                    $.extend(toastr.options, option);
                    toastr.success(content, title);
                },
                error: function (content, title, option) {
                    if (!title) title = '';
                    $.extend(toastr.options, option);
                    toastr.error(content, title);
                },
                info: function (content, title, option) {
                    if (!title) title = '';
                    $.extend(toastr.options, option);
                    toastr.info(content, title);
                },
                warning: function (content, title, option) {
                    if (!title) title = '';
                    $.extend(toastr.options, option);
                    toastr.warning(content, title);
                }
            }
        }();
        /**
         * 初始化标签
         * @param forms
         * @private
         */
        this.initCustomFormComponent = function (forms, fn) {
            if (typeof forms == 'object' && forms.length) {
                //表单清除
                var form;
                function _initSingleForm($form){
                    var _form = $form;
                    // var elements = _form.find('div.form-group label').next();
                    var elements = $('input[name],select[name],textarea[name],div[name]',_form);
                    //控制form内元素的 placeholder位置 监听获得焦点与失去焦点事件
                    elements.each(function () {
                        var $this = $(this);
                        //若该jquery对象存在则为选择器特例 选择多选对象
                        if ($this.next()[0]) {
                            if ($this.next()[0].tagName == "SELECT") {
                                $this = $this.next()
                            }
                        }
                        //多选及单选的tagName
                        var tagname = $this[0].tagName;
                        if ($this.attr('displayvalue')||""==$this.attr('displayvalue')) {
                            $this.val($this.attr('displayvalue'));
                            //添加点击事件 当值等于displayvalue时删除
                            $this.on('click', function () {
                                var val = $this.val() == $this.attr('displayvalue') ? "" : $this.val();
                                $this.val(val);
                            })
                        }
                        if (tagname == 'INPUT') {
                            /*根据input标签是否含有datetype标签元素初始化时间组件*/
                            if ($this.attr('datetype')) {
                                $this.initDateComponent($this.attr('datetype'));
                            }

                        } else if (tagname == 'SELECT') {
                            var url = $this.attr('dataurl');
                            //初始化combobox
                            $this.initCombobox(url, fn);
                        } else if ($this.hasClass('uEditor')) {
                            //ueditor根据dom节点初始化实例
                            UE.getEditor($this.attr('id'));
                        }else if($this.hasClass('btn-group')){
                            //radio 动态加载实例
                            var url = $this.attr('dataurl');
                            if(url){
                                var config = {
                                    url:url,
                                    name:$this.attr('name'),
                                    parseValue:$this.attr('parseValue'),
                                    parseText:$this.attr('parseText'),
                                    customAttrs:$this.attr('customAttrs'),
                                    radioClass :$this.attr('radioClass'),
                                    labelClass:$this.attr('labelClass'),
                                    'df-change':$this.attr('radioChange')
                                }
                                $this.initRadioGroup(config);
                            }
                        }
                    });
                }
                forms.each(function(index){
                    form = $(this);
                    _initSingleForm(form);
                });
                setTimeout(function(){
                    _scanComponentByTagDeclare(forms);
                },1);
            } else {
                console.log('初始化表单失败!');
            }
        }
        /**
         * 在标签解析结束之后 浏览标签生命的组件 并绑定事件
         * @param $dom
         * @private
         */
        function _scanComponentByTagDeclare($dom){
            //加减 组件
            $dom.find(".input-group.spinner").each(function(i, el) {
                var $ig = $(el),
                    $dec = $ig.find('[data-type="decrement"]'),
                    $inc = $ig.find('[data-type="increment"]'),
                    $inp = $ig.find('.form-control'),
                    step = attrDefault($ig, 'step', 1),
                    min = attrDefault($ig, 'min', 0),
                    max = attrDefault($ig, 'max', 0),
                    umm = min < max;
                $dec.off('click').on('click', function(ev) {
                    ev.preventDefault();
                    var num = new Number($inp.val()) - step;
                    if(umm && num <= min) {
                        num = min;
                    }
                    $inp.val(num).trigger('change');
                });
                $inc.off('click').on('click', function(ev) {
                    ev.preventDefault();
                    var num = new Number($inp.val()) + step;
                    if(umm && num >= max) {
                        num = max;
                    }
                    $inp.val(num).trigger('change');;
                });
            });
        }
        $.fn.initDateComponent = function (option) {
            var datetype = $(this).attr("datetype");
            if (datetype) {
                if(datetype=='daterange'){
                    $(this).daterangepicker({
                        format:'YYYY-MM-DD',
                        locale:{
                            applyLabel: '确定',
                            cancelLabel: '取消',
                            fromLabel: '从',
                            toLabel: '至'
                        }
                    });
                }else{
                    $(this).datepicker({
                        format: 'yyyy-mm-dd', template: 'modal',
                        showInputs: false, time: $(this).attr("datetype") == "datetime" ? true : false
                    });
                }
            } else {
                console.log('时间组件初始化失败！');
            }
        }
        /*初始化combobox*/
        $.fn.initCombobox = function (url, fn) {

            var $this = $(this);
            //$this.selectBoxIt().data('selectBoxSelectBoxIt').destroy();
            /*变量用来标示是否每次加载*/
            var autoload = $this.attr('autoload') == "undefined" || $this.attr('autoload') == "false" ? false : true;
            //变量用来判断第一个参数是url还是回调函数
            var realUrl = typeof(url) == "function" ? $this.attr('dataurl') : url;
            //变量用来获取真正的自定义fn  若第一个参数是fn则url为fn否则依然为fn
            var fn = typeof(url) == "function" ? url : fn;
            //变量用来标识url与初始化url是否一致
            var ifChange = $this.attr('dataurl') == realUrl ? false : true;
            //变量用来标示是否传递了url
            var ifUrl = typeof(url) == "string" ? true : false;
            //变量用来标示当前标签的option是否为空
            var hasOption = $this[0].length == 0 ? false : true;
            _initStandard(ifUrl, hasOption, autoload, ifChange, realUrl, $this, fn);
        }
        /**
         * 初始化动态加载的btn-group-radio
         * @param config
         * @param fn
         */
        $.fn.initRadioGroup = function (config,fn) {
            var defaults = {
                labelClass:'btn btn-white'
            },element = $(this);
            defaults = $.extend(defaults,config);
            DF.ajax({
                url:defaults.url
            }).done(function (data) {
                if(data.errorCode=='1' && data.result.length){
                    var customAttrs = defaults.customAttrs,result = data.result;
                    if(customAttrs){
                        var s = customAttrs.substring(1,customAttrs.length-1),arr = s.split('\,'),obj = {};
                        for(var i=0;i<arr.length;i++){
                            var item = arr[i].split('\:');
                            item.length==2 && (obj[item[0]] = item[1]);
                            // if(item.length==2){
                            //     obj['key'] = item[0];
                            //     obj['value'] = item[1];
                            // }
                        }
                        if(!$.isEmptyObject(obj)){
                            for(var i=0;i<result.length;i++){
                                var o = {};
                                for(var k in obj){
                                    o['key'] = k;
                                    //如果自定义属性的值 在数据源中存在那么取数据源的值 否则认为是写死的字符串
                                    if(result[i][obj[k]]){
                                        o['value'] = result[i][obj[k]];
                                    }else{
                                        o['value'] = obj[k];
                                    }
                                }
                                result[i]['customObject'] = o;
                            }
                        }

                    }
                    var source = '{{each items as item}}\
                                <label class="'+defaults.labelClass+'">\
                                    <input type="radio" class="'+defaults.radioClass+'" name="'+defaults.name+'"  {{item.customObject.key}}="{{item.customObject.value}}"\
                                value="{{item.'+defaults.parseValue+'}}" autocomplete="off"><span>{{item.'+defaults.parseText+'}}</span>\
                                </label>\
                                {{/each}}';
                    var html = DF.template(source,{items:result});
                    if($('label',element).length){
                        $('label',element).first().after(html)
                    }else{
                        element.append(html);
                    }
                }
            })
        }
        /*ajax请求数据规则*/
        /*URL为空不加载*/
        /*     option为空	自动加载	不自动加载	URL有变化
         加载	true	      true		   false       true
         不加载	false		  false         true	   false
         */
        function _initStandard(ifUrl, hasOption, autoload, ifChange, realUrl, $this, callback) {
            //判断是否为下拉树
            if(!$this.attr('selectTree')){
                //第一次 自动加载 url改变(联动) 加载url
                if (realUrl && (!hasOption || autoload || ifChange)) {
                    //url存在
                    DF.ajax({
                        url: realUrl,
                        showLoadingBar:false,
                        success: function (data) {
                            //如果select标签中key value 没有值则赋值为key value
                            var key = $this.attr('key') ? $this.attr('key') : "key";
                            var value = $this.attr('value') ? $this.attr('value') : "value";
                            //根据解析方式解析json
                            var items = data["result"];
                            if (items) {
                                $this.attr('groupId') ? parseGroup($this, items) : parseData($this, items);
                            }
                            renderCombobox($this, $this.attr('multiple'));
                            callback && callback($this,data);
                        }
                    });
                }else{
                    renderCombobox($this, $this.attr('multiple'));
                    callback && callback($this);
                }
            }else{
                renderCombobox($this, $this.attr('multiple'));
                callback && callback($this);
            }
        }

        /*渲染组件*/
        function renderCombobox($this, multiple) {
            //获得异步组件 缓存区的值
            var value = (multiple?[]:''),name=$this.attr('name');
            ayncSetF[name] && (value=ayncSetF[name]);
            //多选
            if (multiple) {
                $this.data('select2') && $this.data('select2').destroy && $this.data('select2').destroy();
                $this.val(value).select2({
                    allowClear: true,
                    formatNoMatches: "没有匹配的结果"
                }).on('select2-open', function () {
                    $(this).data('select2').results.addClass('overflow-hidden').perfectScrollbar();
                });
                //if(value.length){
                //$this.trigger('change');
                //}
            } else {
                //单选搜索
                var emptyText = $this.attr('emptyText');
                if(emptyText){
                    $this.find('option').first().before('<option value="">'+emptyText+'</option>');
                }
                if($this.attr('searchable')==="true"){
                    $this.val(value).select2({
                        placeholder: '',
                        allowClear: true,
                        formatNoMatches:function () {
                            return '没有匹配到结果';
                        }
                    }).on('select2-open', function() {
                        $(this).data('select2').results.addClass('overflow-hidden').perfectScrollbar();
                    });
                    if(value!==undefined &&  value!=''){
                        $this.trigger('change');
                    }
                    if(!value){
                        var container = $this.prev();
                        container.find('.select2-chosen').text('');
                    }
                }else{
                    $this.selectBoxIt().data('selectBoxSelectBoxIt').destroy();
                    var config = {
                        showEffect: "fadeIn",
                        showEffectSpeed: 250,
                        hideEffect: "fadeOut",
                        hideEffectSpeed: 250
                    }
                    //emptyText && (config['defaultText'] = emptyText);
                    $this.val(value).selectBoxIt(config).on('open', function () {
                        //如果是下拉树
                        if($this.attr('selectTree')){
                            var selectContainer = $this.next()
                            var dcname = $this.attr('dictCodeName');
                            var dictItemCode = $this.attr('dictitemcode');
                            if(dcname){
                                if(!$this.prev().is('input')){
                                    $this.before('<input type="hidden" name="'+dcname+'">');
                                }
                            }
                            var ul = selectContainer.find('ul.selectboxit-options');
                            if(ul.find('.ztree').length == 0 ){
                                var ztreeSelect = $('<div class="selectTree ztree"></div>');
                                ul.find('li').first().before(ztreeSelect);
                                _loadSTree($this,function(){
                                });
                            }
                            setTimeout(function(){
                                ul.css({
                                    'maxHeight':'400px'
                                });
                            },100);
                        }
                        $(this).data('selectBoxSelectBoxIt').list.perfectScrollbar();
                    });
                    if(value!==undefined &&  value!=''){
                        $this.trigger('change');
                    }
                    if(!value){
                        var container = $this.next();
                        container.find('.selectboxit-text').text('');
                    }
                }

            }
        }
        this.selectTree = function(cfg){
            var df = {},me = this;
            df = $.extend(df,cfg);
            var element = df.id;
            typeof element == 'string'&&( element = $('#'+ element));
            element.selectBoxIt().on('open',function(){
                var selectContainer = element.next();
                var ul = selectContainer.find('ul.selectboxit-options');
                if(ul.find('.ztree').length == 0 ){
                    var ztreeSelect = $('<div class="selectTree ztree"></div>');
                    ul.find('li').first().before(ztreeSelect);
                    me.dataDictTree(df);
                }
                setTimeout(function(){
                    ul.css({
                        'maxHeight':'400px'
                    });
                },100);
            });
        }
        /**
         * 加载字典下拉树
         * @param cfg
         */
        this.dataDictTree = function(cfg){
            //console.log(cfg);
            var df = {
                callback : {
                    //下拉树点击事件
                    onClick:function(event, treeId, treeNode){
                        if(df.selecttree){
                            if((!df.selectLevel || df.selectLevel == treeNode.level) && treeNode.id && treeNode.text){
                                //是否级联父节点名称
                                if(df.cascadeParentName){
                                    selectContainer.find('.selectboxit-text').text(treeNode.getParentNode().text + ' '+treeNode.text);
                                }else{
                                    selectContainer.find('.selectboxit-text').text(treeNode.text);
                                }
                                element.find('option').first().val(treeNode.dictValueCode);
                                element.val(treeNode.dictValueCode);
                                element.prev().val(treeNode.dictItemCode);
                                selectContainer.find('.selectboxit-arrow-container').trigger('click');
                            }
                        }
                    }
                },
                onComplete:function(){}
            }
            df = $.extend(df,cfg);
            var element = df.id;
            typeof element == 'string'&&( element = $('#'+ element));
            if(!element instanceof jQuery) {
                console.log('id为jq对象或者dom字符串');
                return
            }
            var selectContainer = element.next(),
                treeObj,
                treeContainer = selectContainer.find('.ztree');
            //如果是数据字典
            if(df.dictItemCode){
                df.callback.beforeAsync = function(treeId,treeNode){
                    if(treeNode){
                        var param = {};
                        if (treeNode.dictItemCode != undefined) {
                            param['dictItemCode'] = treeNode.dictItemCode;
                        }
                        if (treeNode.dictValueCode != undefined) {
                            param['dictValueCode'] = treeNode.dictValueCode;
                        }
                        if(!$.isEmptyObject(param)){
                            treeObj.setting.async.otherParam = param;
                        }
                        treeObj.setting.async.url = df.url;
                    }
                }
            }
            treeObj = DF.zTree({
                id:df.selecttree?treeContainer:element,
                checkable:df.checkable || false,
                chkboxType:df.chkboxType,
                dragable:df.dragable || false,
                url:df.dictItemCode?df.url+'?dictItemCode='+df.dictItemCode:df.url,
                idKey:df.idKey,
                textKey:df.textKey,
                pidKey:df.pidKey,
                // dictItemCode:df.dictItemCode,
                selectLevel:df.selectLevel,
                //autoParam:['id','pId'],
                callback:df.callback,
                searchable:df.searchable,
                onComplete:function(treeObject){
                    df.onComplete();
                }
            });
            return treeObj

        }
        /**
         * 加载树
         * @param element
         * @param fn
         * @private
         */
        function _loadSTree(element,fn){
            var config = {
                id:element,
                url : element.attr('dataurl'),
                idKey : element.attr('key'),
                textKey : element.attr('parseValue'),
                pidKey : element.attr('parentKey'),
                dictItemCode : element.attr('dictItemCode'),
                levelDictItemCode :  element.attr('levelDictItemCode'),
                cascadeParentName : element.attr('cascadeParentName')=='true',
                selecttree:element.attr('selecttree')=='true',
                selectLevel : element.attr('selectLevel'),
                onComplete :fn,
                searchable:false
            }
            DF.dataDictTree(config);
        }
        /*解析分组加载*/
        function parseGroup($this, data) {
            $this[0].length = 0;
            $this[0].store = data;
            var key = $this.attr("groupId");
            var value = $this.attr("groupName");
            var code = $this.attr("childCode");
            var opt = $('<optgroup>');
            for (var k in data) {
                opt.attr('lable', data[k][value]);
                opt.attr('id', data[k][key]);
                $this.append(opt);
                parseData($this, data[k][code], opt);
            }
        }

        /*解析data方法*/
        function parseData($this, data, group) {
            if (!group) {
                $this[0].length = 0;
                $this[0].store = data;
            }
            for (var k in data) {
                //将combobox的store注册于该combobox
                var value = $this.attr("parseValue");
                var key = $this.attr("key");
                var op = $('<option>');
                op.attr('value', key.match(/\$/) != null ? parseDataByregular(key, data[k]) : data[k][key]);
                op.text(value.match(/\$/) != null ? parseDataByregular(value, data[k]) : data[k][value]);
                if (group) {
                    group.append(op);
                } else {
                    $this.append(op);
                }
            }
        }

        /*根据正则解析数据*/
        function parseDataByregular(rules, data) {
            var returnValue = rules;
            var valueArr = returnValue.match(/\${[^${}\W]+/g);
            for (var i in valueArr) {
                var valueKey = valueArr[i].replace("${", "");
                if (data[valueKey]) {
                    returnValue = returnValue.replace(valueArr[i] + "}", data[valueKey]);
                }
            }
            return returnValue;
        }
        //验证错误位置重写
        function selectErrorHandler(error,element){
            if(element[0].tagName == 'SELECT'&& !element.attr('multiple')){
                element = element.next();
            }
            element.after(error);
        }
        /**
         *
         * @param $form
         */
        this.validate = function ($form) {
            if ($form.length) {
                //表单校验
                $form.validate({
                    debug: true,
                    ignore:'input:hidden,select[hidden-invalid=true]',
                    errorPlacement:selectErrorHandler
                });
                return $form.valid();
            } else {
                console.log('请传入jq.form对象');
            }
        }
        /**
         * 封装jq validate方法
         */
        this.addValid = function (validTag, businessFn, errorMsg) {
            if (typeof(validTag) === 'string' && typeof(businessFn) === 'function') {
                $.validator.addMethod(validTag, function (value, element) {
                    return businessFn(value, element);
                }, errorMsg ? errorMsg : '验证不通过');
            }
        };
        /**
         * 渲染列表顶部按钮或者搜索框（表单）
         * @param grid
         */

        /**
         * 封装dataTable
         * @param config
         */
        var pageNumber = {
            tid:'',
            number:''
        } ;
        this.dataTable = function (config) {
            //参数必填校验
            if (!config.id || (!config.ajax && !config.url) || !config.columns) {
                console.log('table的ID、列、请求url为必要参数!');
                return;
            } else {
                if (!$.isArray(config.columns)) {
                    console.log('columns必须为数组!');
                    return;
                }
            }
            var self = this,
            //列
                col = config.columns,
            //table dom的ID
                tid = config.id,
            //是否跨域
                crossDomain = config.crossDomain?'jsonp':'json',
            //行id的名儿 默认为第一列的列名
                rid = col[0]['data'],
            //查看
                viewForm = config.viewForm,
            //是否多选
                multiple = config.multiple,
            //多选的记录
                selectedRows = {},
            //是否显示搜索框 没设置属性、设置属性true 为显示    false不显示
                searchable = (config.searchable === undefined || config.searchable === true) ? true : false,
            //查看函数
                viewItem = function (rowdata) {
                    //默认 配置
                    var config = {
                        width: '700px',
                        title: '详情页面'
                    }, dom = {form: null, readOnly: true};
                    //判断viewForm 类型 做向下兼容
                    if(viewForm instanceof jQuery){
                        dom.form = viewForm;
                        config.dom = dom;
                    }else if(viewForm instanceof Object){
                        config = $.extend(config,viewForm);
                        if(config.dom){
                            dom.form = config.dom;
                            config.dom = dom;
                        }else{
                            console.error('参数为对象时必须传递dom');
                        }
                    }else{
                        console.error('参数传递错误');
                        return;
                    }
                    self.showModal(config, function (modal) {
                        var form = modal.getForm();
                        self.setFormValues(rowdata, form);
                    });
                },
            //重新加载
                reloadGrid = function () {
                },
            //简单搜索的列
                searchCol = config.searchCol ? config.searchCol : [1],
            //是否启用joinColumn形式传递参数
                joinColumn = config.joinColumn===undefined?true:config.joinColumn,
            //高级查询搜索的列
                seniorSearch = config.seniorSearch || false,
            //高级搜索的扩展函数
                seniorSearchTpl = config.seniorSearchTpl || function(){return false},
            //点击高级按钮之前 既验证表单之前 添加验证信息
                beforeSeniorSearch = config.beforeSeniorSearch || function(){ return true},
            //返回的dataTable
                grid,
            //toolbtns默认新建
                toolbtns = config.toolbtns ? config.toolbtns : [],
            //操作列默认编辑删除
                rowbtns = config.rowbtns ? config.rowbtns : [],
            //渲染第一列的查看详情
                columnDefs = [],
            //是否隐藏操作列
                hideOptionCol = false,
            // 总记录数
                recordsTotal = 0;

            if (rowbtns.length) {
                columnDefs.push({
                    render: function (data, type, row) {
                        // var td = $('<td><div class="option text-center" style="min-width: 102px;display: block;">');
                        var td = $('<td class="option text-center" style="min-width: 102px;display: block;">');
                        // var div = td.find('div.option');
                        //var length = 102+(rowbtns.length-2)*46;
                        //根据权限加载btn
                        for (var i = 0; i < rowbtns.length; i++) {
                            var btn = rowbtns[i];
                            //权限 1.如果页面没有配置权限按钮 2.或者配置了权限按钮 有后台配置 后台配置包含权限按钮
                            if (!btn.authCode || (ad && _isAuthCodePassByAuthorityData(ad,btn.authCode))) {
                                //业务规则
                                /**
                                 *   三种情况 显示按钮 支持两种模式
                                 *   1    !btn.displayValue && !row[btn.displayKey] && !btn.displayReg  //没有配置任何业务显示规则的
                                 *   2    || btn.displayValue && row[btn.displayKey] && btn.displayValue == row[btn.displayKey] && !btn.displayReg //配置displayValue与displayKey且满足条件的
                                 *   3    || !btn.displayValue && !row[btn.displayKey] && btn.displayReg && btn.displayReg(row) //没有配置displayValue与displayKey 但配置displayReg 且此函数返回true的
                                 *
                                 */
                                if (!btn.displayValue && !row[btn.displayKey] && !btn.displayReg || btn.displayValue && row[btn.displayKey] && btn.displayValue == row[btn.displayKey] && !btn.displayReg || !btn.displayValue && !row[btn.displayKey] && btn.displayReg && btn.displayReg(row)) {
                                    var a = $('<a>');
                                    a.attr({
                                        href: 'javascript:;',
                                        name: btn.name,
                                        code: btn.code
                                    });
                                    a.text(rowbtns[i].text);
                                    if (btn.class) {
                                        a.addClass('btn btn-sm btn-icon icon-left').addClass(btn.class);
                                    }
                                    btn.css && a.css(btn.css);
                                    td.append(a);
                                }
                            }else{
                                hideOptionCol = true;
                            }
                        }
                        var length = 102 + (td.find('a').length - 2) * 46;
                        td.css('min-width', length + 'px');
                        var colD = config.columnDefs,customStr;
                        if(colD){
                            for(var c=0;c<colD.length;c++){
                                if(colD[c].targets !== col.length) {
                                    continue;
                                }
                                customStr = colD[c].render(data, type, row);
                                customStr && td.find('a').first().before(customStr);
                            }
                        }
                        return td.html();
                    },
                    "targets": col.length
                });
            }
            //todo
            if(col.length){
                for(var d=0;d<col.length;d++){
                    var column = col[d];
                    if(column['editable']){
                        (function(c,i){
                            var renderFn;
                            for(var e=0;e<config.columnDefs.length;e++){
                                if(config.columnDefs[e].targets==i){
                                    renderFn = config.columnDefs[e].render;
                                }
                            }
                            columnDefs.push({
                                render: function (data,type,row) {
                                    if(renderFn){
                                        var renderStr = $(renderFn(data,type,row));
                                        if(renderStr.is('span')){
                                            renderStr.addClass('editor').attr('data-name',c['data']);
                                            return renderStr[0].outerHTML;
                                        }
                                    }
                                    return '<span class="editor" title="双击编辑" data-name="'+c['data']+'">'+data+'</span>';
                                },
                                targets:d
                            })
                        })(column,d);
                    }
                    if(column['authCode'] && !_isAuthCodePassByAuthorityData(ad,column['authCode'])){
                        column['visible'] = false;
                    }
                }
            }
            if (viewForm) {
                columnDefs.push({
                    render: function (data, type, row) {
                        var a = "<a style='color:#979898;cursor:pointer;text-decoration: underline;' name='view'>" + data + "</a>";
                        return a;
                    },
                    "targets": 1
                });
            }
            if (multiple) {
                columnDefs.push({
                    render: function (data, type, row) {
                        var checkBox = '<label class="cbr-inline">' +
                            '<div class="cbr-replaced">' +
                            '<div class="cbr-input">' +
                            '<input type="checkbox" class="cbr cbr-done">' +
                            '</div>' +
                            '<div class="cbr-state">' +
                            '<span></span>' +
                            '</div>' +
                            '</div>' +
                            '</label>';
                        return '<input type="checkbox" class="cbr">';
                    },
                    "targets": 0
                });
            }
            function getSearchPlaceHolder(selector,searchCol) {
                var placeholder = [];
                var ths = $('tr th','#'+selector),thLength = ths.length;
                ths.each(function (index) {
                    !$.isArray(searchCol) && (searchCol = [searchCol]);
                    if (searchCol.indexOf(index + 1) != -1 && index + 1 != thLength) {
                        placeholder.push($(this).text());
                    }
                });
                placeholder = placeholder.join('/');
                return placeholder;
            }

            /**
             *
             * @param url 请求地址
             * @param seniorSearch 是否是高级查询
             * @param dateElementNames 高级查询的date特殊处理(_start、_end)
             * @param $form 获取serialize()方法的form (不传递 从.sernior_wrapper获取)
             * @param joinColumn 是否启用joinColumn方式传参
             * @returns {*}
             */
            function getSearchUrl(url,seniorSearch,dateElementNames,$form,joinColumn) {
                if(seniorSearch){
                    var form = $form || $('.senior_wrapper',$('#'+tid).prev()),t_url = (form.serialize()).replace(/\+/g,'%20'),s_url,f_url;
                    var obj = self.getSeniorSearchParam(),crp = getCurrentUrl();
                    //高级搜索缓存中存在且 缓存的页面与当前页面一直时才执行
                    if(obj&&obj['_url_']==crp){
                        //hack 兼容 multipleSearchGrid.js 如果origin有 dateRangeColumnNames 那么把这个属性放到obj里
                        if(obj['originalData'] && obj['originalData']['dateRangeColumnNames']){
                            obj['dateRangeColumnNames'] = obj['originalData']['dateRangeColumnNames'];
                        }
                        f_url = self.analyticalUrlParams(obj);
                        senior_param = null;
                    }else{
                        f_url = t_url;
                    }
                    // if(url.indexOf('\?')==-1){
                    //     s_url = url + '?';
                    // }else{
                    //     s_url = url + '&';
                    // }
                    //判断是否有date组件 如果有 自动传递两个值 去掉原来的值
                    for(var k=0;k<dateElementNames.length;k++){
                        //当前url中拥有 如果没有拼凑好的字符串 那么拼装
                        if(f_url.indexOf(dateElementNames[k]+'_start')==-1 && f_url.indexOf(dateElementNames[k]+'_end')==-1){
                            f_url = f_url.replace(dateElementNames[k],dateElementNames[k]+'_start').replace('\%20-%20','&'+ dateElementNames[k] +'_end=');
                        }
                    }
                    s_url = url + (url.indexOf('\?')==-1?'?':'&') + f_url + '&seniorSearch=true';
                    return s_url;
                }else{
                    //获取保存的查询条件
                    var search_str = $('#'+tid+'_search_input').val()||(senior_param && senior_param['_save_status']),visibleColumns = [],s_url;
                    if (!search_str) {
                        return url
                    }else{
                        senior_param=null;
                    }
                    if(url.indexOf('\?')==-1){
                        s_url = url + '?';
                    }else{
                        s_url = url + '&';
                    }
                    for (var ii = 0; ii < col.length; ii++) {
                        if(col[ii]['visible']!==false){
                            visibleColumns.push(col[ii]);
                        }
                    }
                    //joinColumn格式传递参数
                    if(joinColumn){
                        var names = [];
                        for (var j = 0; j < searchCol.length; j++) {
                            var colName = visibleColumns[searchCol[j]-1] && visibleColumns[searchCol[j]-1]['data'];
                            names.push(colName);
                        }
                        var tempurl = 'joinColumnNames='+  names.toString() + '&joinColumnValue=' + search_str;
                        s_url += encodeURI(tempurl) + '&seniorSearch=false';
                    }else{
                        for (var j = 0; j < searchCol.length; j++) {
                            var colName = visibleColumns[searchCol[j]-1] && visibleColumns[searchCol[j]-1]['data'];
                            s_url += colName + '=' + search_str
                            if (j + 1 != searchCol.length) {
                                s_url += '&';
                            }
                        }
                    }
                    // if(searchCol.length==1){
                    //     var colName = visibleColumns[searchCol[0]-1] && visibleColumns[searchCol[0]-1]['data'];
                    //     s_url += colName + '=' + search_str;
                    // }else{
                    //     var names = [];
                    //     for (var j = 0; j < searchCol.length; j++) {
                    //         var colName = visibleColumns[searchCol[j]-1] && visibleColumns[searchCol[j]-1]['data'];
                    //         names.push(colName);
                    //     }
                    //     var tempurl = 'joinColumnNames='+  names.toString() + '&joinColumnValue=' + search_str;
                    //     s_url += encodeURI(tempurl) + '&seniorSearch=false';
                    // }
                    return s_url;
                }
            }
            //高级搜索时的date元素
            var dateElementNames = [];
            //截获向后台发送前的信息
            function retrieveData(sSource, aoData, fnCallback, param) {
                //获取topbar的div
                if(!$('#'+tid).prev().hasClass('search_loaded')){
                    $('#'+tid).prev().addClass('search_loaded');
                    var topwrapper = $('#'+tid).prev(),
                        buttonDiv = $('<button id="' + tid + '_search_button' + '"type="button" class="btn btn-warning btn-icon btn-icon-standalone btn-icon-standalone-right btn-sm pull-right" style="margin-left: 5px;" ></button>') ;
                    buttonDiv.append('<span class="linecons-search" style="padding-left: 6px;padding-right: 14px;line-height: 14px;"> 确定</span>');
                    var placeholder = getSearchPlaceHolder(tid,searchCol), inputLength = (12 * placeholder.length) + 'px';
                    var joinColName = '';
                    var leftDiv = $('<div class="col-xs-6"><form class="senior_wrapper"><label class="pull-left mb0">' +
                        '<input id="'+tid+'_search_input'+'" name="_save_status" autocomplete="off" type="search" class="form-control input-sm" placeholder="' + placeholder + '" style="min-width:195px;width:' + inputLength + '" onkeypress="javascript:return window.event.keyCode!==13;">' +
                        '</label></form></div>');
                    var rightDiv = $('<div class="col-xs-6 df_datatable_toolbar" id="' + tid + '_toolbar' + '">');
                    var seniorRow = $('<div class="row" ><div class="col-xs-12"><form class="senior_wrapper col-xs-12"></form></div></div>');
                    var searchForm = seniorRow.find('.senior_wrapper');
                    //增加loading元素
                    topwrapper.append('<div id="'+tid+'_processing" class="dataTables_processing" style="">Loading...</div>');
                    //渲染高级搜索
                    if(seniorSearch){
                        function _generateFormElement(formObj){
                            var $form = $(formObj.html()),colNames = [],searchTpl,
                                searchElementsWidth = 10,
                                $elements = $form.find('div.form-group label').next();
                            for (var j = 0; j < searchCol.length; j++) {
                                colNames[j] = col[searchCol[j]]['data'];
                            }
                            $elements.each(function(index){
                                var element = $(this),name = element.attr('name'),tagName = $(this)[0].tagName;
                                if(colNames.indexOf(name)!=-1){
                                    $('<div style="display: inline-block;margin-right: 5px;"></div>').append(element.css({
                                        width:'100%'
                                    }).removeAttr('id').removeAttr('required').parent().css({
                                        width:'100%',
                                        height:'40px',
                                        minHeight:0
                                    })).appendTo(searchForm);
                                    if(element.attr('datetype')){
                                        dateElementNames.push(element.attr('name'));
                                    }
                                    if(tagName == 'SELECT' && !element.attr('multiple')){
                                        element.attr('emptyText','请选择');
                                    }
                                }
                            });
                            searchTpl = seniorSearchTpl(searchForm);
                            if(searchTpl){
                                var _searchTpl ;
                                if(typeof searchTpl=='string'){
                                    _searchTpl = [searchTpl];
                                }else if($.isArray(searchTpl)){
                                    _searchTpl = searchTpl
                                }
                                for(var si=0;si<_searchTpl.length;si++){
                                    searchTpl = $(_searchTpl[si]);
                                    var t_width = searchTpl.css('width'),tagName = searchTpl[0].tagName;
                                    var parentFormGroup = $('<div class="form-group no-margin" style="width: 100%;height: 40px;min-height: 0;"></div>');
                                    parentFormGroup.append('<label for="icon" class="control-label">' + searchTpl.attr('placeholder') + '</label>')
                                        .append(searchTpl.css({ width:'100%'}));
                                    $('<div style="display: inline-block;margin-right: 5px;"></div>').append(parentFormGroup).appendTo(searchForm);
                                    searchTpl.removeAttr('id').removeAttr('required').attr('placeholder','');
                                    if(searchTpl.attr('datetype')){
                                        dateElementNames.push(searchTpl.attr('name'));
                                    }
                                    t_width && searchTpl.parent().parent().css({
                                        width:t_width
                                    });
                                    if(tagName == 'SELECT' && !searchTpl.attr('multiple')){
                                        if($('option',searchTpl).length ==0){
                                            searchTpl.append('<option></option>');
                                            searchTpl.attr('emptyText','请选择');
                                        }else{
                                            $('option',searchTpl).first().before('<option selected value="">请选择</option>');
                                        }
                                    }
                                }
                            }
                            searchElementsWidth = 90/searchForm.children().length;
                            searchForm.children().each(function(index){
                                $(this).css('width')=='0px' && $(this).css({
                                    width:searchElementsWidth+'%',
                                    maxWidth:'200px'
                                });
                            })
                            //DF.initCustomFormComponent(searchForm);
                            //DF.initCustomFormPlaceHolder(searchForm,false);
                            searchForm.append(buttonDiv);
                            buttonDiv.removeClass('pull-right').css({
                                marginBottom:'10px'
                            });
                        }
                        if(rowbtns.length){
                            //默认获取rowbtns对象内的表单内容 作为高级搜索的表单
                            for(var y=0;y<rowbtns.length;y++){
                                var rowbtn = rowbtns[y];
                                if(rowbtn && (rowbtn.name=='edit' || rowbtn.name=='create' || rowbtn.name=='goto')){
                                    var fnString = rowbtn.fn.toString(),formString,formObj;
                                    //兼容空格写法
                                    var p = fnString.indexOf('dom:');
                                    var domStart = p == -1?fnString.indexOf('dom :'):p;
                                    if(domStart==-1){
                                        _generateFormElement($('<div>'));
                                        break;
                                    }
                                    var domEnd = domStart + fnString.substring(domStart).indexOf(',');
                                    domStart = domStart + fnString.substring(domStart).indexOf(':')+1;
                                    formString = fnString.substring(domStart,domEnd);
                                    formObj = new Function('return '+formString)();
                                    //console.log(formObj);
                                    //根据表单与配置项目 获取表单元素
                                    _generateFormElement(formObj);
                                    break;
                                }else{
                                    console.warn('初始化高级搜索失败！');
                                }
                            }
                        }else if(viewForm){
                            _generateFormElement(viewForm.dom);
                        }else{
                            _generateFormElement($('<div>'));
                        }
                        //如果高级查询时 grid上方按钮为一整行
                        rightDiv.removeClass('col-xs-6').addClass('col-xs-12').appendTo(seniorRow);
                        ($('.senior_wrapper').length ==0) && topwrapper.append(seniorRow);
                        var reset = $('<button class="btn btn-gray" type="button" style="margin-bottom: 10px;"><span class="linecons-trash" style="line-height: 14px;">清空</span></button>');
                        buttonDiv.after(reset);
                        reset.on('click',function (event) {
                            $(this).parent().reset();
                            $(this).prev().trigger('click');
                        });
                        buttonDiv.off('click').on('click',function (event) {
                            if(beforeSeniorSearch(buttonDiv.parent())){
                                //正则为了把“+”替换成url格式的空格
                                if(DF.validate(buttonDiv.parent())){
                                    grid.ajax.reload();
                                }
                            };
                        });
                        $('input[type=search]',searchForm).each(function(i,e){
                            self.event.bindEnterKey($(e),buttonDiv);
                        });
                    }else{
                        //渲染普通搜索
                        if(searchable){
                            leftDiv.find('input').after(buttonDiv);
                            buttonDiv.off('click').on('click',function (event) {
                                grid.ajax.reload();
                            });
                            self.event.bindEnterKey(buttonDiv.prev(),buttonDiv);
                            topwrapper.append(leftDiv);
                        }else{
                            rightDiv.removeClass('col-xs-6').addClass('col-xs-12');
                        }
                    }
                    //渲染列表顶部按钮
                    if(toolbtns.length){
                        var objs = [];
                        for (var i = 0; i < toolbtns.length; i++) {
                            var btn = toolbtns[i];
                            //没有配置角色权限按钮（系统管理员）、配置角色权限按钮并且匹配的、配置角色权限按钮但没有在脚本中配置权限码的 3种情况正常加载
                            if (!btn.authCode || (ad && _isAuthCodePassByAuthorityData(ad,btn.authCode))) {
                                //业务显示规则
                                if(!btn.displayReg || btn.displayReg && btn.displayReg()){
                                    var b = $('<button>');
                                    b.attr({
                                        name: btn.name,
                                        type: 'button',
                                        class: 'btn pull-right '
                                    });
                                    b.text(btn.text);
                                    if (btn.class) {
                                        b.addClass(btn.class);
                                    }else{
                                        b.addClass('btn-success');
                                    }
                                    if (btn.icon) {
                                        b.append('<i class="' + btn.icon + '">');
                                        b.addClass('icon-right');
                                    }
                                    (function(btn){
                                        b.on('click', function () {
                                            if (multiple) {
                                                btn.fn && btn.fn.call($(this), _getSelectedRows());
                                            } else {
                                                btn.fn && btn.fn.call($(this));
                                            }
                                        });
                                    })(btn);
                                    rightDiv.append(b);
                                }
                            }
                        }
                        topwrapper.append(rightDiv);
                    }
                }
                var obj = self.getSeniorSearchParam(),crp = getCurrentUrl();
                if(obj&&obj['_url_']==crp){
                    var $form = $('.senior_wrapper').last();
                    DF.setFormValues(self.getSeniorSearchParam()['originalData'],$form);
                }
                var flag = param.ajax.indexOf('\?')==-1,url;
                var cascadFormStr = $('#'+tid).attr('searchform');
                if($(cascadFormStr).length){
                    url = getSearchUrl(param.ajax,true,[],$(cascadFormStr),joinColumn)
                }
                // else if(!flag){
                //     //TODO 临时解决方案
                //     url = param.ajax;
                // }
                else{
                    url = seniorSearch?getSearchUrl(config.ajax|| config.url,true,dateElementNames,null,joinColumn):getSearchUrl(config.ajax || config.url,null,null,null,joinColumn);
                }
                //控制隐藏 加载样式
                var inspect = {
                    beforeFn:function () {
                        var flag = config.beforeLoad && config.beforeLoad();
                        if(!flag){
                            $('.dataTables_processing',$('#'+tid).parent()).fadeIn();
                        }
                    },
                    afterFn:function () {
                        var flag = config.afterLoad && config.afterLoad();
                        if(!flag){
                            $('.dataTables_processing',$('#'+tid).parent()).fadeOut();
                        }
                    }
                }
                var before = inspect.beforeFn,
                    after = inspect.afterFn;
                before(param,aoData);
                $.ajax({
                    type: "get",
                    url: url,
                    dataType: crossDomain,
                    data: aoData,
                    success: function (data) {
                        after(data);
                        if(data.result){
                            recordsTotal = data.result.recordsTotal;
                            //服务器端返回的对象的returnObject部分是要求的格式
                            fnCallback(data.result);
                        }
                    }
                });
            }

            function _getSelectedRows() {
                var data = [];
                $('#'+ tid +' tbody input:checked').each(function(){
                    var v = $(this).val();
                    data.push(JSON.parse(v))
                });
                return data;
            }
            //默认配置
            var df = {
                //sAjaxSource: url,
                fnServerData: retrieveData,
                //fnServerParams:'',查询参数
                serverSide: true,
                bLengthChange: false,
                bAutoWidth: true,
                bSort: true,
                bFilter: false,
                bDestroy: true,
                bDeferRender: true,
                bSortClasses:false,
                sDom: "<'row topBar'>t<'row'<'col-xs-6'il><'col-xs-6'p>>",
                ///整个列表加载完后的回调
                language: {
                    "sProcessing": "处理中...",
                    "sLengthMenu": "显示 _MENU_ 条结果",
                    "sZeroRecords": "没有匹配结果",
                    "sInfo": "显示 _START_ 至 _END_ 条结果，共 _TOTAL_ 条",
                    "sInfoEmpty": "显示 0 至 0 条结果，共 0 条",
                    "sInfoFiltered": "(有 _MAX_ 项结果过滤)",
                    "sInfoPostFix": "",
                    "sSearch": "搜索:",
                    "sUrl": "",
                    "sEmptyTable": "表格数据为空",
                    "sLoadingRecords": "载入中...",
                    "sInfoThousands": ",",
                    "oPaginate": {
                        "sFirst": "首页",
                        "sPrevious": "上页",
                        "sNext": "下页",
                        "sLast": "末页"
                    },
                    "oAria": {
                        "sSortAscending": ": 以升序排列此列",
                        "sSortDescending": ": 以降序排列此列"
                    }
                }
            };
            df = $.extend(df, config);
            df.ajax = config.ajax || config.url;
            df.sAjaxSource = config.ajax || config.url;
            //覆盖函数
            //重新布局工具条
            // var fnPreDrawCallback = function (a,b,c) {
            //     debugger;
            //     console.log(a,b,c)
            // }
            // df.fnPreDrawCallback = fnPreDrawCallback;
            var initComplete = function () {
                //cbr_replace();
                config.initComplete && config.initComplete();
            };
            //每行加载后的事件绑定
            var createdRow = function (row, data, index) {
                //列按钮事件
                for (var i = 0; i < rowbtns.length; i++) {
                    //闭包保存下标
                    (function(btn){
                        var name = btn.name;
                        $('td a[name=' + name + ']', row).click(function () {
                            if(name.startsWith('goto')){
                                pageNumber.number = grid.page.info().start;
                                pageNumber.tid = tid;
                            }
                            btn.fn && btn.fn.call($(this), data, row);
                        });
                    })(rowbtns[i]);
                }
                //todo
                for (var j = 0; j < col.length; j++) {
                    if(col[j]['editable']){
                        (function(column){
                            var name = column['data'];
                            $('td span.editor[data-name='+name+']', row).on('click',function () {
                                //btn.fn && btn.fn.call($(this), data, row);
                                var $this = $(this),val = $this.text(),$input = $('<input type="text" class="form-control" style="width:'+($this.width()+40)+'px;">');
                                $this.after($input).hide();
                                $input.val(val).focus();
                                $input.blur(function(){
                                    if($input.val()!==val){
                                        $this.css({
                                            border:'2px solid #F7DE62'
                                        }).attr('title','记录改变');
                                        if(typeof column['editable'] == 'function'){
                                            column['editable'].apply(column,[data,$this,$input.val()]);
                                        }
                                    }
                                    $this.text($input.val()).show();
                                    $input.remove();
                                });
                            });
                        })(col[j]);
                    }
                }
                //查看事件
                if(viewForm){
                    $('td a[name=view]', row).click(function () {
                        viewItem(data);
                    });
                }
                //多选事件
                if (multiple) {
                    $(row).click(function (e) {
                        var tagName = e.target.tagName;
                        var $tr = $(this),$check = $tr.find('input[type=checkbox]');
                        if (tagName != 'A') {
                            if(tagName == 'SPAN'){
                                e.stopPropagation();
                                e.preventDefault();
                                return false;
                            }
                            if($check.is(':checked')){
                                $check.prop('checked', false).trigger('change');
                            }else{
                                $check.prop('checked', true).trigger('change');
                            }
                        }
                    });
                    $(row).find('input[type=checkbox]').val(JSON.stringify(data));
                }
                config.createdRow && config.createdRow(row, data, index);
            };
            df.createdRow = createdRow;
            //列渲染函数
            df.columnDefs = df.columnDefs ? columnDefs.concat(df.columnDefs) : columnDefs;
            //列表加载完的回调函数
            df.initComplete = initComplete;
            if (multiple) {
                var c = $('<input type="checkbox" class="cbr hidden">');
                $('#' + tid +' th').first().addClass('no-sorting').html(c);
                c.on('change', function(ev) {
                    var $chcks = $("#"+ tid +" tbody input[type='checkbox']");
                    if(c.is(':checked')) {
                        $chcks.prop('checked', true).trigger('change');
                    }else{
                        $chcks.prop('checked', false).trigger('change');
                    }
                });
                $("#" +tid ).on('draw.dt', function(){
                    cbr_replace();
                    c.removeClass('hidden');
                    if(c.is(':checked')){
                        c.trigger('click');
                    }
                    //c.trigger('change');
                });

            }
            //隐藏tbody
            $('#' + tid + ' tbody').hide();
            if(typeof pageNumber.number =='number' && (tid ==pageNumber.tid)){
                var a = pageNumber.number;
                pageNumber = {
                    tid:'',
                    number:''
                } ;
                df['displayStart'] = a;
            }
            //调用dataTable
            //$.fn.dataTable.ext.errMode = function(s,h,m){console.log(s,h,m)}
            grid = $('#' + tid).DataTable(df).on('draw', function (tb, obj) {
                //每次列表渲染结束查看最后一列是否应该显示
                var showOptionCol = true,$trs = $('tr','#'+tid);
                $trs.each(function(index,el){
                    if(index>0){
                        // var a_t =  $('td:last a',el)
                        var a_t =  $('td:last',el).html().trim();
                        if(a_t.length){
                            showOptionCol = true;
                            return false;
                        }else{
                            showOptionCol = false;
                        }
                    }
                });
                $trs.each(function(index,el){
                    var td=  $(el).children().last();
                    if(!td.hasClass('dataTables_empty')){
                        !showOptionCol?td.addClass('hidden'):td.removeClass('hidden');
                    }
                });
                $('#' + tid + ' tbody').show();
                var n = $('#'+tid+'_paginate .pagination li.active a').text();
                var jumpTmpl = '<div id="'+tid+'_page_jumper" class="jump_wrapper"> <input type="number" class="form-control" name="jumper" value="'+n+'"> <button class="btn btn-gray">GO</button></div>';
                $('#'+tid+'_page_jumper').remove();
                $('#'+tid+'_paginate').append(jumpTmpl);
                $('#'+tid+'_paginate').find('button.btn').on('click',function () {
                    var pnum = Math.abs(Number($(this).prev().val()))-1;
                    if(pnum==-1){pnum=0}
                    grid.page(pnum).draw(false);
                });
                var input = $('#'+tid+'_paginate').find('input[name=jumper]');
                self.event.bindEnterKey(input,input.next());
                cropImage(tid);
            });
            if (multiple) {
                grid.getSelectedRows = _getSelectedRows;
            }
            grid.getRecordsTotal = function () {
                return recordsTotal;
            }
            function cropImage(tid) {
                $('img[data-src]','#'+tid).lazyLoadImg();
            }
            return grid;
        };
        function _bindTreeSearch(treeObj,textKey) {
            var selector = treeObj.setting.treeId;
            var $tree = treeObj.setting.treeObj;
            var $input = $('#'+ selector +'SearchInput');
            var $btn = $('#'+ selector +'SearchButton');
            $btn.on('click',function () {
                var keyword = $input.val();
                if(keyword){
                    var searchResult = treeObj.getNodesByParamFuzzy(textKey, keyword, null);
                    var result = [];
                    $.each(searchResult,function (index,node) {
                        result = result.concat(node.getPath());
                    });
                    //去重复
                    var res = [];
                    var json = {};
                    for(var i = 0; i < result.length; i++){
                        if(!json[result[i]['id']]){
                            res.push(result[i]['id']);
                            json[result[i]['id']] = 1;
                        }
                    }
                    _renderDictTreeBySearchResult(res,treeObj);
                }else{
                    _renderDictTreeBySearchResult([],treeObj);
                }
                setTimeout(function () {
                    $('li:visible>a',$tree).first().trigger('click');
                },1);
            });
            DF.event.bindEnterKey($input,$btn);
        };
        /**
         * 根据查询结果隐藏节点
         */
        function _renderDictTreeBySearchResult(treeData,treeObj) {
            if(treeData.length) {
                var nodes = treeObj.getNodes();
                _toggleTreeNodeDisplay(nodes,treeData,treeObj);
                treeObj.expandAll(true);
            }else{
                var nodes = treeObj.getNodes();
                _toggleTreeNodeDisplay(nodes,[],treeObj);
                //treeObj.expandAll(true);
            }
        }

        /**
         * 递归显示隐藏树的节点
         * @param nodes
         * @param treeData
         * @param treeObj
         * @private
         */
        function _toggleTreeNodeDisplay(nodes,treeData,treeObj) {
            if(treeData.length){
                $.each(nodes, function (index, node) {
                    if (treeData.indexOf(node['id']) == -1) {
                        treeObj.hideNode(node);
                    }else{
                        treeObj.showNode(node);
                        if(node.isParent){
                            node.children &&　node.children.length &&　_toggleTreeNodeDisplay(node.children,treeData,treeObj);
                        }
                    }
                })
            }else{
                //默认都显示
                $.each(nodes, function (index, node) {
                    !node.isHidden && treeObj.hideNode(node);
                    if(node.isParent){
                        node.children &&　node.children.length &&　_toggleTreeNodeDisplay(node.children,treeData,treeObj);
                    }
                });
                $.each(nodes, function (index, node) {
                    node.isHidden && treeObj.showNode(node);
                    if(node.isParent){
                        node.children &&　node.children.length &&　_toggleTreeNodeDisplay(node.children,treeData,treeObj);
                    }
                });
            }
        }
        /**
         * 根据ID渲染树的查询dom
         */
        function _renderSearchField(id){
            $('#'+ id + 'SearchWrapper').remove();
            var dom = '<div class="input-group input-group-minimal" id="'+id+'SearchWrapper'+'">\
                            <input id="'+id+'SearchInput'+'" type="text"  class="form-control" onkeypress = "javascript:return window.event.keyCode!==13;">\
                            <div id="'+id+'SearchButton'+'" class="input-group-addon" >\
                                <a href="javascript:;" ><i class="linecons-search"></i></a>\
                            </div>\
                        </div>';
            $('#'+id).before(dom);
        }
        /**
         * 基于zTree的封装
         * @param config
         * @returns {*}
         */
        this.zTree = function (config) {
            var id = config.id;
            if(config.searchable===undefined || config.searchable==true){
                _renderSearchField(id);
            }
            var checked = config.checked || false;
            var treeObject = null;
            if (!id) {
                console.error('目标ID不能为空');
                return;
            }
            var callback = $.extend({}, config.callback);
            //dom操作设置
            var view = $.extend({
                dblClickExpand: true,
                expandSpeed: "fast"
            }, config.view);

            //节点设置
            var data = $.extend({
                simpleData: {
                    enable: true
                },
                key: {
                    name: 'text'
                }
            }, config.data);
            //是否有选中
            var check = {
                enable: config.checkable,
                chkboxType: config.chkboxType || {"Y": "ps", "N": "ps"}
            };
            //是否拖拽
            var edit = {
                enable: config.dragable,
                showRemoveBtn: false,
                showRenameBtn: false,
                drag: {
                    minMoveSize: 10
                }
            };
            var idKey = config['idKey'] || 'id', textKey = config['textKey'] || 'text', childrenKey = config['childrenKey'] || 'children', pidKey = config['pidKey'] || 'pId';
            var url = config.url || '';
            var treeData = config.treeData || null;
            //数据转换
            function _covertDataMap(data, idKey, textKey, childrenKey,parentNode) {
                if (!(data  instanceof Array)) {
                    data = [data];
                }
                for (var i = 0; i < data.length; i++) {
                    //id
                    if (!data[i].id) {
                        data[i]['id'] = data[i][idKey];
                    }
                    //text
                    if (!data[i].text) {
                        data[i]['text'] = data[i][textKey];
                    }
                    //支持pid与children两种模式
                    //如果没有children模式
                    if (!data[i].children) {
                        //如果有配置的children映射
                        if (data[i][childrenKey]) {
                            data[i]['children'] = data[i][childrenKey];
                            //如果没有配置children的映射 那么说明使用pid加载
                        } else {
                            data[i]['pId'] = data[i][pidKey];
                        }
                    }
                    //dictItemCode 如果为字典 且不是可以选择的层级
                    if(config.dictItemCode){
                        //data[i]['isParent'] = true;
                    }
                    if(parentNode && (parentNode.level + 1 == config.selectLevel)){
                        data[i]['isParent'] = false;
                    }
                    //如果有children模式 递归
                    if (data[i]['children']) {
                        _covertDataMap(data[i]['children'], idKey, textKey, childrenKey);
                    }
                }
                return data;
            }

            if (treeData) {
                var rootFlag = true;
                var nodes = _covertDataMap(treeData, idKey, textKey, childrenKey);
                if (config.root && rootFlag) {
                    rootFlag = false;
                    var root = $.extend({}, config.root);
                    if (nodes[0].children) {
                        root['children'] = nodes;
                        nodes = root;
                    } else {
                        nodes.push(root);
                    }
                }
                treeObject = $.fn.zTree.init(id instanceof jQuery ? id : $('#' + id), {
                    check: check,
                    edit: edit,
                    view: view,
                    data: data,
                    callback: callback
                }, nodes);
                //异步树
            } else if (url) {
                //root节点与树的初始化函数 只加载一次
                var rootFlag = true, asynFlag = true;
                var async = {
                    enable: true,//启用异步加载
                    url: url, //异步请求地址
                    type: config.type || 'get',
                    autoParam: config.autoParam || {}, //需要传递的参数,为你在ztree中定义的参数名称
                    otherParam: config.otherParam || {},
                    dataFilter: function (treeId, parentNode, responseData) {
                        if(responseData.result){
                            var nodes = _covertDataMap(responseData.result, idKey, textKey, childrenKey,parentNode);
                            if (config.root && rootFlag) {
                                rootFlag = false;
                                var root = $.extend({}, config.root);
                                if (nodes[0].children) {
                                    root['children'] = nodes;
                                    nodes = root;
                                } else {
                                    nodes.push(root);
                                }
                            }
                            return nodes
                        }
                    }
                }
                async = $.extend(async,config.async);
                //截获异步加载成功的函数
                var _onAsyncSuccess = callback.onAsyncSuccess;
                //根据 asynFlag 判断 onComplete方法只执行一次 此方法为整个tree的回调
                callback.onAsyncSuccess = function (event, treeId, treeNode, msg) {
                    asynFlag && config.onComplete && config.onComplete(treeObject);
                    asynFlag = false;
                    _onAsyncSuccess && _onAsyncSuccess(event, treeId, treeNode, msg);
                };
                treeObject = $.fn.zTree.init(id instanceof jQuery ? id : $('#' + id), {
                    check: check,
                    edit: edit,
                    async: async,
                    view: view,
                    data: data,
                    callback: callback
                });

            } else {
                console.error('缺少渲染数据或URL');
            }
            //如果存在treeObject 扩展selectNode方法 点击选中是否触发click事件
            if (treeObject) {
                var _selectNode = treeObject.selectNode;
                treeObject.selectNode = function (node, addFlag, clickFlag) {
                    _selectNode(node, addFlag);
                    clickFlag && $("#" + node.tId + "_a").trigger('click');
                }
            }
            //同步树  加载回调
            if (treeData) {
                config.onComplete && config.onComplete(treeObject);
            }
            if(config.searchable===undefined || config.searchable==true){
                _bindTreeSearch(treeObject,textKey);
            }
            return treeObject;
        }
        /**
         * 初始化帮助图标
         */
        this.initHelp = function () {
            var length = document.querySelectorAll("*[data-intro]").length;
            //如果配置了
            if (length != 0) {
                //只需要加载一次
                $('#showHelp').unbind('click').click(function () {
                    introJs().setOptions({
                        nextLabel: '下一步 &rarr;',
                        prevLabel: '&larr; 上一步',
                        skipLabel: '跳过',
                        doneLabel: "完成",
                        exitOnOverlayClick: false,
                        exitOnEsc: false,
                        'tooltipPosition': 'auto',
                        'positionPrecedence': ['left', 'right', 'bottom', 'top'],
                        showBullets: false
                    }).start();
                }).removeClass('hidden');
            } else {
                $('#showHelp').unbind('click').addClass('hidden');
            }
        }
        /**
         * 初始化系统
         */
        this.initApp = function (devm) {

            if (_FLAG) {
                //权限码
                if (window.__getSecurityRoleBtns) {
                    ad = __getSecurityRoleBtns();
                }
                //给开发模式赋值
                if (typeof devm === 'boolean') {
                    devMode = devm;
                }
                //开发模式
                if (devMode) {
                    //开发方便 快速定位页面
                    var press = false;
                    $(document).keydown(function (event) {
                        var code = event.keyCode;
                        press = true;
                        if (17 == code) {
                            $('#main-content-view').unbind('click').click(function (e) {
                                if(press){
                                    var page = DF.getCurrentPage();
                                    console.log(page);
                                    console.log(event.keyCode);
                                    $('#_LYModal_TIPS').find('.modal-title').text('当前页面');
                                    $('#_LYModal_TIPS').find('.modal-body').text(page);
                                    $('#_LYModal_TIPS').modal('show', {backdrop: 'fade'})
                                }
                            });
                        }
                    }).keyup(function () {
                        press = false;
                        $('#main-content-view').unbind('click');
                    });
                    //非开发模式
                } else {
                    $(document).unbind('keydown');
                }
                _FLAG = false;
            } else {
                console.error('初始化系统失败!');
            }
        }
        /**
         * 扩展DF的事件
         * @param eventName
         * @param $form
         * @param fn
         */
        this.on = function (eventName, $element, fn) {
            var eNname = 'formChange';
            var isChange = false;
            var element = null;
            var callback = function (el) {
                fn && setTimeout(function () {
                    fn(el);
                },0);
            }
            if (eNname == eventName) {
                //表单改变事件
                if ($element instanceof jQuery && $element.length == 1 && $element[0]['tagName'] == 'FORM') {
                    var form = $element;
                    var flag = false;
                    var check_radio = [];
                    var elements = $('input[name],select[name],textarea[name],div[name]',form);
                    elements.each(function () {
                        // form.find('div.form-group label').next().each(function () {
                        var $this = $(this);
                        var tagName = $this[0].tagName;
                        //普通文本、文本域
                        if (tagName == 'INPUT' || tagName == 'TEXTAREA') {
                            if($this.attr('type')=='radio' || $this.attr('type')=='checkbox'){
                                //兼容 checkbox radio
                                var name = $this.attr('name');
                                $('input[name=' + name + ']').off('change').on('change',function () {
                                    isChange = true;
                                    element = $this;
                                    callback($this)
                                    return true;
                                });
                            }else{
                                // $this.bind('input propertychange', function () {
                                //     isChange = true;
                                //     element = $this;
                                //     fn && fn($this);
                                //     return true;
                                // });
                                $this.on('change', function () {
                                    if($this.val()==$this.attr('data-val')){

                                    }else{
                                        isChange = true;
                                        $this.attr('data-val',$this.val());
                                        element = $this;
                                        callback($this);
                                        return true;
                                    }
                                }).attr('data-val',$this.val());
                            }

                            //单选
                        } else if (tagName == 'SELECT') {
                            $this.bind('propertychange', function () {
                                isChange = true;
                                element = $this;
                                callback($this)
                                return true;
                            });
                            //多选 、UEditor
                        } else if (tagName == 'DIV') {
                            //多选
                            var clazz = $this.attr('class').split(' ');
                            if (clazz.indexOf('select2-container') != -1) {
                                var _$this = $this.next();
                                _$this.bind('propertychange', function () {
                                    isChange = true;
                                    element = $this;
                                    callback($this)
                                    return true;
                                });
                            } else if (clazz.indexOf('uEditor') != -1) {
                                var ueid = $this.attr('id');
                                var editor = window.UE && window.UE.getEditor(ueid);
                                if (editor) {
                                    editor.addListener('contentchange', function () {
                                        isChange = true;
                                        element = $this;
                                        callback($this)
                                        return true;
                                    })
                                }
                            } else {
                                //其他
                            }
                        }
                    });
                } else {
                    console.log('入参为一个jq的Form对象');
                }
            }
            //以后可能会做处理
            function _isChange() {
                return isChange;
            }

            function _changeObj() {
                return element;
            }
            function _formValues(){
                return form.serializeObject();
            }
            return {isChange: _isChange, changeObj: _changeObj ,getFormValues:_formValues};

        }
        function _uuid(len) {
            var chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('');
            var uuid = [], i;
            var radix = chars.length;
            if (len) {
                for (i = 0; i < len; i++) {
                    uuid[i] = chars[0 | Math.random()*radix]
                }
            }
            return uuid.join('');
        }
        /**
         * 根据data数据解析{{foo}}模板字符串 基于artTemplate
         * 在页面渲染之前（append之前） 通过art tmpl 解析 模板字符串
         * @param domStr 模板字符
         * @param data   js对象
         * @private
         */
        function _resolveTmpl(domStr,data){
            var htmlSource;
            var bindTmplCache = {};
            if(domStr.trimLeft().startsWith('\<') || domStr.trimLeft().startsWith('\{')){
                htmlSource = domStr;
            }else{
                htmlSource = $('#'+domStr).html();
            }
            var $html = $(htmlSource);
            //是否显示的标签
            var ifShowTags = $html.find('[df-if]');
            if(ifShowTags.length){
                ifShowTags.each(function () {
                    var $this = $(this);
                    var ifVal = $this.attr('df-if');
                    $this.before('{{if '+ifVal+'}}');
                    $this.after('{{/if}}');
                    $this.removeAttr('df-if');
                });
            }
            //遍历出来的dom
            var $forDom = $html.find('[df-for]');
            if($forDom.length){
                var forDomParent = $forDom.parent();
                var forVal = $forDom.attr('df-for');
                //获取绑定的名称 注：必须中间一个空格
                var forValName = forVal.split('\ ')[1];
                //拼凑成arttemplate支持的格式
                $forDom.before('{{'+forVal+'}}');
                $forDom.after('{{/each}}');
                $forDom.removeAttr('df-for');
                //缓存df-for循环出来的模板字符串 当数据改变时做双向绑定
                if(forValName){
                    bindTmplCache[forValName] = forDomParent.html();
                }
                //获取循环内部的click事件标签 需要用代理做click绑定
                var portalTag = $('[df-click]',$forDom);
                if(portalTag.length){
                    forDomParent.attr('df-click',portalTag.attr('df-click'));
                    forDomParent.attr('df-click-tag',portalTag[0].tagName.toLocaleLowerCase());
                    portalTag.removeAttr('df-click');
                }
                forDomParent.empty();
            }
            domStr = $html[0].outerHTML;
            var content = data?$(DF.template(domStr,data)):$(domStr);
            return _spliceHtmlContent(content,bindTmplCache);
        }
        /**
         * 组件封装 modal 窗口形式
         */
        this.createWindow = function (param, fn) {
            var componentId,self = this,config = {
                title: '请选择',
                width: '800px',
                btns: [{
                    text: '确定 ',
                    valid: false,
                    class: 'btn btn-primary',
                    fn: function (modal) {
                        fn && fn.call($(this),self.getComponent(componentId), modal);
                    }
                }]
            };
            if (typeof param == 'object') {
                componentId = param.id,
                    config.dom = $('#'+param.id),
                    config.data = param.codes,
                    config = $.extend(config,param);
            } else if (typeof param == 'string') {
                componentId = param;
            }
            //判断组件是否已经被使用
            if($('#_LYModal_Component').hasClass('in')){
                return this.showModal(config, null, '_LYModal_Component_Reserve');
            }else{
                return this.showModal(config, null, '_LYModal_Component');
            }
        }

        this.createModal = function (param, fn) {
            var modalTemplateId = param.componentId;
            var config = {
                title: '请选择',
                width: '800px',
                data: param.codes
            };
            config = $.extend(config,param);
            var modalId = DF.create({
                id:modalTemplateId,
                codes:{
                    width:config.width,
                    title:config.title,
                }
            },$("body"));
            var modal = $('#'+modalId);
            return modal.modal('show').on('hidden.bs.modal',function () {
                modal.remove();
            });
        }
        /**
         * 创建一个组件
         * @param param
         * @param target
         * @param fn
         */
        this.create = function (param, target, fn) {
            var componentId, tar, self = this,uuid = '_'+_uuid(10),result;
            //处理参数情况
            tar = typeof target == 'string' ? $('#' + target) : target;
            if (!tar instanceof jQuery) {
                console.error('参数传递错误!');
                return;
            }
            if (typeof param == 'object' && param['id']) {
                //对象扩展 传递参数
                componentId = param.id;
                uuid = componentId+uuid;
                ////根据权限加载不同dom
                var codes = param.codes,obj={id:uuid};
                if(codes){
                    if($.isArray(codes)){
                        obj.list = codes;
                    }else{
                        obj = $.extend(codes,obj);
                    }
                }
                // var content = $(DF.template(componentId,obj));
                // result =  _spliceHtmlContent(content);
                result = _resolveTmpl(componentId,obj);
            } else if (typeof param == 'string') {
                componentId = param;
                uuid = componentId+uuid;
                // var content = $(DF.template(componentId, {id:uuid}));
                // result = _spliceHtmlContent(content);
                result = _resolveTmpl(componentId,{id:uuid});
            } else {
                console.error('参数传递错误!');
                return;
            }
            result.html && tar.append(result.html);
            result.script && tar.append(result.script);
            result.bindTmplCache && (param['bindTmplCache'] = result.bindTmplCache);
            //解析标签绑定标签
            var bindData = result.html && _bindDFEvents(result.html,eventQueue,param);
            setTimeout(function () {
                fn && fn(tar, self.getComponent(uuid),bindData);
            },0);
            return uuid;
        }
        /**
         * 分开处理 为了绑定html上的事件
         * @param content
         * @returns {{html: *, script: *}}
         * @private
         */
        function _spliceHtmlContent(content,bindTmplCache) {
            var html,script,result = {};
            if (content.length == 1) {
                html = content.first();
            } else if (content.length == 2) {
                //
            } else if (content.length == 3) {
                html = content.first();
                script = content.eq(2);
            } else {
                html = content;
            }
            html = _filterHtmlByAuthCode(html);
            result['html'] = html;
            result['script'] = script;
            if(!$.isEmptyObject(bindTmplCache)){
                result['bindTmplCache'] = bindTmplCache;
            }
            return result;
        }

        /**
         * modal中的数据根据authCode进行权限过滤
         * @param html
         * @returns html
         * @private
         */
        function _filterHtmlByAuthCode(html) {
            var $el = $('input[authCode],select[authCode],textarea[authCode],div[authCode]',html);
            $el.each(function () {
                var ac = $(this).attr('authCode');
                if(ac){
                    if(ad && !_isAuthCodePassByAuthorityData(ad,ac)){
                        $(this).remove();
                    }
                }
            });
            return html
        }
        /**
         * 对组件的标签进行解析 自动绑定事件
         * @param html
         * @param eventQueue
         * @param componentParams
         * @private
         */
        function _bindDFEvents(html,eventQueue,componentParams){
            //onchange事件
            var onChangeTags = html.find('[df-change]');
            //下拉菜单的select事件
            var onSelectTags = html.find('select[df-select]');
            //点击事件
            var onClickTags = html.find('[df-click]');
            //数据绑定df-model
            var dataBindTags = html.find('[df-model],[df-bind],[df-show]');
            //数据绑定的返回对象
            var scopeData = {};
            var dataBingValues = html.find('[df-value]');
            // 同步视图时 获取声明df-value的标记
            var valuesMap = {};
            dataBingValues.each(function () {
                var tName = $(this).attr('df-value');
                valuesMap[tName] = true;
            });
            html.find('[df-bind],[df-model]').each(function () {
                var tName = $(this).attr('df-bind')||$(this).attr('df-model');
                valuesMap[tName] = true;
            });
            if(onChangeTags.length){
                onChangeTags.each(function(){
                    var $this = $(this);
                    var tagName = this.tagName;
                    //自定义事件 DF.df-change
                    $this.bind('DF.df-change',function(e,flag){
                        var val = $this.val();
                        if(flag !== undefined){
                            val = flag;
                        }
                        var attr = $this.attr('df-change'),fn = eventQueue[attr],result;
                        if(componentParams && componentParams.watcher){
                            fn = componentParams.watcher[attr]
                        }
                        attr && fn && (result = fn.call($this,e,scopeData));
                        //获得被代理的显示隐藏标签 只有在 有result的情况下 做df-show的解析
                        if(result){
                            var onShowTags = html.find('[df-show*='+attr+']');
                            onShowTags.each(function(){
                                var tag = $(this);
                                var arr = tag.attr('df-show').split('\.');
                                if(arr.length>1){
                                    _toggleShow(tag,!!result[arr[1]]);
                                }else{
                                    _toggleShow(tag,!!result);
                                }
                            });
                        }
                    });
                    //如果 df-change的标签上有df-model 那么不做此标签做change的变化
                    if(!$this.attr('df-model')){
                        //判断标签类型做不同事件绑定
                        if(tagName == "SELECT"){
                            $this.change(function(e){
                                $this.trigger('DF.df-change');
                            });
                        }else if(tagName == "INPUT"){
                            if($this.attr('type')=='text'){
                                //日期时间标签
                                var datetype = $this.attr('datetype');
                                if(datetype=="date" || datetype=='datetime' || datetype=='daterange'){
                                    $this.change(function(e){
                                        $this.trigger('DF.df-change');
                                    });
                                }else{//普通文本
                                    $this.bind('input propertychange', function (e) {
                                        $this.trigger('DF.df-change');
                                    });
                                }
                            }else if($this.attr('type')=='checkbox'||$this.attr('type')=='radio'){
                                $this.change(function(e){
                                    $this.trigger('DF.df-change',$this.is(':checked'));
                                });
                            }
                        }else if(tagName == "DIV" && $this.hasClass('btn-group')){
                            $this.on('change','input[type=radio]',function () {
                                $this.trigger('DF.df-change',$('input:checked',$this).val());
                            })
                        }
                    }
                });
            }
            if(onSelectTags.length){
                //TODO
            }
            if(onClickTags.length){
                onClickTags.each(function(){
                    var $this = $(this);
                    var tag = $this.attr('df-click-tag');
                    //for 循环出来的click事件用代理模式绑定
                    if(tag){
                        var attr = $this.attr('df-click'),fn;
                        if(componentParams && componentParams.methods){
                            fn = componentParams.methods[attr]
                        }
                        fn && (function (callback) {
                            $this.on('click',tag,function(e){
                                callback && callback.call($(this),e,scopeData);
                            });
                        })(fn);
                    }else{
                        $this.bind('DF.df-click',function(e){
                            var attr = $this.attr('df-click'),fn = eventQueue[attr];
                            if(componentParams && componentParams.methods){
                                fn = componentParams.methods[attr]
                            }
                            attr && fn && fn.call($this,e,scopeData);
                        });
                        $this.on('click',function(e){
                            $this.trigger('DF.df-click');
                        });
                    }
                });
            }
            if(dataBindTags.length){
                    dataBindTags.each(function () {
                        var $this = $(this);
                        var modelName = $this.attr('df-model') || $this.attr('df-bind') ||$this.attr('df-show');
                        if(modelName){
                            if($this.is('input')||$this.is('textarea')){
                                if($this.attr('type')=='radio'||$this.attr('type')=='checkbox'){
                                    $this.on('change', function (e) {
                                        scopeData[modelName] = $this.prop('checked');
                                        $this.trigger('DF.df-change');
                                    });
                                }else{
                                    $this.on('input propertychange', function (e) {
                                        scopeData[modelName] = $this.val();
                                        $this.trigger('DF.df-change');
                                    });
                                }
                                defineObjectPropery(scopeData,modelName,componentParams['bindTmplCache']);
                            }else if($this.is('select')){
                                $this.on('change', function (e) {
                                    scopeData[modelName] = $this.val();
                                    $this.trigger('DF.df-change');
                                });
                                defineObjectPropery(scopeData,modelName,componentParams['bindTmplCache']);
                            }else{
                                defineObjectPropery(scopeData,modelName,componentParams['bindTmplCache']);
                            }
                        }
                    });
            }
            //控制显示元素
            function _toggleShow(element,flag){
                if(flag){
                    element.css({
                        display:'block',
                        opacity:1
                    }).find('select').removeAttr('hidden-invalid');
                    element.is('select') && element.removeAttr('hidden-invalid');
                }else{
                    element.css({
                        display:'none',
                        opacity:0
                    }).find('select').attr('hidden-invalid','true');
                    element.is('select') && element.attr('hidden-invalid','true');
                }
            }
            //对象属性变化后通知同步dom
            function notifyChange(value,bindTmplCache) {
                if(value){
                    console.log('..同步视图, model为 :',value);
                    var name = '';
                    for(var k in value){
                        name = k
                    }
                    if(valuesMap[name]===true){
                        //df-value df-bind 的取值
                        $('[df-value='+name+']',html).val2(value[name]);
                        $('[df-bind='+name+']',html).val2(value[name]);
                        //$('[df-model='+name+']',html).val2(value,bindTmplCache[name]);
                        if(bindTmplCache && bindTmplCache[name]){
                            //同步 df-for 遍历出来的标签视图
                            $('[df-model='+name+']',html).val2(value,bindTmplCache[name]);
                        }else{
                            $('[df-model='+name+']',html).val2(value[name]);
                        }
                    }else if(bindTmplCache && bindTmplCache[name]){
                        //同步 df-for 遍历出来的标签视图
                        // $('[df-model='+name+']',html).val2(value,bindTmplCache[name]);
                    }
                    //是否显示标签
                    $('[df-show='+name+']',html).each(function () {
                        _toggleShow($(this),value[name]);
                    });
                }
            }
            //通过Object.defineProperty绑定对象
            function defineObjectPropery(scopeData,modelName,bindTmplCache) {
                if(scopeData[modelName]===undefined){
                    var v = ''
                    Object.defineProperty(scopeData, modelName, {
                        get: function() {
                            console.log('df-model '+modelName+ ' access' );
                            return v;
                        },
                        set: function(value) {
                            v = value;
                            console.log('df-model '+ modelName + ' changed' );
                            var modifyModel = {};
                            modifyModel[modelName] = v;
                            notifyChange(modifyModel,bindTmplCache)
                        }
                    });
                }
            }
            if(componentParams && componentParams.codes){
                var initData = componentParams.codes;
                for(var kk in initData){
                    if(scopeData[kk]!==undefined){
                        scopeData[kk] = initData[kk];
                    }
                }
            }
            return scopeData;
        }
        /**
         * 组件调用后的回调函数
         * 返回某个组件的方法
         */
        this.callbackComponent = function (param) {
            var self = this;
            for (var k in param) {
                var flag = true;
                if (components.length) {
                    for (var i = 0; i < components.length; i++) {
                        //新的组件
                        if (components[i][k] !== undefined) {
                            flag = false;
                            //已有组件 覆盖
                            components[i][k] = $.extend(components[i][k], param[k]);
                        }
                    }
                } else {
                    param[k]['destory'] = function(){
                        _destory(k);
                    }
                    typeof param == 'object' && components.push(param);
                    break
                }
                //新的组件
                if (flag) {
                    param[k]['destory'] = function(){
                        _destory(k);
                    }
                    components.push(param)
                }
            };
            function _destory(componentId){
                $('#'+componentId).remove();
                self.deleteCmt(componentId);
            }
        }
        this.getComponent = function (componentID) {
            var result = null;
            if (componentID) {
                for (var i = 0; i < components.length; i++) {
                    if (components[i][componentID]) {
                        result = components[i][componentID];
                        break;
                    }
                }
            } else {
                result = components;
            }
            return result;
        }
        this.deleteCmt = function (componentID) {
            var arr = [];
            if (componentID) {
                for (var i = 0; i < components.length; i++) {
                    if (!components[i][componentID]) {
                        arr.push(components[i]);
                    }
                }
            }
            components = arr;
        }
        this.debug_globalVars = function () {
            //默认的17全局对象
            var j = 18;
            var golbalvars = monitor && monitor.detect() || [];
            var arr = [];
            if (golbalvars.length > j) {
                for (var i = j; i < golbalvars.length; i++) {
                    arr.push(golbalvars[i])
                }
            }
            if (arr.length) {
                console.warn('暴露为全局的对象如下：' + arr + ',共' + arr.length + '个');
            }
        }
        //页脚
        this.initFooter = function () {
            if ($('.main-content').height() - $(window).height() < 0) {
                var mt = $(window).height() - $('#main-content-view').height() - $('.navbar.horizontal-menu').height() - 48;
                public_vars.$mainFooter.css('marginTop', mt);
            } else {
                public_vars.$mainFooter.css('marginTop', 0);
            }
        },
            /**
             * @param message title
             * 覆盖原生弹出框
             */
            this.alert = window.alert = function (message, title ,fn) {
                //内容,标题
                var content, t;
                content = message || '';
                t = title || '提示'
                var config = {
                    dom: $('<div><span>' + content + '</span></div>'),
                    title: t,
                    width: '400px',
                    backdrop:'static',
                    btns: [{
                        text: '确定 ',
                        valid: false,
                        class: 'btn btn-success',
                        fn: function (modal) {
                            modal.modal('hide');
                            fn && fn();
                        }
                    }]
                }
                DF.showModal(config, function (modal) {}, '_LYModal_alert');
            }
        this.confirm = function (message, title,param , callback) {
            //内容,标题
            var content, t, cb ,data;
            var deffer = $.Deferred();
            content = message || '';
            if (typeof title == 'function') {
                cb = title;
                t = '提示';
            } else if(typeof  title == 'object'){
                t = '提示';
                data = title;
            }else if(typeof  param == 'function'){
                cb = param;
                t = title || '提示';
                data = param;
            }else{
                cb = callback;
                t = title || '提示';
                data = param;
            }
            var config = {
                dom: $('<div><span>' + content + '</span></div>'),
                title: t,
                width: '400px',
                backdrop:'static',
                btns: [{
                    text: '确定 ',
                    valid: false,
                    class: 'btn btn-ly',
                    param : data,
                    fn: function (modal,datas) {
                        modal.modal('hide');
                        cb && cb(modal,datas);
                        deffer.resolve(modal,datas);
                    }
                }, {
                    text: '取消 ',
                    valid: false,
                    class: 'btn btn-white',
                    param : data,
                    fn: function (modal,datas) {
                        modal.modal('hide');
                        deffer.reject(modal,datas);
                    }
                }]
            }
            DF.showModal(config, function (modal) {}, '_LYModal_alert');
            return deffer.promise();
        };
        this.showLockScreen = function () {
            //$horizontalNavbar $sidebarMenu $pageContainer
            public_vars.$body.addClass('lockscreen-page');
            public_vars.$sidebarMenu.fadeOut('normal', function () {
                public_vars.$horizontalNavbar.fadeOut('normal', function () {
                    public_vars.$pageContainer.fadeOut('normal', function () {
                        setTimeout(function () {
                            var ls = $(DF.template('df_component_autoLockScreen', {userName: __GlobleVars.userInfo.userName}));
                            public_vars.$body.append(ls);
                            ls.slideDown(1000, function () {
                                $('#ls_login').click(function () {
                                    var userName = __GlobleVars.userInfo.userAccount;
                                    var password = $('#ls_pwd').val();
                                    if (password) {
                                        var url = "service/securityUserT/login/" + userName + "/" + password;
                                        DF.ajax({url: url}).done(function (data) {
                                            if (data.errorCode == 1) {
                                                DF.hideLockScreen();
                                                setTimeout(function () {
                                                    DF.toast.success('登录成功');
                                                    _WebscoketManager.initWebscoket()
                                                }, 2000);
                                            }
                                        })
                                    } else {
                                        DF.toast.error('请输入密码');
                                    }
                                });
                                $(document).unbind('keyup').keyup(function(e){
                                    var curKey = e.which;
                                    if(curKey==13 && $("#ls_pwd").is(":focus")){
                                        $('#ls_login').trigger('click');
                                    }
                                });
                            });
                        }, 1)
                    });
                });
            });
        }
        this.hideLockScreen = function () {
            var ls = $('.locakScreen_wrapper');
            ls.slideUp(1500, function () {
                public_vars.$sidebarMenu.fadeIn('normal', function () {
                    public_vars.$horizontalNavbar.fadeIn('normal', function () {
                        public_vars.$pageContainer.fadeIn('normal', function () {
                            ls.remove();
                            public_vars.$body.removeClass('lockscreen-page');
                        });
                    });
                });
            });
        }


        //template 模板 基于artTemplate
        /**
         * 根据数据生成html模板
         * 基于artTemplate
         * @param source
         * @param data
         */
        this.template = function(source,data){
            for(var k in data){
                if(typeof data[k] ==='boolean'){
                    data[k] = Number(data[k]);
                }
            }
            //如果以‘<’开头那么说明是html源码 否则认为以source为ID
            var html='', s=source||'',item=data||{};
            if(!source){console.error('请传入html模板');return}
            source = $.trim(source);
            if(source.startsWith('\<') || source.startsWith('\{')){
                html = template.compile(s)(item);
            }else{
                html = template(s,item);
            }
            return html;
        }
        // this.getAyncSetF = function(){
        //     return ayncSetF;
        // }
        //绑定事件缓存
        var eventQueue = {};
        this.event = function(){
            return {
                addEvent:function(name,fn){
                    eventQueue[name] = fn;
                },
                getEventQueue:function(){
                    return eventQueue;
                },
                beforePageSwitch:function () {
                    return true;
                },
                afterPageSwitch:function () {
                    console.log('after change');
                },
                bindEnterKey:function (input,btn) {
                    if(input && btn){
                        var $input = input instanceof jQuery?input : $('#'+input);
                        var $btn = btn instanceof jQuery?btn : $('#'+btn);
                        $input.off('keypress').on('keypress',function (event) {
                            if(event.keyCode==13 && !$btn.hasClass('disabled') && !$btn.prop('disabled')){
                                $btn.trigger('click');
                            }
                        });
                    }
                }
            }
        }();
        this.initWebscoket = function(){
            window._WebscoketManager && _WebscoketManager.initWebscoket();
        }
        this.open = function (url,param) {
            var me = this,projectName = me.getCtxPath();
            var defaults = {
                url:'',
                http:'http'
            },re_url;
            if(typeof url == 'string'){
                defaults.url = url;
            }else if(typeof url =='object'){
                defaults = $.extend(defaults,url);
            }else{
                console.error('参数错误!');
            }
            re_url = defaults.url + (param?'?'+me.analyticalUrlParams(param):'') ;
            if(re_url.indexOf('http')==-1&&re_url.indexOf('https')==-1){
                re_url = projectName + re_url;
            }
            if(defaults.http){
                re_url = re_url.replace(location.protocol,defaults.http+':');
            }
            if($.isWindow(defaults.win)){
                defaults.win.location.href = re_url;
            }else{
                var a = $('<a>',{
                    href:re_url,
                    target:'_blank'
                }).appendTo('body');
                a[0].click();
                a.remove();
            }
        }
        //F first L last M 中间 O仅一条
        this.Page = function (option) {
            var me = this,store ={},status = 'M',defaults = {
                url:'',
                style:'pagination',
                extraParam:{
                    currentpage:1,
                    pagesize:10,
                }
            }
            var extraParam = defaults.extraParam;
            defaults = $.extend(defaults,option);
            extraParam = $.extend(extraParam,option.extraParam);
            defaults.extraParam = extraParam;
            function PageTools(option) {
                if(!option.url){
                    console.error('分页至少需要一个url')
                    return
                }
                return {
                    destory:function () {
                        console.log('destory')
                    },
                    init :function (url,param,callback,flag) {
                        if(arguments.length ==1 && typeof arguments[0] =='function'){
                            callback = url ,url = '',param = '';
                        }
                        var _param = $.extend(option.extraParam,param);
                        //每次调用init都会从第一条开始查询
                        if(!flag){
                            _param.currentpage = 1;
                        }
                        var r_url = concatUrl(url || option.url, _param);
                        return me.ajax({
                            url:r_url,
                            type: 'GET',
                            success:function (data,xhrstatus) {
                                if(data.errorCode == '1'){
                                    store.data = data.result;
                                    store.recordstotal = data.result.recordsTotal;
                                    store.currentpage = _param.currentpage;
                                    store.pagesize = _param.pagesize;
                                    store.pagetotal = Math.ceil(store.recordstotal/store.pagesize);

                                    if(store.data.recordsTotal<=store.pagesize){
                                        setTimeout(function () {
                                            status = 'O';
                                            option.turnToLast && option.turnToLast();
                                            option.turnToFirst && option.turnToFirst();
                                        },0)
                                    }
                                    if(store.currentpage ==1){
                                        status = 'F';
                                        setTimeout(function () {
                                            option.turnToFirst && option.turnToFirst();
                                        },0)
                                    }else if(Number(store.currentpage)*Number(store.pagesize) - Number(store.recordstotal)>=0){
                                        status = 'L';
                                        setTimeout(function () {
                                            option.turnToLast && option.turnToLast();
                                        },0)
                                    }else {
                                        status = 'M';
                                    }
                                }
                                callback && callback(data,xhrstatus);
                            }
                        });
                    },
                    nextPage:function (url,callback) {
                        if(status == 'L' || status =='O'){
                            return {'done':function () {}};
                        }
                        if(arguments.length==1 && typeof url == 'function'){
                            callback = url,url = '';
                        }
                        if(typeof callback !=='function'){
                            callback = '';
                        }
                        return this.init(url,{
                            currentpage:store.currentpage + 1,
                            pagesize:store.pagesize,
                        },callback,true);
                    },
                    prevPage:function (url,callback) {
                        if(status == 'F' || status =='O'){
                            return {'done':function () {}};
                        }
                        if(arguments.length==1 && typeof url == 'function'){
                            callback = url,url = '';
                        }
                        if(typeof callback !=='function'){
                            callback = '';
                        }
                        return this.init(url,{
                            currentpage:store.currentpage -1,
                            pagesize:store.pagesize,
                        },callback,true);
                    },
                    getPage:function () {
                        store.status = status;
                        return store;
                        //获取当前状态 当前第几页 每页多少条 一共多少条
                    }
                }
            }
            function concatUrl(url,param) {
                if(url){
                    var paramObj = me.analyticalUrlParams(url);
                    //配置的参数覆盖url问号传递的参数
                    paramObj = $.extend(paramObj,param);
                    var paramStr = me.analyticalUrlParams(paramObj);
                    url = url.substring(0,url.indexOf('?')==-1?url.length:url.indexOf('?'));
                    return url + '?' + paramStr;
                }
            }
            return new PageTools(defaults);
        }
        this.treeGrid = function (config) {
            if(!window.mini){
                console.error('缺少组js件');
                return
            }
            // var _mini = window.mini,
            var id,treegrid;
            // window.mini = null;
            window.mini.parse();
            if(config){
                if(typeof config == 'string'){
                    id = config;
                }else if(typeof config == 'object'){
                    id = config.id;
                }
                treegrid = window.mini.get(id);
                var defaults = {
                    id:'',
                    param:{}
                };
                if(treegrid){
                    defaults = $.extend(defaults,config)
                    treegrid.load(defaults.param);
                    //获取所有按钮
                    var btnsall = defaults.rowbtns.concat(defaults.editingRowbtns);
                    //渲染列表行内按钮
                    treegrid.on('drawcell',function (e) {
                        if(e.column.name == 'action'){
                            console.log(e);
                            var record = e.record;
                            var s = _generateBtnHtml(defaults.rowbtns);
                            if (treegrid.isEditingRow(record)) {
                                s = _generateBtnHtml(defaults.editingRowbtns);
                            }
                            e.cellHtml = s;
                        }
                    });
                    //绑定点击事件
                    treegrid.on('rowmousedown',function (e) {
                        var $tar = $(e.htmlEvent.target);
                        var record = e.record;
                        if($tar.is('a')){
                            (function () {
                                var a_name = $tar.attr('name');
                                for(var i=0;i<btnsall.length;i++){
                                    if(a_name ==btnsall[i]['name']){
                                        btnsall[i]['fn'].call($tar,record);
                                        break;
                                    }
                                }
                            })(record);
                        }
                    });
                    return treegrid;
                }else{
                    console.log('获取不到ID为此节点的DOM对象');
                }
            }else{
                console.error('参数错误');
            }
            function _generateBtnHtml(btns) {
                var a_wrap = $('<div>');
                for(var i=0;i<btns.length;i++){
                    var item = btns[i];
                    var a_tag = $('<a>',{
                        name: item.name,
                        href:'javascritp:;'
                    });
                    a_tag.addClass('btn btn-sm btn-icon icon-left btn-default').addClass(item.btnClass).text(item.text);
                    a_wrap.append(a_tag);
                }
                return a_wrap.html();
            }
        }
        /**
         * 通过权限码判断是否通过数据权限资源
         * @param ad 数据权限资源
         * @param authCode 权限码
         * @private
         */
        function _isAuthCodePassByAuthorityData(ad,authCode) {
            var flag = false;
            if(ad && authCode){
                if(!$.isArray(authCode)){
                    authCode = authCode.split('\,');
                }
                for(var i=0;i<authCode.length;i++){
                    if(ad.indexOf(authCode[i]) != -1){
                        flag = true;
                        break;
                    }
                }
            }
            return flag;
        }
        //页面切换的时候需要遮挡当前页
        function _mainViewIsOverlay(flag) {
            //页面有modal窗口的情况
            if($('body').hasClass('modal-open')){
                //判断当前页面是否有modal窗口 且不是alert与confirm窗口
                if($('.modal.in').length && $('.modal.in').last().attr('id')!=='_LYModal_alert'){
                    var modal = $('.modal.in.last').last();
                    if(!modal.length){
                        modal = $('.modal.in').last();
                    }
                    var modalBody = $('.modal-dialog',modal);
                    $('.main-content-view-loading',modalBody).remove();
                    modalBody.append('<div class="main-content-view-loading">\
                       <div class="loader-2"></div>\
                        </div>');
                }
            }
            if(flag){
                $('.main-content-view-loading').removeClass('loaded');
            }else{
                $('.main-content-view-loading').addClass('loaded');
            }
        }
        //对外暴漏true为显示 false为隐藏
        this.toggleMainViewMask = function (flag) {
            _mainViewIsOverlay(flag);
        }
    };
    window.DF = new DonkishFramework($);
    //jq 验证中文信息
    if($.validator){
        $.extend($.validator.messages, {
            required: "必填字段",
            remote: "请修正该字段",
            email: "请输入正确格式的电子邮件",
            url: "请输入以“http://”或“https://”为开头的网址",
            date: "请输入合法的日期",
            dateISO: "请输入合法的日期 (ISO).",
            number: "请输入合法的数字",
            digits: "只能输入整数",
            creditcard: "请输入合法的信用卡号",
            equalTo: "请再次输入相同的值",
            accept: "请输入拥有合法后缀名的字符串",
            maxlength: $.validator.format("请输入一个长度最多是 {0} 的字符串"),
            minlength: $.validator.format("请输入一个长度最少是 {0} 的字符串"),
            rangelength: $.validator.format("请输入一个长度介于 {0} 和 {1} 之间的字符串"),
            range: $.validator.format("请输入一个介于 {0} 和 {1} 之间的值"),
            max: $.validator.format("请输入一个最大为 {0} 的值"),
            min: $.validator.format("请输入一个最小为 {0} 的值")
        });
        $.validator.addMethod('cardNo',function(value,element){
            // 身份证号码为15位或者18位，15位时全为数字，18位前17位为数字，最后一位是校验位，可能为数字或字符X
            var reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
            return value?reg.test(value):true;
        },'身份证输入不合法');
        $.validator.addMethod('phoneNo',function(value,element){
            // 身份证号码为15位或者18位，15位时全为数字，18位前17位为数字，最后一位是校验位，可能为数字或字符X
            var reg = /^(13[0-9]|14[0-9]|15[0-9]|17[0-9]|18[0-9])\d{8}$/i;
            return value?reg.test(value):true;
        },'手机号码输入不合法');
        $.validator.addMethod('telNo',function(value,element){
            // 区号：前面一个0，后面跟2-3位数字 电话号码：7-8位数字： \d{7,8} 分机号：一般都是3位数字： \d{3,}
            var reg = /^((0\d{2,3})-)(\d{7,8})(-(\d{3,}))?$/;
            return value?reg.test(value):true;
        },'固定电话输入不合法');
    }

    /**
     * 截获$.fn.val 赋值之后 触发 propertychange
     * 取值不截获 增加去除左右空格
     * @type {Function|*}
     * @private
     */
    $.fn._val = $.fn.val;
    $.fn.val = function (value) {
        var $this = $(this);
        if (value === undefined) {
            return $this._val();
        } else {
            $this._val(value);
            //$(this).trigger('propertychange');
            $this.trigger('DF.propertychange');
            //$this.trigger('DF.df-change');
            return $this;
        }
    }
    //根据不同标签设置值
    $.fn.val2 = function (value,templateSource) {
        $(this).length && $(this).each(function () {
            var $this = $(this);
            if(!templateSource){
                if (value === undefined) {
                    return $this.val();
                } else {
                    if($this.is('input')||$this.is('select')||$this.is('textarea')){
                        var type = $this.attr('type');
                        if(type=='radio'||type=='checkbox'){
                            $this.attr('checked','checked');
                        }else{
                            $this.val(value);
                        }
                    }else{
                        var children = $this.children();
                        if(children.size()){
                            var str = value + $this.html();
                            $this.html(str);
                        }else{
                            $this.text(value);
                        }
                    }
                }
            }else {
                $this.html(DF.template(templateSource,value));
            }
        });

    }
    /**
     * 扩展jq重置方法
     */
    $.fn.reset = function () {
        var form = $(this);
        //清空异步组件赋值缓存区
        $('input[name],select[name],textarea[name]',form).each(function () {
            // form.find('div.form-group label').next().each(function () {
            var $this = $(this);
            var tagname = $this[0].tagName;
            if (tagname == 'INPUT' || tagname == 'TEXTAREA') {
                if($this.attr('type')=='radio'||$this.attr('type')=='checkbox'){
                    $this.removeProp('checked');
                }else{
                    $this.val('').attr('data-val','');
                }
                return true;
            } else if (tagname == 'SELECT') {
                $this.val('').trigger('change');
                return true;
            } else if (tagname == 'DIV') {
                $this.val([]).trigger('change');
                return true;
            }
        });
        return form;
    };
    //form转换成json对象
    $.fn.serializeObject = function () {
        var o = {};
        var a = this.serializeArray();
        $.each(a, function () {
            if (o[this.name]) {
                if (!o[this.name].push) {
                    o[this.name] = [o[this.name]];
                }
                o[this.name].push(this.value || '');
            } else {
                var s = this.value;
                typeof s === 'string' ? (s && (s = s.replace && s.replace(/(^\s*)|(\s*$)/g, ""))) : s;
                o[this.name] = s || '';
            }
        });
        return o;
    };
    /**
     *按钮的点击加载样式
     */
    $.fn.showLoading = function () {
        var $btn = $(this);
        var icon = 'fa fa-spin fa-circle-o-notch';
        var itag = $btn.find('i');
        var o_class = '';
        if (itag.length) {
            o_class = itag.attr('class');
            $btn.find('i').remove();
        }
        $btn.append('<i class="' + icon + '">');
        $btn.addClass('disabled').attr({
            disabled: 'disabled'
        });
        o_class && $btn.attr('o_class', o_class)
    }
    $.fn.hideLoading = function () {
        var $btn = $(this);
        var icon = 'fa fa-spin fa-circle-o-notch';
        $btn.find('i').removeClass(icon);
        if ($btn.attr('o_class')) {
            $btn.find('i').addClass($btn.attr('o_class'));
        }
        $btn.removeClass('disabled').removeAttr('disabled');
    }
    //扩展jq 懒加载图片 根据尺寸自动切图
    $.fn.lazyLoadImg = function(config){
        $(this).each(function (index,element) {
            var $this = $(element);
            if($this.attr('data-src')){
                var old_src = $this.attr("data-src");
                // if(old_src==settings.placeholder){return}
                var width = $this.width(),
                    height = $this.height(),
                    filename = '.'+/[^\.]+$/.exec(old_src)[0],
                    crop_src = old_src.replace(filename, '_'+width+'x'+height+filename),
                    img = new Image();
                img.src = crop_src;
                if(img.complete){
                    $this.attr('src',crop_src);
                    img = null;
                    setImgStatus($this);
                    return;
                };
                img.onload  = function(){
                    $this.attr('src',crop_src);
                    setImgStatus($this);
                    img = null;
                };
                img.onerror = function(){
                    var im = new Image();
                    im.src = old_src;
                    if(im.complete){
                        $this.attr('src',old_src);
                        im = null;
                        return;
                    };
                    im.onload = function () {
                        $this.attr('src',old_src);
                        im = null;
                    }
                    im.onerror  = function(){
                        setImgStatus($this);
                        setImgDefault($this);
                    };
                }
            }
        });
        function setImgStatus($img) {
            $img.removeAttr('data-src');
            $img.attr('data-imgload','true');
        }
        function setImgDefault($img) {
            if(config && config.placeholder){
                $img.attr(src,config.placeholder);
            }
        }
    }
    //日期原型扩展
    Date.prototype.Format = function (fmt) {
        var o = {
            "M+": this.getMonth() + 1,                 //月份
            "d+": this.getDate(),                    //日
            "h+": this.getHours(),                   //小时
            "m+": this.getMinutes(),                 //分
            "s+": this.getSeconds(),                 //秒
            "q+": Math.floor((this.getMonth() + 3) / 3), //季度
            "S": this.getMilliseconds()             //毫秒
        };
        if (/(y+)/.test(fmt))
            fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
        for (var k in o)
            if (new RegExp("(" + k + ")").test(fmt))
                fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        return fmt;
    }
    if(String.prototype.startsWith == undefined){
        String.prototype.startsWith = function(str){
            if(str==null||str==""||this.length==0||str.length>this.length)
                return false;
            if(this.substr(0,str.length)==str)
                return true;
            else
                return false;
            return true;
        }
    }
    String.prototype.replaceAll = function(s1,s2) {
        return this.replace(new RegExp(s1,"gm"),s2);
    }
})(jQuery, window ,template,require);