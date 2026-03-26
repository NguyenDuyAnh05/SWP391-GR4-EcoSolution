package org.swp391_group4_backend.ecosolution.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.swp391_group4_backend.ecosolution.service.ImageService;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryImageService implements ImageService {
    private final Cloudinary cloudinary;
    public CloudinaryImageService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }
    @Override
    public String uploadImage(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return uploadResult.get("url").toString();
    }
}
