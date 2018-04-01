/*
Navicat MySQL Data Transfer

Source Server         : community-localhost_bendi
Source Server Version : 50627
Source Host           : 192.168.6.222:3306
Source Database       : develop_localhost

Target Server Type    : MYSQL
Target Server Version : 50627
File Encoding         : 65001

Date: 2016-11-28 13:01:37
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for MsgTmplT
-- ----------------------------
DROP TABLE IF EXISTS `MsgTmplT`;
CREATE TABLE `MsgTmplT` (
  `founderId` varchar(36) NOT NULL DEFAULT '' COMMENT '创建人ID',
  `founderName` varchar(30) NOT NULL DEFAULT '' COMMENT '创建人姓名',
  `modifierId` varchar(36) NOT NULL DEFAULT '' COMMENT '修改人ID',
  `modifierName` varchar(30) NOT NULL DEFAULT '' COMMENT '修改人姓名',
  `createDateTime` datetime NOT NULL DEFAULT '1000-01-01 00:00:00' COMMENT '创建时间',
  `modifyDateTime` datetime NOT NULL DEFAULT '1000-01-01 00:00:00' COMMENT '修改时间',
  `deleteFlag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除 0 否 1是',
  `tmplCode` varchar(128) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '模板编码',
  `tmplType` varchar(24) DEFAULT NULL COMMENT '模板类型',
  `tmplSubject` varchar(34) NOT NULL DEFAULT '' COMMENT '模板名称',
  `tmplContent` varchar(1024) NOT NULL DEFAULT '' COMMENT '模板内容{{解析的key}}',
  `tmplStatus` tinyint(1) NOT NULL DEFAULT '1' COMMENT '模板状态是否启用 0否 1是',
  PRIMARY KEY (`tmplCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of MsgTmplT
-- ----------------------------
INSERT INTO `MsgTmplT` VALUES ('7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员', '', '', '2016-11-23 20:39:39', '2016-11-23 20:39:39', '0', '103958', 'SMS', '管理员取消商品订单提醒', '<p>【驴鱼社区】非常抱歉，您购买的部分商品我们暂时无法提供，您支付的款项稍后会返到您的会员账户中，给您带来不便敬请谅解！详情请致电{1}。</p>', '1');
INSERT INTO `MsgTmplT` VALUES ('7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员', '', '', '2016-11-23 20:31:49', '2016-11-23 20:31:49', '0', '117618', 'SMS', '修改充值卡信息', '<p>【驴鱼社区】您正在修改充值卡信息，本次验证码为：{1}，仅用于修改充值卡使用，有效期30分钟，操作人：{2}。</p>', '1');
INSERT INTO `MsgTmplT` VALUES ('7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员', '', '', '2016-11-23 20:33:02', '2016-11-23 20:33:02', '0', '117619', 'SMS', '开通充值卡', '<p>【驴鱼社区】您正在开通充值卡，本次验证码为：{1}，仅用于开通充值卡使用，有效期30分钟，操作人：{2}。</p>', '1');
INSERT INTO `MsgTmplT` VALUES ('7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员', '', '', '2016-11-23 20:33:39', '2016-11-23 20:33:39', '0', '117620', 'SMS', '激活充值卡', '<p>【驴鱼社区】您正在激活充值卡，本次验证码为：{1}，仅用于激活充值卡使用，有效期30分钟，操作人：{2}。</p>', '1');
INSERT INTO `MsgTmplT` VALUES ('7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员', '', '', '2016-11-23 20:28:32', '2016-11-23 20:28:32', '0', '119809', 'SMS', '充值确认信息-发给店员', '<p>【驴鱼社区】您正在为会员号：{1}的会员使用充值卡充值，充值金额{2}元，本次验证码为：{3}，仅用于会员充值使用，有效期30分钟，操作人：{4}。</p>', '1');
INSERT INTO `MsgTmplT` VALUES ('7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员', '', '', '2016-11-23 20:29:10', '2016-11-23 20:29:10', '0', '120223', 'SMS', '充值发给会员（新）', '<p>【驴鱼社区】您的会员号：{1}正在使用驴鱼充值卡进行充值，金额为{2}元，验证码：{3}，请在30分钟内完成验证。请勿将验证码泄露给其他人，详情可登录官网或关注微信公众平台查询，有问题请致电400-015-5567</p>', '1');
INSERT INTO `MsgTmplT` VALUES ('12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰', '', '', '2016-11-22 15:29:24', '2016-11-22 15:29:24', '0', '122587', 'SMS', '收银台创建会员', '<p>【驴鱼社区】您已成为“驴鱼社区”的会员，初始登录密码：{{password}}，快捷支付密码：{2}，请及时登录官网（{3}）修改您的密码。</p>', '1');
INSERT INTO `MsgTmplT` VALUES ('7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员', '', '', '2016-11-23 20:16:41', '2016-11-23 20:16:41', '0', '122910', 'SMS', '注销家庭（新）', '<p>【驴鱼社区】您正在注销家庭，验证码：{1}。您家庭账户中所有资产将转入到您的个人账户中，并删除本家庭相关信息（不可逆），请慎重操作。请在30分钟内完成验证，驴鱼客服绝不会索取此验证码，请勿将验证码泄露他人，有问题请致电400-015-5567</p>', '1');
INSERT INTO `MsgTmplT` VALUES ('7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员', '12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰a1', '2016-11-23 20:36:28', '2016-11-24 14:58:48', '0', '85826', 'SMS', '驴鱼社区快递模版预约配送', '<p>【驴鱼社区】店内收到你的快递包裹，快递单号为{{1}}，请您及时到店提取或登录系统预约配送。</p>', '1');
INSERT INTO `MsgTmplT` VALUES ('7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员', '', '', '2016-11-23 20:25:13', '2016-11-23 20:25:13', '0', '87434', 'SMS', '驴鱼社区找回密码', '<p>【驴鱼社区】您正在使用短信找回密码，验证码为：{1}，仅用于重置密码，请勿将验证码泄露给其他人。</p>', '1');
INSERT INTO `MsgTmplT` VALUES ('7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员', '', '', '2016-11-23 20:30:06', '2016-11-23 20:30:06', '0', '87574', 'SMS', '充值提醒', '<p>【驴鱼社区】您手机号为{1}的账户于{2}完成一笔充值交易，金额为{3}，余额为{4}，{5}。</p>', '1');
INSERT INTO `MsgTmplT` VALUES ('7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员', '', '', '2016-11-23 20:29:39', '2016-11-23 20:29:39', '0', '87575', 'SMS', '兑换红包', '<p>【驴鱼社区】验证码为：{1}，您于{2}在诚信购买的保单号为{3}的商业保险，金额为：{4}，正在兑换福礼金额为：{5}。</p>', '1');
INSERT INTO `MsgTmplT` VALUES ('7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员', '', '', '2016-11-23 20:26:11', '2016-11-23 20:26:11', '0', '89536', 'SMS', '家庭成员邀请', '<p>【驴鱼社区】您正在邀请会员加入您的家庭，验证码为：{1}，请勿将验证码泄露给其他人。</p>', '1');
INSERT INTO `MsgTmplT` VALUES ('7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员', '', '', '2016-11-23 20:27:47', '2016-11-23 20:27:47', '0', '89537', 'SMS', '家庭成员邀请（受邀请人）', '<p>【驴鱼社区】驴鱼社区会员号为{1}的会员正在邀请您加入他/她的家庭，验证码为：{2}，您的个人账户资产将全部共享到家庭资产中，请勿将验证码泄露给其他人。</p>', '1');
INSERT INTO `MsgTmplT` VALUES ('7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员', '', '', '2016-11-23 20:19:30', '2016-11-23 20:19:30', '0', '90120', 'SMS', '家庭注销成功', '<p>【驴鱼社区】注销家庭成功，您家庭账户中的所有资产已转入您的个人账户中。</p>', '1');
INSERT INTO `MsgTmplT` VALUES ('7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员', '', '', '2016-11-23 20:15:28', '2016-11-23 20:15:28', '0', '90122', 'SMS', '创建家庭成功', '<p>【驴鱼社区】创建家庭成功，您个人账户中的所有资产已转入您的家庭账户中。</p>', '1');
INSERT INTO `MsgTmplT` VALUES ('12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰', '', '', '2016-11-22 15:28:07', '2016-11-22 15:28:07', '0', '90123', 'SMS', '注册时', '<p>【驴鱼社区】您正在注册驴鱼社区会员，验证码为:{{validate}}，仅用于注册会员使用，请勿将验证码泄露给其他人。</p>', '1');
INSERT INTO `MsgTmplT` VALUES ('7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员', '', '', '2016-11-23 20:20:51', '2016-11-23 20:20:51', '0', '90293', 'SMS', '脱离家庭', '<p>【驴鱼社区】您正在脱离家庭，验证码为：{1}，脱离家庭后将不可再使用此家庭任何资产，请谨慎操作。</p>', '1');
INSERT INTO `MsgTmplT` VALUES ('7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员', '', '', '2016-11-23 20:21:27', '2016-11-23 20:21:27', '0', '90294', 'SMS', '脱离家庭成功', '<p>【驴鱼社区】脱离家庭成功。</p>', '1');
INSERT INTO `MsgTmplT` VALUES ('7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员', '', '', '2016-11-23 20:22:38', '2016-11-23 20:22:38', '0', '93732', 'SMS', '会员号码解绑', '<p>【驴鱼社区】您会员号：{1}的会员正在变更手机号码，验证码：{2}，请勿将验证码泄露他人。</p>', '1');
INSERT INTO `MsgTmplT` VALUES ('7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员', '', '', '2016-11-23 20:23:50', '2016-11-23 20:23:50', '0', '93750', 'SMS', '会员绑定成功', '<p>【驴鱼社区】会员手机号码变更成功，会员号：{1}的会员登录名已更换：{2}。</p>', '1');
INSERT INTO `MsgTmplT` VALUES ('7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员', '12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰a1', '2016-11-23 20:37:09', '2016-11-24 15:05:22', '0', '93793', 'SMS', '包裹-普通拒收', '<p>【驴鱼社区】因快递外包装有破损，您的单号为{{1}}的快递包裹已被拒收。详情可登录官网（https://www.lvyushequ.com）或微信公众平台查询。</p>', '1');
INSERT INTO `MsgTmplT` VALUES ('12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰', '', '', '2016-11-22 14:16:53', '2016-11-22 14:16:53', '0', '94631', 'SMS', '送快递提醒', '<p>【驴鱼社区】您的快递包裹{{expressOrderCode}}，预计</p><p>{{deliveryTime}}送达，请保持家中有人！</p>', '1');
INSERT INTO `MsgTmplT` VALUES ('7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员', '', '', '2016-11-23 20:34:13', '2016-11-23 20:34:13', '0', '95019', 'SMS', '修改密码（登陆密码）', '<p>【驴鱼社区】您正在修改登陆密码，验证码：{1}（有效期为30分钟），请勿将验证码泄露给其他人。</p>', '1');
INSERT INTO `MsgTmplT` VALUES ('7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员', '', '', '2016-11-23 20:35:26', '2016-11-23 20:35:26', '0', '95022', 'SMS', '修改密码（快捷支付密码）', '<p>【驴鱼社区】您正在修改快捷支付密码，验证码：{1}（有效期为30分钟），请勿将验证码泄露给其他人。</p>', '1');
INSERT INTO `MsgTmplT` VALUES ('7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员', '', '', '2016-11-23 20:38:14', '2016-11-23 20:38:14', '0', '99900', 'SMS', '驴鱼社区-洗衣预约', '<p>【驴鱼社区】您的洗衣订单{1}已预约成功，客服人员预计30分钟内上门取衣，请保持家中有人！</p>', '1');
INSERT INTO `MsgTmplT` VALUES ('7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员', '', '', '2016-11-23 20:38:41', '2016-11-23 20:38:41', '0', '99903', 'SMS', '驴鱼社区-洗衣支付', '<p>【驴鱼社区】您的洗衣订单{1}已支付成功。详情可登录官网（https://www.lvyushequ.com）或微信公众平台查询。</p>', '1');
INSERT INTO `MsgTmplT` VALUES ('7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员', '12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰a1', '2016-11-23 20:39:10', '2016-11-24 16:28:43', '0', '99904', 'SMS', '驴鱼社区-洗涤完成', '<p>【驴鱼社区】您有{{1}}件衣物已清洗完成，请及时提取或登录系统预约配送。详情可登录官网（https://www.lvyushequ.com）或微信公众平台查询。</p>', '1');
INSERT INTO `MsgTmplT` VALUES ('12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰a', '', '', '2016-11-18 16:43:35', '2016-11-18 16:43:35', '0', '99989', 'SMS', '司机配货密码短信', '<p>尊敬的司机您好：<br/></p><p>您本次的配货密码为{{password}};</p>', '1');
INSERT INTO `MsgTmplT` VALUES ('12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰a1', '12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰a1', '2016-11-25 10:42:57', '2016-11-25 10:46:04', '0', 'ACCEPTFEEDBACK', 'EMAIL', '受理系统问题反馈', '<p>您反馈的问题:{{feedbackContent}}</p><p>已经被【{{feedbackPerson}}】处理/追加<br/></p><p>此问题状态为：【{{feedbackStateName}}】<br/></p><p>优先级:【{{feedbackPriorityName}}】<br/></p><p>内容为:{{content}}<br/></p><p><br/></p>', '1');
INSERT INTO `MsgTmplT` VALUES ('12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰', '12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰a1', '2016-11-22 14:11:25', '2016-11-25 10:38:26', '0', 'ComeToDoorRemind', 'WECHAT_TMPL', '送件上门提醒', '<pre style=\"border-radius: 3px; margin-top: 0px; margin-bottom: 14px; white-space: pre-wrap; font-family: &quot;Microsoft YaHei&quot;, 微软雅黑, Helvetica, 黑体, Arial, Tahoma; color: rgb(34, 34, 34); font-size: 14px; line-height: 22.4px; background-color: rgb(255, 255, 255);\">{{first}}\r\n快递单号：{{keyword1}}\r\n管家名称：{{keyword2}}\r\n联系方式：{{keyword3}}\r\n{{remark}}</pre><p><br/></p>', '1');
INSERT INTO `MsgTmplT` VALUES ('12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰a1', '', '', '2016-11-25 12:27:21', '2016-11-25 12:27:21', '0', 'CommonExpressReciveMsgTmpl', 'WECHAT_TMPL', '快递代收通知', '<p>{{first}}<br/>快递单号：{{keyword1}}<br/>时间：{{keyword2}}</p><p>{{remark}}</p>', '1');
INSERT INTO `MsgTmplT` VALUES ('7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员', '12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰a1', '2016-11-23 20:00:20', '2016-11-25 10:38:23', '0', 'ExpressUnPickCollect', 'WECHAT_TMPL', '快递代收通知', '<p>{{first}}<br/>快递单号：{{keyword1}}<br/>时间：{{keyword2}}<br/>{{remark}}</p><p><br/></p>', '1');
INSERT INTO `MsgTmplT` VALUES ('7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员', '12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰a1', '2016-11-23 20:02:51', '2016-11-25 10:38:20', '0', 'ExpressUnPickNotCollect', 'WECHAT_TMPL', '快递拒收通知', '<p>{{first}}<br/>快递单号：{{keyword1}}<br/>时间：{{keyword2}}<br/>{{remark}}</p>', '1');
INSERT INTO `MsgTmplT` VALUES ('12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰a', '12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰a', '2016-11-18 14:44:42', '2016-11-18 15:23:33', '0', 'FEEDBACKCONTENT', 'EMAIL', '系统问题反馈', '<p>您好：</p><p>&nbsp;系统问题反馈人：【{{feedbackPerson}}】<br/></p><p>&nbsp;优先级:【{{level}}】<br/></p><p>&nbsp;问题状态为:【{{status}}】<br/></p><p>&nbsp;反馈内容如下:<br/></p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; {{Content}}</p><p>祝,好!</p>', '1');
INSERT INTO `MsgTmplT` VALUES ('12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰a1', '12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰a1', '2016-11-25 11:23:15', '2016-11-25 11:23:39', '0', 'FollowEmail', 'EMAIL', '追加系统问题反馈', '<p>系统问题反馈人:【{{founderName}}】<br/>优先级:【{{feedbackPriorityName}}】<br/>问题状态为：【{{feedbackStateName}}】;<br/>反馈内容如下:</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp;{{feedbackContent}}</p><p>此问题被【{{founderName}}】</p><p>追加,追加内容如下:</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;{{content}}</p>', '1');
INSERT INTO `MsgTmplT` VALUES ('12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰a1', '', '', '2016-11-25 11:51:06', '2016-11-25 11:51:06', '0', 'RechargeCardEmail', 'EMAIL', '充值卡充值通知', '<p>【驴鱼社区】您正在为会员号：{{memberPhone}}的会员使用充值卡充值，充值金额{{denomination}}元，本次验证码为：{{random}}，仅用于会员充值使用，有效期30分钟，操作人：{{userName}}。</p>', '1');
INSERT INTO `MsgTmplT` VALUES ('12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰a1', '', '', '2016-11-25 12:10:53', '2016-11-25 12:10:53', '1', 'RechargeCardEmailTrigger', 'EMAIL', '充值卡充值提醒触发器', '<p>return &quot;RechargeCardEmail-&gt;[[emailAddr]]&quot;</p>', '1');
INSERT INTO `MsgTmplT` VALUES ('7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员', '12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰a1', '2016-11-23 20:07:30', '2016-11-25 10:38:17', '0', 'WashFinishTmpl', 'WECHAT_TMPL', '衣物洗涤完成提醒', '<p>{{first}}<br/>订单编号：{{keyword1}}<br/>订单金额：{{keyword2}}<br/>洗涤完成时间：{{keyword3}}<br/>{{remark}}</p>', '1');
INSERT INTO `MsgTmplT` VALUES ('12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰', '12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰', '2016-11-21 16:27:51', '2016-11-22 09:39:29', '0', 'WeChatStateUpDateTmpl', 'WECHAT_TMPL', '洗衣订单状态更新通知', '<pre style=\"border-radius: 3px; margin-top: 0px; margin-bottom: 14px; white-space: pre-wrap; font-family: &quot;Microsoft YaHei&quot;, 微软雅黑, Helvetica, 黑体, Arial, Tahoma; color: rgb(34, 34, 34); font-size: 14px; line-height: 22.4px; background-color: rgb(255, 255, 255);\">{{first}}\r\n订单编号：{{keyword1}}\r\n订单状态：{{keyword2}}\r\n预约时间：{{keyword3}}\r\n送洗衣物：{{keyword4}}\r\n收衣地址：{{keyword5}}\r\n{{remark}}</pre><p><br/></p>', '1');
INSERT INTO `MsgTmplT` VALUES ('7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员', '7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员', '2016-11-23 20:02:29', '2016-11-23 20:03:08', '1', '快递拒收通知', 'WECHAT_TMPL', 'ExpressUnPickNotCollect', '', '1');
/*
Navicat MySQL Data Transfer

Source Server         : community-localhost_bendi
Source Server Version : 50627
Source Host           : 192.168.6.222:3306
Source Database       : develop_localhost

Target Server Type    : MYSQL
Target Server Version : 50627
File Encoding         : 65001

Date: 2016-11-28 13:01:28
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for MsgT
-- ----------------------------
DROP TABLE IF EXISTS `MsgT`;
CREATE TABLE `MsgT` (
  `msgId` varchar(36) NOT NULL COMMENT '消息Id',
  `founderId` varchar(36) NOT NULL DEFAULT '' COMMENT '创建人ID',
  `founderName` varchar(30) NOT NULL DEFAULT '' COMMENT '创建人姓名',
  `modifierId` varchar(36) NOT NULL DEFAULT '' COMMENT '修改人ID',
  `modifierName` varchar(30) NOT NULL DEFAULT '' COMMENT '修改人姓名',
  `createDateTime` datetime NOT NULL DEFAULT '1000-01-01 00:00:00' COMMENT '创建时间',
  `modifyDateTime` datetime NOT NULL DEFAULT '1000-01-01 00:00:00' COMMENT '修改时间',
  `deleteFlag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除 0 否 1是',
  `msgTmplCode` varchar(128) NOT NULL DEFAULT '' COMMENT '消息模板Id',
  `param` varchar(10000) NOT NULL DEFAULT '' COMMENT '消息参数',
  `msgLevel` varchar(12) DEFAULT NULL COMMENT '消息优先级',
  `msgStatus` varchar(12) DEFAULT NULL COMMENT '消息发送状态',
  `msgErrorLog` longtext COMMENT '消息异常',
  `target` varchar(256) NOT NULL DEFAULT '' COMMENT '消息发送目标',
  `failReason` varchar(24) DEFAULT NULL COMMENT '失败原因',
  `msgType` varchar(24) DEFAULT NULL COMMENT '消息类型',
  PRIMARY KEY (`msgId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息记录表';
/*
Navicat MySQL Data Transfer

Source Server         : community-localhost_bendi
Source Server Version : 50627
Source Host           : 192.168.6.222:3306
Source Database       : develop_localhost

Target Server Type    : MYSQL
Target Server Version : 50627
File Encoding         : 65001

Date: 2016-11-28 13:01:32
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for MsgTirggerT
-- ----------------------------
DROP TABLE IF EXISTS `MsgTirggerT`;
CREATE TABLE `MsgTirggerT` (
  `founderId` varchar(36) NOT NULL DEFAULT '' COMMENT '创建人ID',
  `founderName` varchar(30) NOT NULL DEFAULT '' COMMENT '创建人姓名',
  `modifierId` varchar(36) NOT NULL DEFAULT '' COMMENT '修改人ID',
  `modifierName` varchar(30) NOT NULL DEFAULT '' COMMENT '修改人姓名',
  `createDateTime` datetime NOT NULL DEFAULT '1000-01-01 00:00:00' COMMENT '创建时间',
  `modifyDateTime` datetime NOT NULL DEFAULT '1000-01-01 00:00:00' COMMENT '修改时间',
  `deleteFlag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除 0 否 1是',
  `tirggerCode` varchar(128) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '触发器编码',
  `tirggerSubject` varchar(34) NOT NULL DEFAULT '' COMMENT '触发器标题',
  `tirggerRule` varchar(10000) NOT NULL DEFAULT '' COMMENT '触发器规则',
  `tirggerStatus` tinyint(1) NOT NULL DEFAULT '1' COMMENT '触发器是否启用 0否 1是',
  PRIMARY KEY (`tirggerCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of MsgTirggerT
-- ----------------------------
INSERT INTO `MsgTirggerT` VALUES ('12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰a1', '', '', '2016-11-25 10:55:24', '2016-11-25 10:55:24', '0', 'ACCEPTFEEDBACKTrigger', '系统问题反馈受理触发器', 'return \"ACCEPTFEEDBACK->[[emailAddr]]\"', '1');
INSERT INTO `MsgTirggerT` VALUES ('12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰', '12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰', '2016-11-22 14:20:50', '2016-11-22 15:44:10', '0', 'ComeToDoorRemindTrigger', '送件上门提醒消息触发器', 'if(\"[[weChatOpenid]]\"!=\"\"){\r\nreturn \"ComeToDoorRemind->[[weChatOpenid]]\"\r\n}\r\nif([[phone]]!=\"null\"){\r\nreturn \"94631->[[phone]]\"\r\n}', '1');
INSERT INTO `MsgTirggerT` VALUES ('12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰a1', '12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰a1', '2016-11-25 12:28:36', '2016-11-25 12:28:54', '0', 'CommonExpressReciveMsgTmplTrigger', '快递拆包触发器', 'return \"CommonExpressReciveMsgTmpl->[[weChatOpenid]]\"', '1');
INSERT INTO `MsgTirggerT` VALUES ('12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰a', '12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰', '2016-11-18 16:44:32', '2016-11-21 15:09:58', '0', 'DRIVER_LOGISTICAL_PASSWORD', '司机配货短信消息触发器', 'return \"99989->[[person]]\"', '1');
INSERT INTO `MsgTirggerT` VALUES ('12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰a1', '12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰a1', '2016-11-24 15:31:18', '2016-11-24 15:34:11', '1', 'ExpressUnPickCollect', '快递拆包接受消息触发器', '', '1');
INSERT INTO `MsgTirggerT` VALUES ('12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰a1', '12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰a1', '2016-11-24 15:34:29', '2016-11-28 12:31:58', '0', 'ExpressUnPickCollectTrigger', '快递拆包接收消息触发器', 'if(\"[[weChatOpenid]]\"!=\"\"){ return \"ExpressUnPickCollect->[[weChatOpenid]]\"}', '1');
INSERT INTO `MsgTirggerT` VALUES ('12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰a1', '12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰a1', '2016-11-24 15:55:43', '2016-11-28 12:31:34', '0', 'ExpressUnPickNotCollectTrigger', '快递拆包拒收消息触发器', 'if(\"[[weChatOpenid]]\"!=\"\"){ return \"ExpressUnPickNotCollect->[[weChatOpenid]]\" }', '1');
INSERT INTO `MsgTirggerT` VALUES ('12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰a', '12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰', '2016-11-18 14:46:01', '2016-11-20 20:53:42', '0', 'FeedBackTrigger', '系统反馈消息触发器', 'return \"FEEDBACKCONTENT->[[person]]\"', '1');
INSERT INTO `MsgTirggerT` VALUES ('12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰a1', '12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰a1', '2016-11-25 11:24:07', '2016-11-25 11:40:34', '0', 'FollowEmailTrigger', '系统问题反馈追加触发器', 'return \"FollowEmail->[[emailAddr]]\"', '1');
INSERT INTO `MsgTirggerT` VALUES ('12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰a1', '', '', '2016-11-25 12:11:37', '2016-11-25 12:11:37', '0', 'RechargeCardEmailTrigger', '充值卡充值提醒触发器', 'return \"RechargeCardEmail->[[emailAddr]]\"', '1');
INSERT INTO `MsgTirggerT` VALUES ('12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰a1', '', '', '2016-11-24 16:16:01', '2016-11-24 16:16:01', '0', 'WashFinishTrigger', '洗衣完成消息触发器', 'if(\"[[weChatOpenid]]\"!=\"\"){\r\nreturn \"WashFinishTmpl->[[weChatOpenid]]\"\r\n}\r\nif([[memberPhone]]!=\"null\"){\r\nreturn \"99904->[[memberPhone]]\"\r\n}', '1');
INSERT INTO `MsgTirggerT` VALUES ('12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰', '12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰a1', '2016-11-21 16:37:29', '2016-11-24 16:57:59', '0', 'WECHAT-WASH-UPDATE-STATUS-TRIGGER', '洗衣状态更新通知触发器', 'if(\"[[weChatOpenid]]\"!=\"\"){\r\nreturn \"WeChatStateUpDateTmpl->[[weChatOpenid]]\"\r\n}\r\nif([[phone]]!=\"null\"){\r\nreturn \"99900->[[phone]]\"\r\n}', '1');

INSERT INTO `SecurityMenuT` (`menuId`, `parentId`, `menuName`, `menuUrl`, `isLeaf`, `createDateTime`, `modifyDateTime`, `icon`, `sortId`, `founderId`, `founderName`, `modifierId`, `modifierName`, `deleteFlag`) VALUES ('32a13cd07c5246768ed0fc70a255f98f', 'cd95f8b2527a4c10800915baca963179', '消息触发器配置', '/msgcen/msgTirgger', '1', '2016-11-16 21:16:52', '2016-11-16 21:16:52', NULL, '2', '12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰a', NULL, NULL, '0');
INSERT INTO `SecurityMenuT` (`menuId`, `parentId`, `menuName`, `menuUrl`, `isLeaf`, `createDateTime`, `modifyDateTime`, `icon`, `sortId`, `founderId`, `founderName`, `modifierId`, `modifierName`, `deleteFlag`) VALUES ('468f4f6378354b80a31c6a08197697b1', 'cd95f8b2527a4c10800915baca963179', '消息监控', '/msgcen/msgMonitor', '1', '2016-11-21 14:13:40', '2016-11-21 14:13:40', NULL, '3', '7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员', NULL, NULL, '0');
INSERT INTO `SecurityMenuT` (`menuId`, `parentId`, `menuName`, `menuUrl`, `isLeaf`, `createDateTime`, `modifyDateTime`, `icon`, `sortId`, `founderId`, `founderName`, `modifierId`, `modifierName`, `deleteFlag`) VALUES ('70a3a6aec6524bd8b78d4f9e2db9520d', 'cd95f8b2527a4c10800915baca963179', '消息模板管理', '/msgcen/msgTmpl', '1', '2016-11-16 12:44:59', '2016-11-16 12:53:31', NULL, '1', '7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员', '12a6a192-9c4f-4998-ba8e-9157c55f43c6', '宫钰a', '0');
INSERT INTO `SecurityMenuT` (`menuId`, `parentId`, `menuName`, `menuUrl`, `isLeaf`, `createDateTime`, `modifyDateTime`, `icon`, `sortId`, `founderId`, `founderName`, `modifierId`, `modifierName`, `deleteFlag`) VALUES ('cd95f8b2527a4c10800915baca963179', '-1', '消息中心', '', '0', '2016-11-16 12:43:46', '2016-11-16 12:43:46', NULL, '1', '7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员', NULL, NULL, '0');
INSERT INTO `SecurityMenuRoleR` (`relId`, `roleId`, `menuId`, `createDateTime`, `modifyDateTime`, `founderId`, `founderName`, `modifierId`, `modifierName`, `deleteFlag`) VALUES ('f3b1289040f34abe91608be6fb7c400c', '7d6007e5-9659-441d-be00-5ro484f8856d', '32a13cd07c5246768ed0fc70a255f98f', '2016-11-23 11:20:11', '2016-11-23 11:20:11', '7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员', NULL, NULL, '0');
INSERT INTO `SecurityMenuRoleR` (`relId`, `roleId`, `menuId`, `createDateTime`, `modifyDateTime`, `founderId`, `founderName`, `modifierId`, `modifierName`, `deleteFlag`) VALUES ('446fd56b92e8425bb4fdd553bb2d2207', '7d6007e5-9659-441d-be00-5ro484f8856d', '468f4f6378354b80a31c6a08197697b1', '2016-11-23 11:20:11', '2016-11-23 11:20:11', '7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员', NULL, NULL, '0');
INSERT INTO `SecurityMenuRoleR` (`relId`, `roleId`, `menuId`, `createDateTime`, `modifyDateTime`, `founderId`, `founderName`, `modifierId`, `modifierName`, `deleteFlag`) VALUES ('eb6709fc84284e17add93f7c64e897b0', '7d6007e5-9659-441d-be00-5ro484f8856d', '70a3a6aec6524bd8b78d4f9e2db9520d', '2016-11-23 11:20:11', '2016-11-23 11:20:11', '7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员', NULL, NULL, '0');
INSERT INTO `SecurityMenuRoleR` (`relId`, `roleId`, `menuId`, `createDateTime`, `modifyDateTime`, `founderId`, `founderName`, `modifierId`, `modifierName`, `deleteFlag`) VALUES ('7799ce52cbe1415f99c223366bbe3c30', '7d6007e5-9659-441d-be00-5ro484f8856d', 'cd95f8b2527a4c10800915baca963179', '2016-11-23 11:20:11', '2016-11-23 11:20:11', '7d6007e5-9659-441d-be00-5us484f8856d', '系统超级管理员', NULL, NULL, '0');