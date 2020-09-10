package com.demo.whereby.controller;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Base64;

@Controller
public class ImageUploadController {
    AWSCredentials awsCredentials = new BasicAWSCredentials("AKIARCOFU6RC7CT7HONM",
            "/L6CDMEm9U/nZKqqWXUgIfjV2HQpCq2OOAXLlsIk");

    AmazonS3 s3Client = AmazonS3ClientBuilder
            .standard()
            .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
            .withRegion(Regions.AP_SOUTH_1)
            .build();

    @PostMapping("/saveImage")
    public String saveFile(@RequestParam("userId") int currentUserId, @RequestParam("imgFile") MultipartFile multipartFile, Model model) throws IOException {
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

        ObjectMetadata objectMetaData = new ObjectMetadata();
        objectMetaData.setContentType(multipartFile.getContentType());
        objectMetaData.setContentLength(multipartFile.getSize());

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest("whereby-bucket", fileName, multipartFile.getInputStream(), objectMetaData);
            s3Client.putObject(putObjectRequest);
        } catch (IOException e) {
        }

        return "redirect:/viewImg?fileName="+fileName+"&userId="+currentUserId;
    }

    @GetMapping("/viewImg")
    public String viewImg(@RequestParam("userId") int currentUserId,
                          @RequestParam(value = "fileName", required = false) String fileName,
                          final RedirectAttributes redirectAttributes,
                          Model model) throws IOException {

        S3Object file = s3Client.getObject("whereby-bucket", fileName);

        byte[] byteArray = IOUtils.toByteArray(file.getObjectContent());

        redirectAttributes.addFlashAttribute("pic", Base64.getEncoder().encodeToString(byteArray));

        redirectAttributes.addFlashAttribute("fileName", fileName);

        return "redirect:/editProfile?userId="+currentUserId;
    }
}
