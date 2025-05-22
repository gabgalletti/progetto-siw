package it.uniroma3.progettosiwmusica.controller;

import it.uniroma3.progettosiwmusica.model.Artist;
import it.uniroma3.progettosiwmusica.service.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ArtistController {
    @Autowired private ArtistService artistService;

    @GetMapping("/formAddArtist")
    public String formAddArtist(Model model) {
        model.addAttribute("artist", new Artist());
        return "formAddArtist";
    }

}
