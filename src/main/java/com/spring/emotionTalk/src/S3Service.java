package com.spring.emotionTalk.src;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.IOException;

@Service
@NoArgsConstructor
public class S3Service {

    private AmazonS3 s3Client;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static String ACCESS_KEY;
    @Value("${cloud.aws.credentials.accessKey}")
    public void setACCESS_KEY(String ACCESS_KEY){
        this.ACCESS_KEY = ACCESS_KEY;
    }

    private static String SECRET_KEY;
    @Value("${cloud.aws.credentials.secretKey}")
    public void setSECRET_KEY(String SECRET_KEY){
        this.SECRET_KEY = SECRET_KEY;
    }

    private static String BUCKET;
    @Value("${cloud.aws.s3.bucket}")
    public void setBUCKET(String BUCKET){
        this.BUCKET = BUCKET;
    }

    private static String REGION;
    @Value("${cloud.aws.region.static}")
    public void setREGION(String REGION){
        this.REGION = REGION;
    }

    public String upload(MultipartFile file) throws IOException {
        AWSCredentials credentials = new BasicAWSCredentials(this.ACCESS_KEY, this.SECRET_KEY);

        s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(this.REGION)
                .build();

        String fileName = file.getOriginalFilename();

        if (fileName.equals(""))
            return "";

        ObjectMetadata metadata = new ObjectMetadata();
        s3Client.putObject(new PutObjectRequest(BUCKET, fileName, file.getInputStream(), metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        return s3Client.getUrl(BUCKET, fileName).toString();

    }
}
