package com.icm.sna;

import com.mathworks.engine.EngineException;
import com.mathworks.engine.MatlabEngine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

/**
 * 本程序的启动类
 */
@SpringBootApplication
public class SnaApplication {

    /**
     * 将MatlabEngine交由spring管理
     */
    @Bean
    public MatlabEngine startMatlab() {
        try {
            return MatlabEngine.startMatlab();
        } catch (EngineException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
//        System.out.println("Starting");
//        File f=new File("D:\\out.txt");
//        FileOutputStream fileOutputStream = new FileOutputStream(f);
//        PrintStream printStream = new PrintStream(fileOutputStream);
//        System.setOut(printStream);
        SpringApplication.run(SnaApplication.class, args);
    }
}
