package com.siggebig.services;

import com.siggebig.models.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserService {

    private final RestTemplate restTemplate;
    private final String userServiceUrl = "http://localhost:8090/api/users";

    public UserService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public User getUserByUserId(Long id) {
        String url = "http://localhost:8090/api/users/" + id;
        return restTemplate.getForObject(url, User.class);
    }

    // this is scabby, do not like this , will work for now
    public User getUserByUsername(String username) {
        String url = "http://localhost:8090/api/users/username/" + username;
        return restTemplate.getForObject(url, User.class);
    }

//    public User updateUser(User user, String token) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", token);
//        HttpEntity<User> request = new HttpEntity<>(user, headers);
//        String url = "http://localhost:8090/api/users/update/" + user.getId();
//        ResponseEntity<User> response = restTemplate.exchange(url,
//                HttpMethod.PUT, request, User.class);
//        if (response.getStatusCode().is2xxSuccessful()) {
//            return response.getBody();
//        } else {
//            throw new RuntimeException("Failed to update user");
//        }
//    }

}
