package io.github.bhhan.example.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.context.config.ContextFunctionCatalogAutoConfiguration;
import org.springframework.cloud.stream.annotation.EnableBinding;

@SpringBootApplication(exclude = ContextFunctionCatalogAutoConfiguration.class)
@EnableBinding(EventStream.class)
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
