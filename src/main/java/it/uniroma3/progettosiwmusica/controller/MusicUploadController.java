package it.uniroma3.progettosiwmusica.controller;

import it.uniroma3.progettosiwmusica.model.Artist;
import it.uniroma3.progettosiwmusica.model.Music;
import it.uniroma3.progettosiwmusica.service.ArtistService;
import it.uniroma3.progettosiwmusica.service.MusicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class MusicUploadController {
    @Autowired
    private MusicService musicService;
    @Autowired
    private ArtistService artistService;

    // È buona pratica rendere il logger static final
    private static final Logger logger = LoggerFactory.getLogger(MusicUploadController.class);
    @Value("${upload.audio.dir://localhost:8080/uploads/audio_files}")
    private String uploadDir;



    /**
     * Mostra il form per caricare una nuova traccia musicale.
     */
    @GetMapping("/music/add")
    public String showUploadForm(Model model) {
        model.addAttribute("music", new Music());
        model.addAttribute("artists", artistService.getAllArtist());
        model.addAttribute("newArtist", new Artist());

        return "formAddMusic";
    }


    /**
     * Gestisce l'upload di una traccia musicale, assegna un nome dinamico
     * (titolo_musicista-artista.estensione) e salva il file sul server.
     */
    @PostMapping("/music/add")
    public String handleFileUpload(@RequestParam("audioFile") MultipartFile file,
                                   @ModelAttribute("music") Music music,
                                   @RequestParam(value = "artistName", required = false) String artistName,
                                   Model model) {
        try {
            System.out.println("Inizio caricamento file e salvataggio musica");

            // Logica per salvare il file
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFileName = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }

            String cleanedTitle = music.getTitle().replaceAll("[^a-zA-Z0-9\\s]", "").replaceAll("\\s+", "_");

            if (artistName == null || artistName.isEmpty()) {
                artistName = "unknown_artist"; // Nome predefinito in caso di valore nullo
            }

            String cleanedArtist = artistName.replaceAll("[^a-zA-Z0-9\\s]", "").replaceAll("\\s+", "_");
            String fileName = cleanedTitle + "-" + cleanedArtist + fileExtension;

            Path filePath = uploadPath.resolve(fileName);
            while (Files.exists(filePath)) {
                fileName = cleanedTitle + "-" + cleanedArtist + "-" + System.currentTimeMillis() + fileExtension;
                filePath = uploadPath.resolve(fileName);
            }

            file.transferTo(filePath.toFile());
            music.setAudioFilePath(fileName);

            // Associa l'artista alla musica
            Artist artist = artistService.getArtistByName(artistName);
            if (artist == null) {
                artist = new Artist();
                artist.setName(artistName);
                artistService.save(artist);
            }
            music.setArtist(artist);
            System.out.println("Salvataggio musica nel database...");

            // Salva la musica
            musicService.save(music);
            System.out.println("Musica salvata con successo!");

            model.addAttribute("successMessage", "Audio caricato con successo!");
        } catch (IOException e) {
            model.addAttribute("errorMessage", "Errore nel caricamento del file. Riprova.");
            e.printStackTrace();
        }

        return "redirect:/music/add";
    }

}