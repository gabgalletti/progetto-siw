package it.uniroma3.progettosiwmusica.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
public class Music {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String title;

    private String lyrics;
    @ManyToMany
    private List<Artist> artist;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Music music = (Music) o;
        return Objects.equals(id, music.id) && Objects.equals(title, music.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title);
    }
}
