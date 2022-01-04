package com.coolreshab.onlineJudge.service.solutionExecutor;

import com.coolreshab.onlineJudge.constants.SolutionExecutorConstants;
import com.coolreshab.onlineJudge.constants.Verdict;
import com.coolreshab.onlineJudge.entity.GraderVerdictInfo;
import com.coolreshab.onlineJudge.entity.ProcessInfo;
import com.coolreshab.onlineJudge.entity.request.SubmitRequest;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@Service
public class SolutionGraderService {

    private static final String SPACE_REGEX = " +";
    private static final String PROC_STATUS_FILE_PATH_FORMAT = "/proc/%s/status";
    private static final String VM_DATA = "VmData:";
    private static final String VM_STACK = "VmStk:";
    private static final Logger logger = LoggerFactory.getLogger(SolutionGraderService.class);

    private final Map<String, String> languageToRunCommandMapper;

    public SolutionGraderService(
            @Qualifier("languageToRunCommandMapper") Map<String, String> languageToRunCommandMapper) {

        this.languageToRunCommandMapper = languageToRunCommandMapper;
    }

    public GraderVerdictInfo getGraderVerdict(SubmitRequest request) {

        File testDataInputDirectory = new File(String.format(SolutionExecutorConstants.TEST_DATA_PATH_FORMAT,
                request.getProblemId()) + File.separator + SolutionExecutorConstants.INPUT_DIRECTORY_NAME);
        File testDataOutputDirectory = new File(String.format(SolutionExecutorConstants.TEST_DATA_PATH_FORMAT,
                request.getProblemId()) + File.separator + SolutionExecutorConstants.OUTPUT_DIRECTORY_NAME);
        File[] testInputFiles = testDataInputDirectory.listFiles();
        File[] testOutputFiles = testDataOutputDirectory.listFiles();

        if (ArrayUtils.isEmpty(testInputFiles) || ArrayUtils.isEmpty(testOutputFiles)) {
            logger.error("Empty test list found for request {}", request);
            return GraderVerdictInfo.builder().graderVerdict(Verdict.FAILURE).build();
        }

        File submissionDirectory = new File(String.format(SolutionExecutorConstants.COMPILATION_PATH_FORMAT,
                request.getSubmissionId()));
        File generatedOutputFileDirectory = new File(submissionDirectory +
                File.separator + SolutionExecutorConstants.OUTPUT_DIRECTORY_NAME);
        if (!generatedOutputFileDirectory.mkdir()) {
            logger.error("Unable to create output directory {} for request {}", generatedOutputFileDirectory, request);
            return GraderVerdictInfo.builder().graderVerdict(Verdict.FAILURE).build();
        }
        try {
            return runTestData(submissionDirectory, testDataInputDirectory,
                    testDataOutputDirectory, generatedOutputFileDirectory, request);
        } catch (IOException | InterruptedException e) {
            logger.error("Exception occurred during test run for request {}", request, e);
            return GraderVerdictInfo.builder().graderVerdict(Verdict.FAILURE).build();
        }
    }

    private GraderVerdictInfo runTestData(File submissionDirectory,
                                          File testDataInputDirectory,
                                          File testDataOutputDirectory,
                                          File generatedOutputFileDirectory,
                                          SubmitRequest request) throws IOException, InterruptedException {

        String runCommand = languageToRunCommandMapper.get(request.getSolutionLanguage());
        ProcessBuilder processBuilder = new ProcessBuilder(Arrays.asList(runCommand.split(SPACE_REGEX)));
        processBuilder.directory(submissionDirectory);
        double totalTimeTaken = 0, totalMemoryUsed = 0;
        int totalFiles = testDataInputDirectory.listFiles().length;
        for (int i = 1; i <= totalFiles; ++i) {
            File inputFilePath = new File(testDataInputDirectory.getPath() + File.separator +
                    String.format(SolutionExecutorConstants.INPUT_FILE_FORMAT, i));
            processBuilder.redirectInput(inputFilePath);
            File generatedOutputFilePath = new File(generatedOutputFileDirectory.getPath() + File.separator +
                    String.format(SolutionExecutorConstants.OUTPUT_FILE_FORMAT, i));
            processBuilder.redirectOutput(generatedOutputFilePath);
            ProcessInfo processInfo = runProcessUsingLimits(processBuilder.start(), request.getMemoryLimitInKB(),
                    request.getTimeLimitInMillis());

            totalMemoryUsed += processInfo.getMemoryUsed();
            totalTimeTaken += processInfo.getExecutionTime();

            if (processInfo.getProcessVerdict() != Verdict.TEST_RUN_SUCCESS) {
                return GraderVerdictInfo.buildGraderVerdictInfo(processInfo.getProcessVerdict(), totalTimeTaken,
                        totalMemoryUsed, i, processInfo.getExitCode());
            }
            File testOutputFilePath = new File(testDataOutputDirectory.getPath() + File.separator +
                    String.format(SolutionExecutorConstants.OUTPUT_FILE_FORMAT, i));
            if (getFileComparisonVerdict(generatedOutputFilePath, testOutputFilePath).equals(Verdict.WRONG_ANSWER)) {
                return GraderVerdictInfo.buildGraderVerdictInfo(Verdict.WRONG_ANSWER, totalTimeTaken,
                        totalMemoryUsed, i, processInfo.getExitCode());
            }
        }
        return GraderVerdictInfo.buildGraderVerdictInfo(Verdict.ACCEPTED, totalTimeTaken,
                totalMemoryUsed, totalFiles, 0);
    }

