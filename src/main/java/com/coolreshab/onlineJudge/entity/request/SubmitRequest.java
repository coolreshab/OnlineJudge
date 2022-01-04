package com.coolreshab.onlineJudge.entity.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
@Setter
@ToString
public class SubmitRequest {

    private String solutionLanguage;
    private String problemId;
    private String userHandle;
    private String solution;
    private String contestId;
    private Long timeStamp;
    //TODO Decouple timelimit , memorylimit and submission id from the request POJO and get it from a ProblemTable
    private Long timeLimitInMillis;
    private Long memoryLimitInKB;
    private Long submissionId;

    public void validateSubmitRequest() {
        if (solutionLanguage == null || problemId == null || userHandle == null ||
                solution == null || timeStamp == null || timeLimitInMillis == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Null Param Found");
        }
    }

}
