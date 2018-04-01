CREATE TABLE `LocalMaterialMsgArticleR` (
  `founderId` varchar(36) NOT NULL DEFAULT '' COMMENT '创建人ID',
  `founderName` varchar(30) NOT NULL DEFAULT '' COMMENT '创建人姓名',
  `modifierId` varchar(36) NOT NULL DEFAULT '' COMMENT '修改人ID',
  `modifierName` varchar(30) NOT NULL DEFAULT '' COMMENT '修改人姓名',
  `createDateTime` datetime NOT NULL DEFAULT '1000-01-01 00:00:00' COMMENT '创建时间',
  `modifyDateTime` datetime NOT NULL DEFAULT '1000-01-01 00:00:00' COMMENT '修改时间',
  `deleteFlag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除 0 否 1是',
  `localMaterialMsgArticleR` varchar(36) NOT NULL COMMENT '本地素材子图文关联Id',
  `localMaterialId` varchar(36) NOT NULL DEFAULT ',,' COMMENT '本地素材Id',
  `articleId` varchar(36) NOT NULL DEFAULT ',,' COMMENT '子图文Id',
  PRIMARY KEY (`localMaterialMsgArticleR`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `LocalMaterialMsgT` (
  `founderId` varchar(36) NOT NULL DEFAULT '' COMMENT '创建人ID',
  `founderName` varchar(30) NOT NULL DEFAULT '' COMMENT '创建人姓名',
  `modifierId` varchar(36) NOT NULL DEFAULT '' COMMENT '修改人ID',
  `modifierName` varchar(30) NOT NULL DEFAULT '' COMMENT '修改人姓名',
  `createDateTime` datetime NOT NULL DEFAULT '1000-01-01 00:00:00' COMMENT '创建时间',
  `modifyDateTime` datetime NOT NULL DEFAULT '1000-01-01 00:00:00' COMMENT '修改时间',
  `deleteFlag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除 0 否 1是',
  `localMaterialId` varchar(36) NOT NULL COMMENT '本地素材Id',
  `media_id` varchar(36) NOT NULL DEFAULT ',,' COMMENT '微信素材库媒体Id',
  `appId` varchar(36) NOT NULL DEFAULT ',,' COMMENT '子文章Id',
  `update_time` varchar(36) NOT NULL DEFAULT ',,',
  PRIMARY KEY (`localMaterialId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `LocalArticleT` (
  `founderId` varchar(36) NOT NULL DEFAULT '' COMMENT '创建人ID',
  `founderName` varchar(30) NOT NULL DEFAULT '' COMMENT '创建人姓名',
  `modifierId` varchar(36) NOT NULL DEFAULT '' COMMENT '修改人ID',
  `modifierName` varchar(30) NOT NULL DEFAULT '' COMMENT '修改人姓名',
  `createDateTime` datetime NOT NULL DEFAULT '1000-01-01 00:00:00' COMMENT '创建时间',
  `modifyDateTime` datetime NOT NULL DEFAULT '1000-01-01 00:00:00' COMMENT '修改时间',
  `deleteFlag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除 0 否 1是',
  `articleId` varchar(36) NOT NULL DEFAULT '' COMMENT '子图文Id',
  `thumbMediaId` varchar(128) NOT NULL DEFAULT '' COMMENT '图文消息缩略图Id',
  `author` varchar(20) NOT NULL DEFAULT '' COMMENT '图文作者',
  `title` varchar(15) NOT NULL DEFAULT '' COMMENT '图文标题',
  `contentSourceUrl` varchar(256) NOT NULL DEFAULT '' COMMENT '在图文消息页面点击“阅读原文”后的页面',
  `content` text COMMENT '图文消息页面的内容，支持HTML标签',
  `digest` varchar(128) NOT NULL DEFAULT '' COMMENT '图文消息的描述',
  `showCoverPic` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否显示封面，1为显示，0为不显示',
  `url` varchar(256) NOT NULL DEFAULT '',
  `downloadPath` varchar(256) NOT NULL DEFAULT '' COMMENT '图片路径  本地使用',
  PRIMARY KEY (`articleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;