package api.poja.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@PojaGenerated
@EnableAsync
public class PojaApplication {

  public static void main(String[] args) {
    SpringApplication.run(PojaApplication.class, args);
  }
}
