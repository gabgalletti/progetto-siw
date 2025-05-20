package it.uniroma3.progettosiwmusica.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class Lyric {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String lyric;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLyric() {
        return lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Lyric lyric1 = (Lyric) o;
        return Objects.equals(id, lyric1.id) && Objects.equals(lyric, lyric1.lyric);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lyric);
    }
}
