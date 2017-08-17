package com.github.jyoghurt.activiti.business.utils;

import com.github.jyoghurt.activiti.business.enums.CompontentShowType;
import com.github.jyoghurt.activiti.business.module.Button;
import com.github.jyoghurt.activiti.business.module.FormProperties;
import net.sf.json.JSONObject;

/**
 * user:DELL
 * date:2017/5/18.
 */
public class ParseButtonUtil {

    public static void parseButtonRule(JSONObject user, JSONObject var, CompontentShowType showType, FormProperties formProperties) {
        if (null == formProperties || null == formProperties.getButtons() || formProperties.getButtons().size() == 0) {
            return;
        }
        for (Button button : formProperties.getButtons()) {
            button.setFlag(JsUtil.executeRule(user, var, showType, button.getBtnRule()));
        }
    }
}
