package com.lwjscript.ggmusic.controller;

import com.lwjscript.ggmusic.entity.Music;
import com.lwjscript.ggmusic.entity.Playlist;
import com.lwjscript.ggmusic.service.MusicService;
import com.lwjscript.ggmusic.service.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    private MusicService musicService;

    @Autowired
    private PlaylistService playlistService;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @GetMapping("/")
    public String root() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {
        if (authentication != null) {
            String username = authentication.getName();
            List<Music> myMusic = musicService.getUserMusic(username);
            List<Playlist> myPlaylists = playlistService.getUserPlaylists(username);
            model.addAttribute("musics", myMusic);
            model.addAttribute("playlists", myPlaylists);
            model.addAttribute("username", username);
        }
        return "home";
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("files") MultipartFile[] files,
                         Authentication authentication) throws IOException {
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                musicService.uploadMusic(file, authentication.getName());
            }
        }
        return "redirect:/home";
    }

    @PostMapping("/playlist/create")
    public String createPlaylist(@RequestParam("name") String name, Authentication authentication) {
        playlistService.createPlaylist(name, authentication.getName());
        return "redirect:/home";
    }

    @PostMapping("/playlist/add")
    public String addToPlaylist(@RequestParam("playlistId") Long playlistId,
                                @RequestParam("musicId") Long musicId) {
        playlistService.addMusicToPlaylist(playlistId, musicId);
        return "redirect:/home";
    }

    @PostMapping("/playlist/batch-add")
    @ResponseBody
    public String batchAddToPlaylist(@RequestParam("playlistId") Long playlistId,
                                     @RequestParam("musicIds") List<Long> musicIds) {
        playlistService.addMusicsToPlaylist(playlistId, musicIds);
        return "success";
    }

    @PostMapping("/playlist/batch-remove")
    @ResponseBody
    public String batchRemoveFromPlaylist(@RequestParam("playlistId") Long playlistId,
                                          @RequestParam("musicIds") List<Long> musicIds) {
        playlistService.removeMusicsFromPlaylist(playlistId, musicIds);
        return "success";
    }

    @PostMapping("/music/batch-delete")
    @ResponseBody
    public String batchDeleteMusic(@RequestParam("musicIds") List<Long> musicIds,
                                   Authentication authentication) {
        musicService.deleteMusics(musicIds, authentication.getName());
        return "success";
    }
    
    @GetMapping("/playlist/{id}")
    @ResponseBody
    public Playlist getPlaylist(@PathVariable Long id) {
        return playlistService.getPlaylistById(id);
    }

    @GetMapping("/music/file/{fileName}")
    public ResponseEntity<Resource> serveFile(@PathVariable String fileName) {
        try {
            Path file = Paths.get(uploadDir).resolve(fileName);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                throw new RuntimeException("Could not read file: " + fileName);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}
