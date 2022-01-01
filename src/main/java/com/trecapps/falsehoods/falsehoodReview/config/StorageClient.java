package com.trecapps.falsehoods.falsehoodReview.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
public class StorageClient {

    //Logger
    RestTemplate client;
    
    public StorageClient()
    {
        client = new RestTemplate();
    }

    @Value("${storage.url}")
    String baseStorageUrl;

    public ResponseEntity<String> getContents(String id, String app)
    {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("FileId", id);
        headers.add("App", app);
        return client.exchange(baseStorageUrl + "/download", HttpMethod.GET,new HttpEntity(headers),String.class);
    }

    public ResponseEntity<String> SubmitDocument(String name, String contents, String account)
    {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("App", "Falsehoods");
        headers.add("FileName", name);
        headers.add("App", "Falsehoods");
        headers.add("Content-type", "document");
        headers.add("Extension", "md");
        headers.add("Account", account);

        return client.exchange(baseStorageUrl + "/upload",HttpMethod.POST,new HttpEntity<>(contents, headers),String.class);
    }

    public ResponseEntity<String> SubmitJson(String name, String contents, String account, String app)
    {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

        headers.add("FileName", name);
        headers.add("App", app);
        headers.add("Content-type", "json");
        headers.add("Extension", "json");
        headers.add("Account", account);
        return client.exchange(baseStorageUrl + "/upload",HttpMethod.POST,new HttpEntity<>(contents, headers),String.class);
    }

}
