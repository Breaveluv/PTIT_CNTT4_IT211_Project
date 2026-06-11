package com.example.prj.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dttthytqo",
                "api_key", "247656645656871",
                "api_secret", "A0FrCXCMHm4tVcmEpG15ynymFtQ"
        ));
    }
}
