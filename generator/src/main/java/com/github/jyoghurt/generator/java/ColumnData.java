package com.github.jyoghurt.generator.java;

/**
 * 表字段类
 * @author Administrator
 *
 */
public class ColumnData {

	private String columnName;
	private String dataType;
	private String columnComment;
	private String columnNameContainEntity;	//${entity.columnName}
    private Boolean isPriKey;
	private Long columnLength;
	private Boolean isNullable;

    public Boolean getIsPriKey() {
        return isPriKey;
    }

    public void setIsPriKey(Boolean isPriKey) {
        this.isPriKey = isPriKey;
    }
    public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getColumnComment() {
		return columnComment;
	}
	public void setColumnComment(String columnComment) {
		this.columnComment = columnComment;
	}
	public String getColumnNameContainEntity() {
		return columnNameContainEntity;
	}
	public void setColumnNameContainEntity(String columnNameContainEntity) {
		this.columnNameContainEntity = columnNameContainEntity;
	}

	public Long getColumnLength() {
		return columnLength;
	}

	public void setColumnLength(Long columnLength) {
		this.columnLength = columnLength;
	}

	public Boolean getIsNullable() {
		return isNullable;
	}

	public void setIsNullable(Boolean isNullable) {
		this.isNullable = isNullable;
	}
}
