package it.uniroma3.progettosiwmusica.controller;

import it.uniroma3.progettosiwmusica.model.Credentials;
import it.uniroma3.progettosiwmusica.model.Music;
// Rimuovi l'import di User se non lo usi qui
// import it.uniroma3.progettosiwmusica.model.User;
// Rimuovi l'import di MusicRepository se usi MusicService
// import it.uniroma3.progettosiwmusica.repository.MusicRepository;
import it.uniroma3.progettosiwmusica.model.User;
import it.uniroma3.progettosiwmusica.service.CredentialsService;
import it.uniroma3.progettosiwmusica.service.MusicService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Optional;

@Controller
public class MusicController {
    @Autowired
    private MusicService musicService;
    @Autowired
    private CredentialsService credentialsService;

    private static final Logger logger = LoggerFactory.getLogger(MusicController.class);

    @GetMapping("/home")
    public String home(Model model) {
        return "home";
    }


    @GetMapping("/music/{id}")
    public String music(@PathVariable("id") Long id, Model model) {
        Optional<Music> musicOptional = this.musicService.getMusicById(id);
        if (musicOptional.isPresent()) {
            model.addAttribute("music", musicOptional.get());
            return "music";
        } else {
            logger.warn("Music with id {} not found. Redirecting to all music page.", id);
            return "redirect:/music/all";
        }
    }

    @GetMapping("/music/all")
    public String musics(Model model) {
        model.addAttribute("musics", this.musicService.getAllMusics());
        return "musics";
    }


    @PostMapping("/music/update/{id}")
    public String updateMusic(@PathVariable("id") Long id,
                              @Valid @ModelAttribute("music") Music musicData,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Errore nella validazione dei dati.");
            return "redirect:/music/" + id;
        }
        musicService.updateMusic(id, musicData.getTitle(), musicData.getLyrics());
        redirectAttributes.addFlashAttribute("successMessage", "Musica aggiornata con successo!");
        return "redirect:/music/" + id;
    }


    @PostMapping("/music/delete/{id}")
    public String deleteMusic(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            musicService.deleteMusic(id);
            redirectAttributes.addFlashAttribute("successMessage", "Musica eliminata con successo!");
        } catch (Exception e) {
            logger.error("Errore durante l'eliminazione della musica con ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Errore durante l'eliminazione della musica.");
        }
        return "redirect:/music/all";
    }

}