package com.siggebig.services;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class PictureService {

    private final RestTemplate restTemplate;

    public PictureService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Long> uploadPictures(MultipartFile[] files) throws IOException {
        String url = "http://localhost:8091/api/pictures/upload/";

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", "Bearer your-token-here"); // Set the authorization token if needed

        // Prepare body
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        Arrays.stream(files).forEach(file -> {
            try {
                // Only add file if it's not empty and convert it to a resource
                if (!file.isEmpty()) {
                    body.add("files", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Prepare the request entity
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Make the request
        ResponseEntity<List<Long>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<List<Long>>() {}
        );

        // Return the response body
        return response.getBody();
    }
}

