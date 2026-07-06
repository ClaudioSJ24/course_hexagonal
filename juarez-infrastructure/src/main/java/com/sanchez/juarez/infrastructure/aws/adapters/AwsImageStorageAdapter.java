package com.sanchez.juarez.infrastructure.aws.adapters;

import com.juarez.domain.entities.product.ProductImage;
import com.juarez.domain.exceptions.MyBusinessException;
import com.juarez.domain.ports.services.ImageStorageServicePort;
import com.sanchez.juarez.infrastructure.aws.models.AwsConfigModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

@Slf4j
@Service

public class AwsImageStorageAdapter implements ImageStorageServicePort {
    private static final Logger log = LoggerFactory.getLogger(AwsImageStorageAdapter.class);
    private final S3Client s3Client;
    private final AwsConfigModel awsConfigModel;

    public AwsImageStorageAdapter(S3Client s3Client, AwsConfigModel awsConfigModel) {
        this.s3Client = s3Client;
        this.awsConfigModel = awsConfigModel;
    }

    @Override
    public ProductImage upload(String imageName, byte[] imageData) {

        try {
            final var key = "products/" + imageName;

            final var putObjRequest = PutObjectRequest
                    .builder()
                    .bucket(awsConfigModel.bucketName())
                    .key(key)
                    .contentType(this.determinateContentType(imageName))
                    .contentLength((long) imageData.length)
                    .build();

            this.s3Client.putObject(putObjRequest, RequestBody.fromBytes(imageData));

            final var imgUrl = this.buildUrlImage(key);
            log.info("Image uploaded successfully in {}.", imgUrl);
            return new ProductImage(imgUrl.toString());
        }catch (S3Exception s3e) {
        log.error("Error uploading image", s3e);
        throw new MyBusinessException("Error uploading image" + s3e.getMessage());
    } catch (Exception e) {
        log.error("Unexpected uploading deleting image", e);
        throw new MyBusinessException("Error uploading image" + e.getMessage());
    }
    }



    @Override
    public void delete(ProductImage img) {
        try{
            final var key = this.getKeyFromUrl(img.imageUrl());

            final var deleteObjRequest = DeleteObjectRequest
                    .builder()
                    .bucket(awsConfigModel.bucketName())
                    .key(key)
                    .build();
            this.s3Client.deleteObject(deleteObjRequest);
            log.info("Deleted image success{}", img.imageUrl());
        }catch (S3Exception s3e) {
            log.error("Error deleting image", s3e);
            throw new MyBusinessException("Error deleting image" + s3e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error deleting image", e);
            throw new MyBusinessException("Error deleting image" + e.getMessage());
        }
    }

    @Override
    public byte[] download(ProductImage img) {
        try {
            final var key = this.getKeyFromUrl(img.imageUrl());

            final var getObjRequest = GetObjectRequest
                    .builder()
                    .bucket(awsConfigModel.bucketName())
                    .key(key)
                    .build();

            final var bytes = this.s3Client.getObjectAsBytes(getObjRequest).asByteArray();
            log.info("Download image: {} bytes", bytes.length);
            return bytes;

        }catch (S3Exception s3e) {
            log.error("Error downloading image", s3e);
            throw new MyBusinessException("Error downloading image" + s3e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error downloading image", e);
            throw new MyBusinessException("Error downloading image" + e.getMessage());
        }
    }

    /**
     *
     * @param url https://amazonaws/erp-products/products/mac-01.png
     * @return products/mac-01.png
     */

    private String getKeyFromUrl(String url){
        var bucketName = awsConfigModel.bucketName();
        var parts = url.split("/" + bucketName + "/");

        if (parts.length > 1) {
            return parts[1];
        }
        log.warn("No bucket name found for url : "+ url);
        return url;
    }

    private Object buildUrlImage(String key) {
        final var placeHolder = "%s/%s/%";

        return String.format(
                placeHolder,
                this.awsConfigModel.endpoint(),
                this.awsConfigModel.bucketName(),
                key
        );
    }

    private String determinateContentType(String fileName) {

        final var extension = fileName.substring(fileName.lastIndexOf(".") + 1 ).toLowerCase();
        return switch (extension){
            case "jpg" -> "image/jpeg";
            case "png" -> "image/png";
            case "webp" -> "image/webp";
            default -> "application/octet-stream";
        };

    }
}
