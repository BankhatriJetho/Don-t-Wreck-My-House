package com.dwmyhouse;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Spring configuration class
 * Scans the com.dwmyhouse package for Spring components and loads application properties
 */
@Configuration
@ComponentScan("com.dwmyhouse")
@PropertySource("classpath:application.properties")
public class AppConfig {

}
