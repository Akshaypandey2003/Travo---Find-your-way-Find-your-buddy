package com.user.Service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    
      String uploadImage(MultipartFile contactImg,String fileName);
     
      String getUrlFromPublicId (String publicId);

       void removeImage(String publicId);
}
