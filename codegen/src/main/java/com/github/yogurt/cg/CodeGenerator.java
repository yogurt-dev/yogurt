package com.github.yogurt.cg;

import com.esotericsoftware.yamlbeans.YamlReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;


/**
 * @author jtwu
 */
@Mojo(name = "generate", requiresProject = false)
@Slf4j
public class CodeGenerator extends AbstractMojo {


	@Parameter
	private String configurationFile;

	/**
	 * 项目地址
	 */
	@Parameter(defaultValue = "${basedir}")
	private String basedir;
	/**
	 * 用户名
	 */
	@Parameter(defaultValue = "${user.name}")
	private String userName;


	/**
	 * The project currently being build.
	 */
	@Parameter(defaultValue = "${project}")
	private MavenProject project;

	/**
	 * The current Maven session.
	 */
	@Parameter(defaultValue = "${session}")
	private MavenSession session;

	/**
	 * The Maven BuildPluginManager component.
	 */
	@Component
	private BuildPluginManager pluginManager;

	private ClassDefinition classDefinition;

	private FileTmplContext fileTmplContext;

	private static final List<String> IGNORE_COLUMNS = Arrays.asList("creator_id", "modifier_id", "is_deleted", "gmt_create", "gmt_modified");

	@Override
	public void execute() throws MojoExecutionException {
		try {
			Configuration configuration = loadConfig();
			//初始化类描述信息
			createClassDefinition(configuration);
			//先生成po，给QueryDSL提供生成素材
			generatePOFile();
			//生成QueryDSL代码
			executeQueryDSLCodegen();
			//创建文件
			generateFile();

		} catch (Exception e) {
			log.error("生成失败", e);
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void generatePOFile() throws MojoExecutionException {
		log.info("\n                                      " +
				"\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" +
				"\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" +
				"\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@  @@@@@@@@" +
				"\n@@@@  @@@@@  @@@@    @@@@@@@   @@@@@@  @@@  @@@@@  @  @@@@      @@@@@@" +
				"\n@@@@@  @@@  @@@  @@@  @@@@  @@@  @@@@  @@@  @@@@@   @@@@@@@@  @@@@@@@@" +
				"\n@@@@@@  @  @@@@  @@@  @@@@  @@@  @@@@  @@@  @@@@@  @@@@@@@@@  @  @@@@@" +
				"\n@@@@@@@@  @@@@@@@   @@@@@@@@     @@@@@    @  @@@@  @@@@@@@@@@   @@@@@@" +
				"\n@@@@@@@  @@@@@@@@@@@@@@@@@@@@@@  @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" +
				"\n@@@@@@  @@@@@@@@@@@@@@@@@@@  @@  @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" +
				"\n@@@@@  @@@@@@@@@@@@@@@@@@@@@    @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" +
				"\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" +
				"\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" +
				"\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" +
				"\n");

		Map<String, Object> context = fileTmplContext.getContext();
		String fileDirPath = fileTmplContext.getFileDirPath();
		try {
			CommonPageParser.writerPage(context, "PO.ftl", fileDirPath, fileTmplContext.getPoPath());
		} catch (Exception e) {
			throw new MojoExecutionException("文件生成失败", e);
		}

	}

	/**
	 * 调用jooq代码生成器
	 */
	private void executeQueryDSLCodegen() throws MojoExecutionException {
		File qPoPath = new File(fileTmplContext.getFileDirPath() + fileTmplContext.getQPoPath());
		if (!qPoPath.exists()) {
			new File(qPoPath.getParent()).mkdirs();
		} else {
			log.info(qPoPath + "文件已存在!");
			return;
		}
		executeMojo(
				plugin(
						groupId("com.mysema.maven"),
						artifactId("apt-maven-plugin"),
						version("1.1.3"),
						dependencies(
								dependency(
										groupId("com.github.yogurt-dev"),
										artifactId("codegen"),
										version("3.0.1-SNAPSHOT")),
								dependency(
										groupId("com.querydsl"),
										artifactId("querydsl-apt"),
										version("4.2.1"))
						)
				),
				goal("process"),
				configuration(
						element(name("outputDirectory"), "target/generated-sources/java"),
						element(name("processor"), "com.querydsl.apt.jpa.JPAAnnotationProcessor")
				),
				executionEnvironment(
						project,
						session,
						pluginManager
				)
		);

		File queryDSLDir = new File(fileTmplContext.getFileDirPath().replace("src/main", "target/generated-sources") + fileTmplContext.getQPoPath());
		try {

			FileSystemUtils.copyRecursively(queryDSLDir, qPoPath);
			FileUtils.deleteDirectory(new File(basedir + File.separator + "target/generated-sources"));
		} catch (IOException e) {
			throw new MojoExecutionException("复制QueryDSL文件失败", e);
		}
	}


	/**
	 * 创建类描述信息
	 *
	 * @param conf jooq配置信息
	 */
	private void createClassDefinition(Configuration conf) throws ClassNotFoundException, MojoExecutionException {
		classDefinition = new ClassDefinition().setPackageName(conf.getPackageName())
				.setClassName(getClassName(conf.getTableName()));
		createTableDesc(conf);
		createFileTmplContext(conf);
	}

	private void createFileTmplContext(Configuration conf) {
//		此处使用jooq的配置文件及加载
		String table = conf.getTableName();
		String className = classDefinition.getClassName();

		String lowerName = className.substring(0, 1).toLowerCase() + className.substring(1);

		// java路径
		String javaPath = File.separator + conf.getDirectory() + File.separator;
		fileTmplContext = new FileTmplContext();
		fileTmplContext.setPoPath(File.separator + "po" + File.separator + className + "PO.java");
		fileTmplContext.setQPoPath(File.separator + "po" + File.separator + "Q" + className + "PO.java");

		fileTmplContext.setVoPath(File.separator + "vo" + File.separator + className + "VO.java");

		fileTmplContext.setAoPath(File.separator + "ao" + File.separator + className + "AO.java");

		fileTmplContext.setDaoPath(File.separator + "dao" + File.separator + className + "DAO.java");

		fileTmplContext.setDaoImplPath(File.separator + "dao" + File.separator + "impl" + File.separator + className + "DAOImpl.java");

		fileTmplContext.setServicePath(File.separator + "service" + File.separator + className + "Service.java");

		fileTmplContext.setServiceImplPath(File.separator + "service" + File.separator + "impl" + File.separator + className + "ServiceImpl.java");

		fileTmplContext.setControllerPath(File.separator + "controller" + File.separator + className + "Controller.java");
		Map<String, Object> context = new HashMap<>(7);
		context.put("className", className);
		context.put("lowerName", lowerName);
		context.put("table", table);
		context.put("modulePackage", classDefinition.getPackageName());
		context.put("fields", classDefinition.getFieldDefinitions());
		context.put("priKeys", classDefinition.getPriKeys());
		context.put("userName", userName);
		context.put("tableComment", StringUtils.endsWith(classDefinition.getComment(), "表")
				? StringUtils.substringBeforeLast(classDefinition.getComment(), "表")
				: classDefinition.getComment());
		fileTmplContext.setContext(context);
		fileTmplContext.setFileDirPath(basedir + javaPath + replaceSeparator(classDefinition.getPackageName()));
	}

	private String replaceSeparator(String s) {
		return s.replaceAll("\\.", Matcher.quoteReplacement(File.separator));
	}

	/**
	 * 读取jooq的配置文件
	 */
	private Configuration loadConfig() throws MojoExecutionException {
//		File file = new File(configurationFile);
//
//		if (!file.isAbsolute()) {
//			file = new File(project.getBasedir(), configurationFile);
//		}
//		ClassLoader classLoader = getClass().getClassLoader();
//		String path = classLoader.getResource("generator.yml").getPath();
		try {
			YamlReader reader = new YamlReader(new FileReader(project.getBasedir() + File.separator + configurationFile));
			return reader.read(Configuration.class);
		} catch (Exception e) {
			throw new MojoExecutionException("配置文件", e);
		}
	}

	/**
	 * 创建表描述信息
	 *
	 * @param conf 配置信息
	 */
	private void createTableDesc(Configuration conf) throws ClassNotFoundException, MojoExecutionException {
		Class.forName("com.mysql.jdbc.Driver");
		String sqLColumns = "select TABLE_SCHEMA,TABLE_NAME,TABLE_COMMENT from information_schema.`TABLES` where table_name = '"
				+ conf.getTableName() + "' and table_schema='" + conf.getTableSchema() + "' ";
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = DriverManager.getConnection(conf.getJdbcUrl(), conf.getJdbcUser(), conf.getJdbcPassword());

			ps = con.prepareStatement(sqLColumns);
			rs = ps.executeQuery();
			while (rs.next()) {
				classDefinition.setComment(rs.getString("TABLE_COMMENT"));
			}

		} catch (SQLException e) {
			throw new MojoExecutionException("读取数据库失败", e);
		} finally {
			try {
				rs.close();
				ps.close();
				con.close();
			} catch (SQLException e) {
				throw new MojoExecutionException("读取数据库失败", e);
			}
		}
		if (StringUtils.isEmpty(classDefinition.getComment())) {
			throw new MojoExecutionException("表" + conf.getTableSchema() + "." + conf.getTableName() + "不存在或没有表描述信息");
		}
		createFieldDefinition(conf);
	}

	/**
	 * 创建属性描述信息
	 *
	 * @param conf 配置信息
	 */
	private void createFieldDefinition(Configuration conf) throws ClassNotFoundException, MojoExecutionException {
		Class.forName("com.mysql.jdbc.Driver");

		List<FieldDefinition> fieldDefinitions = new ArrayList<>();
		String sqLColumns = "SELECT distinct COLUMN_NAME, DATA_TYPE, COLUMN_COMMENT,COLUMN_KEY,CHARACTER_MAXIMUM_LENGTH" +
				",IS_NULLABLE,COLUMN_DEFAULT,COLUMN_TYPE,ORDINAL_POSITION  FROM information_schema.columns WHERE table_name = '"
				+ conf.getTableName() + "' and table_schema='" + conf.getTableSchema() + "' order by ORDINAL_POSITION";
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = DriverManager.getConnection(conf.getJdbcUrl(), conf.getJdbcUser(), conf.getJdbcPassword());
			ps = con.prepareStatement(sqLColumns);
			rs = ps.executeQuery();
//		支持联合主键
			classDefinition.setPriKeys(new ArrayList<>());
			while (rs.next()) {
				FieldDefinition fieldDefinition = new FieldDefinition();
//          处理is开头字段，根据阿里开发规范去掉is
				String codeName = rs.getString("COLUMN_NAME");
				if (StringUtils.startsWith(codeName, "is_")) {
					codeName = StringUtils.replaceOnce(codeName, "is_", "");
				}
				fieldDefinition.setColumnName(rs.getString("COLUMN_NAME"))
						.setColumnType(rs.getString("DATA_TYPE"))
						.setCodeName(getFieldName(codeName))
						.setClassName(getType(rs.getString("DATA_TYPE"), codeName))
						.setComment(rs.getString("COLUMN_COMMENT"))
						.setIsPriKey("PRI".equals(rs.getString("COLUMN_KEY")))
						.setColumnLength(rs.getInt("CHARACTER_MAXIMUM_LENGTH"))
						.setNullable("YES".equals(rs.getString("IS_NULLABLE")));
//          enum类型的字段需要动态创建
				if ("enum".equalsIgnoreCase(fieldDefinition.getColumnType())) {
					fieldDefinition.setEnumClassName(getClassName(fieldDefinition.getCodeName()) + "Enum")
							.setEnumValues(
									getEnumValues(fieldDefinition.getColumnName(), rs.getString("COLUMN_TYPE"),
											fieldDefinition.getComment()))
							.setClassFullName(StringUtils.join(classDefinition.getPackageName(), ".enums.",
									fieldDefinition.getClassName()));
				}
				if (IGNORE_COLUMNS.contains(fieldDefinition.getColumnName())) {
					continue;
				}
				fieldDefinitions.add(fieldDefinition);
//          设置主键
				if (fieldDefinition.getIsPriKey()) {
					classDefinition.getPriKeys().add(fieldDefinition);
				}
			}
		} catch (SQLException e) {
			throw new MojoExecutionException("读取数据库失败", e);
		} finally {
			try {
				rs.close();
				ps.close();
				con.close();
			} catch (SQLException e) {
				throw new MojoExecutionException("读取数据库失败", e);
			}
		}
		classDefinition.setFieldDefinitions(fieldDefinitions);
	}


