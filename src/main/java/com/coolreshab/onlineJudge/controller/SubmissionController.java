package com.coolreshab.onlineJudge.controller;

import com.coolreshab.onlineJudge.entity.dynamodb.SubmissionHistoryEntity;
import com.coolreshab.onlineJudge.entity.request.SubmitRequest;
import com.coolreshab.onlineJudge.service.SubmissionService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @GetMapping("/{name}")
    public String getGreetings(@PathVariable("name") String name) {
        return "Gutan Tag " + name;
    }

    @PostMapping(value = "/submit", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public List<SubmissionHistoryEntity> submitSolution(SubmitRequest submitRequest) {
        submitRequest.validateSubmitRequest();
        submissionService.submitSolution(submitRequest);
        return submissionService
    }

}
