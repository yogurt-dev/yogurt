package com.github.jyoghurt.common.payment.business.alipay.common.trade.model.builder;


import com.github.jyoghurt.common.payment.business.alipay.common.trade.model.hb.*;
import com.github.jyoghurt.common.payment.business.alipay.common.trade.utils.Utils;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by liuyangkly on 15/8/27.
 */
public class AlipayHeartbeatSynContentBuilder extends RequestBuilder {

    private Product product;

    private Type type;

    @SerializedName("equipment_id")
    private String equipmentId;

    @SerializedName("equipment_status")
    private EquipStatus equipmentStatus;

    private String time;

    @SerializedName("manufacturer_app_id")
    private String manufacturerPid;

    @SerializedName("sys_service_provider_id")
    private String providerId;

    @SerializedName("store_id")
    private String storeId;

    @SerializedName("equipment_position")
    private String equipmentPosition;

    @SerializedName("bbs_position")
    private String bbsPosition;

    @SerializedName("network_status")
    private String networkStatus;

    @SerializedName("network_type")
    private String networkType;

    private String battery;

    @SerializedName("wifi_mac")
    private String wifiMac;

    @SerializedName("wifi_name")
    private String wifiName;

    private String ip;

    private String mac;

    @SerializedName("trade_info")
    private List<TradeInfo> tradeInfoList;

    @SerializedName("exception_info")
    private List<ExceptionInfo> exceptionInfoList;

    @SerializedName("extend_info")
    private Map<String, Object> extendInfo;

    @Override
    public boolean validate() {
        if (product == null) {
            throw new NullPointerException("product should not be NULL!");
        }
        if (type == null) {
            throw new NullPointerException("type should not be NULL!");
        }
        if (StringUtils.isEmpty(equipmentId)) {
            throw new NullPointerException("equipment_id should not be NULL!");
        }
        if (equipmentStatus == null) {
            throw new NullPointerException("equipment_status should not be NULL!");
        }
        if (StringUtils.isEmpty(time)) {
            throw new NullPointerException("time should not be NULL!");
        }
        return true;
    }

    public String getMac() {
        return mac;
    }

    public AlipayHeartbeatSynContentBuilder setMac(String mac) {
        this.mac = mac;
        return this;
    }

    public String getNetworkType() {
        return networkType;
    }

    public AlipayHeartbeatSynContentBuilder setNetworkType(String networkType) {
        this.networkType = networkType;
        return this;
    }

    public String getBattery() {
        return battery;
    }

    public AlipayHeartbeatSynContentBuilder setBattery(String battery) {
        this.battery = battery;
        return this;
    }

    public String getWifiMac() {
        return wifiMac;
    }

    public AlipayHeartbeatSynContentBuilder setWifiMac(String wifiMac) {
        this.wifiMac = wifiMac;
        return this;
    }

    public String getWifiName() {
        return wifiName;
    }

    public AlipayHeartbeatSynContentBuilder setWifiName(String wifiName) {
        this.wifiName = wifiName;
        return this;
    }

    public String getNetworkStatus() {
        return networkStatus;
    }

    public AlipayHeartbeatSynContentBuilder setNetworkStatus(String networkStatus) {
        this.networkStatus = networkStatus;
        return this;
    }

    public String getBbsPosition() {
        return bbsPosition;
    }

    public AlipayHeartbeatSynContentBuilder setBbsPosition(String bbsPosition) {
        this.bbsPosition = bbsPosition;
        return this;
    }

    public String getManufacturerPid() {
        return manufacturerPid;
    }

    public AlipayHeartbeatSynContentBuilder setManufacturerPid(String manufacturerPid) {
        this.manufacturerPid = manufacturerPid;
        return this;
    }

    public String getProviderId() {
        return providerId;
    }

    public AlipayHeartbeatSynContentBuilder setProviderId(String providerId) {
        this.providerId = providerId;
        return this;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public AlipayHeartbeatSynContentBuilder setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
        return this;
    }

    public String getEquipmentPosition() {
        return equipmentPosition;
    }

    public AlipayHeartbeatSynContentBuilder setEquipmentPosition(String equipmentPosition) {
        this.equipmentPosition = equipmentPosition;
        return this;
    }

    public EquipStatus getEquipmentStatus() {
        return equipmentStatus;
    }

    public AlipayHeartbeatSynContentBuilder setEquipmentStatus(EquipStatus equipmentStatus) {
        this.equipmentStatus = equipmentStatus;
        return this;
    }

    public List<ExceptionInfo> getExceptionInfoList() {
        return exceptionInfoList;
    }

    public AlipayHeartbeatSynContentBuilder setExceptionInfoList(List<ExceptionInfo> exceptionInfoList) {
        this.exceptionInfoList = exceptionInfoList;
        return this;
    }

    public Map<String, Object> getExtendInfo() {
        return extendInfo;
    }

    public AlipayHeartbeatSynContentBuilder setExtendInfo(Map<String, Object> extendInfo) {
        this.extendInfo = extendInfo;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public AlipayHeartbeatSynContentBuilder setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public Product getProduct() {
        return product;
    }

    public AlipayHeartbeatSynContentBuilder setProduct(Product product) {
        this.product = product;
        return this;
    }

    public String getStoreId() {
        return storeId;
    }

    public AlipayHeartbeatSynContentBuilder setStoreId(String storeId) {
        this.storeId = storeId;
        return this;
    }

    public String getTime() {
        return time;
    }

    public AlipayHeartbeatSynContentBuilder setTime(String time) {
        this.time = time;
        return this;
    }

    public List<TradeInfo> getTradeInfoList() {
        return tradeInfoList;
    }

    public AlipayHeartbeatSynContentBuilder setSysTradeInfoList(List<SysTradeInfo> sysTradeInfoList) {
        if (Utils.isListNotEmpty(sysTradeInfoList)) {
            this.tradeInfoList = new ArrayList<TradeInfo>(sysTradeInfoList);
        }
        return this;
    }

    public AlipayHeartbeatSynContentBuilder setPosTradeInfoList(List<PosTradeInfo> posTradeInfoList) {
        if (Utils.isListNotEmpty(posTradeInfoList)) {
            this.tradeInfoList = new ArrayList<TradeInfo>(posTradeInfoList);
        }
        return this;
    }

    public Type getType() {
        return type;
    }

    public AlipayHeartbeatSynContentBuilder setType(Type type) {
        this.type = type;
        return this;
    }
}
