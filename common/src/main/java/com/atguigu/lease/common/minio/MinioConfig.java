package com.atguigu.lease.common.minio;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
* 配置类: 管理,创建 MinioClient
* */
@Configuration
public class MinioConfig {
      @Autowired
    MinioProperties properties;


      //连接,登录 minio
      @Bean
    public MinioClient minioClient(){
          return MinioClient.builder()
                  .endpoint(properties.getEndpoint())
                  .credentials(properties.getAccessKey(), properties.getSecretKey())
                  .build();
      }
}
