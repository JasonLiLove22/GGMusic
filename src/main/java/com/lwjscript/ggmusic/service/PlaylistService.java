package com.lwjscript.ggmusic.service;

import com.lwjscript.ggmusic.entity.Music;
import com.lwjscript.ggmusic.entity.Playlist;
import com.lwjscript.ggmusic.entity.User;
import com.lwjscript.ggmusic.repository.MusicRepository;
import com.lwjscript.ggmusic.repository.PlaylistRepository;
import com.lwjscript.ggmusic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaylistService {
    @Autowired
    private PlaylistRepository playlistRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MusicRepository musicRepository;

    public void createPlaylist(String name, String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        Playlist playlist = new Playlist();
        playlist.setName(name);
        playlist.setCreator(user);
        playlistRepository.save(playlist);
    }

    public List<Playlist> getUserPlaylists(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        return playlistRepository.findByCreator(user);
    }

    public void addMusicToPlaylist(Long playlistId, Long musicId) {
        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow();
        Music music = musicRepository.findById(musicId).orElseThrow();
        if (!playlist.getMusics().contains(music)) {
            playlist.getMusics().add(music);
            playlistRepository.save(playlist);
        }
    }

    public void addMusicsToPlaylist(Long playlistId, List<Long> musicIds) {
        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow();
        List<Music> musics = musicRepository.findAllById(musicIds);
        for (Music music : musics) {
            if (!playlist.getMusics().contains(music)) {
                playlist.getMusics().add(music);
            }
        }
        playlistRepository.save(playlist);
    }

    public void removeMusicsFromPlaylist(Long playlistId, List<Long> musicIds) {
        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow();
        List<Music> musicsToRemove = musicRepository.findAllById(musicIds);
        playlist.getMusics().removeAll(musicsToRemove);
        playlistRepository.save(playlist);
    }
    
    public Playlist getPlaylistById(Long id) {
        return playlistRepository.findById(id).orElseThrow();
    }
}
