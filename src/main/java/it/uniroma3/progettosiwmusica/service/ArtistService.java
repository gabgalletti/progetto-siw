package it.uniroma3.progettosiwmusica.service;

import it.uniroma3.progettosiwmusica.model.Artist;
import it.uniroma3.progettosiwmusica.repository.ArtistRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ArtistService {
    @Autowired private ArtistRepository artistRepository;

    @Transactional
    public Artist save(Artist artist) {
        return artistRepository.save(artist);
    }

    public Optional<Artist> getArtistById(long id) {
        return artistRepository.findById(id);
    }

    public Artist getArtistByName(String name) {
        return artistRepository.findByName(name);
    }

    public Iterable<Artist> getAllArtists(){
        return artistRepository.findAll();
    }

    public void deleteArtist(Artist artist){
        artistRepository.delete(artist);
    }

    public List<Artist> findByNameContainingIgnoreCase(String name) {return artistRepository.findByNameContainingIgnoreCase(name);}

    @Transactional
    public void updateArtist(Long id, String name, String description) {
        Artist artist = artistRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid artist Id:" + id));
        artist.setName(name);
        artist.setDescription(description);
    }

    @Transactional
    public void deleteArtist(Long id) {
        if (!artistRepository.existsById(id)) {
            throw new IllegalArgumentException("Artista con ID " + id + " non trovato.");
        }
        artistRepository.deleteById(id);
    }

}