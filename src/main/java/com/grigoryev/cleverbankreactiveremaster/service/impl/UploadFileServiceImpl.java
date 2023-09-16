package com.grigoryev.cleverbankreactiveremaster.service.impl;

import com.grigoryev.cleverbankreactiveremaster.exception.internalservererror.UploadFileException;
import com.grigoryev.cleverbankreactiveremaster.service.UploadFileService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Service
public class UploadFileServiceImpl implements UploadFileService {

    @Override
    public void uploadCheck(String check) {
        Path path = Paths.get(findPaths("BankCheck.txt"));
        writeFile(check, path);
    }

    @Override
    public void uploadStatement(String statement) {
        Path path = Paths.get(findPaths("TransactionStatement.txt"));
        writeFile(statement, path);
    }

    @Override
    public void uploadAmount(String amount) {
        Path path = Paths.get(findPaths("AmountStatement.txt"));
        writeFile(amount, path);
    }

    private static void writeFile(String file, Path path) {
        try {
            Files.write(path, file.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        } catch (IOException e) {
            throw new UploadFileException("Sorry! We got Server upload file problems");
        }
    }

    private static String findPaths(String fileName) {
        return "src/main/resources/check/"
                .concat(fileName);
    }

}
