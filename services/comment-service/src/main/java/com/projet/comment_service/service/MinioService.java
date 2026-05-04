package com.projet.comment_service.service;

import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class MinioService {

    private static final Logger log = LoggerFactory.getLogger(MinioService.class);
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/png",
            "image/jpeg",
            "image/webp",
            "image/gif",
            "application/pdf",
            "text/plain",
            "application/zip",
            "application/x-zip-compressed",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    private final MinioClient minioClient;
    private final String endpointUrl;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${upload.max-file-size-bytes:10485760}")
    private long maxFileSizeBytes;

    @Value("${upload.presigned-url-expiry-minutes:60}")
    private int presignedUrlExpiryMinutes;

    public MinioService(@Value("${minio.url}") String url,
                        @Value("${minio.access-key}") String accessKey,
                        @Value("${minio.secret-key}") String secretKey) {
        this.endpointUrl = url;
        this.minioClient = MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }

    public String uploadFile(MultipartFile file, String prefix) {
        validateAttachment(file);

        try {
            ensureBucketExists();

            String objectName = prefix + "-" + UUID.randomUUID() + getExtension(file.getOriginalFilename());
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            return objectName;
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Impossible de stocker la piece jointe.",
                    ex
            );
        }
    }

    public String buildFileUrl(String objectReference) {
        if (!StringUtils.hasText(objectReference)) {
            return objectReference;
        }

        String normalizedReference = normalizeObjectReference(objectReference);
        if (isAbsoluteUrl(normalizedReference)) {
            return normalizedReference;
        }

        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucket)
                    .object(normalizedReference)
                    .expiry(presignedUrlExpiryMinutes, TimeUnit.MINUTES)
                    .build());
        } catch (Exception ex) {
            log.warn("Unable to generate presigned URL for comment object {}", normalizedReference, ex);
            return objectReference;
        }
    }

    private void ensureBucketExists() throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
    }

    private void validateAttachment(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La piece jointe est vide.");
        }
        if (file.getSize() > maxFileSizeBytes) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La piece jointe depasse la taille autorisee.");
        }
        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le format de piece jointe n'est pas autorise.");
        }
    }

    private String normalizeObjectReference(String objectReference) {
        if (!isAbsoluteUrl(objectReference)) {
            return objectReference;
        }

        try {
            URI uri = URI.create(objectReference);
            String path = uri.getPath();
            String bucketPrefix = "/" + bucket + "/";
            if (path != null && path.startsWith(bucketPrefix) && path.length() > bucketPrefix.length()) {
                return path.substring(bucketPrefix.length());
            }
        } catch (IllegalArgumentException ex) {
            log.warn("Invalid comment object reference {}", objectReference, ex);
        }

        return objectReference;
    }

    private boolean isAbsoluteUrl(String value) {
        return value.startsWith("http://") || value.startsWith("https://");
    }

    private String getExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot) : "";
    }
}
