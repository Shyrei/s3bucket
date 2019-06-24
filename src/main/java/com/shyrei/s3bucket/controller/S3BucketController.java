package com.shyrei.s3bucket.controller;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.shyrei.s3bucket.model.Image;
import com.shyrei.s3bucket.repository.S3BucketRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.amazonaws.services.s3.model.CannedAccessControlList.PublicRead;

@Controller
@Slf4j
public class S3BucketController {

    private final static String PREFIX = "images/";

    @Autowired
    private S3BucketRepository repository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @RequestMapping("/")
    public String allImages(Model model) {
        model.addAttribute("images", repository.findAll());
        return "images";
    }

    @RequestMapping("/{name}")
    public String getImage(@PathVariable String name, Model model) {
        List<Image> all = repository.findAll();
        Optional<Image> first = all.stream().filter(e -> e.getName().equals(name)).findFirst();
        model.addAttribute("image", first.get());
        return "image";
    }

    @RequestMapping("/index")
    public String allImages() {
        return "index";
    }


    @PostMapping("/upload")
    public String saveImage(Model model, @RequestParam String name, @RequestParam String comment, @RequestParam MultipartFile file) throws IOException {
        // get metadata and upload file to s3 bucket
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new InstanceProfileCredentialsProvider(false))
                .build();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        s3.putObject(new PutObjectRequest(bucket, PREFIX.concat(name), file.getInputStream(), objectMetadata).withCannedAcl(PublicRead));

        //save image model to RDS DB
        Image image = new Image(name, comment);
        image.setUrl(s3.getUrl(bucket, PREFIX.concat(name)));
        repository.save(image);

        return "success";
    }
}
