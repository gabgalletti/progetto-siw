package it.uniroma3.progettosiwmusica.service;

import it.uniroma3.progettosiwmusica.model.Artist;
import it.uniroma3.progettosiwmusica.repository.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArtistService {
    @Autowired private ArtistRepository artistRepository;

    public Artist getArtistById(Long id) {
        return artistRepository.findById(id).get();
    }
}
