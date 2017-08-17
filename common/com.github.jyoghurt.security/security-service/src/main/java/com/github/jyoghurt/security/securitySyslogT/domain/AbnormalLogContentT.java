package com.github.jyoghurt.security.securitySyslogT.domain;

/**
 * @Project: 驴鱼社区-车险帮
 * @Package: com.df.security.securitySyslogT.domain
 * @Description:
 * @author: baoxiaobing@lvyushequ.com
 * @date: 2016-02-23 14:43
 */
public class AbnormalLogContentT {

    private String founderId;

    private String founderName;

    private String ipAddress;

    private String abnormalLogContent;

    private String count;

    public String getModifyDateTime() {
        return modifyDateTime;
    }

    public void setModifyDateTime(String modifyDateTime) {
        this.modifyDateTime = modifyDateTime;
    }

    private String modifyDateTime;

    public String getModifyTime_start() {
        return modifyTime_start;
    }

    public void setModifyTime_start(String modifyTime_start) {
        this.modifyTime_start = modifyTime_start;
    }

    public String getModifyTime_end() {
        return modifyTime_end;
    }

    public void setModifyTime_end(String modifyTime_end) {
        this.modifyTime_end = modifyTime_end;
    }

    private String modifyTime_start;

    private String modifyTime_end;

    public String getFounderId() {
        return founderId;
    }

    public void setFounderId(String founderId) {
        this.founderId = founderId;
    }

    public String getFounderName() {
        return founderName;
    }

    public void setFounderName(String founderName) {
        this.founderName = founderName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getAbnormalLogContent() {
        return abnormalLogContent;
    }

    public void setAbnormalLogContent(String abnormalLogContent) {
        this.abnormalLogContent = abnormalLogContent;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
