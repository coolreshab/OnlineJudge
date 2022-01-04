package com.coolreshab.onlineJudge.entity;

import com.coolreshab.onlineJudge.constants.Verdict;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class GraderVerdictInfo {

    private Verdict graderVerdict;
    private Double avgExecutionTime;
    private Double avgMemoryUsed;
    private Integer lastTestCaseExecuted;
    private Integer processReturnCode;

    public static GraderVerdictInfo buildGraderVerdictInfo(Verdict verdict, double totalTimeTaken,
                                                           double totalMemoryUsed, int lastTestCase, int exitCode) {
        GraderVerdictInfo graderVerdictInfo = null;
        if (verdict.equals(Verdict.TIME_LIMIT_EXCEEDED)) {
            graderVerdictInfo = GraderVerdictInfo.builder().graderVerdict(Verdict.TIME_LIMIT_EXCEEDED).
                    avgMemoryUsed(totalMemoryUsed / lastTestCase).lastTestCaseExecuted(lastTestCase).build();
        } else if (verdict.equals(Verdict.MEMORY_LIMIT_EXCEEDED)) {
            graderVerdictInfo = GraderVerdictInfo.builder().graderVerdict(Verdict.MEMORY_LIMIT_EXCEEDED).
                    avgExecutionTime(totalTimeTaken / lastTestCase).lastTestCaseExecuted(lastTestCase).build();
        } else if (verdict.equals(Verdict.RUNTIME_ERROR)) {
            graderVerdictInfo = GraderVerdictInfo.builder().graderVerdict(Verdict.RUNTIME_ERROR).
                    avgMemoryUsed(totalMemoryUsed / lastTestCase).avgExecutionTime(totalTimeTaken / lastTestCase).
                    lastTestCaseExecuted(lastTestCase).processReturnCode(exitCode).build();
        } else if (verdict.equals(Verdict.WRONG_ANSWER)) {
            graderVerdictInfo = GraderVerdictInfo.builder().graderVerdict(Verdict.WRONG_ANSWER).
                    avgMemoryUsed(totalMemoryUsed / lastTestCase).avgExecutionTime(totalTimeTaken / lastTestCase).
                    lastTestCaseExecuted(lastTestCase).build();
        } else if (verdict.equals(Verdict.ACCEPTED)) {
            graderVerdictInfo = GraderVerdictInfo.builder().graderVerdict(Verdict.ACCEPTED).
                    avgMemoryUsed(totalMemoryUsed / lastTestCase).
                    avgExecutionTime(totalTimeTaken / lastTestCase).build();
        }
        return graderVerdictInfo;
    }
}
