/**
 * Created by dengguangyi on 2015/12/17.
 */
;(function ($, window, undefined) {
    var hdl ;
    var devMode;
    localStorage.setItem('__devMode','false');
    function rotationIfSec(devMode){
        console.log('等待..');
        if(typeof __getSecurityRoleBtns==='function'){
            DF.initApp(devMode);
            console.log('donkish框架初始化完成..');
            if(hdl){
                clearInterval(hdl);
            }
        }
    };
    function covertString2Boolean(flag){
        return flag ==='true';
    }
    //获取当前缓存的开发模式
    if(window.localStorage){
        //如果没有缓存
        if(localStorage.__devMode===undefined){
            //获取后台的当前系统开发模式参数
            $.ajax({
                url:'appconfig/getDevMode',
                success:function(data){
                    if(data.result && data.result.devMode){
                        devMode = covertString2Boolean(data.result.devMode);
                        localStorage.__devMode = devMode;
                        hdl = setInterval(function(){rotationIfSec(devMode)},1000);
                    }
                }
            });
        }else{
            devMode = covertString2Boolean(localStorage.__devMode);
            hdl = setInterval(function(){rotationIfSec(devMode)},300);
        }
    }
})(jQuery,window);