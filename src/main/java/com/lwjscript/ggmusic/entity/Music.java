package com.lwjscript.ggmusic.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Music {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String artist;
    private String filePath; // Store the file path on disk
    private String fileName;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User uploader;
}
