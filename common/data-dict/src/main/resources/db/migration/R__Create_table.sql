CREATE TABLE data_dict(
  id bigint PRIMARY KEY NOT NULL COMMENT '主键' AUTO_INCREMENT,
  item_code char(40) NOT NULL COMMENT '字典项',
  value_code char(40) NOT NULL COMMENT '字典值',
  value_name char(60) COMMENT '字典名称',
  value_desc char(200) COMMENT '描述',
  parent_id bigint COMMENT '父字典id',
  creator_id bigint NOT NULL DEFAULT 0 COMMENT '创建人ID',
  gmt_create datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  modifier_id bigint NULL COMMENT '修改人ID',
  gmt_modified datetime NULL COMMENT '修改时间',
  is_deleted tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识'
)