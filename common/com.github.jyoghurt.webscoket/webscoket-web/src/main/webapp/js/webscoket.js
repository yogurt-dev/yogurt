/**
 * Created by dell on 2016/2/3.
 */

//webscoketManager组件
(function () {
    var webscoketManager = function ($) {
        //scoket属性集合
        /*scoket的连接方法池*/
        this.scoketHandleMessageList = [];
        /*重连计数器*/
        this.reconnectionCounter = 0;
        //scoket对象
        this.webScoket;
        //scoket方法集合
        this.initWebscoket = function () {
            var me = this;
            var pathname = document.location.pathname;
            var path = pathname.substring(1, pathname.length);
            path = path.substring(0, path.indexOf('/'));
            var header = document.location.protocol == "http:" ? 'ws://' : 'wss://';
            var URI = document.location.host + '/' + path + '/';
            URI.replace("//", "/");
            URI = header + URI;
            if ('WebSocket' in window) {
                me.webScoket = new WebSocket(URI + 'webSocketServer');
            } else if ('MozWebSocket' in window) {
                me.webScoket = new MozWebSocket(URI + 'webSocketServer');
            }
            if (me.webScoket) {
                me.webScoket.onopen = function (message) {
                    console.log('连接成功!');
                }
                //连接失败
                me.webScoket.onerror = function (message) {
                    console.log('失连接败!');
                }
                //连接关闭
                me.webScoket.onclose = function (message, state) {
                    me.reconnection();
                    console.log('连接关闭!' + "closeNum:" + me.reconnectionCounter);
                }
                //消息接收
                me.webScoket.onmessage = function (message) {
                    var msgObj = JSON.parse(message.data)
                    var scoketType = msgObj && msgObj.scoketType;
                    for (var i in me.scoketHandleMessageList) {
                        for (var ii in me.scoketHandleMessageList[i]) {
                            if (scoketType == ii) {
                                me.scoketHandleMessageList[i][ii](message);
                            }
                        }
                    }
                }
            }
        }
        /*获取scoket方法*/
        this.getWebScoket = function () {
            return this.webScoket;
        }
        this.setScoketHandleMessageList = function (Module, fn) {
            var HandleMessageFnMap = {}
            HandleMessageFnMap[Module] = fn;
            this.scoketHandleMessageList.push(HandleMessageFnMap)
        }
        this.getScoketHandleMessageList = function () {
            return this.scoketHandleMessageList;
        }

        /*scoket断线重连的方法*/
        this.reconnection = function () {
            var me = this;
            me.reconnectionCounter++;
            if (me.reconnectionCounter <= 2) {
                me.initWebscoket();
            }
        }
    }
    //注册webscoket
    window._WebscoketManager = new webscoketManager($);
})(jQuery)
