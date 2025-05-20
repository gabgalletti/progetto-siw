package it.uniroma3.progettosiwmusica.repository;

import it.uniroma3.progettosiwmusica.model.Music;
import org.springframework.data.repository.CrudRepository;

public interface MusicRepository extends CrudRepository<Music, Long> {
}
