<html >
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1.0,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no" />
    <meta name="description" content="Xenon Boostrap Admin Panel" />
    <meta name="author" content="" />
</head>
<body>
<div class="mod67" style="margin: 0px; padding: 0px; width: 100%; height: 100%; overflow: hidden; position: relative;">
    <style type="text/css">
        body{
            margin: 0;
            padding: 0;
            font-family: 微软雅黑, 宋体, Arial, Helvetica, sans-serif;
        }
        .mune_67 li.active{
            background-color: #eee;
        }
    </style>
    <div class="main67" style="margin: 0px; padding: 0px; width: 100%; height: 100%; position: relative;bottom: 62px">

        <div style="margin: 0px; padding: 0px;">
            <img id="bannermodsrc" src="${data.webImg}" style="border: 0px none; vertical-align: middle; height: 100%; width: 100%;">
        </div>
        <!--
        <a id="goto_left" style="outline: 0px; display: block; width: 32px; height: 40px; overflow: hidden; position: absolute; top: 187.5px; margin-top: -56px; left: 15px; background-image: url(http://www.dodoca.com/www/images/mod/mod67/pc_left.png); background-position: 50% 50%; background-repeat: no-repeat;"></a>
        <a id="goto_right" style=";outline: 0px; display: block; width: 32px; height: 40px; overflow: hidden; position: absolute; top: 187.5px; margin-top: -56px; right: 15px; background-image: url(http://www.dodoca.com/www/images/mod/mod67/pc_right.png); background-position: 50% 50%; background-repeat: no-repeat;"></a>
        -->
        </div>
    <ul class="mune_67" style="margin: 0px; padding: 0px; list-style: none; position: absolute; left: 0px; bottom: -1px; width: 100%; overflow: hidden; height: 62px; z-index: 10;">
    <#list data.list as e>
         <li class="navs" id="${e_index}" style="margin: 0px; padding: 0px; list-style: none; width: 20%; height: 100%; overflow: hidden; float: left;">
             <a style="text-align:center;outline: 0px; display: block; height: 100%; padding-top: 0px;text-decoration: none;" href="${e.url}">
                 <i style="display: block; width: 100%; height: 42px; margin: 0px auto;">
                     <img src="${e.menuImg}"  style="height: 42px;width:42px;border: 0px none; vertical-align: middle; display: inline-block;-webkit-user-select: none;">
                  </i><b  style="display: block; text-align: center; font-size:14px;line-height: 16px; overflow: hidden;color: #373e4a">${e.menuName}
             </b>
             </a>
         </li>
    </#list>
   </ul>
</div>
</body>
</html>