	private EnumFieldDefinition[] getEnumValues(String columnName, String columnType, String comment) {
		String[] enumFields = StringUtils.split(StringUtils.substringBetween(columnType, "(", ")")
				.toUpperCase().replace("'", ""), ",");
		List<EnumFieldDefinition> list = new ArrayList<>();
		Map<String, String> enumComments = parseComment(comment);
		if (enumComments == null) {
			log.warn("'{}'注释格式无法解析，不能生成创建enum注解，注释示例：渠道类型(ALI:某宝,JD:东哥)", columnName);
		}
		for (String enumField : enumFields) {
			list.add(new EnumFieldDefinition().setName(enumField).setAnnotation(MapUtils.getString(enumComments, enumField)));
		}
		return list.toArray(new EnumFieldDefinition[enumFields.length]);
	}

	/**
	 * 解析注释
	 * <p>
	 * 注释格式 (Y:是,N:否)
	 *
	 * @param comment 列注释
	 * @return 枚举值:注释的map
	 */
	private Map<String, String> parseComment(String comment) {
		if (StringUtils.isEmpty(comment)) {
			return null;
		}
		//处理中文符号
		comment = comment.replaceAll("（", "(");
		comment = comment.replaceAll("）", ")");
		comment = comment.replaceAll("，", ",");
		comment = comment.replaceAll("：", ":");
		comment = StringUtils.substringBetween(comment, "(", ")");

		if (StringUtils.isEmpty(comment)) {
			return null;
		}
		String[] enums = StringUtils.split(comment, ",");
		if (enums.length == 1) {
			return null;
		}
		Map<String, String> map = new HashMap<>(enums.length);
		for (String enumString : enums) {
			String[] kv = enumString.split(":");
			if (kv.length == 1) {
				return null;
			}
			map.put(kv[0], kv[1]);
		}
		return map;
	}

