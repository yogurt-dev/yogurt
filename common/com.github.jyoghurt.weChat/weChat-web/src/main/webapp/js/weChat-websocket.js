/**
 * Created by zhangjl on 2015/9/10.
 */
;
(function ($, window, undefined) {
//聊天功能变量
//好友列表信息
    var friendListMsg;
//websocket对象
    var websocket=_WebscoketManager.getWebScoket();
    var myName;
    var appIdD;//订阅号
    var appIdF;//服务号
    var appIdQ;//企业号
    /*重连计数器*/
    var reconnectionCounter = 0;
    //聊天分组变量名称
    var chatName = "来自微信的消息";
    /*tab类型*/
    var tabType = "online";
    /*当前用户app的分组数据*/
    var appGroup;
    /*搜索条件*/
    var searchAppName = "null";
    var searchNickName = "null";
    /*对话框常量*/
    /*chat闪烁池 当flashCounter为0时进行添加否则保持闪烁*/
    var flashMap = {};
//初始化聊天(入口函数)
    function initTheChatWindow() {
        //创建好友分组
        createGroup(chatName, false);
        //初始化websocket
        initWebScoket();
        //获取自己
        getMe();
        //初始化tab
        initTab();
        /*初始化搜索*/
        initSearch();
    }
//断开后重新注册scoket方法
    function registerScoket(){
        websocket=DF.getWebScoket();
    }
//初始化websocket
    function initWebScoket() {
        _WebscoketManager.setScoketHandleMessageList("WeChatScoketBean",//解析接收的消息
            function onmessage(message) {
                var conversation = JSON.parse(message.data);
                if (!conversation.type) {
                    var groupName = chatName
                    if ($('.chat-group a[databind="' + conversation.fromUserId + '"][datagroup="' + groupName + '"]').length == 0) {
                        createWin(chatName, conversation.fromUserName, conversation.fromUserId, false, conversation.appId, conversation.appsecret, decodeURIComponent(conversation.appName));
                    }
                    firstPlaceFlash(conversation.fromUserId, groupName);
                    if (conversation.ifreply == "0") {
                        newMsgFlash(conversation.fromUserId, groupName);
                    }
                    receiveContent(conversation, groupName);
                } else {
                    var conversation = JSON.parse(message.data);
                    var groupName = chatName
                    removeFlash(conversation.fromUserId, groupName);
                    receiveContent(conversation, groupName, true);
                }
            });
    }

    function initTab() {
        $('a.chat[datatype="chatRead"]').on('click', function () {
            tabType = "online";
            $('div.groupDiv').each(function () {
                var me = $(this);
                if (me.attr('databind') == chatName) {
                    me.show();
                } else {
                    me.hide();
                }

            });
            if (!$(this).find('i').hasClass("web_wechat_tab_chat_hl")) {
                $(this).find('i').toggleClass("web_wechat_tab_chat_hl");
            }
            $('a.chat[datatype="chatHis"]').find('i').removeClass("web_wechat_tab_chat_hl");
            $('.chat-conversation').removeClass('is-open');
        })
        $('a.chat[datatype="chatHis"]').on('click', function () {
            tabType = "history";
            $('div.groupDiv').each(function () {
                var me = $(this);
                if (me.attr('databind') == chatName) {
                    me.hide();
                } else {
                    me.show();
                }
            });
            if (!$(this).find('i').hasClass("web_wechat_tab_chat_hl")) {
                $(this).find('i').toggleClass("web_wechat_tab_chat_hl");
            }
            $('a.chat[datatype="chatRead"]').find('i').removeClass("web_wechat_tab_chat_hl");
            $('.chat-conversation').removeClass('is-open');
        })
        $('a.chat[datatype="refurbish"]').on('click', function () {
            refurbish();
        })
    }

    function refurbish() {
        searchAppName = "null";
        searchNickName = "null";
        $('[name="weChatSearch"]').val("");
        flashMap = {};
        removeFlash();
        $('div.groupDiv').remove();
        //初始化联系人
        initChat(0, 5, appIdD, appIdF, appIdQ);
        //初始化历史联系人
        getGroup(appIdD, appIdF, appIdQ);
    }

    function initSearch() {
        $('[name="weChatSearch"]').keypress(function (event) {
            if (event.keyCode != 13) {
                return;
            }
            var me = $(this);
            var meValue = me.val();
            if (!meValue || meValue == "" || meValue == null) {
                refurbish();
                return;
            }
            searchAppName = meValue.indexOf(".") > -1 ? meValue.split(".")[1] == "" ? "null" : meValue.split(".")[1] : "null";
            searchNickName = meValue.indexOf(".") > -1 ? meValue.split(".")[0] == "" ? "null" : meValue.split(".")[0] : meValue;
            if (tabType == "online") {
                onlineSearch(searchAppName, searchNickName);
            } else {
                hisSearch(searchAppName, searchNickName);
            }
        })
    }

    function onlineSearch(appName, nickName) {
        $('div.groupDiv').each(function () {
            var me = $(this);
            if (!me.is(':hidden')) {
                me.remove();
            }
        })
        $.ajax({
            url: 'conversation/search/' + tabType + '/' + appName + "/" + nickName,
            success: function (data) {
                for (var i in data.result) {
                    createWin(chatName, data.result[i].fromUserName, data.result[i].fromUserId, false, data.result[i].appId, data.result[i].appsecret, decodeURIComponent(data.result[i].appName), data.result[i].img && data.result[i].img);
                    if (data.result[i].ifreply == "0") {
                        newMsgFlash(data.result[i].fromUserId, chatName, false);
                    }
                }
                changeNum(chatName, "group", chatName, data.result.length);
            }
        });
    }

    function hisSearch(appName, nickName) {
        $('div.groupDiv').each(function () {
            var me = $(this);
            if (!me.is(':hidden')) {
                me.remove();
            }
        })
        $.ajax({
            url: 'conversation/search/' + tabType + '/' + appName + "/" + nickName,
            success: function (data) {
                for (var i in data.result) {
                    createWin(data.result[i].appName, data.result[i].fromUserName, data.result[i].fromUserId, true, data.result[i].appId, data.result[i].appsecret, decodeURIComponent(data.result[i].appName), data.result[i].img && data.result[i].img);
                }
                changeNum(data.result[i].appName, "group", data.result[i].appName, data.result.length);
            }
        });
    }

//创建联系人窗口
    function createWin(groupName, personName, OPENID, ifHis, appId, appsecret, showName, img) {
        //创建好友分组
        createGroup(groupName, ifHis, appId);
        //创建好友
        createFriend(groupName, personName, OPENID, ifHis, appId, appsecret, showName);
        //创建新对话框
        createDiaLogBox(groupName, personName, OPENID, ifHis, appId, appsecret, showName, img);
        //选择某人聊天后的点击事件

    }

//创建分组
    function createGroup(groupName, ifHis, appId) {
        var groupTrueName = ifHis ? "" : "";
        //判断是否存在该分组若已经存在则添加人员 否则新建分组
        if ($(".groupDiv[databind=" + groupName + "]").length == 0) {
            $(".chat-inner").append('<div class="groupDiv" databind=' + groupName + '></div>')
            $("div.groupDiv[databind='" + groupName + "']").append('<div class="chat-group suspend form-group-separator" databind=' + groupName + '></div>').append('<button class="btn btn-white btn-load btn-block" datatype="group" datagroup="' + groupName + '"databind="' + groupName + '"> <i class="fa-bars"></i>加载更多</button>');
            //添加收缩展开事件
            $('.groupDiv[databind=' + groupName + '] strong').on('click', function () {
                contraction($('.chat-group[databind=' + groupName + ']'));
            })
            $(".btn.btn-white.btn-load.btn-block[databind='" + groupName + "'][datatype='group']").on('click', function (even) {
                if (ifHis) {
                    initHisChatByGroup(groupName, appId, $('.chat-group.suspend[databind="' + groupName + '"]').find('a').length, $('.chat-group.suspend[databind="' + groupName + '"]').find('a').length + 5);
                } else {
                    initChat($('.chat-group.suspend[databind="' + groupName + '"]').find('a').length, $('.chat-group.suspend[databind="' + groupName + '"]').find('a').length + 5, appIdD, appIdF, appIdQ);
                }
            })
            if (groupName != chatName) {
                $('.chat-group[databind=' + groupName + ']').append('<strong databind=' + groupName + '>' + groupName + '<i class="fa-angle-down" databind="groupName"></i>' + groupTrueName + '</strong>');
            } else {
                $('.chat-group[databind=' + groupName + ']').append('<strong databind=' + groupName + '></strong>');
            }
            //默认隐藏分组
            if (groupName != chatName && tabType != "history") {
                $('div.groupDiv[databind="' + groupName + '"]').hide()
            }
            if (tabType == "history") {
                $('div.groupDiv[databind="' + chatName + '"]').hide()
            }
        }
    }

//创建好友
    function createFriend(groupName, personName, OPENID, ifHis, appId, appsecret, showName) {
        var spanClass = ifHis ? "user-status is-offline" : "user-status is-online";
        //若为来自微信的消息则显示showName而历史联系人各级showName分组无需显示showName
        var showName = ifHis ? personName : personName + "&nbsp;·&nbsp;" + showName;
        //若好友已经存在则无需创建
        if ($(".chat-group.suspend a[databind='" + OPENID + "'][datagroup='" + groupName + "']").length == 0) {
            $(".chat-group[databind='" + groupName + "']")
                .append('<a href="#" databind="' + OPENID + '" datagroup="' + groupName + '" dataAppId="' + appId + '"><span class="' + spanClass + '" databind=' + OPENID + '" datagroup=' + groupName + '></span> <em>' + showName + '</em></a>');
            //添加点击好友事件
            clickFriend(OPENID, groupName);
        }
    }

//创建对话框详细内容
    function createDiaLogBox(groupName, personName, OPENID, ifHis, appId, appsecret, appName, img) {
        //判断对话框是否存在若存在则不创建 否则创建新对话框
        if ($(".chat-conversation[databind='" + OPENID + "'][datagroup='" + groupName + "']").length <= 0) {
            var spanClass = ifHis ? "user-status is-offline" : "user-status is-online";
            //根据创建与某人的对话框
            $('.chat-inner').after('<div class="chat-conversation" databind="' + OPENID + '" datagroup="' + groupName + '" dataImg="' + img + '"></div>');
            //根据用户信息获取对话框对象
            var $chat_conversation = $(".chat-conversation[databind='" + OPENID + "'][datagroup=" + groupName + "]");
            //创建对话框头部信息
            $(".chat-conversation[databind='" + OPENID + "'][datagroup=" + groupName + "]").append('<div class="conversation-header" style="background-color: #eee;border-bottom: 1px solid #d6d6d6;" databind="' + OPENID + '" datagroup="' + groupName + '"></div>')
            $(".conversation-header[databind='" + OPENID + "'][datagroup='" + groupName + "']").append('<a href="#" class="conversation-close">&times</a>')
                .append('<span class="' + spanClass + '" datagroup="' + groupName + '" databind="' + OPENID + '"></span>')
                .append('<span class="display-name">' + personName + '</span>')
            //创建聊天内容
            $(".chat-conversation[databind='" + OPENID + "'][datagroup=" + groupName + "]")
                .append('<ul class="conversation-body" style="background-color: #eee;overflow-x: hidden;" databind="' + OPENID + '" datagroup="' + groupName + '"></ul>')
            $(".chat-conversation ul[databind=" + OPENID + "][datagroup=" + groupName + "]").append('<button class="btn btn-white  btn-load btn-block" style="background-color: #eee;" datatype="li" databind="' + OPENID + '" datagroup="' + groupName + '"> <i class="fa-bars"></i>加载更多</button>');
            //添加历史记录点击事件
            $(".btn.btn-white.btn-load.btn-block[databind='" + OPENID + "'][datatype='li'][datagroup=" + groupName + "]").on('click', function (even) {
                receiveConversation(OPENID, groupName);
            })
            var iClass = "fa-flyway.wechat";
            //创建对话框
            $(".chat-conversation[databind='" + OPENID + "'][datagroup=" + groupName + "]").append('<div class="chat-textarea" databind="' + OPENID + '" datagroup="' + groupName + '"> </div>');

            //添加关闭事件
            $(".conversation-close").on('click', function (ev) {
                ev.preventDefault();
                $chat_conversation.removeClass('is-open');
            });
            if (!ifHis) {
                $('.chat-textarea[databind=' + OPENID + '][datagroup=' + groupName + ']')
                    .append('<textarea class="form-control autogrow" placeholder="请输入消息&nbsp;(ctrl+回车发送消息)" style="height:52px;background-color: #eee;border-top: 1px solid #d6d6d6;" databind="' + OPENID + '" datagroup="' + groupName + '"></textarea><i class=' + iClass + ' databind="' + OPENID + '" datagroup="' + groupName + '"></i>');
                $('.chat-textarea[databind=' + OPENID + '][datagroup=' + groupName + ']').keypress(function (event) {
                    if (event.ctrlKey && (event.keyCode == 13 || event.keyCode == 10)) {
                        if (websocket&&websocket.readyState == 3) {
                            registerScoket();
                            return;
                        }
                        var conversation = {};
                        //封装接收的数据
                        var myDate = new Date().Format("yyyy-MM-dd HH:mm:ss");
                        conversation.createDateTime = myDate.toLocaleString();
                        conversation.fromUserId = OPENID;
                        conversation.fromUserName = personName;
                        conversation.context = $(".chat-conversation textarea[databind=" + OPENID + "][datagroup=" + groupName + "]")[0].value;
                        conversation.receive = "0";
                        conversation.ifreply = "1";
                        conversation.img = $('div.chat-conversation[f="' + chatName + '"][databind="' + OPENID + '"]').attr('dataImg');
                        saveConversation(conversation);
                        conversation.fromUserName = myName;
                        receiveContent(conversation, groupName, true);
                        conversation.appId = appId;
                        conversation.appsecret = appsecret;
                        conversation.type = "radio";
                        conversation.scoketType="WeChatScoketBean";
                        websocket.send(JSON.stringify(conversation));
                        removeFlash(OPENID, chatName);
                        //接收后消息置空
                        $(".chat-conversation textarea[databind=" + OPENID + "][datagroup=" + groupName + "]")[0].value = "";
                        //回复消息需要根据当前联系人个数刷新联系人列表
                        refurbishChat($('.chat-group.suspend[databind="' + chatName + '"]').find('a').length, OPENID, appIdD, appIdF, appIdQ);
                    }
                });
                //监听文本窗口发送事件
                $('.chat-textarea[databind=' + OPENID + '][datagroup=' + groupName + '] i').on('click', function () {
                    if (websocket&&websocket.readyState == 3) {
                        registerScoket();
                        return;
                    }
                    var conversation = {};
                    //封装接收的数据
                    var myDate = new Date().Format("yyyy-MM-dd hh:mm:ss");
                    conversation.createDateTime = myDate.toLocaleString();
                    conversation.fromUserId = OPENID;
                    conversation.fromUserName = personName;
                    conversation.context = $(".chat-conversation textarea[databind=" + OPENID + "][datagroup=" + groupName + "]")[0].value;
                    conversation.receive = "0";
                    conversation.ifreply = "1";
                    conversation.img = $('div.chat-conversation[datagroup="' + chatName + '"][databind="' + OPENID + '"]').attr('dataImg');
                    saveConversation(conversation);
                    conversation.fromUserName = myName;
                    receiveContent(conversation, groupName, true);
                    conversation.appId = appId;
                    conversation.appsecret = appsecret;
                    conversation.type = "radio";
                    conversation.scoketType="WeChatScoketBean";
                    websocket.send(JSON.stringify(conversation));
                    //取消闪烁
                    removeFlash(OPENID, chatName);
                    //接收后消息置空
                    $(".chat-conversation textarea[databind=" + OPENID + "][datagroup=" + groupName + "]")[0].value = "";
                    //回复消息需要根据当前联系人个数刷新联系人列表
                    refurbishChat($('.chat-group.suspend[databind="' + chatName + '"]').find('a').length, OPENID, appIdD, appIdF, appIdQ);
                });
            }
        }
    }

//接收历史记录java.io.IOException: Negative seek offset
    function receiveRecords(conversation, groupName, resetScroll) {
        debugger;
        var liClass = conversation.receive == "1" ? " class='style '" : " class='style '";
        //若有消息则在消息上面添加历史记录 否则在ul下加载历史记录
        if ($(".conversation-body li[databind='" + conversation.fromUserId + "'][datagroup='" + groupName + "']").length > 0) {
            $($(".conversation-body li[databind='" + conversation.fromUserId + "'][datagroup='" + groupName + "']")[0]).before("<li databind=" + conversation.fromUserId + liClass + " datagroup='" + groupName + "'></li>");
        } else {
            $(".chat-conversation ul[databind='" + conversation.fromUserId + "'][datagroup='" + groupName + "']").append("<li databind=" + conversation.fromUserId + liClass + " datagroup='" + groupName + "'></li>");
        }
        createWeChatTalk($(".conversation-body li[databind='" + conversation.fromUserId + "'][datagroup='" + groupName + "']").first(), conversation);
        if (resetScroll) {
            resetTextScroll($(".chat-conversation ul[databind='" + conversation.fromUserId + "'][datagroup='" + groupName + "']"));
        }
    }

    /*构造微信对话框*/
    function createWeChatTalk($dom, conversation, receive) {
        var flag = conversation.receive == "1" ? false : true;
        debugger;
        var receiveClass = flag ? "bubble_primary right" : "bubble_default left";
        var messageClass = flag ? "me" : "you";
        var preClass = flag ? "bubble_primary" : "bubble_default";
        var showHead = $('div.chat-conversation[datagroup="' + chatName + '"][databind="' + $dom.attr('databind') + '"]').attr('dataImg');
        if (!showHead || showHead == "undefined") {
            showHead = conversation.img != "" ? conversation.img : "./img/wx/default.jpg";
        }
        var head = flag ? "./img/wx/online.jpg" : showHead;
        var createDateTime = ""
        if (typeof(conversation.createDateTime) == "string") {
            createDateTime = conversation.createDateTime;
        } else if (typeof(conversation.createDateTime) == "number") {
            createDateTime = new Date(conversation.createDateTime).Format("yyyy-MM-dd HH:mm:ss");
        }
        var me = $dom;
        me.append('<div class="message ' + messageClass + '"></div>')//对话框外层
        me.find('.message').append('<p ng-if="message.MMTime" class="message_system"><span class="content ng-binding">' + createDateTime + '</span></p>')//时间
        me.find('.message_system').after('<div class="content"></div>');//聊天内容主体
        me.find('.message_system').after('<img class="avatar"  src="' + head + '">');
        if (receive) {
            me.find('div.content').append('<h4 class="nickname">' + conversation.fromUserName + '</h4>')
        } else {
            if (flag) {
                me.find('div.content').append('<h4 class="nickname">' + conversation.founderName + '</h4>')
            }
        }
        me.find('div.content').append('<div class="bubble ' + receiveClass + '"></div>')//小箭头
        me.find('.bubble').append('<pre class="' + preClass + ' pre" style="margin: 5px">' + conversation.context + '</pre>');//具体内容
    }

//聊天框中接收聊天内容及时间
    function receiveContent(conversation, groupName, receive) {
        var liClass = conversation.receive == "1" ? " class='style '" : " class='style '";
        $(".chat-conversation ul[databind='" + conversation.fromUserId + "'][datagroup='" + groupName + "']").append("<li databind=" + conversation.fromUserId + liClass + " datagroup='" + groupName + "'></li>");
        var ArrNum = $(".conversation-body li[databind='" + conversation.fromUserId + "'][datagroup='" + groupName + "']").length - 1;
        createWeChatTalk($($(".conversation-body li[databind='" + conversation.fromUserId + "'][datagroup='" + groupName + "']")[ArrNum]), conversation, receive);
        resetTextScroll($(".chat-conversation ul[databind='" + conversation.fromUserId + "'][datagroup='" + groupName + "']"));
    }

//重置对话框滚动条
    function resetTextScroll($this) {
        if ($this && $this.get(0)) {
            $this.get(0).scrollTop = $this.get(0).scrollHeight;
        }
    }

//保存聊天记录
    function saveConversation(conversation) {
        $.ajax({
            data: JSON.stringify(conversation),
            type: 'post',
            url: 'conversation',
            contentType: 'application/json',
            dataType: 'json',
            success: function (data) {

            }
        })
    }

//获取聊天记录
    function receiveConversation(OPENID, groupName, resetTextScroll) {
        $.ajax({
            data: {
                fromUserId: OPENID,
                expandTime: $('.conversation-body li[databind="' + OPENID + '"][datagroup="' + groupName + '"] span').length > 0 ? parseToDate($('.conversation-body li[databind=' + OPENID + '][datagroup=' + groupName + '] span').first().text()) : new Date(),
                iDisplayLength: 5
            },
            type: 'get',
            url: 'conversation/list/',
            dataType: 'json',
            success: function (data) {
                for (var i in data.result.data) {
                    receiveRecords(data.result.data[i], groupName, resetTextScroll);
                }
                changeNum(OPENID, "li", groupName, data.result.data.length);
            }
        });
    }

//分组收缩展开方法
    function contraction(group) {
        //如果是收缩标示则改为展开标示并展开，反之亦然
        if (group.find('i')[0].className == "fa-angle-down") {
            $(group.find('i')[0]).removeClass("fa-angle-down");
            $(group.find('i')[0]).toggleClass("fa-angle-up");
            group.find('a').fadeOut("normal");
        } else {
            $(group.find('i')[0]).removeClass("fa-angle-up");
            $(group.find('i')[0]).toggleClass("fa-angle-down")
            group.find('a').fadeIn("normal");
        }

    }

//修改用户状态
    function updateStatus(OPENID, status) {
        $(".conversation-header small")[0].textContent = status;
    }


//刷新来自微信的消息窗口
    function refurbishChat(limit, OPENID, appIdD, appIdF, appIdQ) {
        $.ajax({
            url: 'conversation/refurbishChat/' + tabType + '/' + searchAppName + "/" + searchNickName,
            success: function (data) {
                for (var i in data.result) {
                    createWin(chatName, data.result[i].fromUserName, data.result[i].fromUserId, false, data.result[i].appId, data.result[i].appsecret, decodeURIComponent(data.result[i].appName), data.result[i].img && data.result[i].img);
                    if (data.result[i].ifreply == "0") {
                        newMsgFlash(data.result[i].fromUserId, chatName, false);
                    }
                }
                changeNum(chatName, "group", chatName, data.result.length);
            }
        })
    }

    /**/
//初始化来自微信的消息聊天窗口
    function initChat(limitStart, limitEnd, appIdD, appIdF, appIdQ) {
        $.ajax({
            data: {},
            type: 'get',
            async: false,
            url: 'conversation/initChat/' + limitStart + "/" + limitEnd + "/" + appIdD + "/" + appIdF + "/" + appIdQ,
            dataType: 'json',
            success: function (data) {
                for (var i in data.result) {
                    createWin(chatName, data.result[i].fromUserName, data.result[i].fromUserId, false, data.result[i].appId, data.result[i].appsecret, decodeURIComponent(data.result[i].appName), data.result[i].img && data.result[i].img);
                    if (data.result[i].ifreply == "0") {
                        newMsgFlash(data.result[i].fromUserId, chatName, false);
                    }
                }
                changeNum(chatName, "group", chatName, data.result.length);
            }
        });
    }

//初始化所有历史聊天窗口
    function initHisChat(group, limitStart, limitEnd) {
        for (var i in group) {
            initHisChatByGroup(decodeURIComponent(group[i].appName), group[i].appId, limitStart, limitEnd);
        }

    }

//初始化单组历史聊天
    function initHisChatByGroup(groupName, appId, limitStart, limitEnd) {
        $.ajax({
            data: {},
            type: 'get',
            url: 'conversation/initHisChat/' + limitStart + "/" + limitEnd + "/" + appId,
            dataType: 'json',
            success: function (data) {
                for (var i in data.result) {
                    createWin(groupName, data.result[i].fromUserName, data.result[i].fromUserId, true, data.result[i].appId, data.result[i].appsecret, decodeURIComponent(data.result[i].appName), data.result[i].img && data.result[i].img);
                }
                changeNum(groupName, "group", groupName, data.result.length);
            }
        });
    }

//获取分组信息
    function getGroup(appIdD, appIdF, appIdQ) {
        $.ajax({
            data: {},
            type: 'get',
            url: 'relevanceChat/findGroup/' + appIdD + "/" + appIdF + "/" + appIdQ,
            dataType: 'json',
            success: function (data) {
                appGroup = data.result;
                initHisChat(appGroup, 0, 5);
            }
        });
        return appGroup;
    }


//改变加载更多按钮状态 若不足5条则显示为没有更多
    function changeNum(databind, datatype, groupName, num) {
        if ($('button[databind="' + databind + '"][datatype="' + datatype + '"][datagroup="' + groupName + '"]')[0]) {
            if (num >= 5) {
                $('button[databind="' + databind + '"][datatype="' + datatype + '"][datagroup="' + groupName + '"]')[0].textContent = "加载更多";
            } else {
                $('button[databind="' + databind + '"][datatype="' + datatype + '"][datagroup="' + groupName + '"]').hide();
            }
        }
    }

//将闪动置于第一位
    function firstPlaceFlash(OPENID, groupName) {
        //若有该好友则删除原a标签 至于第一位a标签
        if ($('a[databind="' + OPENID + '"][datagroup="' + groupName + '"]').length > 0) {
            var $this = $('a[databind="' + OPENID + '"][datagroup="' + groupName + '"]');
            $('a[databind="' + OPENID + '"][datagroup="' + groupName + '"]').remove();
            $('strong[databind="' + chatName + '"]').after($this);
            clickFriend(OPENID, groupName);
        }
    }

//新消息闪动
    function newMsgFlash(OPENID, groupName) {
        var flag = false;
        for (var k in flashMap) {
            flag = true;
        }
        //若池子中没有该用户的闪烁事件 则添加 否则不添加
        var $this = $('a[databind="' + OPENID + '"][datagroup="' + groupName + '"] ').find('span');
        if (!$this.hasClass('point_flash')) {
            $this.toggleClass('point_flash');
        }
        flashMap[OPENID] = 1
        //当flashCounter为0时进行添加否则保持闪烁
        if (!flag) {
            $('a[data-toggle="chat"] i.fa-comments-o').toggleClass('point_flash_chat');
            $('a[data-toggle="chat"] i.fa-comments-o').toggleClass('active_comments');
        }
    }

//取消闪动
    function removeFlash(OPENID, groupName) {
        delete flashMap[OPENID];
        var flag = false;
        for (var k in flashMap) {
            flag = true;
        }
        $('a[databind="' + OPENID + '"][datagroup="' + groupName + '"] ').find('span').removeClass('point_flash');
        //若计数器为0则没有新消息否则取消自身闪动而chat继续闪动
        if (!flag) {
            $('.fa-comments-o').removeClass('point_flash_chat');
            $('a[data-toggle="chat"] i.fa-comments-o').removeClass('active_comments');
        }
    }

//点击好友事件
    function clickFriend(OPENID, groupName) {
        $(".chat-group a[databind='" + OPENID + "'][datagroup='" + groupName + "']").on('click', function (ev) {
            //关闭其他对话窗口 打开点击的对话窗口
            if ($('.chat-conversation.is-open').length > 0) {
                $('.chat-conversation').removeClass('is-open');
            } else {
                $(".chat-conversation[databind='" + OPENID + "'][datagroup='" + groupName + "']").toggleClass('is-open');
                $(".chat-conversation textarea[databind='" + OPENID + "'][datagroup='" + groupName + "']").trigger('autosize.resize').focus();
                //不足5条则没经过初始化 对聊天记录进行初始化
                if ($('.conversation-body[databind="' + OPENID + '"][datagroup="' + groupName + '"] li').length < 5) {
                    receiveConversation(OPENID, groupName, true);
                }
            }
            resetTextScroll($(".chat-conversation ul[databind='" + OPENID + "'][datagroup='" + groupName + "']"));
        });
    }

    /*获取用户信息及其相关appId*/
    function getMe() {
        $.ajax({
            type: 'get',
            url: 'conversation/getMe',
            dataType: 'json',
            success: function (data) {
                myName = data.result.user.userName;
                appIdD = data.result.unit.appId;
                appIdF = data.result.unit.appIdF;
                appIdQ = data.result.unit.appIdQ;
                //初始化联系人
                initChat(0, 5, appIdD, appIdF, appIdQ);
                //初始化历史联系人
                getGroup(appIdD, appIdF, appIdQ);
            }
        });
    };
//  注册事件
    jQuery(document).ready(function ($) {
        //点击微信图标弹出聊天窗口 并调用创建好友列表方法
        $(".navbar-inner ul").on('click', function (even) {
            var myHeight = $('.chat-inner.ps-container').get(0).scrollHeight;
            $('.chat-inner.ps-container').css('max-height', myHeight);
        })
    });
//String转Date
    function parseToDate(string) {
        string = string.replace(/-/g, "/");
        return new Date(string);
    }

    Date.prototype.Format = function (fmt) { //author: meizz
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
    //启动
    initTheChatWindow();
})(jQuery, window);