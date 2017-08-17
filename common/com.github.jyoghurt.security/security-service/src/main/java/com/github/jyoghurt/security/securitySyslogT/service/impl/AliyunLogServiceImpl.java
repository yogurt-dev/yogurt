package com.github.jyoghurt.security.securitySyslogT.service.impl;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.QueriedLog;
import com.aliyun.openservices.log.request.GetHistogramsRequest;
import com.aliyun.openservices.log.request.GetLogsRequest;
import com.aliyun.openservices.log.response.GetHistogramsResponse;
import com.aliyun.openservices.log.response.GetLogsResponse;
import com.github.jyoghurt.dataDict.service.DataDictUtils;
import com.github.jyoghurt.security.securitySyslogT.dao.SecuritySyslogTMapper;
import com.github.jyoghurt.security.securitySyslogT.domain.SecuritySyslogT;
import com.github.jyoghurt.security.securitySyslogT.service.SecuritySyslogTService;
import com.github.jyoghurt.core.exception.BaseErrorException;
import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.result.QueryResult;
import com.github.jyoghurt.core.service.impl.ServiceSupport;
import com.google.gson.Gson;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jtwu on 2017/7/5.
 */
@Service("aliyunLogService")
public class AliyunLogServiceImpl extends ServiceSupport<SecuritySyslogT, SecuritySyslogTMapper> implements SecuritySyslogTService {


    @Override
    public QueryResult<SecuritySyslogT> getData(SecuritySyslogT entity, QueryHandle queryHandle) {

        String endpoint = DataDictUtils.getDataDictValue("aliyunConfig", "aliyunConfig_endpoint").getDictValueName(); // 选择与上面步骤创建
        // project 所属区域匹配的
        // Endpoint
        String accessKeyId = DataDictUtils.getDataDictValue("aliyunConfig", "aliyunConfig_accessKeyId").getDictValueName(); // 使用您的阿里云访问密钥 AccessKeyId
        String accessKeySecret = DataDictUtils.getDataDictValue("aliyunConfig", "aliyunConfig_accessKeySecret").getDictValueName(); // 使用您的阿里云访问密钥
        // AccessKeySecret
        String project = DataDictUtils.getDataDictValue("aliyunConfig", "aliyunConfig_logProject").getDictValueName(); // 上面步骤创建的项目名称

        String logstore = null;
        switch (entity.getAccessSystem()) {
            case SERVER:
                logstore = DataDictUtils.getDataDictValue("aliyunConfig", "aliyunConfig_logstore_server").getDictValueName(); // 上面步骤创建的日志库名称
                break;
            case MANAGER:
                logstore = DataDictUtils.getDataDictValue("aliyunConfig", "aliyunConfig_logstore_manager").getDictValueName(); // 上面步骤创建的日志库名称
                break;

        }
        String query = getQuery(entity);

        // 构建一个客户端实例
        Client client = new Client(endpoint, accessKeyId, accessKeySecret);
        // 列出当前 project 下的所有日志库名称
        int from = getStartTime(entity);
        int to = getEndTime(entity);
        try {
            // 查询日志数据
            int log_offset = queryHandle.getPage() * queryHandle.getRows();
            queryHandle.search();
            int log_line = 10;
            GetLogsRequest logsRequest = new GetLogsRequest(project, logstore, from, to, "", query,
                    log_offset, log_line, false);
            GetLogsResponse logsResponse = client.GetLogs(logsRequest);

            // 查询总数
            GetHistogramsRequest histogramsRequest = new GetHistogramsRequest(project, logstore, "", query, from, to);
            GetHistogramsResponse histogramsResponse = client.GetHistograms(histogramsRequest);

            return newQueryResult().setRecordsTotal(histogramsResponse.GetTotalCount()).setData(convert(logsResponse));
        } catch (Exception e) {
            throw new BaseErrorException(e);
        }
    }

    private String getQuery(SecuritySyslogT entity) {
        String query = "";
        String moduleName = entity.getModuleName();
        String duration = entity.getDuration();
        String[] da = duration.split("-");

        if (!StringUtils.isEmpty(moduleName)) {
            query = appendQuery(query,"( moduleName = " + moduleName + "* or LogMessage = " + moduleName + "* )");
        }
        if (!StringUtils.isEmpty(duration)) {
            query = appendQuery(query,"( invokeDuration in [ " + da[0] + " " + da[1] + " ] )");
        }
        return query;
    }

    private String appendQuery(String query, final String appendCondition) {
        if(StringUtils.isNotEmpty(query)){
            query += " and ";
        }
        query += appendCondition;
        return query;
    }
    private List<SecuritySyslogT> convert(GetLogsResponse logsResponse) {
        if (logsResponse == null || CollectionUtils.isEmpty(logsResponse.GetLogs())) {
            return new ArrayList<>();
        }
        List<SecuritySyslogT> result = new ArrayList();
        for (QueriedLog log : logsResponse.GetLogs()) {
            Gson gson = new Gson();
            SecuritySyslogT syslogT = gson.fromJson(log.GetLogItem().ToJsonString(), SecuritySyslogT.class);
            result.add(syslogT);
        }
        return result;

    }

    private int getStartTime(SecuritySyslogT entity) {
        if(StringUtils.isEmpty(entity.getCreateDateTime_start())){
            return 0;
        }
        String startTime = entity.getCreateDateTime_start() + " 00:00:00";
        return parseTime(startTime);
    }

    private int getEndTime(SecuritySyslogT entity) {
        if(StringUtils.isEmpty(entity.getCreateDateTime_end())){
            return (int) (System.currentTimeMillis()/1000);
        }
        String endTime = entity.getCreateDateTime_end() + " 23:59:59";
        return parseTime(endTime);
    }

    private int parseTime(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date d = sdf.parse(time);
            return (int) (d.getTime() / 1000);
        } catch (ParseException e) {
            throw new BaseErrorException(e);
        }
    }

    @Override
    public SecuritySyslogTMapper getMapper() {
        return null;
    }

    @Override
    public SecuritySyslogT find(Serializable id) {
        return null;
    }

}