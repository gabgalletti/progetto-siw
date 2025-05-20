package it.uniroma3.progettosiwmusica.repository;

import it.uniroma3.progettosiwmusica.model.Artist;
import org.springframework.data.repository.CrudRepository;

public interface ArtistRepository extends CrudRepository<Artist, Long> {
}
