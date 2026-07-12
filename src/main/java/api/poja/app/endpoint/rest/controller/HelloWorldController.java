package api.poja.app.endpoint.rest.controller;

import api.poja.app.service.HelloWorldService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class HelloWorldController {
  private final HelloWorldService service;

  @GetMapping("/hello")
  public String helloWorld(@RequestParam String name) {
    return service.uploadHelloWorldMessage(name);
  }
}
