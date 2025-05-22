package it.uniroma3.progettosiwmusica.service;

import it.uniroma3.progettosiwmusica.model.Music;
import it.uniroma3.progettosiwmusica.repository.MusicRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MusicService {
    private static final Logger logger = LoggerFactory.getLogger(MusicService.class);
    @Autowired MusicRepository musicRepository;

    public Music getMusicById(Long id) {
        return musicRepository.findById(id).get();
    }
}
