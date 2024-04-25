package com.siggebig.services;

import org.springframework.core.io.InputStreamResource;

import java.io.InputStream;

/**
 * Helper class to enable the sending of InputStream as MultipartFile using RestTemplate
 */
public class MultipartInputStreamFileResource extends InputStreamResource {

    private final String filename;

    MultipartInputStreamFileResource(InputStream inputStream, String filename) {
        super(inputStream);
        this.filename = filename;
    }

    @Override
    public String getFilename() {
        return this.filename;
    }

    @Override
    public long contentLength() {
        return -1; // We do not know the content length
    }
}

