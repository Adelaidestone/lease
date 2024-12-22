package com.atguigu.lease.common.minio;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
//属性类：读取配置文件中minio的配置信息
@ConfigurationProperties(prefix = "minio")
@Component
@Data
public class MinioProperties {
    private String endpoint;
    private String bucketName;
    private String accessKey;
    private String secretKey;
}
