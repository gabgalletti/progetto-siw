package it.uniroma3.progettosiwmusica.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

@Entity
public class Music {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String title;

    @Column(columnDefinition = "TEXT") // Buono per testi lunghi
    private String lyrics;


    @ManyToOne
    @JoinColumn(name = "artist_id") // Colonna di chiave esterna nella tabella `Music`
    private Artist artist;


    private String audioFilePath;
    private String fileUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public Artist getArtist(){
        return artist;
    }
    public void setArtist(Artist artist){
        this.artist = artist;
    }

    public String getAudioFilePath() { // Getter per il percorso del file audio
        return audioFilePath;
    }

    public void setAudioFilePath(String audioFilePath) { // Setter coerente con il nome del campo
        this.audioFilePath = audioFilePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Music music = (Music) o;
        if (id != null && music.id != null) {
            return Objects.equals(id, music.id);
        }
        return Objects.equals(title, music.title);
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return Objects.hash(id);
        }
        return Objects.hash(title);
    }
    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
    public String getFileUrl(){
        return this.fileUrl;
    }
}