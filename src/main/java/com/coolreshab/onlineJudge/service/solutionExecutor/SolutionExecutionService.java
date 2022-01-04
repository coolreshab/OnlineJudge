package com.coolreshab.onlineJudge.service.solutionExecutor;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.coolreshab.onlineJudge.constants.Verdict;
import com.coolreshab.onlineJudge.entity.GraderVerdictInfo;
import com.coolreshab.onlineJudge.entity.dynamodb.SubmissionHistoryEntity;
import com.coolreshab.onlineJudge.entity.request.SubmitRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SolutionExecutionService {

    private static final Logger logger = LoggerFactory.getLogger(SolutionExecutionService.class);

    private final SolutionCompilerService compilerService;
    private final SolutionGraderService graderService;
    private final DynamoDBMapper dynamoDBMapper;

    public SolutionExecutionService(SolutionCompilerService compilerService,
                                    SolutionGraderService graderService,
                                    DynamoDBMapper dynamoDBMapper) {
        this.compilerService = compilerService;
        this.graderService = graderService;
        this.dynamoDBMapper = dynamoDBMapper;
    }

    public void executeSolution(SubmitRequest request) {
        request.setSubmissionId(getNextSubmissionId());
        Verdict compilationVerdict = compilerService.getCompilationVerdict(request);
        SubmissionHistoryEntity submissionHistoryEntity = compilationVerdict.equals(Verdict.COMPILATION_SUCCESS) ?
                getSubmissionHistoryEntity(graderService.getGraderVerdict(request), request) :
                getSubmissionHistoryEntity(compilationVerdict, request);
        logger.info("Saving submission results {} in submission history table", submissionHistoryEntity);
        dynamoDBMapper.save(submissionHistoryEntity);
    }

    private SubmissionHistoryEntity getSubmissionHistoryEntity(Verdict compilerVedict,
                                                               SubmitRequest submitRequest) {
        SubmissionHistoryEntity submissionHistoryEntity = getSubmissionHistoryEntity(submitRequest);
        submissionHistoryEntity.setVerdict(compilerVedict.getVerdictCode());
        return submissionHistoryEntity;
    }

    private SubmissionHistoryEntity getSubmissionHistoryEntity(GraderVerdictInfo graderVerdictInfo,
                                                               SubmitRequest submitRequest) {
        SubmissionHistoryEntity submissionHistoryEntity = getSubmissionHistoryEntity(submitRequest);
        submissionHistoryEntity.setVerdict(graderVerdictInfo.getGraderVerdict().getVerdictCode());
        submissionHistoryEntity.setAvgExecutionTime(graderVerdictInfo.getAvgExecutionTime());
        submissionHistoryEntity.setAvgMemoryUsed(graderVerdictInfo.getAvgMemoryUsed());
        submissionHistoryEntity.setLastTestCaseExecuted(graderVerdictInfo.getLastTestCaseExecuted());
        submissionHistoryEntity.setProcessReturnCode(graderVerdictInfo.getProcessReturnCode());
        return submissionHistoryEntity;
    }

    private SubmissionHistoryEntity getSubmissionHistoryEntity(SubmitRequest request) {
        return SubmissionHistoryEntity.builder().submissionId(request.getSubmissionId()).
                problemId(request.getProblemId()).solution(request.getSolution()).
                solutionLanguage(request.getSolutionLanguage()).timeStamp(request.getTimeStamp()).
                userHandle(request.getUserHandle()).build();
    }

    private Long getNextSubmissionId() {
        // TODO Implement a synchronised submissionId generator
        return System.currentTimeMillis();
    }

}
