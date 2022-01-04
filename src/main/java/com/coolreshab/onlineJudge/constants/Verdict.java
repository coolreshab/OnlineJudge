package com.coolreshab.onlineJudge.constants;

import lombok.Getter;

@Getter
public enum Verdict {

    COMPILATION_SUCCESS("CompilationSuccess"), COMPILATION_ERROR("CE"),
    WRONG_ANSWER("WA"), ACCEPTED("AC"), RUNTIME_ERROR("RTE"),
    TIME_LIMIT_EXCEEDED("TLE"), MEMORY_LIMIT_EXCEEDED("MLE"),
    FAILURE("Failure"), TEST_RUN_SUCCESS("TestRunSuccess");

    private String verdictCode;

    Verdict(String verdictCode) {
        this.verdictCode = verdictCode;
    }
}
