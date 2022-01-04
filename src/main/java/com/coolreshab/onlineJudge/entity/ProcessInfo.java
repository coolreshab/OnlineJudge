package com.coolreshab.onlineJudge.entity;

import com.coolreshab.onlineJudge.constants.Verdict;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ProcessInfo {

    private long executionTime;
    private long memoryUsed;
    private int exitCode;
    private Verdict processVerdict;
}
