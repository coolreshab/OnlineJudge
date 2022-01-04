package com.coolreshab.onlineJudge.service.solutionExecutor;

import com.coolreshab.onlineJudge.constants.SolutionExecutorConstants;
import com.coolreshab.onlineJudge.constants.Verdict;
import com.coolreshab.onlineJudge.entity.request.SubmitRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class SolutionCompilerService {

    private static final String SPACE = " ";
    private static final Logger logger = LoggerFactory.getLogger(SolutionCompilerService.class);

    private final Map<String, String> languageToCompileCommandMapper;
    private final Map<String, String> languageToFileTypeMapper;

    public SolutionCompilerService(
            @Qualifier("languageToCompileCommandMapper") Map<String, String> languageToCompileCommandMapper,
            @Qualifier("languageToFileTypeMapper") Map<String, String> languageToFileTypeMapper) {

        this.languageToCompileCommandMapper = languageToCompileCommandMapper;
        this.languageToFileTypeMapper = languageToFileTypeMapper;
    }

    public Verdict getCompilationVerdict(SubmitRequest request) {
        try {
            createSubmissionFile(request);
            ProcessBuilder processBuilder = new ProcessBuilder(splitString(languageToCompileCommandMapper.
                    get(request.getSolutionLanguage())));
            File submissionDirectory = getSubmissionDirectory(request.getSubmissionId());
            processBuilder.directory(submissionDirectory);
            File compileErrorFile = new File(submissionDirectory.getPath() + File.separator +
                    SolutionExecutorConstants.COMPILATION_ERROR_FILE_NAME);
            processBuilder.redirectError(compileErrorFile);
            processBuilder.start().waitFor();
            return compileErrorFile.length() > 0 ? Verdict.COMPILATION_ERROR : Verdict.COMPILATION_SUCCESS;

        } catch (Exception e) {
            logger.error("Exception occurred during the compilation process for request {}", request, e);
            return Verdict.FAILURE;
        }
    }

    private void createSubmissionFile(SubmitRequest request) throws Exception {
        File submissionDirectory = getSubmissionDirectory(request.getSubmissionId());
        if (submissionDirectory.mkdirs()) {
            File submissionFile = new File(submissionDirectory.getPath() + File.separator +
                    SolutionExecutorConstants.COMPILER_FILE_NAME +
                    languageToFileTypeMapper.get(request.getSolutionLanguage()));
            try (FileWriter fileWriter = new FileWriter(submissionFile)) {
                fileWriter.write(request.getSolution());
            }
        } else {
            throw new Exception("Failed to create directory for submission file");
        }
    }

    private File getSubmissionDirectory(Long submissionId) {
        return new File(String.format(SolutionExecutorConstants.COMPILATION_PATH_FORMAT, submissionId));
    }

    private List<String> splitString(String command) {
        return Arrays.asList(command.split(SPACE));
    }

}
