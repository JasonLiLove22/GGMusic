package com.lwjscript.ggmusic.service;

import com.lwjscript.ggmusic.entity.Music;
import com.lwjscript.ggmusic.entity.Playlist;
import com.lwjscript.ggmusic.entity.User;
import com.lwjscript.ggmusic.repository.MusicRepository;
import com.lwjscript.ggmusic.repository.PlaylistRepository;
import com.lwjscript.ggmusic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class MusicService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Autowired
    private MusicRepository musicRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PlaylistRepository playlistRepository;

    public void uploadMusic(MultipartFile file, String username) throws IOException {
        User user = userRepository.findByUsername(username).orElseThrow();

        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) return;

        String nameWithoutExt = originalFilename;
        String extension = "";
        if (originalFilename.contains(".")) {
            int lastDot = originalFilename.lastIndexOf(".");
            nameWithoutExt = originalFilename.substring(0, lastDot);
            extension = originalFilename.substring(lastDot);
        }

        String title = nameWithoutExt;
        String artist = "Unknown";

        // Logic: Artist - Title
        if (nameWithoutExt.contains(" - ")) {
            String[] parts = nameWithoutExt.split(" - ", 2);
            artist = parts[1].trim();
            title = parts[0].trim();
        }
        else{
            if (nameWithoutExt.contains("-")) {
                String[] parts = nameWithoutExt.split("-", 2);
                artist = parts[1].trim();
                title = parts[0].trim();
            }
        }
        
        String fileName = UUID.randomUUID().toString() + extension;
        Path filePath = Paths.get(uploadDir, fileName);
        
        file.transferTo(filePath.toFile());

        Music music = new Music();
        music.setTitle(title);
        music.setArtist(artist);
        music.setFileName(fileName);
        music.setFilePath(filePath.toString());
        music.setUploader(user);

        musicRepository.save(music);
    }

    public List<Music> getUserMusic(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        // Changed to sort by Title ASC
        return musicRepository.findByUploaderOrderByTitleAsc(user);
    }
    
    public Music getMusicById(Long id) {
        return musicRepository.findById(id).orElseThrow();
    }

    @Transactional
    public void deleteMusics(List<Long> musicIds, String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        List<Music> musics = musicRepository.findAllById(musicIds);

        for (Music music : musics) {
            // Security check: ensure the user owns the music
            if (!music.getUploader().getId().equals(user.getId())) {
                continue;
            }

            // 1. Remove from all playlists (Manual cleanup for ManyToMany)
            List<Playlist> playlists = playlistRepository.findAll();
            for (Playlist playlist : playlists) {
                if (playlist.getMusics().remove(music)) {
                    playlistRepository.save(playlist);
                }
            }

            // 2. Delete file from disk
            try {
                Path path = Paths.get(music.getFilePath());
                Files.deleteIfExists(path);
            } catch (IOException e) {
                e.printStackTrace(); // Log error but continue
            }

            // 3. Delete from database
            musicRepository.delete(music);
        }
    }
}
