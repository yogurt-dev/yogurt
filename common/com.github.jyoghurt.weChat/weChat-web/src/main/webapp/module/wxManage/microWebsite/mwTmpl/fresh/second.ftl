<html >
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1.0,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no" />
    <meta name="author" content="" />
</head>
<body>
<style type="text/css">
    body{
        margin: 0;
        padding: 0;
        font-family: 微软雅黑, 宋体, Arial, Helvetica, sans-serif;
        overflow-x: hidden;
        overflow-y: auto;
        font-size: 14px; color: rgb(51, 51, 51);
        height: 100%;
        text-align: start;
    }
</style>
<div class="mod55" style="margin: 0px; padding: 0px; width: 100%px; overflow: hidden; position: relative;">
    <div class="main55" style="margin: 0px; padding: 0px; width: 100%; overflow: hidden;">
        <div class="menu55" style="margin: 0px; padding: 0px; width: 100%; overflow: hidden; border-top-width: 1px; border-top-style: solid; border-top-color: rgb(42, 42, 42);">
        <#list list as e>
            <a href="${e.url}" id="${e_index}" class="nav" style="outline: 0px; display: block; height: 82px; overflow: hidden; border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: rgb(42, 42, 42); background-image: url(../../../module/wxManage/microWebsite/mwTmpl/fresh/img/second_bg.jpg); background-repeat: repeat;">
                <span class="menu55_text" style="display: block; width: 40%; height: 80px; float: left;">
                    <b id="content_title1167468" class="navuse" style="display: block; text-indent: 16px; height: 22px; line-height: 20px; overflow: hidden; color: rgb(255, 255, 255); font-size: 14px; margin-top: 15px;">${e.menuName}</b>
                    <span id="content_titlems1167468" style="line-height: 1.2; display: block; padding-left: 16px; font-size: 14px; color: rgb(165, 165, 165); height: 32px; overflow: hidden;">${e.resume}</span>
                </span>
                <span class="menu55_img" style="display: block; width: 50%; height: 80px; float: left; text-align: center;">
                    <img src="${e.menuImg}" id="" width="298" height="135" style="border: 0px none; vertical-align: middle; width: 150px; height: 68px; margin-top: 7px; transform: skew(-20deg);">
                </span>
            </a>
        </#list>
        </div>
    </div>
</div>
</body>
</html>