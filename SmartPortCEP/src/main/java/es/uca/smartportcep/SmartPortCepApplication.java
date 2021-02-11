package es.uca.smartportcep;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import utils.EsperUtils;

@SpringBootApplication
public class SmartPortCepApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartPortCepApplication.class, args);
        new EsperUtils();
    }

}
