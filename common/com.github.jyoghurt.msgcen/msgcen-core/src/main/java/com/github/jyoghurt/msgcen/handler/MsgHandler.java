package com.github.jyoghurt.msgcen.handler;

import net.sf.json.JSONObject;

/**
 * Created by limiao on 2016/11/28.
 */
public class MsgHandler {

    public static JSONObject arrayToJson(String[] array) {
        JSONObject jsonObject = new JSONObject();
        if (array == null || array.length == 0) {
            return jsonObject;
        }
        int i = 1;
        for (String a : array) {
            jsonObject.put(i, a);
            i++;
        }
        return jsonObject;
    }
}
