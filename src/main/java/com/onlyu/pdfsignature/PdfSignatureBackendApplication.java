package com.onlyu.pdfsignature;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PdfSignatureBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PdfSignatureBackendApplication.class, args);
    }

}
