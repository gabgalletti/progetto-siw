package it.uniroma3.progettosiwmusica.controller;

import it.uniroma3.progettosiwmusica.model.Artist;
import it.uniroma3.progettosiwmusica.service.ArtistService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class ArtistController {
    @Autowired private ArtistService artistService;

    @GetMapping("/formAddArtist")
    public String formAddArtist(Model model) {
        model.addAttribute("artist", new Artist());
        return "formAddArtist";
    }

    @GetMapping("/artists")
    public String getAllArtists(Model model) {
        model.addAttribute("artists", this.artistService.getAllArtists());
        return "artists"; // Assicurati di avere un template artists.html
    }

    @GetMapping("/artist/{id}")
    public String getArtist(@PathVariable("id") Long id, Model model) {
        Optional<Artist> artist = this.artistService.getArtistById(id);
        if (artist.isPresent()) {
            model.addAttribute("artist", artist);
            return "artist";
        }
        return "redirect:/artists";
    }

    @PostMapping("/artists")
    public String saveArtist(@Valid @ModelAttribute("artist") Artist artist, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {return "formAddArtist";}
        Artist savedArtist = this.artistService.save(artist);
        return "redirect:/artist/" + savedArtist.getId();
    }
}