package dev.stanislavskyi.feedback_bot_vgr.trello_api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class TrelloService {

    private final WebClient webClient;

    @Value("${trello.api.key}")
    private String apiKey;

    @Value("${trello.api.token}")
    private String apiToken;

    private final static String BASE_URL = "https://api.trello.com/1";

    private final static String TODAY_LIST = "68b5d6666078be1bee9c765a";

    public TrelloService(WebClient.Builder webClientBuilder){
        this.webClient = webClientBuilder.baseUrl(BASE_URL).build();
    }

    public Mono<String> createCard(String name, String description) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/cards")
                        .queryParam("key", apiKey)
                        .queryParam("token", apiToken)
                        .queryParam("idList", TODAY_LIST)
                        .queryParam("name", name)
                        .queryParam("desc", description)
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> response.get("id").toString());
    }
}
