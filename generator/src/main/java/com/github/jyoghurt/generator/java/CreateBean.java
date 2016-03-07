package com.github.jyoghurt.generator.java;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CreateBean {
	static String url;
	static String username;
	static String password;
	static String rt = "\r\n\t";
	static String dbInstance;
	//modify by limiao 20160307 如下字段不生成到domain
//	private static final List ingoreColumns = Arrays.asList("createDateTime", "modifyDateTime", "operatorId",
//			"operatorName");
	private static final List ingoreColumns = Arrays.asList("founderId", "founderName", "modifierId",
			"modifierName","deleteFlag","createDateTime","modifyDateTime");
	String SQLTables = "show tables";
	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("static-access")
	public void setMysqlInfo(String url, String username, String password, String dbInstance) {
		this.url = url;
		this.username = username;
		this.password = password;
		this.dbInstance = dbInstance;
	}

	public Connection getConnection() throws SQLException {
		System.out.println(url);
		return DriverManager.getConnection(url, username, password);
	}

	public List<String> getTables() throws SQLException {
		Connection con = this.getConnection();
		PreparedStatement ps = con.prepareStatement(SQLTables);
		ResultSet rs = ps.executeQuery();
		List<String> list = new ArrayList<String>();
		while (rs.next()) {
			String tableName = rs.getString(1);
			list.add(tableName);
		}
		rs.close();
		ps.close();
		con.close();
		return list;
	}


	public List<ColumnData> getColumnDatas(String tableName) throws SQLException {
		String SQLColumns = "SELECT distinct COLUMN_NAME, DATA_TYPE, COLUMN_COMMENT,COLUMN_KEY,CHARACTER_MAXIMUM_LENGTH" +
				",IS_NULLABLE  FROM information_schema.columns WHERE table_name =  '" + tableName + "' " + "and table_schema='" + dbInstance + "' ";
		Connection con = this.getConnection();
		PreparedStatement ps = con.prepareStatement(SQLColumns);
		List<ColumnData> columnList = new ArrayList<ColumnData>();
		ResultSet rs = ps.executeQuery();
		StringBuffer str = new StringBuffer();
		StringBuffer getset = new StringBuffer();
		while (rs.next()) {
			String name = rs.getString(1);
			String type = rs.getString(2);
			String comment = rs.getString(3);
			String priKey = rs.getString(4);
			Long length=rs.getLong(5);
			String isNullable= rs.getString(6);
			type = this.getType(type);
			if (ingoreColumns.contains(name)) {
				continue;
			}
			ColumnData cd = new ColumnData();
			cd.setColumnName(name);
			cd.setDataType(type);
			cd.setColumnComment(comment);
			cd.setColumnNameContainEntity("${entity." + name + " }");
			cd.setIsPriKey("PRI".equals(priKey));
			cd.setColumnLength(length);
			cd.setIsNullable("NO".equals(isNullable));
			columnList.add(cd);
		}
		argv = str.toString();
		method = getset.toString();
		rs.close();
		ps.close();
		con.close();
		return columnList;
	}

	private String method;
	private String argv;


	public String getBeanFeilds(String tableName) throws SQLException {
		List<ColumnData> dataList = getColumnDatas(tableName);
		StringBuffer str = new StringBuffer();
		StringBuffer getset = new StringBuffer();
		for (ColumnData d : dataList) {

			String name = getTablesColumnToAttributeName(d.getColumnName());
			String type = d.getDataType();
			String comment = d.getColumnComment();
			// type=this.getType(type);
			String maxChar = name.substring(0, 1).toUpperCase();
			str.append("\r\n\t/** \r\n\t * ").append(comment).append("  \r\n\t */");
			if (d.getIsPriKey()) {
				str.append("\r\n\t@javax.persistence.Id");
			}
			str.append("\r\n\t").append("private ").append(type + " ").append(name).append(";");
			String method = maxChar + name.substring(1, name.length());
			getset.append("\r\n\t\r\n\t").append("public ").append(type + " ").append("get" + method + "() {\r\n\t");
			getset.append("    return this.").append(name).append(";\r\n\t}");
			getset.append("\r\n\t\r\n\t").append("public ").append(getTablesNameToClassName(tableName)).append(" ")
					.append("set" + method + "(" + type + " " + name + ") {\r\n\t");
			getset.append("\tthis.").append(name).append(" = ").append(name).append(";\r\n\t\treturn this;\r\n\t}");
		}
		argv = str.toString();
		method = getset.toString();
		return argv + method;
	}

	public String getType(String type) {
		switch (type = type.toLowerCase()) {
			case "char":
			case "varchar":
			case "text":
				return "String";
			case "int":
				return "Integer";
			case "bigint":
				return "java.math.BigInteger";
			case "decimal":
				return "java.math.BigDecimal";
			case "timestamp":
			case "date":
			case "datetime":
				// return "java.sql.Timestamp";
				return "java.util.Date";
			case "float":
				return "Float";
			case "double":
				return "Double";
			case "tinyint":
				return "Boolean";
			default:
				return null;
		}
	}

	public void getPackage(int type, String createPath, String content, String packageName, String className,
			String extendsClassName, String... importName) throws Exception {
		if (null == packageName) {
			packageName = "";
		}
		StringBuffer sb = new StringBuffer();
		sb.append("package ").append(packageName).append(";\r\n");
		sb.append("\r\n");
		for (int i = 0; i < importName.length; i++) {
			sb.append("import ").append(importName[i]).append(";\r\n");
		}
		sb.append("\r\n");
		sb.append("/**\r\n *  entity. @author wolf Date:"
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\r\n */");
		sb.append("\r\n");
		sb.append("\r\npublic class ").append(className);
		if (null != extendsClassName) {
			sb.append(" extends ").append(extendsClassName);
		}
		if (type == 1) { // bean
			sb.append(" ").append("implements java.io.Serializable {\r\n");
		} else {
			sb.append(" {\r\n");
		}
		sb.append("\r\n\t");
		sb.append("private static final long serialVersionUID = 1L;\r\n\t");
		String temp = className.substring(0, 1).toLowerCase();
		temp += className.substring(1, className.length());
		if (type == 1) {
			sb.append("private " + className + " " + temp + "; // entity ");
		}
		sb.append(content);
		sb.append("\r\n}");
		System.out.println(sb.toString());
		this.createFile(createPath, "", sb.toString());
	}

	public String getTablesNameToClassName(String tableName) {
		String[] split = tableName.split("_");
		if (split.length > 1) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < split.length; i++) {
				String tempTableName = split[i].substring(0, 1).toUpperCase()
						+ split[i].substring(1).toLowerCase();
				sb.append(tempTableName);
			}
			System.out.println(sb.toString());
			return sb.toString();
		} else {
			String tempTables = split[0].substring(0, 1).toUpperCase() + split[0].substring(1, split[0].length());
			return tempTables;
		}
	}


	public String getTablesColumnToAttributeName(String columnName) {
		String[] split = columnName.split("_");
		if (split.length > 1) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < split.length; i++) {
				String tempTableName = "";
				if (i == 0) {
					tempTableName = split[i].substring(0, 1).toLowerCase() + split[i].substring(1, split[i].length());
				} else {
					tempTableName = split[i].substring(0, 1).toUpperCase() + split[i].substring(1, split[i].length());
				}
				sb.append(tempTableName);
			}
			System.out.println(sb.toString());
			return sb.toString();
		} else {
			String tempTables = split[0].substring(0, 1).toLowerCase() + split[0].substring(1, split[0].length());
			return tempTables;
		}
	}


	public void createFile(String path, String fileName, String str) throws IOException {
		FileWriter writer = new FileWriter(new File(path + fileName));
		writer.write(new String(str.getBytes("utf-8")));
		writer.flush();
		writer.close();
	}


	static String selectStr = "select ";
	static String from = " from ";

	public Map<String, Object> getAutoCreateSql(String tableName) throws Exception {
		Map<String, Object> sqlMap = new HashMap<String, Object>();
		List<ColumnData> columnDatas = getColumnDatas(tableName);
		String columns = this.getColumnSplit(columnDatas);
		String[] columnList = getColumnList(columns); // 表所有字段
		String columnFields = getColumnFields(columns); // 表所有字段 按","隔开
		String insert = "insert into " + tableName + "(" + columns.replaceAll("\\|", ",") + ")\r\n values(#{"
				+ columns.replaceAll("\\|", "},#{") + "})";
		String update = getUpdateSql(tableName, columnList);
		String updateSelective = getUpdateSelectiveSql(tableName, columnDatas);
		String selectById = getSelectByIdSql(tableName, columnList);
		String delete = getDeleteSql(tableName, columnList);
		String batchDelete = getBatchDeleteSql(tableName, columnList);
		sqlMap.put("columnList", columnList);
		sqlMap.put("columnFields", columnFields);
		sqlMap.put("insert", insert);
		sqlMap.put("update", update);
		sqlMap.put("delete", delete);
		sqlMap.put("updateSelective", updateSelective);
		sqlMap.put("selectById", selectById);
		sqlMap.put("batchDelete", batchDelete);
		return sqlMap;
	}


	public String getDeleteSql(String tableName, String[] columnsList) throws SQLException {
		StringBuffer sb = new StringBuffer();
		sb.append("delete ");
		sb.append("\t from ").append(tableName).append(" where ");
		sb.append(columnsList[0]).append(" = #{").append(columnsList[0]).append("}");
		return sb.toString();
	}


	public String getBatchDeleteSql(String tableName, String[] columnsList) throws SQLException {
		StringBuffer sb = new StringBuffer();
		sb.append("delete ");
		sb.append("\t from ").append(tableName).append(" where ");
		sb.append(columnsList[0]).append(" in ");
		return sb.toString();
	}


	public String getSelectByIdSql(String tableName, String[] columnsList) throws SQLException {
		StringBuffer sb = new StringBuffer();
		sb.append("select <include refid=\"Base_Column_List\" /> \r\n");
		sb.append("\t from ").append(tableName).append(" where ");
		sb.append(columnsList[0]).append(" = #{").append(columnsList[0]).append("}");
		return sb.toString();
	}


	public String getColumnFields(String columns) throws SQLException {
		String fields = columns;
		if (fields != null && !"".equals(fields)) {
			fields = fields.replaceAll("[|]", ",");
		}
		return fields;
	}


	public String[] getColumnList(String columns) throws SQLException {
		String[] columnList = columns.split("[|]");
		return columnList;
	}


	public String getUpdateSql(String tableName, String[] columnsList) throws SQLException {
		StringBuffer sb = new StringBuffer();

		for (int i = 1; i < columnsList.length; i++) {
			String column = columnsList[i];
			sb.append(column + "=#{" + column + "}");
			// 最后一个字段不需要","
			if ((i + 1) < columnsList.length) {
				sb.append(",");
			}
		}
		String update = "update " + tableName + " set " + sb.toString() + " where " + columnsList[0] + "=#{"
				+ columnsList[0] + "}";
		return update;
	}

	public String getUpdateSelectiveSql(String tableName, List<ColumnData> columnList) throws SQLException {
		StringBuffer sb = new StringBuffer();
		ColumnData cd = columnList.get(0); // 获取第一条记录，主键
		sb.append("\t<trim  suffixOverrides=\",\" >\r\n");
		for (int i = 1; i < columnList.size(); i++) {
			ColumnData data = columnList.get(i);
			String columnName = data.getColumnName();
			sb.append("\t<if test=\"").append(columnName).append(" != null ");
			// String 还要判断是否为空
			if ("String" == data.getDataType()) {
				sb.append(" and ").append(columnName).append(" != ''");
			}
			sb.append(" \">\r\n\t\t");
			sb.append(columnName + "=#{" + columnName + "},\r\n");
			sb.append("\t</if>\r\n");
		}
		sb.append("\t</trim>");
		String update = "update " + tableName + " set \r\n" + sb.toString() + " where " + cd.getColumnName() + "=#{"
				+ cd.getColumnName() + "}";
		return update;
	}


	public String getColumnSplit(List<ColumnData> columnList) throws SQLException {
		StringBuffer commonColumns = new StringBuffer();
		for (ColumnData data : columnList) {
			commonColumns.append(data.getColumnName() + "|");
		}
		return commonColumns.delete(commonColumns.length() - 1, commonColumns.length()).toString();
	}

}
