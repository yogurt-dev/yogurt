/**
 * Created by dell on 2016/2/3.
 */

//webscoketManager组件
(function () {
    var vertx = function ($) {
        var ENV = {
            MANAGER: "MANAGER",
            SERVER: "SERVER"
        };
        this.address = {}
        this.websocket = {};
        this.handles = {};//address fn
        /**
         *
         * @param address 客户端地址
         * @param fn   接收消息方法
         */
        this.registerHandler = function (address, fn, env) {
            for (var i in ENV) {
                if (env && env != ENV[i]) {
                    continue;
                }
                if (!this.handles[ENV[i]]) {
                    this.handles[ENV[i]] = {};
                }
                if (this.handles[ENV[i]][address]) {
                    alert(address + "已被注册");
                    return;
                }
                var handle = {"key": address, "fn": fn};
                this.handles[ENV[i]][address] = handle;
            }
        };
        /**
         * 初始化vertx websocket
         */
        this.initwebsocket = function () {
            var me = this;
            //注册服务端
            DF.ajax({
                url: "vertxRegister/init",
                success: function (resp) {
                    console.log("vertxRegister/init", resp);
                    var result = resp.result;
                    me.address = result;
                    for (var i in ENV) {
                        me._initWebsocket(ENV[i]);
                    }
                }
            });
        };
        this._initWebsocket = function (env) {
            //注册客户端
            var me = this;
            var address = me.address[env];
            var eb = new EventBus(location.protocol + "//" + address + "/eventbus");
            var handles = this.handles;
            eb.onopen = function () {
                this.reconnectCount = 0;
                //遍历并注册handles
                for (var ii in handles[env]) {
                    var handle = handles[env][ii];
                    //注册接收信息方法
                    console.log(env, ii, handle["key"], "zc")
                    eb.registerHandler(handle["key"] + "_Client", function (error, message) {
                        console.log(env, ii, handle["key"])
                        handle["fn"] && handle["fn"](message.body ? message.body : "")
                    });
                    //注册send方法
                    /**
                     * 发送消息
                     * @param message  消息
                     */
                    handle.send = function (message) {
                        me.websocket[env].send(handle["key"] + "_Server", message);
                    }
                }
            };
            eb.onclose = function (e) {
                console.log(env, "socket连接断开zc")
                console.log("socket连接断开", e);
                me.reconnect(env);
            };
            me.websocket[env] = eb;
        };
        /**
         * 根据地址获得客户端handle
         * @param handleAddress   地址
         * @returns {*}
         */
        this.getHandle = function (env, handleAddress) {
            return this.handles[env][handleAddress];
        };
        this.reconnectCount = 0;
        /**
         * 断线重连  最多连续重试三次
         */
        this.reconnect = function (env) {
            if (this.reconnectCount >= 3) {
                return;
            }
            this._initWebsocket(env);
            this.reconnectCount++;
        }
    };
    window._vertx = new vertx($);
})(jQuery);
