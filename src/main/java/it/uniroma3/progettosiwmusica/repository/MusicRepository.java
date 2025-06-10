package it.uniroma3.progettosiwmusica.repository;

import it.uniroma3.progettosiwmusica.model.Music;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MusicRepository extends CrudRepository<Music, Long> {
    List<Music> findByTitleContainingIgnoreCase(String name);
}
