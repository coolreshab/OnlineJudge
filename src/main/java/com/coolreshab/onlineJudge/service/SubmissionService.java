package com.coolreshab.onlineJudge.service;

import com.coolreshab.onlineJudge.dao.SubmissionHistoryDAO;
import com.coolreshab.onlineJudge.entity.request.SubmitRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SubmissionService {

    private static final Logger logger = LoggerFactory.getLogger(SubmissionService.class);

    private final SubmissionHistoryDAO submissionHistoryDAO;

    public SubmissionService(SubmissionHistoryDAO submissionHistoryDAO) {
        this.submissionHistoryDAO = submissionHistoryDAO;
    }

    public void submitSolution(SubmitRequest submitRequest) {
        // Generate UUID and create a SubmissionHistoryEntity
        submissionHistoryDAO.saveSubmissionData(submitRequest);


    }
}
