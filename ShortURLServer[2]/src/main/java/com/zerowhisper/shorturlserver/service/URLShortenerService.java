package com.zerowhisper.shorturlserver.service;

import com.zerowhisper.shorturlserver.entity.URL;
import com.zerowhisper.shorturlserver.repository.URLRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class URLShortenerService {
    private final URLRepository urlRepository;
    private final SnowflakeGenerator snowflakeGenerator;
    private final Environment environment;

    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    // Example function to generate Snowflake ID (use an actual Snowflake ID library)
    private long generateSnowflakeID() {
        return snowflakeGenerator.nextId();
    }

    // Method to convert a long (Snowflake ID) to Base62
    private String encodeBase62(long snowflakeID) {
        StringBuilder sb = new StringBuilder();
        while (snowflakeID > 0) {
            int remainder = (int) (snowflakeID % 62);
            sb.append(BASE62.charAt(remainder));
            snowflakeID /= 62;
        }
        return sb.reverse().toString();
    }

    // Method to shorten URL (limit Base62 to 7-8 characters)
    private String shortenURL(long snowflakeID) {
        String base62Encoded = encodeBase62(snowflakeID);
        // Truncate to first 7-8 characters for the shortened URL
        return base62Encoded.length() > 8
                ? base62Encoded.substring(0, 8)
                : base62Encoded;
    }

    public URL shorten(String url) {
        System.out.println(environment.getProperty("local.server.port"));
        url = url.substring(1, url.length() - 1);
        if (checkAvailability(url)) {
            System.out.println("URL already exists: " + url);
            return findByOriginalURL(url);
        }
        return urlRepository.save(buildURL(url));
    }

    private boolean checkAvailability(String url) {
        return urlRepository.findByOriginalURL(url).isPresent();
    }

    private URL buildURL(String url) {
        if (urlRepository.findByOriginalURL(url).isPresent()) {
            return urlRepository.findByOriginalURL(url).get();
        }
        URL newURL = new URL();
        long newSnowflake = generateSnowflakeID();
        newURL.setId(newSnowflake);
        newURL.setOriginalURL(url.replace("\"", ""));
        newURL.setShortenedURL(shortenURL(newSnowflake));
        return newURL;
    }

    public URL findByShortenedURL(String shortenedURL) {
        return urlRepository.findByShortenedURL(shortenedURL)
                .orElseThrow(() -> new RuntimeException("URL not found " + shortenedURL));
    }

    public URL findByOriginalURL(String originalURL) {
        return urlRepository.findByOriginalURL(originalURL)
                .orElseThrow(() -> new RuntimeException("URL not found " + originalURL));
    }
}
