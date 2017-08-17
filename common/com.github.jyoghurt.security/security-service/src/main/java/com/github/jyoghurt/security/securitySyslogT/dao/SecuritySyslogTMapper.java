package com.github.jyoghurt.security.securitySyslogT.dao;

import com.github.jyoghurt.security.securitySyslogT.domain.AbnormalLogContentT;
import com.github.jyoghurt.security.securitySyslogT.domain.SecuritySyslogT;
import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * SecuritySyslogT Mapper
 *
 */
public interface SecuritySyslogTMapper extends BaseMapper<SecuritySyslogT> {

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    SecuritySyslogT selectById(@Param(ENTITY_CLASS) Class<SecuritySyslogT> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<SecuritySyslogT> pageData(@Param(ENTITY_CLASS) Class<SecuritySyslogT> entityClass,@Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<SecuritySyslogT> findAll(@Param(ENTITY_CLASS) Class<SecuritySyslogT> entityClass, @Param(DATA) Map<String, Object> data);
    @Select("SELECT\n" +
            "\tsysLog.founderId,\n" +
            "\tsysLog.ipAddress,\n" +
            "\tsysLog.founderName,\n" +
            "\tsysLog.abnormalLogContent,\n" +
            "\tMAX(sysLog.modifyDateTime) AS modifyDateTime,\n" +
            "\tcount(sysLog.abnormalLogContent) AS count\n" +
            "FROM\n" +
            "\t(\n" +
            "\t\tSELECT\n" +
            "\t\t\t*\n" +
            "\t\tFROM\n" +
            "\t\t\tSecuritySyslogT ss\n" +
            "\t\tWHERE\n" +
            "\t\t\tss.abnormalLogContent <> ''\n" +
            "\t\tAND FROM_UNIXTIME(\n" +
            "\t\t\tUNIX_TIMESTAMP(ss.modifyDateTime),\n" +
            "\t\t\t'%H'\n" +
            "\t\t) >= #{startTime}\n" +
            "\t\tAND FROM_UNIXTIME(\n" +
            "\t\t\tUNIX_TIMESTAMP(ss.modifyDateTime),\n" +
            "\t\t\t'%H'\n" +
            "\t\t) < #{endTime}\n" +
            "\t) sysLog\n" +
            "WHERE\n" +
            "\tsysLog.modifyDateTime >= str_to_date(#{startDate}, '%Y-%m-%d')\n" +
            "AND sysLog.modifyDateTime < str_to_date(#{endDate}, '%Y-%m-%d')\n" +
            "GROUP BY\n" +
            "\tsysLog.abnormalLogContent,\n" +
            "\tsysLog.founderName\n" +
            "HAVING\n" +
            "\tcount(sysLog.abnormalLogContent) >= #{count}")
    List<AbnormalLogContentT> abnormalLogList(@Param("startDate") String startDate,@Param("endDate") String endDate,
                                          @Param("count") int count,@Param("startTime") int startTime,@Param
                                                      ("endTime") int endTime);
}
