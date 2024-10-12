package com.zerowhisper.shorturlserver.controller;

import com.zerowhisper.shorturlserver.service.URLShortenerService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class URLShortenerController {
    private final URLShortenerService urlShortenerService;

    @Value("${serverID}")
    private String serverId;

    @PostMapping("/shorten")
    public ResponseEntity<?> shorten(@RequestBody final String url) {
        System.out.println(serverId + ", " + url);
        return ResponseEntity.ok(urlShortenerService.shorten(url));
    }

    @GetMapping("/get-url-entity")
    public ResponseEntity<?> findByShortenedURL(@RequestParam("surl") String shortenedURL) {
        System.out.println(serverId + ", " + shortenedURL);
        return ResponseEntity.ok(urlShortenerService.findByShortenedURL(shortenedURL));
    }

    @GetMapping("/get-url")
    public void redirectToOriginalURL(@RequestParam("surl") String shortenedURL, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_FOUND); // 302 status code
        response.setHeader("Location", urlShortenerService.findByShortenedURL(shortenedURL).getOriginalURL());
    }
}
