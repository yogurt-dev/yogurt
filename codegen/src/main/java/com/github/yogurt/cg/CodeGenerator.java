package com.github.yogurt.cg;

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
import org.jooq.codegen.GenerationTool;
import org.jooq.meta.jaxb.Configuration;
import org.jooq.meta.jaxb.Generator;
import org.jooq.meta.jaxb.Jdbc;
import org.springframework.util.FileSystemUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
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

	private static final List<String> IGNORE_COLUMNS = Arrays.asList("creator_id", "modifier_id", "is_deleted", "gmt_create", "gmt_modified");

	@Override
	public void execute() throws MojoExecutionException {
		try {
			Configuration configuration = loadConfig();
			//调用jooq生成所需文件
			executeJooqCodegen();
			//初始化类描述信息
			createClassDefinition(configuration);
			//创建文件
			generateFile(configuration);
			//删除jooq生成的多余daos
			postHandle(configuration);
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}


	/**
	 * 调用jooq代码生成器
	 */
	private void executeJooqCodegen() throws MojoExecutionException {
		executeMojo(
				plugin(
						groupId("org.jooq"),
						artifactId("jooq-codegen-maven"),
						version("3.11.5"),
						dependencies(
								dependency(
										groupId("mysql"),
										artifactId("mysql-connector-java"),
										version("5.1.47")),
//                              因为需要用到JooqGeneratorStrategy，所以将codegen引入
								dependency(
										groupId("com.github.yogurt"),
										artifactId("codegen"),
										version("2.0.0-SNAPSHOT")),
								dependency(
										groupId("org.apache.commons"),
										artifactId("commons-lang3"),
										version("3.7"))
						)
				),
				goal("generate"),
				configuration(
						element(name("configurationFile"), "src/main/resources/jooqConfig.xml")
				),
				executionEnvironment(
						project,
						session,
						pluginManager
				)
		);
	}

	/**
	 * 创建类描述信息
	 *
	 * @param configuration jooq配置信息
	 */
	private void createClassDefinition(Configuration configuration) throws SQLException, ClassNotFoundException {
		Generator generator = configuration.getGenerator();
		classDefinition = new ClassDefinition().setPackageName(generator.getTarget().getPackageName())
				.setClassName(getClassName(generator.getDatabase().getIncludes()));
		createTableDesc(configuration);

	}


	/**
	 * 读取jooq的配置文件
	 */
	private Configuration loadConfig() {
		File file = new File(configurationFile);

		if (!file.isAbsolute()) {
			file = new File(project.getBasedir(), configurationFile);
		}
		try (FileInputStream in = new FileInputStream(file)) {
			return GenerationTool.load(in);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 创建表描述信息
	 *
	 * @param configuration 配置信息
	 */
	private void createTableDesc(Configuration configuration) throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		Jdbc jdbc = configuration.getJdbc();
		Generator generator = configuration.getGenerator();
		String sqLColumns = "select TABLE_SCHEMA,TABLE_NAME,TABLE_COMMENT from information_schema.`TABLES` where table_name = '" + generator.getDatabase().getIncludes() + "' "
				+ "and table_schema='" + generator.getDatabase().getInputSchema() + "' ";
		Connection con = DriverManager.getConnection(jdbc.getUrl(), jdbc.getUser(), jdbc.getPassword());
		PreparedStatement ps = con.prepareStatement(sqLColumns);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			classDefinition.setComment(rs.getString("TABLE_COMMENT"));
		}
		rs.close();
		ps.close();
		con.close();
		createFieldDefinition(configuration);
	}

	/**
	 * 创建属性描述信息
	 *
	 * @param configuration 配置信息
	 */
	private void createFieldDefinition(Configuration configuration) throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		Jdbc jdbc = configuration.getJdbc();
		Generator generator = configuration.getGenerator();
		List<FieldDefinition> fieldDefinitions = new ArrayList<>();
		String sqLColumns = "SELECT distinct COLUMN_NAME, DATA_TYPE, COLUMN_COMMENT,COLUMN_KEY,CHARACTER_MAXIMUM_LENGTH" +
				",IS_NULLABLE,COLUMN_DEFAULT,COLUMN_TYPE,ORDINAL_POSITION  FROM information_schema.columns WHERE table_name = '" + generator.getDatabase().getIncludes() + "' "
				+ "and table_schema='" + generator.getDatabase().getInputSchema() + "' order by ORDINAL_POSITION";
		Connection con = DriverManager.getConnection(jdbc.getUrl(), jdbc.getUser(), jdbc.getPassword());
		PreparedStatement ps = con.prepareStatement(sqLColumns);
		ResultSet rs = ps.executeQuery();
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
					.setNullable("NO".equals(rs.getString("IS_NULLABLE")));
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
		rs.close();
		ps.close();

		con.close();
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

	private void generateFile(Configuration configuration) throws Exception {
//        此处使用jooq的配置文件及加载
		Generator generator = configuration.getGenerator();
		String table = generator.getDatabase().getIncludes();
		String className = classDefinition.getClassName();

		String lowerName = className.substring(0, 1).toLowerCase() + className.substring(1);

		// java路径
		String javaPath = File.separator + configuration.getGenerator().getTarget().getDirectory() + File.separator;

		String poPath = File.separator + "po" + File.separator + className + "PO.java";


		String daoPath = File.separator + "dao" + File.separator + className + "DAO.java";

		String daoImplPath = File.separator + "dao" + File.separator + "impl" + File.separator + className + "DAOImpl.java";

		String servicePath = File.separator + "service" + File.separator + className + "Service.java";

		String serviceImplPath = File.separator + "service" + File.separator + "impl" + File.separator + className + "ServiceImpl.java";

		String controllerPath = File.separator + "controller" + File.separator + className + "Controller.java";
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

		String fileDirPath = basedir + javaPath + replaceSeparator(classDefinition.getPackageName());
		log.info("\n                                      " +
				"\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" +
				"\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" +
				"\n@@@@@  @@@@@  @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" +
				"\n@@@@@@  @@@  @@@@    @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" +
				"\n@@@@@@@  @  @@@  @@@  @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" +
				"\n@@@@@@@@@  @@@@  @@@  @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" +
				"\n@@@@@@@@  @@@@@@@   @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" +
				"\n@@@@@@@  @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" +
				"\n@@@@@@  @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" +
				"\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" +
				"\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" +
				"\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" +
				"\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" +
				"\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" +
				"\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" +
				"\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" +
				"\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" +
				"\n                                      ");
		CommonPageParser.writerPage(context, "PO.ftl", fileDirPath, poPath);
		CommonPageParser.writerPage(context, "DAO.ftl", fileDirPath, daoPath);
		CommonPageParser.writerPage(context, "DAOImpl.ftl", fileDirPath, daoImplPath);
		CommonPageParser.writerPage(context, "Service.ftl", fileDirPath, servicePath);
		CommonPageParser.writerPage(context, "ServiceImpl.ftl", fileDirPath, serviceImplPath);
		CommonPageParser.writerPage(context, "Controller.ftl", fileDirPath, controllerPath);
//      生成枚举类型
		for (FieldDefinition fieldDefinition : classDefinition.getFieldDefinitions()) {
			if (!"enum".equals(fieldDefinition.getColumnType())) {
				continue;
			}
			context.put("fieldDefinition", fieldDefinition);
			String enumPath = File.separator + "enums" + File.separator + getClassName(fieldDefinition.getColumnName()) + "Enum.java";
			CommonPageParser.writerPage(context, "Enum.ftl", fileDirPath, enumPath);
		}

	}

	/**
	 * 后处理
	 * 1.删除jooq多余文件，jooq目前无法通过配置方式解决
	 * 2.DefaultSchema的引用改为yogurt的
	 */

	private void postHandle(Configuration configuration) throws Exception {
		String path = StringUtils.join(basedir, File.separator, configuration.getGenerator().getTarget().getDirectory()
				, File.separator, replaceSeparator(classDefinition.getPackageName()));
		FileSystemUtils.deleteRecursively(new File(StringUtils.join(path, File.separator, "tables")));
		String jooqPath = replaceSeparator(StringUtils.join(path, ".dao.jooq."));
		//删除并替换为yogurt的DefaultSchema
		FileSystemUtils.deleteRecursively(new File(StringUtils.join(jooqPath, "DefaultCatalog.java")));
		FileSystemUtils.deleteRecursively(new File(StringUtils.join(jooqPath, "DefaultSchema.java")));

		File file = new File(StringUtils.join(jooqPath, File.separator, classDefinition.getClassName(), ".java"));
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
		CharArrayWriter caw = new CharArrayWriter();
		String line;
		//以行为单位进行遍历
		while ((line = br.readLine()) != null) {
			//替换每一行中符合被替换字符条件的字符串
			line = line.replaceAll("DefaultSchema", "com.github.yogurt.core.dao.jooq.DefaultSchema");
			//将该行写入内存
			caw.write(line);
			//添加换行符，并进入下次循环
			caw.append(System.getProperty("line.separator"));
		}
		//关闭输入流
		br.close();

		//将内存中的流写入源文件
		FileWriter fw = new FileWriter(file);
		caw.writeTo(fw);
		fw.close();


	}

	private String replaceSeparator(String s) {
		return s.replaceAll("\\.", Matcher.quoteReplacement(File.separator));
	}

}
