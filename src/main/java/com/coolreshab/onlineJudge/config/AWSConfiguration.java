package com.coolreshab.onlineJudge.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSConfiguration {

    @Bean
    public AWSCredentials awsCredentials(@Value("${aws.accessKey}") String awsAccessKey,
                                         @Value("${aws.secretKey}") String awsSecretKey) {
        return new BasicAWSCredentials(awsAccessKey, awsSecretKey);
    }

    @Bean
    public DynamoDBMapper dynamoDBMapper(AWSCredentials awsCredentials,
                                         @Value("${aws.region}") String awsRegion) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(awsRegion)
                .build();
        return new DynamoDBMapper(client);
    }

}
