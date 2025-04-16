package com.dwmyhouse;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.nio.file.Path;

@Configuration
@ComponentScan("com.dwmyhouse")
@PropertySource("classpath:application.properties")
public class AppConfig {

}
