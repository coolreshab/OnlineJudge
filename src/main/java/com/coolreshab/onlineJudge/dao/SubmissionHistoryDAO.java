package com.coolreshab.onlineJudge.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.coolreshab.onlineJudge.entity.dynamodb.SubmissionHistoryEntity;
import com.coolreshab.onlineJudge.entity.request.SubmitRequest;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class SubmissionHistoryDAO {

    //TODO: Complete the DAO implementation + Pagination

    private final DynamoDBMapper dynamoDBMapper;

    public SubmissionHistoryDAO(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    public List<SubmissionHistoryEntity> getSubmissionDataByUser(String userHandle) {
        return Collections.emptyList();
    }

    public List<SubmissionHistoryEntity> getSubmissionDataByUserAndContest(String userHandle, String contestId) {
        return Collections.emptyList();
    }

    public void saveSubmissionData(SubmitRequest submitRequest) {

    }

}
