<html >
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1.0,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no" />
    <meta name="description" content="Xenon Boostrap Admin Panel" />
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
        font-size: 12px; color: rgb(51, 51, 51);
        height: 100%;
        text-align: start;
        background: rgb(42, 116, 157);
    }
    .mod23{
        margin: 0px;
        padding: 0px;
        float: left;
        width: 100%;
        background: rgb(42, 116, 157);
    }
    .mod23 .telnav_pc{
        margin: 10px 7px;
        padding: 0px;
        float: left;
    }
    .mod23 .telbtn_pc{
        margin: 0px 0px 4px;
        padding: 0px;
        float: left;
        width: 100%;
        height: 64px;
        overflow: hidden;
        cursor: pointer;
        background: rgba(63, 100, 121, 0.701961);
    }
    .mod23 .telbtn_pc .icon_pc{
        margin: 0px;
        padding: 0px;
        float: right;
        display: inline-block;
        width: 24px;
        height: 60px;
        margin-right: 5px;
    }
    .mod23 .telbtn_pc .t_content_pc{
        margin: 0px 10px;
        padding: 0px;
        float: left;
        display: inline-block;
        min-width: 108px;
        height: 60px;
        width: 40%;
    }
    .mod23 .telbtn_pc .t_content_pc .t_t1_pc{
        margin: 12px 0px 0px;
        padding: 0px;
        color: rgb(243, 243, 243);
        width: 108px;
        line-height: 18px;
        height: 20px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }
    .mod23 .telbtn_pc .t_content_pc .t_t2_pc{
        margin: 0px;
        padding: 0px;
        font-size: 10px;
        color: rgb(199, 199, 199);
        width: 100%;
        line-height: 15px;
        overflow: hidden;
    }
</style>
<div class="mod23">
    <div class="telnav_pc">

<#list list as e>
    <div class="telbtn_pc" id="${e_index}" >
        <div class="pic_pc" style="margin: 0px; padding: 0px; float: left; display: inline-block; width: 102px; height: 64px;">
            <img src="${e.menuImg}" id="content_img1061226" style="border: 0px none; vertical-align: middle; width: 102px; height: 64px;
            ">
         </div>
        <div class="t_content_pc" >
            <div class="t_t1_pc" >${e.menuName}</div>
            <div class="t_t2_pc" >${e.resume}</div>
        </div>
        <div class="icon_pc" >
            <a href="${e.url}">
            <img src="../../../module/wxManage/microWebsite/mwTmpl/basic/img/arrow.png" style="border: 0px none; vertical-align: middle; display: block; width: 18px; height: 18px; margin-top: 20px;">
            </a>
        </div>
    </div>
</#list>
</div>
</div>
</body>
</html>