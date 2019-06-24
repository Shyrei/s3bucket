package com.shyrei.s3bucket.repository;

import com.shyrei.s3bucket.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface S3BucketRepository extends JpaRepository<Image, Long> {
}
