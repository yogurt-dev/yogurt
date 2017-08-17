/**
 * Created by Administrator on 2016/4/15.
 */
    //基于 jq的步骤插件
(function (factory) {
    if (typeof define === 'function' && define.amd) {
        define(['jquery'], factory);
    } else {
        factory(jQuery);
    }
}(function ($) {
    var stepBar = function(options, el){
        if(el.length===0){
            return console.warn('no selector called ' + el.selector);
        }
        //validate options
        if(!options || !options.data){
            return console.error('1 argument required or must be object with property "data"');
        }
        var defaults = {
                style:'',
                width:214,
                data:[]
            },
            me = this,
            defaults = $.extend(defaults,options),
            steps = defaults.data,
            current;
        //render page
        _initStepBar(steps)

        function render(doms){
            if(doms){
                el.html(doms)
            }
        }
        //create dom
        function createDom(items){
            if(!$.isArray(items)) return;
            var lis = '';
            for(var i=0;i<items.length;i++){
                var item = items[i],check='<div class="check"><i></i></div>',divStatus = '<div class="">',sbt='';
                if(item && item.step){
                    if(item.active){
                        current=item;
                        divStatus = '<div class="step-cur">';
                    }
                    if(current && current.step  <= item.step){
                        check = '<div class="check">'+item.step+'</div>';
                    }else{
                        divStatus = '<div class="step-done">';
                    }
                    if(item.subtitle){
                        sbt = '<div class="step-time">'+item.subtitle+'</div>';
                    }
                    lis += '<li style="width:'+(defaults.width/items.length)+'px;">'+ divStatus + '<div class="step-name">'+item.title+'</div>'+
                        '<div class="step-no"><div class="step-bar"><em class="step-bar-left"></em><em class="step-bar-right"></em></div>'+check+'</div>'+ sbt +
                        '</div>'+
                        '</li>';
                }else{
                    console.error('param require "step" property');
                    break;
                }
            }
            var html = '<div class="flowstep '+ defaults.style+ '">\
            	            <ul>' + lis + '</ul>\
                        </div>';
            var doms = $(html);
            doms.find('li>div').first().addClass('first-step');
            doms.find('li>div').last().addClass('last-step');
            return doms;
        }
        function _initStepBar (datas){
            current = undefined;
            render(createDom(datas));
        }
        //export
        var  result = {
            version:'1.0.2',
            reloadStepBar:function(datas){
                _initStepBar(datas);
                return this;
            },
            getCurStep:function(){
                return current;
            },
            setStep:function(num){
                if(Number(num).toString() == 'NaN'){
                    return console.warn('argument error');
                    if(Number(num) ===0){
                        return console.warn('step num must be > 0');
                    }
                }
                var i= 0,l=steps.length;
                if(num<=l){
                    for(;i<l;i++){
                        steps[i].active = false;
                    }
                    steps[num-1].active = true;
                    _initStepBar(steps);
                }else{
                    return console.warn('the "step num" must be <= stepbar' + '\'s'+ ' length');
                }
                return this;
            },
            destroy:function(){
                current = null;
                steps = null ;
                el.children().remove();
            }
        };
        return result;
    };
    $.fn.flowStepBar = function(options){
        return new stepBar(options, this);
    };
}));