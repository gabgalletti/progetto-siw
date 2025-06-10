package it.uniroma3.progettosiwmusica.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList; // <<--- AGGIUNGI QUESTO IMPORT
import java.util.List;
import java.util.Objects;

@Entity
public class Music {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String title;

    @Column(columnDefinition = "TEXT") // Buono per testi lunghi se necessario
    private String lyrics;


    @ManyToOne
    @JoinColumn(name = "artist_id") // Colonna di chiave esterna nella tabella `Music`
    private Artist artist;

    private String duration;

    private String audioFilePath; // <<--- NOME CAMPO COERENTE
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

    public String getDuration() {return duration;}

    public void setDuration(String duration) {this.duration = duration;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Aggiunto per efficienza
        if (o == null || getClass() != o.getClass()) return false;
        Music music = (Music) o;
        // Per le entità JPA, è spesso meglio basare equals/hashCode solo sull'ID
        // se l'entità è gestita (ha un ID non nullo).
        if (id != null && music.id != null) {
            return Objects.equals(id, music.id);
        }
        // Fallback per entità non ancora persistite (usa con cautela nelle collezioni)
        return Objects.equals(title, music.title); // Modificato per usare solo il titolo come fallback
    }

    @Override
    public int hashCode() {
        // Coerente con equals
        if (id != null) {
            return Objects.hash(id);
        }
        return Objects.hash(title); // Modificato
    }


    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
    public String getFileUrl(){
        return this.fileUrl;
    }
}