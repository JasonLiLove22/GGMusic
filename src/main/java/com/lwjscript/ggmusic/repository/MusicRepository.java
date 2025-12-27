package com.lwjscript.ggmusic.repository;

import com.lwjscript.ggmusic.entity.Music;
import com.lwjscript.ggmusic.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MusicRepository extends JpaRepository<Music, Long> {
    // 原有的方法
    List<Music> findByUploader(User uploader);
    
    // 新增：按标题字典序升序排序
    List<Music> findByUploaderOrderByTitleAsc(User uploader);
}
