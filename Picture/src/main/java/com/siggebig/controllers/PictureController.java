package com.siggebig.controllers;

import com.siggebig.exceptions.PictureNotFoundException;
import com.siggebig.models.Picture;
import com.siggebig.models.User;
import com.siggebig.repositorys.PictureRepository;
import com.siggebig.services.AccessService;
import com.siggebig.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/pictures")
public class PictureController {

    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AccessService accessService;

    @GetMapping("/{pictureId}")
    public ResponseEntity<byte[]> getPictureById(@PathVariable("pictureId") long pictureId) {

        Picture picture = pictureRepository.findById(pictureId)
                .orElseThrow(() -> new PictureNotFoundException("Picture not found with id: " + pictureId));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        return new ResponseEntity<>(picture.getData(), headers, HttpStatus.OK);

    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<?> uploadUserPicture(
            @PathVariable("userId") long userId,
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String token
            ) throws IOException {

        User user = userService.getUserByUserId(userId);

        accessService.verifyUserToken(user, token);

        if(user.getPictureId()==null) {
            Picture picture = new Picture();
            picture.setData(file.getBytes());
            Picture savedPic = pictureRepository.save(picture);
            user.setPictureId(savedPic.getId());

            userService.updateUser(user, token);

            return new ResponseEntity<>(HttpStatus.OK);
        }

        Picture picture = pictureRepository.findById(user.getPictureId())
                .orElseThrow(() -> new PictureNotFoundException("Picture not found with id: " + user.getPictureId()));


        picture.setData(file.getBytes());
        pictureRepository.save(picture);

        return new ResponseEntity<>(HttpStatus.OK);
    }


}