    private Verdict getFileComparisonVerdict(File generatedOutputFile, File testOutputFile) throws IOException {
        try (BufferedReader generatedFileReader = new BufferedReader(new FileReader(generatedOutputFile));
             BufferedReader testFileReader = new BufferedReader(new FileReader(testOutputFile))) {
            String generatedOutput, testOutput;
            while ((generatedOutput = generatedFileReader.readLine()) != null &&
                    (testOutput = testFileReader.readLine()) != null) {
                if (!generatedOutput.trim().equals(testOutput.trim())) {
                    return Verdict.WRONG_ANSWER;
                }
            }
            while ((testOutput = testFileReader.readLine()) != null) {
                if (!testOutput.trim().isEmpty()) {
                    return Verdict.WRONG_ANSWER;
                }
            }
            while (generatedOutput != null && generatedOutput.trim().isEmpty()) {
                generatedOutput = generatedFileReader.readLine();
            }
            if (generatedOutput != null) {
                return Verdict.WRONG_ANSWER;
            }
        }
        return Verdict.ACCEPTED;
    }

    private ProcessInfo runProcessUsingLimits(Process process, long memoryLimitInKB, long timeLimitInMillis)
            throws InterruptedException {
        long startTime = System.currentTimeMillis();
        ProcessInfo processInfo = new ProcessInfo();
        while (process.isAlive()) {
            Thread.sleep(Math.min(Math.max(0, timeLimitInMillis - System.currentTimeMillis() + startTime), 100));
            processInfo.setExecutionTime(System.currentTimeMillis() - startTime);
            processInfo.setMemoryUsed(Math.max(processInfo.getMemoryUsed(), getMemoryUsage(process.pid())));
            if (processInfo.getExecutionTime() > timeLimitInMillis) {
                processInfo.setProcessVerdict(Verdict.TIME_LIMIT_EXCEEDED);
                process.destroy();
                return processInfo;
            } else if (processInfo.getMemoryUsed() > memoryLimitInKB) {
                processInfo.setProcessVerdict(Verdict.MEMORY_LIMIT_EXCEEDED);
                process.destroy();
                return processInfo;
            }
        }
        processInfo.setExitCode(process.exitValue());
        processInfo.setProcessVerdict(process.exitValue() != 0 ? Verdict.RUNTIME_ERROR : Verdict.TEST_RUN_SUCCESS);
        return processInfo;
    }

    private long getMemoryUsage(long processId) {
        long vmStack = 0, vmData = 0;
        File statusFile = new File(String.format(PROC_STATUS_FILE_PATH_FORMAT, processId));
        if (!statusFile.exists()) {
            return 0;
        }
        try (BufferedReader procFileReader = new BufferedReader(new FileReader(statusFile))) {
            String statusInfo;
            while ((statusInfo = procFileReader.readLine()) != null) {
                if (statusInfo.startsWith(VM_DATA)) {
                    vmData = Long.parseLong(statusInfo.split(SPACE_REGEX)[1]);
                } else if (statusInfo.startsWith(VM_STACK)) {
                    vmStack = Long.parseLong(statusInfo.split(SPACE_REGEX)[1]);
                }
            }
        } catch (IOException e) {
            logger.error("Swallowing the IO exception of reading proc status file", e);
        }
        return vmStack + vmData;
    }
}
