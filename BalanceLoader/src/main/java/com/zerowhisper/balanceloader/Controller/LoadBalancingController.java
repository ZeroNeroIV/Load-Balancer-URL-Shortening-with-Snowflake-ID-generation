package com.zerowhisper.balanceloader.Controller;

import com.zerowhisper.balanceloader.Service.LoadBalancerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequiredArgsConstructor
public class LoadBalancingController {
    private final List<String> servers = List.of("http://localhost:8081", "http://localhost:8082", "http://localhost:8083");
    private final AtomicInteger counter = new AtomicInteger(0);
    private final LoadBalancerService loadBalancerService;

    @PostMapping("/shorten")
    public Mono<String> handleEndpoint1(@RequestBody String body) {
        return balance("shorten", null, null, body, HttpMethod.POST);
    }

    @GetMapping("/get-url-entity")
    public Mono<String> handleEndpoint2(@RequestParam("surl") String param) {
        return balance("get-url-entity", "surl", param, null, HttpMethod.GET);
    }

    @GetMapping("/get-url")
    public Mono<String> handleEndpoint3(@RequestParam("surl") String param) {
        return balance("get-url", "surl", param, null, HttpMethod.GET);
    }

    @RequestMapping(value = "/{path}")
    public Mono<String> balance(@PathVariable String path, String paramName, @RequestParam String param, @RequestBody Object body, HttpMethod httpMethod) {
        int serverIndex = counter.incrementAndGet() % servers.size();
        return loadBalancerService.tryNextServer(path, httpMethod, paramName, param, body, serverIndex, servers, servers.size());
    }
}
