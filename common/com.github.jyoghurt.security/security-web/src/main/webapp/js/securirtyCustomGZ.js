;
(function ($, window, undefined) {
    treeObjs = null;
    var humimg = 'linecons-hum';
    var compimg = 'linecons-comp';
    var deptimg = 'linecons-dept';
    var rootimg = 'linecons-root';
    var resourceList = new Array();//保存资源的容器

    var unitList = new Array();//用户管理机构的集合
    //控制资源列表显示还是隐藏
    function resourceTableShowHidden(displayFlag){
        if('show'==displayFlag){
            $('#showResource').css("display", "block");
        }else{
            $('#showResource').css("display", "none");
        }
    }

    //资源对象
    function resourceObj() {
        this.userResourceRId = "";
        this.userId = "";
        this.belongModel = "";
        this.resourceType = "";
        this.resourceId = "";
        this.resourceName = "";
    }
    function unitResourceObj() {
        this.userUnitId = "";
        this.userIdR = "";
        this.unitIdR = "";
        this.userNameR = "";
        this.unitNameR = "";
        this.parentUnitId = "";
        this.parentUnitName = "";
    }
    function unitResourceDataConvert(checkNodes){
        if(checkNodes){
            var data = JSON.parse(checkNodes);
            for(var da in data){
                var obj = new resourceObj();
                obj.resourceId = '';
                obj.belongModel = 'securityUser';
                obj.resourceType = 'unit';
                obj.resourceId = data[da].nodeId;
                obj.resourceName = data[da].nodeName?data[da].nodeName:"";
                resourceList.push(obj);
            }
        }
    }
    function userResourceDataConvert(checkNodes){
        resourceList.length = 0;
        if(checkNodes){
            var data = JSON.parse(checkNodes);
            for(var da in data){
                var obj = new resourceObj();
                obj.resourceId = data[da].dictValueId;
                obj.belongModel = data[da].module;
                obj.resourceType = data[da].dictItemCode;
                obj.resourceId = data[da].dictValueCode;
                obj.resourceName = data[da].dictValueName;
                resourceList.push(obj);
            }
        }
    }


    function userRoleDataConvert(selectedRoles){
        if(selectedRoles){
            var data = JSON.parse(selectedRoles);
            $(data).each(function(index,element){
                resourceList.push(element);
            })
        }
    }


    function userManageUnitSetData(data){
        var userId = $("#userId").val();
        unitList.length = 0;//初始化
        var names = new Array();
        var ids = new Array();
        for(var da in data){
            var obj = new unitResourceObj();
            obj.unitIdR = data[da].nodeId;
            obj.unitNameR = data[da].nodeName?data[da].nodeName:"";
            obj.parentUnitId = data[da].pNodeId?data[da].pNodeId:"";
            obj.userIdR = $("#userId").val();
            obj.parentUnitName = "";
            obj.userNameR = "";
            names.push(data[da].nodeName);
            ids.push(data[da].nodeId);
            unitList.push(obj);
        }
        $("#contentUnit").val(names.toString());
        $("#contentUnit").attr("contentResourceId", ids.toString());
    }

    var guidFrameWork = function () {
        var treeViewObj = null, accountFormObj = null, userInfo = null, isChange = false, selectData = new Object(), grid;
        this.isModify = '0',this.$checkableTree = null,this.checkedRole = null,this.resourceList = null;
        this.formIsChange=false;
        //删除人员 走后台
        this.delPerson = function (selectedId) {
            DF.confirm('确认删除吗？').done(function () {
                //删除当前选中节点
                //清空区域
                $("#showCompany").children().remove();
                resourceTableShowHidden('hide');

                if(selectedId){
                    var url = 'service/securityUserT/' + selectedId;
                    var sfn = function (data, status) {
                        var currentId = treeObjs.getNodeByParam("unitId",selectedId,null)&&treeObjs.getNodeByParam("unitId",selectedId,null).parentId
                        var nodes = treeObjs.getSelectedNodes();
                        for (var i = 0, l = nodes.length; i < l; i++) {
                            treeObjs.removeNode(nodes[i]);
                        }
                        currentId = currentId=='-1'?null:currentId;

                        treeObjs.selectNode(treeObjs.getNodeByParam("unitId",currentId,null), false, true)
                        DF.toast.success(data.message);
                    };
                    DF.ajax({
                        type: "delete",
                        url: url,
                        success: sfn
                    });
                }else{
                    $('.curSelectedNode').trigger('click');
                }

            })
        }
        //公司页面初始化
        this.comPanyInit = function () {
            //如果点击的是企业通讯录
            if ($("#flag").val() != '1' && KendoTreeView.getSelectedNodeId() == '-1') {
                //隐藏 保存、删除按钮
                $("#saveBtn").css("display", "none");
                $("#delBtn").css("display", "none");
                //隐藏输入框
                $("#unitForm input").each(function () {
                    if ($(this).attr("id") != 'appendNodeToSelected' && $(this).attr("id") != 'appendHumNodeToSelected' && $(this).attr("id") != 'saveBtn' && $(this).attr("id") != 'delBtn') {
                        $(this).parent().parent().parent().css("display", "none");
                    }
                });
                $("#unitForm select").each(function () {
                    $(this).parent().parent().parent().css("display", "none");
                })
            }

            function unitSuccess(data, status) {
                $("#parentId").empty();
                var data = data.result;
                for (var i = 0; i < data.length; i++) {
                    var option = $('<option>');
                    option.val(data[i].unitId);
                    option.text(data[i].unitName);
                    $("#parentId").append(option);
                }
                //渲染所属单位下拉单选框事件
                $("#parentId").selectBoxIt().data('selectBoxSelectBoxIt').destroy();
                $("#parentId").selectBoxIt().on('open', function () {
                    $(this).data('selectBoxSelectBoxIt').list.perfectScrollbar();
                });
            }

            //初始化上级单位下拉菜单
            DF.ajax({
                url: "securityUnitT/findUnitAll",
                success: unitSuccess,
                async: false,
                type: 'GET',
                error: GUID.unitError
            });

            //渲染组织机构类型下拉单选框事件
            $("#type").selectBoxIt().on('open', function () {
                $(this).data('selectBoxSelectBoxIt').list.perfectScrollbar();
            }).on('change', function () {
                if ($(this).val() == "0") {
                    changeColState("4s");
                } else if ($(this).val() == "1") {
                    changeColState("Proxy");
                } else {
                    changeColState("Assessor");
                }
            });
            function changeColState(col) {
                $("input[id*='Col']").parent().parent().parent().css("display", "none");
                // $("input[id*='Col']").val("");
                $("input[id$='" + col + "']").parent().parent().parent().css("display", "block");
            }

            //设置上级单位、组织机构类型状态
            $("#type").removeAttr("disabled");

            function treeSuccess(data, status) {
                $("#type").empty();
                var dataSingle = data.result;
                for (var i = 0; i < dataSingle.length; i++) {
                    var option = $('<option>');
                    option.val(dataSingle[i].key);
                    option.text(dataSingle[i].value);
                    $("#type").append(option);
                }
                //渲染所属单位下拉单选框事件
                $("#type").selectBoxIt().data('selectBoxSelectBoxIt').destroy();
                $("#type").selectBoxIt().on('open', function () {
                    $(this).data('selectBoxSelectBoxIt').list.perfectScrollbar();
                });
            }

            //初始化公司类型下拉菜单
            DF.ajax({
                url: "securityUnitT/queryUnitTypeByUser",
                success: treeSuccess,
                async: false,
                type: 'GET',
                error: GUID.unitError
            });
            // //隐藏组织机构类型字段
            if ($("#forType").val() == '0') {
                $("#type").parent().parent().parent().css("display", "block");
            }
            //显示组织机构类型字段
            if ($("#forType").val() == '1') {
                $("#type").parent().parent().parent().css("display", "none");
            }

            //如果当前模板状态为新增，那么隐藏新增、删除按钮
            if ("1" == $("#flag").val()) {
                $("#appendNodeToSelected").css("display", "none");
                $("#appendHumNodeToSelected").css("display", "none");
                $("#delBtn").css("display", "none");
            }

            /*渲染单位类型下拉菜单事件*/

            $("#compType").selectBoxIt().on('open', function () {
                $(this).data('selectBoxSelectBoxIt').list.perfectScrollbar();
            }).on('change', function () {
                if ("1" == $(this).val()) {
                    GUID.deptHideControl();
                } else {
                    GUID.deptShowControl();
                }
            });


            //定义新增单位功能按钮事件
            $("#appendNodeToSelected").click(GUID.appendCompany);

            //定义新增人员功能按钮事件
            $("#appendHumNodeToSelected").click(GUID.appendHum);

            //注册删除按钮事件
            $("#delBtn").click(function () {
                GUID.deleteCompany();
            })

            //注册保存按钮事件
            $("#saveBtn").click(function () {
                //进行入库操作
                GUID.saveCompanyHandler($("#unitForm"), $("#flag").val());
            })


            if ($("#flag").val() == '1') { //新增操作，清空所有input的值
                $("#unitForm input:not(#saveBtn):not(#delBtn):not(#saveBtn)").val('');
            }

            /*当前点击节点为非公司并且不是新增操作时候，隐藏公司属性*/
            if (("0" != $("#forCompType").val()) && $("#flag").val() != '1') {
                var s = $("#unitForm").find("div .form-group label").next();
                for (var i = 0; i < s.length; i++) {
                    if (i != 1 && i != 2 && i != 3 && i != 4) {
                        $(s[i]).parent().css("display", "none");
                    }
                }
            }
        }

        this.getRoleSelected = function(){
            var checkedNodes = GUID.$checkableTree.getCheckedNodes(true);
            var ids = new Array();
            for (var i = 0; i < checkedNodes.length; i++) {
                ids.push(checkedNodes[i].id);
            }
            return ids.toString();
        }
        this.roleInit = function () {
            var selectData = new Object();
            //获取菜单树据
            function getData() {
                var treeDate = new Object();
                var ids = $("#forRoleId").val() ? $("#forRoleId").val() : "null";
                var sfn = function (data, status) {
                    //所有节点
                    treeDate = data.result.all;
                    //已选中
                    selectData = data.result.selected;

                    for (var i = 0; i < treeDate.length; i++) {
                        for (var j = 0; j < selectData.length; j++) {
                            if (treeDate[i].menuId == selectData[j].menuId) {
                                treeDate[i].checked = 'true';
                                treeDate[i].open = 'true';
                                break;
                            }
                        }
                    }

                };
                DF.ajax({
                    url: "service/securityMenuT/queryMenuByRoleId/" + ids,
                    type: 'GET',
                    async: false,
                    success: sfn
                });
                return treeDate;
            }

            function processTable(data, idField, foreignKey, rootLevel) {
                var hash = {};
                for (var i = 0; i < data.length; i++) {
                    var item = data[i];
                    var id = item[idField];
                    var parentId = item[foreignKey];

                    hash[id] = hash[id] || [];
                    hash[parentId] = hash[parentId] || [];

                    item.children = hash[id];
                    hash[parentId].push(item);
                }
                return hash[rootLevel];
            }

            function zTreeOnCheck(event, treeId, treeNode) {
                var checkedNodes = GUID.$checkableTree.getCheckedNodes(true);
                var ids = new Array();
                for (var i = 0; i < checkedNodes.length; i++) {
                    ids.push(checkedNodes[i].id);
                }
                $("#forMenuId").val(ids.toString());
            };


            //定义菜单树
            GUID.$checkableTree = DF.zTree({
                id: 'menuTree',
                checkable: true,
                dragable: false,
                treeData: processTable(getData(), "menuId", "parentId", '-1'),
                idKey: 'menuId',
                textKey: 'menuName',
                callback: {
                    onCheck: zTreeOnCheck
                },
                onComplete: function (treeObject) {
                }
            });
        }


        //删除公司
        this.deleteCompany = function () {
            var info = "确认删除吗？";
            if(KendoTreeView.getSelectedNode().children&&KendoTreeView.getSelectedNode().children.length>0){
                info = '该机构下包含其他机构或人员，确认删除吗？';
            }
            DF.confirm(info).done(function () {
                var id = KendoTreeView.getSelectedNodeId();
                if (id != '-1') {
                    //清空区域
                    $("#showCompany").children().remove();
                    var sfn = function (data, status) {
                        KendoTreeView.removeSelectedNode();
                        DF.toast.success(data.message);
                    };
                    DF.ajax({
                        url: 'securityUnitT/' + id,
                        type: 'DELETE',
                        success: sfn
                    });
                } else {
                    DF.toast.error("根节点无法删除");
                }
            })
        }

        //保存公司信息处理方法
        this.saveCompanyHandler = function (form, flag) {
            $("#compType").attr("disabled", false);
            GUID.saveOrUPComanydate(form, flag, function (data, status) {
                $("#compType").attr("disabled", true);
                if (data.result.txstate == '0') {
                    DF.toast.success("数据更新成功，同步企业邮箱成功");
                }
                if (data.result.txstate == '1') {
                    DF.toast.success("数据更新成功");
                }
            });
        }

        //保存表单or编辑表单(公司)  走后台
        this.saveOrUPComanydate = function (form, flag, fn) {
            $("#parentId").attr("disabled", false);
            $("#type").attr("disabled", false);
            form.validate({
                debug: true
            });
            if (form.valid()) {
                var data = form.serializeObject();
                data.menuTs = data.menus;
                delete data.menus;
                var id = KendoTreeView.getSelectedNodeId();
                if (id == '-1') {
                    var obj = new Object();
                    obj.name = 'parentId';
                    obj.value = '9999';
                    data.parentId = '9999';
                }

                //修改
                if ($("#unitId").val()) {
                    flag = '0';
                } else {//新增
                    flag = '1';
                }

                var url = flag == "0" ? 'securityUnitT/editUnit' : 'securityUnitT/addUnit';
                var sfn = function (data, status) {
                    if(data.errorCode=='90805'){
                        DF.toast.error(data.message);
                        return false;
                    }
                    //$("#unitId").val(data.result.unitId);
                    var unitId = data.result.unitId;
                    $("#parentId").attr("disabled", true);
                    $("#type").attr("disabled", true);
                    KendoTreeView.nodeOper(flag, data, 'comp', flag, function () {
                        KendoTreeView.findAndTriggerClick(treeObjs.getNodes(), unitId)
                    });
                    fn && fn(data, status);
                };
                DF.ajax({
                    data: JSON.stringify(data),
                    type: flag == "0" ? 'PUT' : 'POST',
                    url: url,
                    success: sfn
                });
            }

        };

        //新增单位处理动作
        this.appendCompany = function (e) {
            DF.toggleMainViewMask(true);
            $("#securityUnitTForm").reset();
            var content = $("#securityUnitTForm").html();
            //设置模板状态为新增状态
            $("#flag").val("1");
            DF.renderPage(content, $("#showCompany"), function () {
                if (treeObjs.getSelectedNodes()[0].id == '-1') {
                    $("#compType").attr("disabled", true);
                }
                GUID.comPanyInit();
                function sigleUnitSuccess(data, status) {
                    // if (data.result.parentId != '9999') {
                    //     $("#type").parent().parent().parent().css("display", "none");
                    // } else {
                    //     $("#type").parent().parent().parent().css("display", "block");
                    // }
                    //如果是新增动作
                    if ($("#flag").val() == '1') {
                        var insertData = new Object();
                        data.result.parentId = KendoTreeView.getSelectedNodeId();
                        /*设置父节点ID*/
                        insertData.parentId = data.result.parentId;
                        insertData.type = data.result.type==null?"0":data.result.type;
                        insertData.compType = '0';
                    }
                    DF.setFormValues(insertData, $("#unitForm"));
                    DF.toggleMainViewMask(false);
                }

                //根据组织机构ID查询组织机构信息
                DF.ajax({
                    url: "securityUnitT/find/" + $("#forUnitId").val(),
                    success: sigleUnitSuccess,
                    type: 'GET',
                    error: GUID.unitError
                });
            });
        };

        //新增人员
        this.appendHum = function (e) {
            DF.toggleMainViewMask(true);
            var content = $("#guidForm").html();
            //设置模板状态为新增状态
            $("#flag").val("1");
            DF.renderPage(content, $("#showCompany"), function () {
                WIZARD.init();
                DF.renderPage($("#securityUserTForm").html(), $("#userInfo"), function () {
                    resourceList.length = 0;//初始化
                    unitList.length = 0;//初始化
                    function saveHandler(modal){
                        GUID.setFormIsChange(true);
                        userResourceDataConvert(modal.find('input[id="selectedCity"]').attr('resourcecontent'));
                        unitResourceDataConvert(modal.find('input[id="selectedUnit"]').attr('resourcecontent'));
                        userRoleDataConvert(modal.find('input[id="selectedRole"]').attr('resourcecontent'));
                        GUID.resourceList = resourceList;
                        DF.hideModal(modal);
                    }
                    $("#contentResources").bind("click",function(){
                        debugger;
                        var config = {
                            dom: $("#resourceForm"),
                            title:'资源配置',
                            width:'1000',
                            cache:false,
                            backdrop: 'static',
                            btns: [{
                                text: '确定',
                                icon: 'btn-info',
                                valid:false,
                                fn: saveHandler
                            }]
                        }
                        DF.showModal(config);
                    })

                    $("#delPBtn").click(function () {
                        GUID.delPerson();
                    })
                    //定义管理机构事件
                    GUID.setAccountFormObj(DF.on('formChange', $('#securityUserTFormU')));
                    DF.toggleMainViewMask(false);
                });
            });
        };

        //保存人员信息
        this.saveHandler = function (form, flag) {
            return GUID.saveOrUPdate(form, flag);
        }
        //保存表单or编辑表单  走后台
        this.saveOrUPdate = function (form, flag, fn) {
            /*为隐藏域赋值*/
            $("#belongOrg").val($("#forUnitId").val());
            var data = form.serializeObject();
            debugger;
            data.securityUserResourceRs = resourceList;
            data.securityUserUnitRs = unitList;
            form.validate({
                debug: true
            });

            if (checkMail($("#emailAddr"), data.userId) == false) {
                return false;
            }
            if (GUID.getAccountFormObj().isChange()||GUID.getFormIsChange()) {
                var bflag = '';

                if (form.valid()) {
                    var url;
                    if (data.userId) {
                        url = 'service/securityUserT/editXD';
                        bflag = '0';
                    } else {
                        url = 'service/securityUserT/addXD';
                        bflag = '1';
                    }
                    var sfn = function (data, status) {
                        $("#userId").val(data.result.userId);
                        /*进行人员节点处理*/
                        KendoTreeView.nodeOper(flag, data, 'hum', bflag);
                        fn && fn(data, status);

                        if (data.result.txstate == '0') {
                            DF.toast.success("数据更新成功，同步企业邮箱成功");
                        }
                        if (data.result.txstate == '1') {
                            DF.toast.success("数据更新成功");
                        }
                    };
                    DF.ajax({
                        url: url,
                        type: "POST",
                        data: JSON.stringify(data),
                        async: false,
                        success: sfn
                    });
                    return true;
                } else {
                    return false;
                }
            } else {
                if (!form.valid()) {
                    return false;
                }
                return true;
            }
        }

        /*如果当前结点不是公司，那么隐藏掉多余的属性*/
        this.deptHideControl = function () {
            var s = $("#unitForm").find("div .form-group label").next();
            for (var i = 0; i < s.length; i++) {
                if (i != 1 && i != 2 && i != 3 && i != 4) {
                    $(s[i]).parent().css("display", "none");
                }
            }
        }

        /*如果选择的是公司，显示属性*/
        this.deptShowControl = function () {
            var s = $("#unitForm").find("div .form-group label").next();
            for (var i = 0; i < s.length; i++) {
                if (i != 0 && i != 1 && i != 2 && i != 3 && i != 4) {
                    $(s[i]).parent().css("display", "block");
                }
            }
        }

        this.setUserInfo = function (obj) {
            this.userInfo = obj;
        }
        this.setFormIsChange = function(obj){
            this.formIsChange = obj;
        }
        this.getFormIsChange = function(){
            return this.formIsChange;
        }
        this.getUserInfo = function () {
            return this.userInfo;
        }
        this.setAccountFormObj = function (obj) {
            this.accountFormObj = obj;
        }
        this.getAccountFormObj = function () {
            return this.accountFormObj;
        }
        this.setTreeViewObj = function (treeObj) {
            this.treeViewObj = treeObj;
        }
        this.getTreeViewObj = function () {
            return this.treeViewObj;
        }
        this.setIsChange = function (obj) {
            this.isChange = obj;
        }
        this.getIsChange = function () {
            return this.isChange;
        }
        this.unitError = function (xhr, s, e) {
            DF.toast.error("数据加载失败");
        }

    }
    window.GUID = new guidFrameWork();

        //kendoTree封装
        var kendoTreeObj = function () {
            var url = null, content = null, treeObj = null, filter = null,checkable = false,dragable = true,//获取树数据的请求地址
            defaultSelect = true,callBackOncheck,userResourceTable,$treeObj=null;//树加载后，是否默认选中第一个节点
            //初始化方法 url-数据请求地址 content-树展现所需要的div对象（需要jquery对象） filterContent-查询输入框对象

            this.reloadUserResourceTable = function(){
                userResourceTable&&userResourceTable.getGrid().ajax.reload();
            }

            this.getZtreeObj = function(){
                return $treeObj;
            }

            this.init = function (url, content, filterContent,config,callBackOncheck) {
                if (!url || !content) {
                    console.log("url和content为必须参数");
                    return;
                }
                if (filterContent) {
                    //注册搜索框回车事件
                    filterContent.keypress(function (e) {
                        if (e.type != "keypress" || kendo.keys.ENTER == e.keyCode) {
                            var filterText = filterContent.val();
                            if (filterText !== "") {
                                treeObj.dataSource.filter({
                                    field: "text",
                                    operator: "contains",
                                    value: filterText
                                });
                            } else {
                                treeObj.dataSource.filter({});
                            }
                        }
                    });
                }
                this.url = url;
                this.content = content;
                var defaultConfig = {
                    checkable:false,
                    dragable:true,
                    defaultSelect:true
                }
                var cf = $.extend(defaultConfig,config);
                this.checkable = cf.checkable;
                this.dragable = cf.dragable;
                this.callBackOncheck = callBackOncheck;
                this.defaultSelect = cf.defaultSelect;
            }
            //获取当前选中节点
            this.getSelectedNodeId = function () {
                //return (this.treeObj.dataItem(this.treeObj.select())).id;
                //return KendoTreeView.getSelectedNodeId();
                return (treeObjs.getSelectedNodes()[0]).id;
            }
            this.getSelectedNode = function(){
                return treeObjs.getSelectedNodes()[0];
            }
            //删除选中节点
            this.removeSelectedNode = function () {
                //this.treeObj.remove(this.treeObj.select());
                var nodes = treeObjs.getSelectedNodes();
                for (var i = 0, l = nodes.length; i < l; i++) {
                    treeObjs.removeNode(nodes[i]);
                }
            }
            //显示树
            this.show = function (onclick) {
                var _treeObj = null;
                var rootNode = null;
                var belongCompany = null;
                var rootLevel = null;

                /*获取当前登陆人所属公司*/

                function sfn(data, status) {
                    belongCompany = data.result.belongCompany;
                    var iconSkins;
                    compTypes = belongCompany.compType;

                    if (compTypes == "0") {
                        iconSkins = compimg;
                    }

                    if (compTypes == "1") {
                        iconSkins = deptimg;
                    }

                    if (compTypes == "3") {
                        iconSkin = humimg;
                    }

                    if (compTypes == "2") {
                        iconSkin = rootimg;
                    }

                    rootLevel = belongCompany.parentId;
                    rootNode = {
                        id: '-1',
                        text: "企业通讯录",
                        iconSkin: iconSkins,
                        "isParent": true,
                        compType: belongCompany.compType,
                        open: true
                    }
                }

                DF.ajax({
                    url: "service/securityUserT/userInfo",
                    type: "GET",
                    async: false,
                    success: sfn
                })


                /*加载kendoTreeView树组件*/
                function unitSuccess(data) {
                    var treeData = KendoTreeView.processTable(data.result, "unitId", "parentId", '-1');

                    function zTreeBeforeDrag(treeId, treeNodes) {
                        if (confirm('确认移动吗？')) {
                            return true;
                        }
                        return false;
                    }

                    function beforeDrop(treeId, treeNodes, targetNode, moveType) {
                        if (!targetNode.drop) {
                            DF.toast.error("禁止将节点移动到人员下面！");
                        }
                        return targetNode ? targetNode.drop !== false : true;
                    }

                    function zTreeOnDrop(event, treeId, treeNodes, targetNode, moveType) {
                        console.log("执行拖拽", treeNodes, targetNode, moveType);
                        //获取原节点ID
                        var sourceNodeId = treeNodes[0].id;
                        //获取原节点类型
                        var compType = treeNodes[0].compType;
                        //获得目标节点ID
                        var targetNodeId = targetNode.id;
                        //获取目标节点类型
                        var targetType = targetNode.compType;


                        function sfn(data, status) {
                            DF.toast.success("节点移动成功");
                        }

                        DF.ajax({
                            url: "securityUnitT/dragAndDrop/" + compType + "/" + sourceNodeId + "/" + targetType + "/" + targetNodeId + "/" + moveType,
                            type: "PUT",
                            success: sfn
                        })

                    }

                    _treeObj = DF.zTree({
                        id: KendoTreeView.content,
                        checkable: KendoTreeView.checkable,
                        dragable: KendoTreeView.dragable,
                        root: rootNode,
                        treeData: treeData,
                        idKey: 'unitId',
                        textKey: 'unitName',
                        callback: {
                            onClick: function (event, treeId, treeNode) {
                                console.log(treeNode);
                                onclick ? onclick(event, treeId, treeNode) : KendoTreeView.onSelect(event, treeId, treeNode);
                            },
                            onDrop: zTreeOnDrop,
                            beforeDrag: zTreeBeforeDrag,
                            beforeDrop: beforeDrop,
                            onCheck:KendoTreeView.callBackOncheck
                        },
                        onComplete: function (treeObject) {
                            if(KendoTreeView.defaultSelect){
                                treeObjs = treeObject;
                                if (treeObject.getNodes().length > 0) {
                                    treeObject.selectNode(treeObject.getNodes()[0], false, true);
                                }
                            }
                        }
                    });
                }

                DF.ajax({
                    url: KendoTreeView.url,
                    success: unitSuccess,
                    async: false,
                    type: 'GET',
                    error: GUID.unitError
                });
                $treeObj = _treeObj;
                return _treeObj;
            }
            //为树节点添加图片
            this.addImg = function (data) {
                if (data.compType == '0') {//如果是公司
                    data.imageUrl = 'img/company.png';
                } else if (data.compType == '1') {  //部门
                    data.imageUrl = 'img/group.png';
                } else if (data.compType == '3') { //人
                    data.imageUrl = 'img/hum.png';
                } else if (data.compType == "2") { //跟节点
                    data.imageUrl = 'img/Documents.png';
                }
                return data;
            }
            //组装树所需要的数据格式
            this.processTable = function (data, idField, foreignKey, rootLevel) {
                var hash = {};
                for (var i = 0; i < data.length; i++) {
                    var item = data[i];
                    var id = item[idField];
                    var parentId = item[foreignKey];

                    hash[id] = hash[id] || [];
                    hash[parentId] = hash[parentId] || [];
                    if (item.compType == "0") {
                        item.iconSkin = compimg;
                        item.drop = true;
                    }

                    if (item.compType == "1") {
                        item.iconSkin = deptimg;
                        item.drop = true;
                    }

                    if (item.compType == "3") {
                        item.iconSkin = humimg;
                        item.drop = false;
                    }

                    if (item.compType == "2") {
                        item.iconSkin = rootimg;
                    }
                    item.children = hash[id];
                    hash[parentId].push(item);
                }
                return hash[rootLevel];
            }
            //转换后台获取的数据，转换为kendoTree所需要的格式
            this.converText = function (data) {
                for (var i = 0; i < data.length; i++) {
                    data[i].id = data[i].unitId;
                    data[i].text = data[i].unitName;
                    data[i].parent = data[i].parentId;
                    delete data[i].unitId;
                    delete data[i].unitName;
                    delete data[i].parentId;
                    data[i] = this.addImg(data[i]);
                }
                return data;
            }
            //点击节点为人员时，渲染人员信息
            this.humRender = function (content, selectedId) {
                GUID.setFormIsChange(false);
                //初始化标志位为查询状态
                $("#flag").val("0");
                console.log(content);
                //初始化form表单，设置各类控件为指定样式
                DF.renderPage(content, $("#showCompany"), function () {
                    WIZARD.init();
                    DF.renderPage($("#securityUserTForm").html(), $("#userInfo"), function () {

                        resourceList.length = 0;
                        unitList.length = 0;

                        function saveHandler(modal){
                            GUID.setFormIsChange(true);
                            debugger;
                            userResourceDataConvert(modal.find('input[id="selectedCity"]').attr('resourcecontent'));
                            unitResourceDataConvert(modal.find('input[id="selectedUnit"]').attr('resourcecontent'));
                            userRoleDataConvert(modal.find('input[id="selectedRole"]').attr('resourcecontent'));
                            GUID.resourceList = resourceList;
                            DF.hideModal(modal);
                        }

                        $("#contentResources").bind("click",function(){
                            debugger;
                            var config = {
                                dom: $("#resourceForm"),
                                title:'资源配置',
                                width:'1000',
                                cache:false,
                                backdrop: 'static',
                                btns: [{
                                    text: '确定',
                                    icon: 'btn-info',
                                    valid:false,
                                    fn: saveHandler
                                }]
                            }
                            DF.showModal(config);
                        })

                        //读取人员的资源信息
                        DF.load('js/multipleSearchGrid.js',function () {
                            var searchConfig = {
                                id: "resourceTable",
                                ajax: "service/securityUserT/userResourceByUserId?userId="+KendoTreeView.getSelectedNodeId(),
                                searchable: false,
                                columns: [
                                    {"data": "userResourceRId", "visible": false ,"orderable":false},
                                    {"data": "resourceType","orderable":false},
                                    {"data": "resourceName","orderable":false},
                                    {"data": "modifyDateTime", "visible": false,"orderable":false},
                                    {"data": "founderName", "visible": false,"orderable":false}
                                ],
                                columnDefs: [
                                    {
                                        render: function (data, type, row) {
                                            if('store'==data){
                                                return "门店";
                                            }
                                            if('role'==data){
                                                return "角色";
                                            }
                                            if('unit'==data){
                                                return "机构";
                                            }
                                            if('stock'==data){
                                                return "仓库";
                                            }
                                            if('City'==data){
                                                return "城市";
                                            }
                                            if('thirdPartyCompany'==data){
                                                return "销售公司";
                                            }
                                        },
                                        "targets": 1
                                    }
                                ],
                                //初始化结束后回调
                                initComplete: function () {
                                    resourceTableShowHidden('show');
                                }
                            }
                            var searchGrid = $('#resourceTable').multipleSearchGrid(searchConfig);
                            userResourceTable = searchGrid;
                        });


                        //注册删除员工按钮事件
                        $("#delPBtn").click(function () {
                            GUID.delPerson(selectedId);
                        })
                        GUID.setIsChange(false);
                        function sfn(datas, status) {
                            GUID.setUserInfo(datas.result);
                            var daContent = datas.result.securityUserResourceRs;
                            var dataUnit = datas.result.securityUserUnitRs;
                            $("#forUnitId").val(datas.belongOrg);
                            DF.setFormValues(datas.result, $("#securityUserTFormU"));
                            GUID.setAccountFormObj(DF.on('formChange', $('#securityUserTFormU')));
                            $("#contentUnit").trigger("propertychange");
                            //设置包含的资源，修改人员初始化
                            resourceSetValue(daContent,"1");
                            //设置管理单位信息
                            userUnitSetValue(dataUnit,"1");
                            DF.toggleMainViewMask(false);
                        }

                        //后台请求查询当前用户信息
                        DF.ajax({
                            url: "service/securityUserT/getUserById/" + selectedId,
                            success: sfn
                        });
                    })
                });
            }

            function resourceSetValue(data,type){
                resourceList.length = 0;
                var names = new Array();
                var ids = new Array();
                //修改
                if("1"==type){
                    for(var da in data){
                        var obj = new resourceObj();
                        obj.belongModel = data[da].belongModel;
                        obj.resourceId = data[da].resourceId;
                        obj.resourceName = data[da].resourceName;
                        obj.resourceType = data[da].resourceType;
                        names.push(data[da].resourceName);
                        ids.push(data[da].resourceId);
                        resourceList.push(obj);
                    }
                    $("#contentResource").attr("contentResourceId", ids.toString());
                    $("#contentResource").attr("module", JSON.stringify(data));
                }
                //新增
                if("0"==type){
                        for (var da in data) {
                            var obj = new resourceObj();
                            obj.resourceId = data[da].dictValueId;
                            obj.belongModel = data[da].module;
                            obj.resourceType = data[da].dictItemCode;
                            obj.resourceId = data[da].dictValueCode;
                            obj.resourceName = data[da].dictValueName;
                            resourceList.push(obj);
                            names.push(data[da].dictValueName);
                            ids.push(data[da].dictValueCode);
                        }
                    $("#contentResource").attr("contentResourceId", ids.toString());
                    $("#contentResource").attr("module", JSON.stringify(data));
                }
                $("#contentResource").trigger("propertychange");
            }

            //设置用户管理机构
            function userUnitSetValue(data,type){
                var userId = $("#userId").val();
                unitList.length = 0;//初始化
                var names = new Array();
                var ids = new Array();
                for(var da in data){
                    var obj = new unitResourceObj();
                    obj.unitIdR = data[da].unitIdR;
                    obj.unitNameR = data[da].unitNameR?data[da].unitNameR:"";
                    obj.parentUnitId = data[da].pNodeId?data[da].parentUnitId:"";
                    obj.userIdR = $("#userId").val();
                    obj.parentUnitName = "";
                    obj.userNameR = "";
                    names.push(data[da].unitNameR);
                    ids.push(data[da].unitIdR);
                    unitList.push(obj);
                }
                $("#contentUnit").val(names.toString());
                $("#contentUnit").attr("contentResourceId", ids.toString());
            }

            //找出某一结点，然后模拟点击事件
            this.findAndTriggerClick = function (nodes, nodeId) {
                var node = treeObjs.getNodeByParam("id", nodeId);
                $("#" + node.tId + " a:first").trigger("click");
            }


            //点击节点为人员时，且请求为查看的时候，渲染人员信息
            this.humRenderTxl = function (content) {
                //初始化标志位为查询状态
                $("#flag").val("0");
                //初始化form表单，设置各类控件为指定样式

                DF.renderPage(content, $("#showCompany"), function () {
                    DF.setFormReadOnly($("#securityUserTFormU"), true);
                    function sfn(datas, status) {
                        GUID.setUserInfo(datas.result);
                        $("#forUnitId").val(datas.belongOrg);
                        DF.setFormValues(datas.result, $("#securityUserTFormU"));
                    }

                    //后台请求查询当前用户信息
                    DF.ajax({
                        url: "service/securityUserT/getUserById/" + KendoTreeView.getSelectedNodeId(),
                        success: sfn
                    });
                });
            }


            //点击节点为组织机构的时候，渲染组织机构信息
            this.compRender = function (content, readOnly, selected) {
                resourceTableShowHidden('hidden');
                GUID.setFormIsChange(false);
                /*定义组织机构ID*/
                $("#forUnitId").val(KendoTreeView.getSelectedNodeId());
                //设置状态位为修改
                $("#flag").val("0");
                var parentId;
                //根据ID到后台查询单位信息，将单位信息铺垫到右侧区域
                DF.renderPage(content, $("#showCompany"), function () {
                    function sigleUnitSuccess(data, status) {
                        //如果是新增动作
                        if ($("#flag").val() == '1') {
                            data.result.parentId = (this.treeObj.dataItem(this.treeObj.select())).id;
                        }
                        //$("#parentId").val(data.result.parentId);
                        $('#forType').val(data.result.compType);
                        GUID.comPanyInit();
                        if (parentId == '-1') {
                            $("#compType").attr("disabled", true);
                        }
                        if (readOnly) {
                            DF.setFormReadOnly($("#unitForm"), true);
                        }
                        DF.setFormValues(data.result, $("#unitForm"));
                    }

                    //根据组织机构ID查询组织机构信息
                    DF.ajax({
                        url: "securityUnitT/find/" + $("#forUnitId").val(),
                        success: sigleUnitSuccess,
                        type: 'GET',
                        async: false,
                        error: GUID.unitError
                    });
                });
            }
            //树点击事件
            this.onSelect = function (event, treeId, treeNode) {
                DF.toggleMainViewMask(true);
                var _selected = treeNode.id;
                GUID.setUserInfo(null);
                var compType = treeNode.compType;
                //var imgname = (KendoTreeView.treeObj.dataItem(_selected)).imageUrl;
                var content;
                $("#forCompType").val(compType);
                /*如果点击的是人员，那么渲染右侧区域为人员表单*/
                if (compType == '3') {
                    content = $("#guidForm").html();
                    KendoTreeView.humRender(content, _selected);
                }
                /*如果点击的是组织机构，那么渲染右侧区域为组织机构表单*/
                else {
                    content = $("#securityUnitTForm").html();
                    KendoTreeView.compRender(content, false, _selected);
                }
            }


            this.nodeOper = function (flag, data, nodeType, bflag, fn) {
                var selectedNode = treeObjs.getSelectedNodes();
                var img, id, text;
                if (selectedNode.length == 0) {
                    selectedNode = null;
                }
                if ("hum" == nodeType) {
                    img = humimg;
                    id = data.result.userId;
                    text = data.result.userName;
                    data.result.compType = "3";
                } else {
                    //如果是公司
                    if (data.result.compType == '0' || data.result.parentId == '-1') {
                        img = compimg;
                    } else if (data.result.compType == '1') {  //部门
                        img = deptimg;
                    } else if (data.result.compType == '3') {
                        //img = 'img/hum.png';
                        img = humimg;
                    }
                    id = data.result.unitId;
                    text = data.result.unitName;
                }
                //如果是新增操作
                if (bflag == '1') {
                    var newNode = {id: id, text: text, iconSkin: img, compType: data.result.compType,unitId:data.result.userId,parentId:selectedNode[0].unitId};
                    treeObjs.addNodes(selectedNode[0], newNode);
                }
                //如果当前为修改操作，更新dom 设置为当前保存的内容
                if (bflag == '0') {
                    var selectNode;
                    var id;

                    //如果是人员
                    if (nodeType == 'hum') {
                        id = data.result.userId;

                    } else {
                        id = data.result.unitId;
                    }
                    selectNode = treeObjs.getNodeByParam("id", id);

                    selectNode.text = text;
                    selectNode.iconSkin = img;
                    treeObjs.updateNode(selectNode);
                }
                fn && fn();
            }

        }
        window.KendoTreeView = new kendoTreeObj();

        var loginUserInfo = function () {
            this.belongOrg;
        }

        window.LoginUser = new loginUserInfo();

        //向导封装
        var wizard = function () {
            this.init = function () {
                /*form-wizard组件加载，定义下一页时候的校验规则，会初始化所有向导控件*/
                $(".form-wizard").each(function (i, el) {
                    var $this = $(el),
                        $tabs = $this.find('> .tabs > li'),
                        $progress = $this.find(".progress-indicator"),
                        _index = $this.find('> ul > li.active').index();
                    /*初始化bootstrapWizard标签*/
                    $this.bootstrapWizard({
                        tabClass: "",
                        onTabShow: function ($tab, $navigation, index) {
                            /*设置已完成步骤进度条样式*/
                            var pct = $tabs.eq(index).position().left / $tabs.parent().width() * 100;
                            $tabs.removeClass('completed').slice(0, index).addClass('completed');
                            $progress.css({width: pct + '%'});


                            //控制保存及下一步按钮是否显示
                            $("#saveAccountBt").css('display', index == '2' ? 'block' : 'none');
                            $("#nextBT").css("display", index == 2 ? 'none' : 'block');

                            setEnable(index);
                            if (index == 0) {
                                setDisable(1);
                                setDisable(2);
                            }
                            if (index == 1) {

                                //刷新资源列表
                                KendoTreeView.reloadUserResourceTable();
                                setDisable(0);
                                setDisable(2);
                            }
                            if (index == 2) {

                                //刷新资源列表
                                KendoTreeView.reloadUserResourceTable();
                                setDisable(0);
                                setDisable(1);
                            }
                            function setDisable(index) {
                                $(".tabs li:eq(" + index + ")").css("background-color", "#BFBFBF");
                                $(".tabs li:eq(" + index + ") i").css({
                                    "background": "url(img/sort.png) -28px 0px no-repeat",
                                    "width": "14px",
                                    "height": "40px",
                                    "position": "absolute",
                                    "top": "0px",
                                    "left": "50px"
                                });
                                $(".tabs li:eq(" + index + ") span:eq(0)").css({
                                    "font-size": "18px",
                                    "color": "#fff",
                                    "position": "absolute",
                                    "left": "20px",
                                    "top": "8px",
                                    "font-weight": "bold",
                                    "font-family": "Verdana",
                                    "font-style": "normal",
                                    "color": "#D4D4D4"
                                });
                                $(".tabs li:eq(" + index + ") a").css({"color": "#D4D4D4"});
                            }

                            function setEnable(index) {
                                if (index == 0) {
                                    $(".tabs li:eq(" + index + ")").css("background-color", "#8dc63f");
                                    $(".tabs li:eq(" + index + ") i").css({
                                        "background": "url(img/sort.png) 0px 0px no-repeat",
                                        "width": "14px",
                                        "height": "40px",
                                        "position": "absolute",
                                        "top": "0px",
                                        "left": "50px"
                                    });
                                }
                                if (index == 1) {
                                    $(".tabs li:eq(" + index + ")").css("background-color", "#ffba00");
                                    $(".tabs li:eq(" + index + ") i").css({
                                        "background": "url(img/sort.png) -14px 0px no-repeat",
                                        "width": "14px",
                                        "height": "40px",
                                        "position": "absolute",
                                        "top": "0px",
                                        "left": "50px"
                                    });
                                }
                                if (index == 2) {
                                    $(".tabs li:eq(" + index + ")").css("background-color", "#adadad");
                                    $(".tabs li:eq(" + index + ") i").css({
                                        "background": "url(img/sort.png) -28px 0px no-repeat",
                                        "width": "14px",
                                        "height": "40px",
                                        "position": "absolute",
                                        "top": "0px",
                                        "left": "50px"
                                    });
                                }
                                $(".tabs li:eq(" + index + ") span:eq(0)").css("color", "#fff");
                                $(".tabs li:eq(" + index + ") a").css({"color": "#fff"});

                            }


                            /*  第二个页面初始化加载
                             *  1、根据上一个标签页选择的人员信息，查询所在单位具备的角色信息并展现
                             *  2、标注哪些角色是当前用户所具备的，复选框进行标注
                             * */
                            if (index == '1') {
                                /*业务处理部分*/
                                WIZARD.secDule($("#userId").val());
                            }

                            /*第三个页面初始化加载*/
                            if (index == '2') {
                                DF.renderPage($("#securityUserTAccountForm").html(), $("#accountContent"), function () {
                                    /*在第一个页面选择的人员的账号*/
                                    var account;
                                    if (GUID.getUserInfo()) {
                                        account = GUID.getUserInfo().userAccount;
                                    }
                                    if (typeof(account) != 'undefined' && account != "") {
                                        $("#userAccount").val(account);
                                    }
                                });
                            }
                        },
                        onNext: WIZARD.checkFormWizardValidaion,
                        onTabClick: WIZARD.checkFormWizardValidaion
                    });
                })

                /*注册保存账号按钮点击事件*/
                $("#saveAccountBt").bind("click", function () {
                    WIZARD.saveAccountInfo($("#securityUserTAccountFormU"), "0");
                })
            }
            /*下一步校验
             1、对于第一个sheet页，校验规则为树上选择的节点必须为人
             2、对于第二个sheet页，校验规格为datagrid列表中必须有被选中的记录
             3、第三个sheet页，账号和密码为必输项
             */
            this.checkFormWizardValidaion = function (tab, navigation, index) {
                //var stepNo = tab[0].innerText;
                var stepNo = $(tab).children('a').children('span').html();
                /*定义第一个页面的校验规则，如果用户没有添加姓名和联系方式，那么要提示*/
                if (stepNo.indexOf("1") >= 0) {
                    return GUID.saveHandler($("#securityUserTFormU"), $("#flag").val());
                }
                /*1、第二个页面校验（必须选中角色列表中的某些项）2、提交第二个页面所选择的角色信息*/
                if (stepNo.indexOf("2") >= 0) {
                    /*记录所选择的角色ID*/
                    var selectedItem = new Array();
                    /*获取选中的角色项目*/
                    var selectedC = $("#roleListTable input[type='checkbox']:checked");
                    /*判断是否有角色列表中是否有记录被选中*/
                    if (selectedC.length == '0') {
                        DF.toast.error("请选择角色信息");
                        return false;
                    }
                    /*将角色列表中被选中记录的ID记录到selectedItem中*/
                    $(selectedC).each(function () {
                        selectedItem.push(this.id);
                    });

                    function humRoleError(xhr, s, e) {
                        DF.toast.error("配置角色失败");
                        return false;
                    }

                    function humRoleSuccess(data, state) {
                        DF.toast.success("角色配置已更新");
                    }

                    //如果角色发生变化，那么提交后台入库

                    if (GUID.getIsChange()) {
                        /*执行第二个页面的入库操作*/
                        DF.ajax({
                            url: "service/securityUserT/editUserRole/" + $("#userId").val() + "/" + selectedItem.toString(),
                            type: 'POST',
                            error: humRoleError,
                            success: humRoleSuccess
                        });
                    }

                }
                /*校验通过，进入下一个页面*/
                return true;
            }

            //第二个页面处理方法
            this.secDule = function (userId) {
                /*被选择的角色*/
                //TODO
                var selectedRole;
                /*角色列表对象*/
                function unitSuccess(data, status) {
                    //获得当前选中节点的ID
                    var userId = $("#userId").val();
                    selectedRole = data;
                    //定义角色列表
                    GUID.grid = DF.dataTable({
                        id: "roleListTable",
                        ajax: "service/securityRoleT/listForGuid/" + userId,
                        bPaginate: false,
                        searchCol: 2,
                        searchable: false,
                        rowbtns: [
                            {
                                text: '编辑',
                                name: 'edit',
                                class: 'btn-success',
                                displayKey: 'btrow',
                                displayValue: '0',
                                fn: WIZARD.editRow
                            },
                            {
                                text: '删除',
                                name: 'delete',
                                class: 'btn-danger',
                                displayKey: 'btrow',
                                displayValue: '0',
                                fn: WIZARD.deleteRow
                            }
                        ],
                        toolbtns: [
                            {text: '新建', name: 'create', class: 'btn-success', icon: '', fn: WIZARD.editRow},
                        ],
                        columns: [
                            {bVisible: true, bSortable: false},
                            {data: "roleId", bVisible: false, bSortable: false},
                            {data: "menuTs", bVisible: false},
                            {data: "roleName", bSortable: false}
                        ],
                        createdRow: function (row, data, index) {
                            $('input', row).click(function () {
                                setTreeNodeCheckEnable(_treeObjRight, _treeObjRight.getNodes());
                                WIZARD.checkedNodeMenuIds(_treeObjRight.getNodes(), WIZARD.menuHandle($("#roleListTable" +
                                    " input[type='checkbox']:checked")));
                                setTreeNodeCheckDisable(_treeObjRight, _treeObjRight.getNodes());
                            })
                        },
                        columnDefs: [
                            {
                                targets: 0,
                                render: function (data, type, row) {
                                    var ischeck = "0";
                                    var td;
                                    var _selectedRole = selectedRole.result;
                                    var selectedRoleAll = new Array();

                                    if(GUID.checkedRole){
                                        for(var i=0;i<GUID.checkedRole.length;i++){
                                            selectedRoleAll.push(GUID.checkedRole[i].id);
                                        }
                                    }else{
                                        for(var j=0;j<_selectedRole.length;j++){
                                            selectedRoleAll.push(_selectedRole[j].roleId);
                                        }
                                    }


                                    for (var i = 0; i < selectedRoleAll.length; i++) {
                                        //如果是当前角色已具备角色，那么设置为选中
                                        if (selectedRoleAll[i] == row.roleId) {
                                            ischeck = "1";
                                            break;
                                        }
                                    }
                                    if (ischeck == "1") {
                                        td = '<td>' +
                                            "<input type='checkbox' class='cbr' id='" + row.roleId +
                                            "' menuIds='" + row.menuTs + "' checked='checked' onchange='checkBoxChange()'>" +
                                            '</td>';
                                    } else {
                                        td = '<th>' +
                                            "<input type='checkbox' class='cbr' id='" + row.roleId + "' menuIds='" + row.menuTs + "' onchange='checkBoxChange()'>" +
                                            '</td>';
                                    }
                                    return td;
                                }
                            },
                            {
                                targets: 3,
                                render: function (data, type, row) {
                                    var before = "";
                                    if(row.roleType=='1'){
                                        before = "系统角色-";
                                    }
                                    else if(row.roleType=='2'){
                                        before = '自定义角色-';
                                    }
                                    return before+data;
                                }
                            }
                        ],
                        //重新加载
                        reloadGrid: function () {
                            GUID.grid.ajax.reload();
                        },
                        //初始化结束后回调
                        initComplete: function () {
                            $('#roleListTable').next().remove();
                            DF.ajax({
                                url: "service/securityMenuT/queryMenuByRoleId/" + "null",
                                success: roleSuccess,
                                error: GUID.unitError
                            });
                            /*加载菜单树成功回掉*/
                            function roleSuccess(data, status) {
                                var menuData = WIZARD.converMenuText(data.result.all);
                                var selectedMenu = WIZARD.converMenuText(data.result.selected);
                                //获取已选取的menuId
                                var selectedIds = WIZARD.menuHandle($("#roleListTable" +
                                    " input[type='checkbox']:checked"));

                                if (selectedIds) {
                                    for (var i = 0; i < menuData.length; i++) {
                                        menuData[i].open = 'true';
                                        //menuData[i].chkDisabled = 'true';
                                        for (var j = 0; j < selectedIds.length; j++) {
                                            if (menuData[i].id == selectedIds[j]) {
                                                menuData[i].checked = 'true';
                                                break;
                                            }
                                        }
                                    }
                                }

                                _treeObjRight = DF.zTree({
                                    id: 'roleTree',
                                    checkable: true,
                                    dragable: false,
                                    treeData: KendoTreeView.processTable(menuData, "id", "parent", '-1'),
                                    idKey: 'id',
                                    textKey: 'text',
                                    callback: {
                                        onCheck: function (event, treeId, treeNode) {
                                            return false;
                                        }
                                    },
                                    onComplete: function (treeObject) {
                                        setTreeNodeCheckDisable(treeObject,treeObject.getNodes());
                                    }
                                });


                            }
                        }
                    });
                }

                /*根据用户ID查询其具备的角色*/
                DF.ajax({
                    url: "service/securityRoleT/queryRoleByUserIdSelected/" + (userId ? userId : "null"),
                    success: unitSuccess,
                    error: GUID.unitError
                });

                //设置树所有节点可选择
                function setTreeNodeCheckEnable(treeObj,nodes){
                    for (var i = 0; i < nodes.length; i++) {
                        treeObj.setChkDisabled(nodes[i], false);
                        if ((nodes[i].children).length > 0) {
                            setTreeNodeCheckEnable(treeObj, nodes[i].children)
                        }
                    }
                }

                //设置树所有节点不可选择
                function setTreeNodeCheckDisable(treeObj,nodes){
                    for (var i = 0; i < nodes.length; i++) {
                        treeObj.setChkDisabled(nodes[i], true);
                        if ((nodes[i].children).length > 0) {
                            setTreeNodeCheckDisable(treeObj, nodes[i].children)
                        }
                    }
                }
            }
            /**
             *编辑或者新建 逻辑判断 无后台
             * rowdata 行数据
             */
            this.editRow = function (rowdata) {
                $("#forRoleId").val('');
                if (rowdata) {

                    $("#forRoleId").val(rowdata.roleId);
                }

                //编辑还是保存
                var flag = rowdata ? true : false;

                //如果是新增操作
                if (flag) {
                    $("#operType").val("0");
                    //弹出菜单的业务逻辑方法
                    function saveHandler(modal) {
                        WIZARD.saveOrUPdate(modal, flag, function (data, status) {
                            //TODO
                            DF.toast.success(data.message);
                            DF.hideModal(modal);
                            GUID.checkedRole = $('input[type="checkbox"]:checked');
                            GUID.grid.ajax.reload();
                        });
                    }

                    //modal窗口的配置项目
                    var config = {
                        dom: $('#securityRoleTForm'),
                        title: flag ? '修改角色' : '新增角色',
                        cache: false,
                        btns: [{
                            text: '保存',
                            icon: 'btn-info',
                            fn: saveHandler
                        }]
                    };
                    DF.showModal(config, function (modal) {
                        /*回调函数中的参数
                         * modal为当前的模块窗口 其中提供 getForm()方法 返回 业务的form 此form为 jq对象
                         */
                        GUID.roleInit();
                        flag && WIZARD.loadForm(modal, rowdata);
                    });
                } else {
                    $("#operType").val("1");
                    //弹出菜单的业务逻辑方法
                    function saveHandler(modal) {
                        WIZARD.saveOrUPdate(modal, flag, function (data, status) {
                            DF.toast.success(data.message);
                            DF.hideModal(modal);
                            GUID.grid.ajax.reload();
                        });
                    }

                    //modal窗口的配置项目
                    var config = {
                        dom: $('#securityRoleTForm'),
                        title: flag ? '编辑页面' : '新建页面',
                        cache: false,
                        btns: [{
                            text: '保存',
                            icon: 'btn-info',
                            fn: saveHandler
                        }]
                    };
                    DF.showModal(config, function (modal) {
                        /*回调函数中的参数
                         * modal为当前的模块窗口 其中提供 getForm()方法 返回 业务的form 此form为 jq对象
                         */
                        GUID.roleInit();
                        flag && WIZARD.loadForm(modal, rowdata);
                    });
                }
            }
            //删除 走后台
            this.deleteRow = function (rowdata) {
                DF.confirm('确认删除吗？').done(function () {
                    var url = 'service/securityRoleT';
                    var sfn = function (data, status) {
                        GUID.grid.ajax.reload();
                        DF.toast.success(data.message);
                    };
                    DF.ajax({
                        type: 'DELETE',
                        url: 'service/securityRoleT/' + rowdata.roleId,
                        success: sfn
                    });
                })
            };
            //加载表单  如果rowdata 数据满足  可以直接赋值 不用走后台
            this.loadForm = function (modal, rowdata) {
                var form = modal.getForm();
                if (rowdata) {
                    DF.setFormValues(rowdata, form);
                }
            };
            //保存表单or编辑表单  走后台

            this.saveOrUPdate = function (modal, flag, fn) {
                var form = modal.getForm();
                var data = form.serializeObject();
                data.menuTs = GUID.getRoleSelected();
                // delete data.menus;
                if (GUID.$checkableTree.getCheckedNodes(true).length<=0) {
                    DF.toast.error("请选择菜单");
                } else {
                    var url = flag ? 'service/securityRoleT/editRole' : 'service/securityRoleT/addRole';
                    var sfn = function (data, status) {
                        fn && fn(data, status);
                    };
                    DF.ajax({
                        data: JSON.stringify(data),
                        type: flag ? 'PUT' : 'POST',
                        url: url,
                        success: sfn
                    });
                }
            };
            this.checkedNodeMenuIds = function (nodes, menuIds) {
                if (menuIds) {
                    for (var i = 0; i < nodes.length; i++) {
                        _treeObjRight.checkNode(nodes[i], false, true);
                        for (var j = 0; j < menuIds.length; j++) {
                            if (nodes[i].id == menuIds[j]) {
                                _treeObjRight.checkNode(nodes[i], true, true);
                                break;
                            }
                        }
                        _treeObjRight.updateNode(nodes[i]);

                        if (nodes[i].children.length) {
                            WIZARD.checkedNodeMenuIds(nodes[i].children, menuIds);
                        }

                    }
                }
            }
            /*处理树组件的数据集*/
            this.menuHandle = function (menuIds) {
                var menus = [];
                var result = [];
                var obj = {};
                if (menuIds) {
                    for (var i = 0; i < menuIds.size(); i++) {
                        menus.push($(menuIds[i]).attr("menuids"));
                    }
                }
                menus = menus.toString().split(',');
                //console.log("当前未去重ID="+menus);
                for (var j = 0; j < menus.length; j++) {
                    var menuId = menus[j];
                    if (!obj[menuId]) {
                        result.push(menuId);
                    }
                    obj[menuId] = 1;
                }
                return result
            }
            /*转换json对象的格式为tree所需要的格式*/
            this.converMenuText = function (data) {
                for (var i = 0; i < data.length; i++) {
                    data[i].id = data[i].menuId;
                    data[i].text = data[i].menuName;
                    data[i].parent = data[i].parentId;
                    delete data[i].menuId;
                    delete data[i].menuName;
                    delete data[i].parentId;
                }
                return data;
            }
            //保存表单or编辑表单  走后台
            this.saveAccountInfo = function (form, flag) {

                //进行入库操作
                var sfn = function (data, status) {
                    var flag = data.result;
                    if (1 == flag && (GUID.getUserInfo() ? GUID.getUserInfo().userAccount : '') != ($("#userAccount").val() ? $("#userAccount").val() : "")) {
                        DF.toast.error("账号已存在");
                        $("#userAccount").focus();
                    } else {
                        form.validate({
                            debug: true
                        });
                        /*Form校验*/
                        if (form.valid()) {
                            $("#userIdN").val($("#userId").val());
                            var data = form.serializeArray();
                            var url =
                                'service/securityUserT/updateAccountInfo/' + $("#userId").val() + '/' + data[1].value + "/" + data[2].value;
                            var sfn = function (data, status) {
                                KendoTreeView.findAndTriggerClick(treeObjs.getNodes(), data.result);
                                DF.toast.success("账号已更新");
                            };
                            /*保存账号信息的后台AJAX请求*/
                            DF.ajax({
                                data: data,
                                type: 'POST',
                                url: url,
                                success: sfn
                            });
                        }
                    }
                };
                DF.ajax({
                    url: 'service/securityUserT/userMulCheck/' + $("#userAccount").val(),
                    type: 'GET',
                    success: sfn
                });
            }
        }
        window.WIZARD = new wizard();


    }

    )(jQuery, window);


handleTextBox = function (callback) {
    return function (e) {

        if (e.type != "keypress" || kendo.keys.ENTER == e.keyCode) {
            callback(e);
        }
    };
}

/*检测邮箱，是否为163、126、sina、yahoo.com.cn*/
function checkMail(obj, userId) {
    //规则校验
    if (obj.val()) {
        // if (!obj.val()) {
        //     return false;
        // }
        var mail = obj.val().split("@")[1];
        if (mail) {
            if (mail.indexOf("163.com") >= 0 || mail.indexOf("126.com") >= 0 || mail.indexOf("sina.com") >= 0 || mail.indexOf("yahoo.com.cn") >= 0) {
                obj.val("");
                DF.toast.error('禁止使用"163"、"126"、"sina"、"yahoo"邮箱，请更换');
                return false;
            }
        }
        function sfn(data, status) {
            if (1 == data.result) {
                DF.toast.error("邮箱已存在");
                obj.val("").focus();
                return false;
            }
            return true;
        }

        DF.ajax({
            url: 'service/securityUserT/checkEmail',
            type: 'POST',
            async: false,
            data: {email: obj.val() + "splitChar" + userId},
            success: sfn
        });
    }

}

function checkBoxChange() {
    GUID.setIsChange(true);
}
