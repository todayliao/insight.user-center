package com.insight.usercenter.config;

import com.github.pagehelper.PageHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author 宣炳刚
 * @date 2018/2/2
 * @remark
 */
@Configuration
public class PageHelperConfig {

    /**
     * 配置mybatis的分页插件pageHelper
     *
     * @return PageHelper
     */
    @Bean
    public PageHelper pageHelper() {
        Properties properties = new Properties();
        properties.setProperty("offsetAsPageNum", "true");
        properties.setProperty("rowBoundsWithCount", "true");
        properties.setProperty("reasonable", "true");
        properties.setProperty("dialect", "mysql");

        PageHelper pageHelper = new PageHelper();
        pageHelper.setProperties(properties);
        return pageHelper;
    }
}
