/* =========================================================
 *application.js
 *车险帮应用 启动 JS
 *包括获取权限、加载菜单、消息等、注销登录的功能
 *
 * ========================================================= */
////jQuery(window).trigger('resize'); 自适应


;
/*生成公告手机页面*/
(function ($, window, undefined) {
    //注册事件
    function registPageEvent() {
        $('body').append('<script src="build/js/DFCallback.js">');
        public_vars && public_vars.$pageLoadingOverlay && public_vars.$pageLoadingOverlay.addClass('loaded');
        setTimeout(function () {
            DF.gotoPage('/document');
        }, 200);
    }
    registPageEvent();
})(jQuery, window);

