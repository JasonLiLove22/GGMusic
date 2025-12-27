package com.lwjscript.ggmusic.repository;

import com.lwjscript.ggmusic.entity.Playlist;
import com.lwjscript.ggmusic.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findByCreator(User creator);
}
