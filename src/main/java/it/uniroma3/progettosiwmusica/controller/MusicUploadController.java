package it.uniroma3.progettosiwmusica.controller;

import it.uniroma3.progettosiwmusica.model.Artist;
import it.uniroma3.progettosiwmusica.model.Music;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Per i messaggi di feedback

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects; // Per Objects.requireNonNull

@Controller
public class MusicUploadController {

    // Definisci la cartella dove salvare i file MP3.
    // Assicurati che questa cartella esista e che l'applicazione abbia i permessi per scriverci.
    // È consigliabile renderla configurabile tramite application.properties.
    private static final String UPLOAD_DIR = "../../resources/uploads/audio_files/"; //cartella 'uploads/audio_files' nella root del progetto

    @PostMapping("/formAddMusic")
    public String handleFileUpload(@RequestParam("title") String title,
                                   @RequestParam("artist") Artist artist,
                                   @RequestParam("audioFile") MultipartFile file,
                                   String lyrics,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {

        // 0. Creazione della directory di upload se non esiste
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            // Gestisci l'errore di creazione della directory
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating upload directory: " + e.getMessage());
            return "redirect:/formAddMusic"; // o ricarica la pagina del form
        }

        // 1. Validazione base del file (es. non vuoto, tipo)
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please, select a file to upload.");
            return "redirect:/formAddMusic"; // Ritorna alla pagina del form
        }

        // Verifica l'estensione del file (esempio base)
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".mp3") || !originalFilename.toLowerCase().endsWith(".wav") || !originalFilename.toLowerCase().endsWith(".ogg")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid format. Please upload a mp3, wav or ogg file.");
            return "redirect:/formAddMusic";
        }

        try {
            // 2. Ottieni il nome del file e costruisci il percorso di destinazione
            // È una buona pratica generare un nome univoco per evitare sovrascritture o usare il nome originale con cautela
            // Esempio con nome originale (potrebbe necessitare di sanificazione per caratteri speciali)
            Path destinationFile = Paths.get(UPLOAD_DIR).resolve(
                    Paths.get(Objects.requireNonNull(file.getOriginalFilename()))).normalize().toAbsolutePath();

            // Assicurati che il percorso di destinazione sia all'interno della directory di upload consentita
            // (prevenzione di attacchi di tipo "Path Traversal")
            if (!destinationFile.getParent().equals(Paths.get(UPLOAD_DIR).toAbsolutePath())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Invalid path.");
                return "redirect:/formAddMusic";
            }

            // 3. Salva il file sul server
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            // 4. Qui potresti salvare le informazioni del file (titolo, artista, percorso del file) nel database
            // Esempio: musicService.saveMusicMetadata(titolo, artista, destinationFile.toString());

            redirectAttributes.addFlashAttribute("successMessage",
                    " "+ file.getOriginalFilename() + "' successfully loaded as '" + title + "' by " + artist.getName() + "!");
            Music music = new Music();
            music.setTitle(title);
            music.setLyrics(lyrics);
            model.addAttribute("title",title);

        } catch (IOException e) {
            // Gestisci altre eccezioni di I/O
            e.printStackTrace(); // Logga l'errore per debug
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error loading file: " + e.getMessage());
        }

        return "redirect:/formMusicSuccess"; // O una pagina di successo
    }
}
