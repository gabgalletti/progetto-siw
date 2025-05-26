package it.uniroma3.progettosiwmusica.controller;

import it.uniroma3.progettosiwmusica.model.Music;
// Rimuovi l'import di User se non lo usi qui
// import it.uniroma3.progettosiwmusica.model.User;
// Rimuovi l'import di MusicRepository se usi MusicService
// import it.uniroma3.progettosiwmusica.repository.MusicRepository;
import it.uniroma3.progettosiwmusica.service.MusicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional; // Importa Optional

@Controller
public class MusicController {
    @Autowired
    private MusicService musicService;
    // È buona pratica rendere il logger static final
    private static final Logger logger = LoggerFactory.getLogger(MusicController.class);

    @GetMapping("/home")
    public String home(Model model) {
        // model.getAttribute("user"); // Questa riga non fa nulla di utile qui, recupera solo
        // Se devi passare l'utente, assicurati che sia gestito correttamente (es. tramite SecurityContext)
        return "home";
    }

    @GetMapping("/music/{id}")
    public String music(@PathVariable("id") Long id, Model model) {
        Optional<Music> musicOptional = this.musicService.getMusicById(id);
        if (musicOptional.isPresent()) {
            model.addAttribute("music", musicOptional.get()); // Estrai l'oggetto Music dall'Optional
            return "music"; // Nome del template senza .html
        } else {
            logger.warn("Music with id {} not found. Redirecting to all music page.", id);
            // Potresti voler aggiungere un messaggio flash per l'utente qui
            // redirectAttributes.addFlashAttribute("errorMessage", "Music track not found.");
            return "redirect:/music/all"; // Reindirizza se la musica non viene trovata
        }
    }

    @GetMapping("/music/all")
    public String musics(Model model) {
        model.addAttribute("musics", this.musicService.getAllMusics());
        return "musics";
    }
}