	private String getType(String dataType, String columnName) {
		switch (dataType.toLowerCase()) {
			case "char":
			case "varchar":
			case "text":
				return "String";
			case "int":
				return "Integer";
			case "long":
			case "bigint":
				return "Long";
			case "decimal":
				return "java.math.BigDecimal";
			case "timestamp":
			case "date":
			case "datetime":
				return "java.time.LocalDateTime";
			case "float":
				return "Float";
			case "double":
				return "Double";
			case "tinyint": {
				return "Boolean";
			}
			case "enum": {
				return StringUtils.join(getClassName(columnName), "Enum");
			}
			default:
				return "String";
		}
	}

	private String getClassName(String name) {
		String[] split = name.split("_");
		if (split.length > 1) {
			StringBuilder sb = new StringBuilder();
			for (String aSplit : split) {
				String tempTableName = aSplit.substring(0, 1).toUpperCase()
						+ aSplit.substring(1).toLowerCase();
				sb.append(tempTableName);
			}
			return sb.toString();
		} else {
			return split[0].substring(0, 1).toUpperCase() + split[0].substring(1);
		}
	}


	private String getFieldName(String columnName) {
		String[] split = columnName.split("_");
		if (split.length > 1) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < split.length; i++) {
				String tempTableName;
				if (i == 0) {
					tempTableName = split[i].substring(0, 1).toLowerCase() + split[i].substring(1);
				} else {
					tempTableName = split[i].substring(0, 1).toUpperCase() + split[i].substring(1);
				}
				sb.append(tempTableName);
			}
			return sb.toString();
		} else {
			return split[0].substring(0, 1).toLowerCase() + split[0].substring(1);
		}
	}

	private void generateFile() throws MojoExecutionException {
		Map<String, Object> context = fileTmplContext.getContext();
		String fileDirPath = fileTmplContext.getFileDirPath();
		try {

			CommonPageParser.writerPage(context, "VO.ftl", fileDirPath, fileTmplContext.getVoPath());
//			CommonPageParser.writerPage(context, "AO.ftl", fileDirPath, fileTmplContext.getAoPath());
			CommonPageParser.writerPage(context, "DAO.ftl", fileDirPath, fileTmplContext.getDaoPath());
//			CommonPageParser.writerPage(context, "DAOImpl.ftl", fileDirPath, fileTmplContext.getDaoImplPath());
			CommonPageParser.writerPage(context, "Service.ftl", fileDirPath, fileTmplContext.getServicePath());
			CommonPageParser.writerPage(context, "ServiceImpl.ftl", fileDirPath, fileTmplContext.getServiceImplPath());
			CommonPageParser.writerPage(context, "Controller.ftl", fileDirPath, fileTmplContext.getControllerPath());
//      生成枚举类型
			for (FieldDefinition fieldDefinition : classDefinition.getFieldDefinitions()) {
				if (!"enum".equals(fieldDefinition.getColumnType())) {
					continue;
				}
				context.put("fieldDefinition", fieldDefinition);
				String enumPath = File.separator + "enums" + File.separator + getClassName(fieldDefinition.getColumnName()) + "Enum.java";
				CommonPageParser.writerPage(context, "Enum.ftl", fileDirPath, enumPath);
			}
		} catch (Exception e) {
			throw new MojoExecutionException("文件生成失败", e);
		}
	}
}
