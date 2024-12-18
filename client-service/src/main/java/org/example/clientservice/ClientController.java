package org.example.clientservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.function.client.WebClient;

@Controller
public class ClientController {
    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);

    private final WebClient webClient;

    public ClientController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080/api/messages").build();
    }

    @GetMapping("/")
    public String home() {
        logger.info("Rendering the form for message submission");
        return "index";
    }

    @PostMapping("/send-message")
    public ResponseEntity<String> sendMessage(String message, Model model) {
        logger.info("Received message in Client Service: {}", message);

        String response = webClient.post()
                .uri(uriBuilder -> uriBuilder.queryParam("message", message).build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
        logger.info("Received response from Disruptor Service: {}", response);

        model.addAttribute("response", response);
        return ResponseEntity.ok(response);
    }
}

