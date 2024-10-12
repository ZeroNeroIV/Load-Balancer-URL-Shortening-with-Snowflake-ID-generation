package com.zerowhisper.balanceloader.Service;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class LoadBalancerService {
    public Mono<String> tryNextServer(String path,
                                      HttpMethod method,
                                      String paramName,
                                      String param,
                                      Object requestBody,
                                      int serverIndex,
                                      List<String> servers,
                                      int retries) {
        // If all retries are exhausted, return an error
        if (retries == 0) {
            return Mono.error(new RuntimeException("All servers are down"));
        }

        // Select the server based on the counter and wrap it in WebClient
        String server = servers.get(serverIndex);
        WebClient webClient = WebClient.create(server);

        if (method == HttpMethod.GET) {
            return get(webClient, path, paramName, param, serverIndex, servers, retries);
        }
        return webClient
                .post()
                .uri("/" + path)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(RuntimeException.class, e -> {
                    int nextServerIndex = (serverIndex + 1) % servers.size();
                    return tryNextServer(path, method, paramName, param, requestBody, nextServerIndex, servers, retries - 1);
                });
    }

    public Mono<String> get(WebClient webClient,
                            String path,
                            String paramName,
                            String param,
                            int serverIndex,
                            List<String> servers,
                            int retries) {
        return webClient
                .get()
                .uri(urlBuilder -> urlBuilder
                        .path("/" + path)
                        .queryParam(paramName, param)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(RuntimeException.class, e -> {
                    int nextServerIndex = (serverIndex + 1) % servers.size();
                    return get(webClient, path, paramName, param, nextServerIndex, servers, retries - 1);
                });
    }
}