package it.uniroma3.progettosiwmusica.controller;

import it.uniroma3.progettosiwmusica.model.Artist;
import it.uniroma3.progettosiwmusica.service.ArtistService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
            model.addAttribute("artist", artist.get());
        }
        else{
            model.addAttribute("artist", null);
        }
        return "artist";
    }

    @GetMapping("/searchArtists")
    public ResponseEntity<?> searchArtists(@RequestParam String query) {
        try {
            List<Artist> artists = artistService.findByNameContainingIgnoreCase(query);
            return ResponseEntity.ok(artists);
        } catch (Exception e) {
            // Log per debugging
            System.err.println("Error searching artists: " + e.getMessage());
            e.printStackTrace();
            // Restituisci un'errore personalizzato
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unable to fetch artist data");
        }

    }


    @PostMapping("/artists")
    public String saveArtist(@Valid @ModelAttribute("artist") Artist artist, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {return "formAddArtist";}
        Artist savedArtist = this.artistService.save(artist);
        return "redirect:/artist/" + savedArtist.getId();
    }
}