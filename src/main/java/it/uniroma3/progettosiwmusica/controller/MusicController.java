package it.uniroma3.progettosiwmusica.controller;

import it.uniroma3.progettosiwmusica.model.Music;
import it.uniroma3.progettosiwmusica.model.User;
import it.uniroma3.progettosiwmusica.repository.MusicRepository;
import it.uniroma3.progettosiwmusica.service.MusicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class MusicController {
    @Autowired private MusicService musicService;
    Logger logger = LoggerFactory.getLogger(MusicController.class);

    @GetMapping("/")
    public String home(Model model) {
        model.getAttribute("user");
        return "home";
    }

    @GetMapping("/formAddMusic")
    public String formAddMusic(Model model) {
        model.addAttribute("music", new Music());
        return "formAddMusic";
    }

    @GetMapping("/music/{id}")
    public String music(@PathVariable ("id") Long id, Model model) {
        model.addAttribute("music", this.musicService.getMusicById(id));
        return "music.html";
    }

}
