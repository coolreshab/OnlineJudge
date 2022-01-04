package com.coolreshab.onlineJudge.entity.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@DynamoDBTable(tableName = "SubmissionHistory")
@ToString
@Getter
@Setter
@Builder
public class SubmissionHistoryEntity {

    private static final String HASH_KEY = "submissionId";
    private static final String GLOBAL_SECONDARY_INDEX_NAME = "user-submission-index";

    private static final String SOLUTION_LANGUAGE = "solutionLanguage";
    private static final String PROBLEM_ID = "problemId";
    private static final String USER_HANDLE = "userHandle";
    private static final String SOLUTION = "solution";
    private static final String TIME_STAMP = "timeStamp";
    private static final String VERDICT = "verdict";
    private static final String AVG_EXECUTION_TIME = "avgExecutionTime";
    private static final String AVG_MEMORY_USED = "avgMemoryUsed";
    private static final String LAST_TEST_CASE_EXECUTED = "lastTestCaseExecuted";
    private static final String PROCESS_RETURN_CODE = "processReturnCode";
    private static final String CONTEST_ID = "contestId";

    @DynamoDBHashKey(attributeName = HASH_KEY)
    private Long submissionId;

    @DynamoDBAttribute(attributeName = SOLUTION_LANGUAGE)
    private String solutionLanguage;

    @DynamoDBAttribute(attributeName = PROBLEM_ID)
    private String problemId;

    @DynamoDBIndexHashKey(attributeName = USER_HANDLE, globalSecondaryIndexName = GLOBAL_SECONDARY_INDEX_NAME)
    private String userHandle;

    @DynamoDBIndexRangeKey(attributeName = CONTEST_ID, globalSecondaryIndexName = GLOBAL_SECONDARY_INDEX_NAME)
    private String contestId;

    @DynamoDBAttribute(attributeName = SOLUTION)
    private String solution;

    @DynamoDBAttribute(attributeName = TIME_STAMP)
    private Long timeStamp;

    @DynamoDBAttribute(attributeName = VERDICT)
    private String verdict;

    @DynamoDBAttribute(attributeName = AVG_EXECUTION_TIME)
    private Double avgExecutionTime;

    @DynamoDBAttribute(attributeName = AVG_MEMORY_USED)
    private Double avgMemoryUsed;

    @DynamoDBAttribute(attributeName = LAST_TEST_CASE_EXECUTED)
    private Integer lastTestCaseExecuted;

    @DynamoDBAttribute(attributeName = PROCESS_RETURN_CODE)
    private Integer processReturnCode;

}
