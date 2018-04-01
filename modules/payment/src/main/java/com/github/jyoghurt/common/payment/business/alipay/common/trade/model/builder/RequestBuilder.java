package com.github.jyoghurt.common.payment.business.alipay.common.trade.model.builder;


import com.github.jyoghurt.common.payment.business.alipay.common.trade.utils.GsonFactory;

/**
 * Created by liuyangkly on 15/7/31.
 */
public abstract class RequestBuilder {

    // 验证bizContent对象
    public abstract boolean validate();

    // 将bizContent对象转换为json字符串
    public String toJsonString() {
        // 使用gson将对象转换为json字符串
        /**
         * See https://sites.google.com/site/gson/gson-user-guide#TOC-Using-Gson
         * Object Examples

         class BagOfPrimitives {
         private int value1 = 1;
         private String value2 = "abc";
         private transient int value3 = 3;
         BagOfPrimitives() {
         // no-args constructor
         }
         }

         (Serialization)
         BagOfPrimitives obj = new BagOfPrimitives();
         Gson gson = new Gson();
         String json = gson.toJson(obj);
         ==> json is {"value1":1,"value2":"abc"}
         */
        return GsonFactory.getGson().toJson(this);
    }
}
