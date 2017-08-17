package com.github.jyoghurt.wechatbasic.common.util;

import java.util.HashMap;
import java.util.Map;

public class MapUtil {
    public static Map<String, Double> transformWorldGeoLatLngToChina(
            Map<String, Double> latLonMap) {
        Map<String, Double> resultMap = new HashMap<String, Double>(2);
        final double a = 6378245.0;
        final double ee = 0.00669342162296594323;
        double wgLat = latLonMap.get("latitude");
        double wgLon = latLonMap.get("longitude");

        if (outOfChinaOfLatLng(latLonMap)) {
            return latLonMap;
        }

        double dLat = transformLatOfXY(wgLon - 105.0, wgLat - 35.0);
        double dLon = transformLonOfXY(wgLon - 105.0, wgLat - 35.0);
        double radLat = wgLat / 180.0 * Math.PI;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0)
                / ((a * (1 - ee)) / (magic * sqrtMagic) * Math.PI);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * Math.PI);
        resultMap.put("latitude", wgLat + dLat);
        resultMap.put("longitude", wgLon + dLon);
        return resultMap;

    }

    public static Map<String, Double> transChina2BD(
            Map<String, Double> latLonMap) {
        Map<String, Double> resultMap = new HashMap<String, Double>(2);
        double x_pi = 3.14159265358979324 * 3000.0 / 180.0;
        double x = latLonMap.get("longitude"), y = latLonMap.get("latitude");
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
        resultMap.put("latitude", z * Math.sin(theta) + 0.006);
        resultMap.put("longitude", z * Math.cos(theta) + 0.0065);
        return resultMap;
    }

    public static boolean outOfChinaOfLatLng(Map<String, Double> latLonMap) {
        double lat = latLonMap.get("latitude");
        double lon = latLonMap.get("longitude");

        if (lon < 72.004 || lon > 137.8347)
            return true;
        if (lat < 0.8293 || lat > 55.8271)
            return true;
        return false;
    }

    public static double transformLatOfXY(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y
                + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x
                * Math.PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * Math.PI) + 40.0 * Math.sin(y / 3.0
                * Math.PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * Math.PI) + 320 * Math.sin(y
                * Math.PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    public static double transformLonOfXY(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1
                * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x
                * Math.PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * Math.PI) + 40.0 * Math.sin(x / 3.0
                * Math.PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * Math.PI) + 300.0 * Math.sin(x
                / 30.0 * Math.PI)) * 2.0 / 3.0;
        return ret;
    }

}
