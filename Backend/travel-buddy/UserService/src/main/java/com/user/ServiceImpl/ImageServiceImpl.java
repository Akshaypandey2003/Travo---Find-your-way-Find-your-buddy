package com.user.ServiceImpl;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.user.Helper.AppConstants;
import com.user.Service.ImageService;

@Service
public class ImageServiceImpl implements ImageService{

   private Cloudinary cloudinary;
  
  //here using constructor injection instead of @Autowired(both will work the same)
    public ImageServiceImpl(Cloudinary cloudinary) {
    this.cloudinary = cloudinary;
  }

    @Override
    public String uploadImage(MultipartFile img,String fileName) {
      //Code to upload image on server

      try {
        // byte [] data = new byte[img.getInputStream().available()];
        //  img.getInputStream().read(data);
        byte[] data = img.getBytes();
         cloudinary.uploader().upload(data, ObjectUtils.asMap(
          "public_id",fileName,
          "resource_type", "auto",
          "quality", "auto:eco"
         ));
         return this.getUrlFromPublicId(fileName);  //returning image url
      } catch (IOException e) {
       
        e.printStackTrace();
        return null;
      }
    }

    @Override
    public String getUrlFromPublicId(String publicId) {

     return cloudinary.url().transformation(
      new Transformation<>().quality("auto:eco")
      .width(AppConstants.CONTACT_IMAGE_WIDTH)
      .height(AppConstants.CONTACT_IMAGE_HEIGHT)
      .crop(AppConstants.CONTACT_IMAGE_CROP)
     ).generate(publicId);
    }  
    
    
    public void removeImage(String publicId) {
      try {
        System.out.println("Removing image with public id: " + publicId);
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
}
