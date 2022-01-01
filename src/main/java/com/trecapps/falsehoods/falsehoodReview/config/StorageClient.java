package com.trecapps.falsehoods.falsehoodReview.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
public class StorageClient {

    //Logger
    WebClient client;
    
    public StorageClient()
    {
        client = WebClient.builder().build();
    }

    @Value("${storage.url}")
    String baseStorageUrl;

    public Mono<String> getContents(String id, String app)
    {
        return client.get().uri(baseStorageUrl + "/download")
                .header("FileId", id)
                .header("App", app)
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

    public Mono<String> SubmitDocument(String name, String contents, String account)
    {
        return client.post().uri(baseStorageUrl + "/upload")
                .header("FileName", name)
                .header("App", "Falsehoods")
                .header("Content-type", "document")
                .header("Extension", "md")
                .header("Account", account)
                .body(contents, String.class)
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

    public Mono<String> SubmitJson(String name, String contents, String account, String app)
    {
        return client.post().uri(baseStorageUrl + "/upload")
                .header("FileName", name)
                .header("App", app)
                .header("Content-type", "json")
                .header("Extension", "json")
                .header("Account", account)
                .body(contents, String.class)
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
