package com.github.jyoghurt.core.configuration.impl;

import com.github.jyoghurt.core.configuration.PageConvert;
import com.github.jyoghurt.core.utils.SpringContextUtils;

import static com.github.jyoghurt.core.configuration.PageConvert.DataTable;
import static com.github.jyoghurt.core.configuration.PageConvert.Donkish;
import static com.github.jyoghurt.core.configuration.PageConvert.EasyUI;

/**
 * Created by Administrator on 2015/8/28.
 */
//@Configuration
public class PageConfiguration {

    private static PageConvert pageConvert;

    public static PageConvert create() {
        if (pageConvert != null) {
            return pageConvert;
        }
        switch (SpringContextUtils.getProperty("tableJsLib")) {
            case DataTable: {
                pageConvert = new DataTablePageService<>();
                break;
            }
            case Donkish: {
                pageConvert = new DonkishPageService<>();
                break;
            }
            case EasyUI: {
                pageConvert = new DataTablePageService<>();
                break;
            }
            default: {
                pageConvert = (PageConvert) SpringContextUtils.getBean(SpringContextUtils.getProperty("tableJsLib")+"PageService");
            }
        }
        return pageConvert;
    }

//
//    @Bean(name = "pageConvert")
//    @Conditional(DataTablePageService.class)
//    public PageConvert dataTablePageConvert(){
//        return new DataTablePageService();
//    }
//
//    @Bean(name = "pageConvert")
//    @Conditional(EasyUIPageService.class)
//    public PageConvert easyUIConvert(){
//        return new DataTablePageService();
//    }
//
//    @Bean(name = "pageConvert")
//    @Conditional(DonkishPageService.class)
//    public PageConvert donkishConvert(){
//        return new DonkishPageService();
//    }


}
