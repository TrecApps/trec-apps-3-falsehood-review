package com.trecapps.falsehoods.falsehoodReview.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
public class StorageClient {

    //Logger

    @Autowired
    WebClient client;

    @Value("${storage.url}")
    String baseStorageUrl;

    Mono<String> getFalsehoodContents(String id)
    {
        return client.get().uri(baseStorageUrl + "/download")
                .header("FileId", id)
                .header("App", "Falsehoods")
                .exchangeToMono((ClientResponse resp) ->{
                    if(resp.statusCode().isError())
                    {
                        // To-Do: Log Error

                        throw new WebClientResponseException(resp.rawStatusCode(),
                                resp.statusCode().getReasonPhrase(), resp.headers().asHttpHeaders(), null, null);
                    }
                    return resp.bodyToMono(String.class);
                });
    }

}
