package com.github.jyoghurt.excel;

import com.github.jyoghurt.excel.annotations.ExportExcel;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by limiao on 2016/5/9.
 */
public class TestBean {

    private int id;

    @ExportExcel(title = "姓名", order = 1)
    private String name;

    @ExportExcel(title = "密码", order = 2)
    private int password;

    @ExportExcel(title = "日期", order = 3)
    private Date date;

    @ExportExcel(title = "BigDecimal", order = 4)
    private BigDecimal big;


    public TestBean(String name, int password, Date date, BigDecimal big) {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
}
