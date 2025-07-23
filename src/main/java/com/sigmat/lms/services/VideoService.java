package com.sigmat.lms.services;

import com.sigmat.lms.models.Video;
import com.sigmat.lms.repository.VideoRepo;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Service
public class VideoService {

    private final VideoRepo videoRepo;
    private final Path videoStoragePath = Paths.get("uploads/videos");

    public VideoService(VideoRepo videoRepo) {
        this.videoRepo = videoRepo;
        createStorageDirectory();
    }

    private void createStorageDirectory() {
        try {
            Files.createDirectories(videoStoragePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create video storage directory", e);
        }
    }

    public Video uploadVideo(MultipartFile file, String title, String description) throws IOException {
        // Check for existing title
        if (videoRepo.findByTitle(title).isPresent()) {
            throw new IllegalArgumentException("Video with title '" + title + "' already exists");
        }

        // Generate unique filename
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path destination = videoStoragePath.resolve(filename);

        // Save file
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        // Create and save video record
        Video video = new Video();
        video.setTitle(title);
        video.setDescription(description);
        video.setFilePath(destination.toString());

        return videoRepo.save(video);
    }

    public List<Video> getAllVideos() {
        return videoRepo.findAll();
    }

    public Optional<Video> getVideoById(Long id) {
        return videoRepo.findById(id);
    }

    public void deleteVideo(Long id) throws IOException {
        Video video = videoRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Video not found"));

        // Delete file
        Files.deleteIfExists(Paths.get(video.getFilePath()));

        // Delete database record
        videoRepo.delete(video);
    }

    public Video updateVideo(Long id, MultipartFile file, String title, String description) throws IOException {
        Video existingVideo = videoRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Video not found"));

        // Handle file update if new file provided
        if (file != null && !file.isEmpty()) {
            // Delete old file
            Files.deleteIfExists(Paths.get(existingVideo.getFilePath()));

            // Save new file
            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path destination = videoStoragePath.resolve(filename);
            Files.copy(file.getInputStream(), destination);
            existingVideo.setFilePath(destination.toString());
        }

        // Update fields
        existingVideo.setTitle(title);
        existingVideo.setDescription(description);

        return videoRepo.save(existingVideo);
    }

    public Optional<Video> searchByTitle(String title) {
        return videoRepo.findByTitle(title);
    }

    public void streamVideo(Long id, HttpServletResponse response) throws IOException {
        Video video = videoRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Video not found"));

        File videoFile = new File(video.getFilePath());

        try (RandomAccessFile raf = new RandomAccessFile(videoFile, "r")) {
            long fileLength = videoFile.length();

            // Set response headers
            response.setContentType("video/mp4");
            response.setContentLengthLong(fileLength);
            response.setHeader("Accept-Ranges", "bytes");

            // Stream video content
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = raf.read(buffer)) != -1) {
                response.getOutputStream().write(buffer, 0, bytesRead);
            }
        }
    }
}