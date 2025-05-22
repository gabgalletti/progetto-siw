package it.uniroma3.progettosiwmusica.repository;

import it.uniroma3.progettosiwmusica.model.Artist;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ArtistRepository extends CrudRepository<Artist, Long> {
    public Artist findByName(String name);
}
