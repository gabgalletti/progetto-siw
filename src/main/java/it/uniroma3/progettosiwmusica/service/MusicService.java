package it.uniroma3.progettosiwmusica.service;

import it.uniroma3.progettosiwmusica.model.Music;
import it.uniroma3.progettosiwmusica.repository.MusicRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MusicService {
    private static final Logger logger = LoggerFactory.getLogger(MusicService.class);
    @Autowired MusicRepository musicRepository;

    @Transactional
    public Music save(Music music) {
        return musicRepository.save(music);
    }
    public Optional<Music> getMusicById(Long id) {
        return musicRepository.findById(id);
    }

    public Iterable<Music> getAllMusics() {
        return musicRepository.findAll();
    }

    public List<Music> findByTitleContainingIgnoreCase(String title) {
        return musicRepository.findByTitleContainingIgnoreCase(title);
    }

    @Transactional
    public void updateMusic(Long id, String title, String lyrics) {
        Music music = musicRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid music Id:" + id));
        music.setTitle(title);
        music.setLyrics(lyrics);
    }

    @Transactional
    public void deleteMusic(Long id) {
        if (!musicRepository.existsById(id)) {
            throw new IllegalArgumentException("Musica con ID " + id + " non trovata.");
        }
        musicRepository.deleteById(id);
    }

}
