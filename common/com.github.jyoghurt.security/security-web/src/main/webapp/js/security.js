/**
 * Created by Administrator on 2015/11/20.
 */
/* =========================================================
 *security.js
 *菜单、登出、用户信息等JS
 *
 * ========================================================= */
;
(function ($, window, DF, undefined) {

    //全局共享 用户信息/获取系统时间
    __GlobleVars = {};
    //获取系统时间
    __GlobleVars.getSysDateTime = function () {
        var systemDateTime = null;

        function sfn(data, status) {
            systemDateTime = data.result;
        }

        DF.ajax({
            url: 'service/securityUserT/getSysDateTime',
            type: 'GET',
            showLoadingBar: false,
            async: false,
            success: sfn
        });
        return systemDateTime;
    }


    //获取菜单信息
    DF.ajax({
        url: "service/securityMenuT/getMenu",
        dataType: 'json',
        success: function (resp) {
            if (resp.result) {
                var secMenus = resp.result.gTask;
                secMenus.length && todoMenuGenerater(secMenus);
                var menus = resp.result.userMenu;
                var html = bootStrapMenuGenerater(menus, '-1', '0', '1');
                html = $(html).html();
                $("#main-menu").hide().append(html);
                //注册 左侧菜单点击事件
                $('.back-side').on('click', 'li a', function (event) {
                    //console.log('点击目标:'+event.target);
                    var hasUl = $(this).parent().children('ul');
                    var $this = $(this);
                    var url = $this.attr('href');
                    if (hasUl.length > 0) {
                        $('.back-side ul.secondMenu').remove();
                        var li = $(this).parent();
                        var top = li.offset()['top'];
                        var ul = hasUl.clone();
                        ul.addClass('secondMenu');
                        ul.css({
                            top:top+'px'
                        });
                        if(top + hasUl.height()-$('body').height() >+0){
                            var h = top -  hasUl.height() + 42;
                            ul.css({
                                'top':h+'px'
                            }).addClass('slideTop')
                        }
                        $('.back-side').append(ul.show());
                        $('#main-menu').find('>li.active').not($(this).parent()).find('>a').next().hide();
                        $('#main-menu').find('>li.active').not($(this).parent()).removeClass('active');
                        $(this).parent().addClass('active');
                        if(url == document.location.hash){
                            DF.reloadPage();
                        }
                    }else{
                        if(url.indexOf('\/')!==-1){
                            //正常跳转清空 查询条件
                            DF.clearSeniorSearchParam();
                            if(url == document.location.hash){
                                DF.reloadPage();
                            }
                        }
                        setTimeout(function () {
                            $('.back-side ul.secondMenu').remove();
                        },100)
                    }
                    if ($(this).attr('href') == 'javascript:;') {
                        return false;
                    }
                });
                $('#top_bar').on('click','li.first-Menu a',function (event) {
                    var $this = $(this);
                    var url = $this.attr('href');
                    if(url.indexOf('\/')!==-1){
                        //正常跳转清空 查询条件
                        DF.clearSeniorSearchParam();
                        if(url == document.location.hash){
                            DF.reloadPage();
                        }
                    }
                });
                $("#main-menu").show();


                var deferArray = [];
                //权限按钮
                deferArray.push(DF.ajax({
                    url: 'securityRoleButtonR/getButtonByUserId',
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function (data) {
                        var ad = ['help', 'user'];
                        if (data.result) {
                            ad = ad.concat(data.result);
                        }
                        console.log('数据返回');
                        window.__getSecurityRoleBtns = function () {
                            return ad;
                        }
                    }
                }));
                //版本按钮
                deferArray.push(DF.ajax({
                    url: 'switch/list',
                    success: function (data) {
                        var vcodes = data.result;
                        console.log('版本数据返回');
                        window.__getVersionCodes = function () {
                            return vcodes;
                        }
                    }
                }));
                $.when.apply($,deferArray).done(function () {
                    $.getScript('js/security-plugin.js');
                });
            }
        }
    });
    //获取登录用户信息
    DF.ajax({
        url: "service/securityUserT/userInfo",
        dataType: 'json',
        success: function (resp) {
            __GlobleVars.userInfo = resp.result;
            var username = resp.result ? resp.result.userName : "";
            var useraccount = resp.result ? resp.result.userAccount : "";
            $("#userName").text(username).attr('useraccount',useraccount);
            /*注册scoket超时落锁*/
            window._vertx && window._vertx.registerHandler("SessionTime", function (message) {
                DF.showLockScreen()
            });
        }
    });
//定义生成bootstrap菜单的数据结构
    /**
     *
     * @param obj json对象
     * @param parentId 父节点ID
     * @param isLeaf 是否为叶子节点 0-否，1-是
     * @param isHead 是否是头 0-否，1-是
     * @returns {string}
     */
    function bootStrapMenuGenerater(obj, parentId, isLeaf, isHead) {
        var html = "";
        if (isLeaf == '0') {
            html += "<ul>";
        }
        //循环查找当前id的 子节点
        for (var i = 0, j = obj.length; i < j; i++) {
            //遍历所有json对象，找到父节点id等于当前节点id的对象，并生成html代码
            var item = obj[i];
            if (item.parentId == parentId) {
                //如果是叶子节点要增加访问链接
                if (item.isLeaf == '0') {
                    html += "<li class='has-sub'>";
                    html += "<a href='javascript:;' >";
                    if (item.icon && item.icon.indexOf('\/') !== -1) {
                        html += "<img src='" + item.icon + "'>";
                    } else {
                        html += "<i class='" + item.icon + "'></i>";
                    }
                } else {
                    html += "<li>";
                    html += "<a  href='#" + item.menuUrl + "'menuType='" + item.menuType + "'>";
                    if (item.icon && item.icon.indexOf('\/') !== -1) {
                        html += "<img src='" + item.icon + "'>";
                    } else {
                        html += "<i class='" + item.icon + "'></i>";
                    }
                }
                html += "<span class='title' name='" + item.menuName + "'>" + ' ' + item.menuName + "</span>";
                html += "</a>";
                html += bootStrapMenuGenerater(obj, item.menuId, item.isLeaf, '0');
                html += "</li>";
            }
        }
        if (isLeaf == '0') {
            html += "</ul>";
        }
        return html;
    }

    /**
     * 顶部待办菜单的模板
     * @param menus
     */
    function todoMenuGenerater(menus) {
        var strVar = "";
        strVar += "    <li class=\"dropdown xs-left\" id=\"todo-notifications\">\n";
        strVar += "        <a href=\"#\" data-toggle=\"dropdown\" class=\"notification-icon notification-icon-messages\">\n";
        strVar += "            <i class=\"fa-bell-o\" style=\"font-size: 17px;\"><\/i>\n";
        strVar += "            <span class=\"badge badge-info total_todo\"><\/span>\n";
        strVar += "        <\/a>\n";
        strVar += "        <ul class=\"dropdown-menu notifications\" style=\"width: 270px;\">\n";
        strVar += "            <li class=\"top\">\n";
        strVar += "                <p class=\"\">\n";
        strVar += "                    <!--<a href=\"#\" class=\"pull-right\">设置为已读<\/a>-->\n";
        strVar += "                    您共有 <strong class=\"total_todo\">0<\/strong> 条代办信息\n";
        strVar += "                <\/p>\n";
        strVar += "            <\/li>\n";
        strVar += "            <li>\n";
        strVar += "                <ul class=\"dropdown-menu-list list-unstyled ps-scrollbar ps-container ps-active-y\" style=\"padding-bottom: 15px;\">\n";
        strVar += "                    {{each items as item}}\n";
        strVar += "                    <li class=\"notification-info\">\n";
        strVar += "                        <a href=\"#{{item.gTaskUrl}}\" type=\"{{item.gTaskType}}\">\n";
        strVar += "                            <i class=\"fa-file-text-o\"><\/i>\n";
        strVar += "									<span class=\"line\">\n";
        strVar += "                                        {{item.menuName}}\n";
        strVar += "									<\/span>\n";
        strVar += "                            <span class=\"badge badge-danger inline-display\"><\/span>\n";
        strVar += "                        <\/a>\n";
        strVar += "                    <\/li>\n";
        strVar += "                    {{/each}}\n";
        strVar += "                <\/ul>\n";
        strVar += "            <\/li>\n";
        strVar += "        <\/ul>\n";
        strVar += "    <\/li>\n";
        var html = DF.template(strVar,{items:menus});
        $('#showHelp').after(html);
        //console.log(html);
    }

})(jQuery, window, DF);

