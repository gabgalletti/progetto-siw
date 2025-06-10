package it.uniroma3.progettosiwmusica.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList; // <<--- AGGIUNGI QUESTO IMPORT
import java.util.List;
import java.util.Objects;

@Entity
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank
    private String name;

    @Column(columnDefinition = "TEXT") // Buono per testi lunghi se necessario
    private String description;

    private String photoPath;
    private String photoUrl;

    // Artist è il LATO INVERSE (non proprietario) della relazione ManyToMany
    // "artists" è il nome del campo List<Artist> artists in Music.java
    @JsonIgnore
    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Music> musics = new ArrayList<>();

    private String imageUrl;

    public String getImageUrl() {return imageUrl;}

    public void setImageUrl(String imageUrl) {this.imageUrl = imageUrl;}


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Music> getMusics() {
        return musics;
    }

    public void setMusics(List<Music> musics) {
        this.musics = musics;
    }

    public void setPhotoPath(String photoPath){ this.photoPath = photoPath; }

    public String getPhotoPath(){ return photoPath; }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Aggiunto
        if (o == null || getClass() != o.getClass()) return false;
        Artist artist = (Artist) o;
        if (id != null && artist.id != null) {
            return Objects.equals(id, artist.id);
        }
        // L'implementazione originale con id e name è accettabile,
        // ma basarsi solo sull'ID (se non nullo) è più comune per entità JPA.
        // Se usi name, assicurati che sia univoco o che la logica di business lo gestisca.
        return Objects.equals(name, artist.name); // Fallback se gli ID sono null
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return Objects.hash(id);
        }
        return Objects.hash(name); // Fallback
    }

    public String getPhotoUrl(){ return this.photoUrl; }
    public void setPhotoUrl(String fileUrl) { this.photoUrl = fileUrl;}
}