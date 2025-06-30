package com.blog.Services;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class CloudinaryService {
    
    @Autowired
    private Cloudinary cloudinary;

    public void deleteImage(String publicId) throws IOException {
        Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        if (!"ok".equals(result.get("result"))) {
            throw new IOException("Failed to delete image: " + publicId + ". Cloudinary response: " + result);
        }
    }
}
