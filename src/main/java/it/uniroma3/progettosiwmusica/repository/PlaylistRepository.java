package it.uniroma3.progettosiwmusica.repository;

import it.uniroma3.progettosiwmusica.model.Playlist;
import org.springframework.data.repository.CrudRepository;

public interface PlaylistRepository extends CrudRepository<Playlist, Long> {
}
