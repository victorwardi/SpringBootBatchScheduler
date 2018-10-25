package br.com.correios.capturaeventossro;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class CapturaEventosSroApplication {

    public static void main(String[] args) {
        SpringApplication.run(CapturaEventosSroApplication.class, args);
    }
}
