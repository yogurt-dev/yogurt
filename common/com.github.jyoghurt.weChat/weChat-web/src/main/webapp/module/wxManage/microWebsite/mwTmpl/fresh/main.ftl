<html >
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1.0,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no" />
    <meta name="author" content="" />
</head>
<body>
<div class="mod91" id="mod" style="margin:0;padding:0;width:100%;height:100%;overflow:hidden;position:relative;background:url('../../../module/wxManage/microWebsite/mwTmpl/fresh/img/bg_pc.jpg');background-size: cover;">
    <style>
        body{
            margin: 0;
            padding: 0;
            font-size: 14px;
            font-family:"microsoft yahei";
        }
        a{
            text-decoration: none;
        }
    </style>
    <div class="main91" style="margin:0;padding:0;width: 100%;height: 100%;">
        <div class="menu91" style="margin:0 auto;padding:0;width:280px;position:relative;top:21.34px;z-index:100;">
        <#list data.list as e>
            <#if e_index==0>
                <a class="nav"   href="${e.url}" id="${e_index}" style="outline:0;display:block;position:absolute;width:92px;height:92px;z-index:99;left:0;top:0;background-image:url(../../../module/wxManage/microWebsite/mwTmpl/fresh/img/small_bg_pc.png);background-repeat:no-repeat">
                    <i style="display:block;width:34px;height:34px;margin:18px auto 0">
                        <img src="${e.menuImg}" id="" style="border:0 none;vertical-align:middle;width:34px;height:34px">
                    </i>
                    <span class="navuse" id="" style="display:block;width:54px;height:18px;text-align:center;margin:0 auto;color:#666">${e.menuName}</span>
                </a>
            </#if>
            <#if e_index==1>
                <a class="nav"   href="${e.url}" id="${e_index}" style="outline:0;display:block;position:absolute;width:92px;height:92px;z-index:99;left:0;top:92px;background-image:url(../../../module/wxManage/microWebsite/mwTmpl/fresh/img/small_bg_pc.png);background-repeat:no-repeat">
                    <i style="display:block;width:34px;height:34px;margin:18px auto 0">
                        <img src="${e.menuImg}" id="" style="border:0 none;vertical-align:middle;width:34px;height:34px">
                    </i>
                    <span class="navuse" id="" style="display:block;width:54px;height:18px;text-align:center;margin:0 auto;color:#666">${e.menuName}</span>
                </a>
            </#if>
            <#if e_index==2>
                <a class="nav"   href="${e.url}" id="${e_index}" style="outline:0;display:block;position:absolute;width:92px;height:92px;z-index:99;right:0;top:0;background-image:url(../../../module/wxManage/microWebsite/mwTmpl/fresh/img/small_bg_pc.png);background-repeat:no-repeat">
                    <i style="display:block;width:34px;height:34px;margin:18px auto 0">
                        <img src="${e.menuImg}" id="" style="border:0 none;vertical-align:middle;width:34px;height:34px">
                    </i>
                    <span class="navuse" id="" style="display:block;width:54px;height:18px;text-align:center;margin:0 auto;color:#666">${e.menuName}</span>
                </a>
            </#if>
            <#if e_index==3>
                <a class="nav"   href="${e.url}" id="${e_index}" style="outline:0;display:block;position:absolute;width:92px;height:92px;z-index:99;right:0;top:92px;background-image:url(../../../module/wxManage/microWebsite/mwTmpl/fresh/img/small_bg_pc.png);background-repeat:no-repeat">
                    <i style="display:block;width:34px;height:34px;margin:18px auto 0">
                        <img src="${e.menuImg}" id="" style="border:0 none;vertical-align:middle;width:34px;height:34px">
                    </i>
                    <span class="navuse" id="" style="display:block;width:54px;height:18px;text-align:center;margin:0 auto;color:#666">${e.menuName}</span>
                </a>
            </#if>
            <#if e_index==4>
                <a class="nav"   href="${e.url}" id="${e_index}" style="outline:0;display:block;position:absolute;width:184px;height:184px;z-index:90;left:48px;top:0;background-image:url(../../../module/wxManage/microWebsite/mwTmpl/fresh/img/big_bg_pc.png);background-repeat:no-repeat">
                    <i style="display:block;width:50px;height:50px;margin:55px auto 0">
                        <img src="${e.menuImg}" id="" style="border:0 none;vertical-align:middle;width:50px;height:50px">
                    </i>
                    <span class="navuse" id="" style="display:block;width:90px;height:18px;text-align:center;margin:0 auto;color:#fff">${e.menuName}</span>
                </a>
            </#if>
        </#list>
        </div>
    </div>
</div>
</body>
</html>