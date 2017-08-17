package com.github.jyoghurt.excel.util;

import com.github.jyoghurt.excel.annotations.ExportExcel;
import com.github.jyoghurt.excel.annotations.ImportExcel;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by limiao on 2016/5/9.
 */
public class TestBean1 {

    private int id;


    @ImportExcel(order = 1)
    private String name;


    @ImportExcel(order = 2)
    private int password;

    @ExportExcel(title = "日期", order = 3)
    @ImportExcel(order = 3)
    private Date date;

    @ExportExcel(title = "BigDecimal", order = 5)
    @ImportExcel(order = 5)
    //, javaScriptBody = "if(param>5){return 1000;}else{return 2000}"
    private BigDecimal big;

    @ImportExcel(order = 6, javaScriptBody = "return param=='是';")
    private boolean flag;


    public TestBean1() {
    }

    public TestBean1(String name, int password, Date date, BigDecimal big) {
        this.name = name;
        this.password = password;
        this.date = date;
        this.big = big;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ExportExcel(title = "姓名", order = 6)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ExportExcel(title = "密码", order = 2)
    public int getPassword() {
        return password;
    }

    public void setPassword(int password) {
        this.password = password;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public BigDecimal getBig() {
        return big;
    }

    public void setBig(BigDecimal big) {
        this.big = big;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
