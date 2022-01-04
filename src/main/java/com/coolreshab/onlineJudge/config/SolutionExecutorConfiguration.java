package com.coolreshab.onlineJudge.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static com.coolreshab.onlineJudge.constants.SolutionExecutorConstants.COMPILER_FILE_NAME;

@Configuration
public class SolutionExecutorConfiguration {

    @Bean
    public Map<String, String> languageToFileTypeMapper() {
        Map<String, String> languageToFileTypeMap = new HashMap<>();
        languageToFileTypeMap.put("c++", ".cpp");
        languageToFileTypeMap.put("c", ".c");
        languageToFileTypeMap.put("java", ".java");
        languageToFileTypeMap.put("python", ".py");
        return languageToFileTypeMap;
    }

    @Bean
    public Map<String, String> languageToCompileCommandMapper(
            @Qualifier("languageToFileTypeMapper") Map<String, String> languageToFileTypeMapper) {

        Map<String, String> languageToCompileCommandMap = new HashMap<>();
        languageToCompileCommandMap.put("c++", "g++ -std=c++17 -o " + COMPILER_FILE_NAME + " " +
                COMPILER_FILE_NAME + languageToFileTypeMapper.get("c++"));
        languageToCompileCommandMap.put("c", "gcc -o " + COMPILER_FILE_NAME + " " +
                COMPILER_FILE_NAME + languageToFileTypeMapper.get("c"));
        languageToCompileCommandMap.put("java", "javac " + COMPILER_FILE_NAME + languageToFileTypeMapper.get("java"));
        languageToCompileCommandMap.put("python", "python -m py_compile " + COMPILER_FILE_NAME +
                languageToFileTypeMapper.get("python"));
        return languageToCompileCommandMap;
    }

    @Bean
    public Map<String, String> languageToRunCommandMapper(
            @Qualifier("languageToFileTypeMapper") Map<String, String> languageToFileTypeMapper) {

        Map<String, String> languageToRunCommandMap = new HashMap<>();
        languageToRunCommandMap.put("c++", "./" + COMPILER_FILE_NAME);
        languageToRunCommandMap.put("c", "./" + COMPILER_FILE_NAME);
        languageToRunCommandMap.put("java", "java " + COMPILER_FILE_NAME);
        languageToRunCommandMap.put("python", "python " + COMPILER_FILE_NAME + languageToFileTypeMapper.get("python"));
        return languageToRunCommandMap;
    }

}
