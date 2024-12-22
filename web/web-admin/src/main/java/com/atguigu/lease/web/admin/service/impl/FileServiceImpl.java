package com.atguigu.lease.web.admin.service.impl;


import com.atguigu.lease.common.minio.MinioProperties;
import com.atguigu.lease.web.admin.service.FileService;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.*;
import io.minio.SetBucketPolicyArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


@Service
public class FileServiceImpl implements FileService {
    @Autowired
    MinioProperties properties;

    @Autowired
    MinioClient minioClient;

    @Override
    public String upload(MultipartFile file) {

        try {
            //桶的名字
            String bucketName = properties.getBucketName();
            //判断桶是否存在
            boolean result = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!result) {
                //创建桶
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                String policy ="""
                        {
                          "Statement" : [ {
                            "Action" : "s3:GetObject",
                            "Effect" : "Allow",
                            "Principal" : "*",
                            "Resource" : "arn:aws:s3:::%s/*"
                          } ],
                          "Version" : "2012-10-17"
                        }
                        """.formatted(bucketName);

                //设置访问策略
                    minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucketName).config(policy).build());
            }else{
                //桶已存在
            }


            //上传文件
            //1.设置文件别名 在桶容器中保证文件名字具备一致性
            String fileName = new SimpleDateFormat("yyyyMMdd").format(new Date() ) + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .stream(file.getInputStream(),file.getSize(),-1)
                    .object(file.getContentType())
                    .build());


            return String.join("/",properties.getEndpoint(),properties.getBucketName(),fileName);

        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }





    /*
    /把图片上传到minio
     */

}
