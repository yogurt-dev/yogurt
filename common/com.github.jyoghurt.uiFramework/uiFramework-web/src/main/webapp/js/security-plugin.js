
/**
 * Created by Administrator on 2015/11/23.
 */
(function ($) {
    //初始化系统内容 可能为空
    $('body').append($('<script src="js/application.js">'));
    //初始化组件内容 可能为空
    DF.getTmpl('/common/component', function (component) {
        $('#df_component').append(component);
    });
})(jQuery);
