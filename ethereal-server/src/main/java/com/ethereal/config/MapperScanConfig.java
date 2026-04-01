package com.ethereal.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.ethereal.module.**.mapper")
public class MapperScanConfig {
}
