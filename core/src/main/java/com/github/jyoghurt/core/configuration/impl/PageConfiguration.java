package com.github.jyoghurt.core.configuration.impl;

import com.github.jyoghurt.core.configuration.PageConvert;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Administrator on 2015/8/28.
 */
@Configuration
public class PageConfiguration {

    @Bean(name = "pageConvert")
    @Conditional(DataTablePageService.class)
    public PageConvert dataTablePageConvert(){
        return new DataTablePageService();
    }

    @Bean(name = "pageConvert")
    @Conditional(EasyUIPageService.class)
    public PageConvert easyUIConvert(){
        return new DataTablePageService();
    }

    @Bean(name = "pageConvert")
    @Conditional(DonkishPageService.class)
    public PageConvert donkishConvert(){
        return new DonkishPageService();
    }
}
