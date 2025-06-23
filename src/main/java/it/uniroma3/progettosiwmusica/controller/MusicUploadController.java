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
import java.util.List;
import java.util.Optional;
import java.util.UUID; // Importa UUID per nomi di file univoci

@Controller
public class MusicUploadController {

    @Autowired
    private MusicService musicService;

    @Autowired
    private ArtistService artistService;

    private static final Logger logger = LoggerFactory.getLogger(MusicUploadController.class);

    // Inietta i percorsi per i file audio
    @Value("${upload.audio.physical-dir:C:/Users/vvbla/Desktop/uploads/audio-files/}")
    private String audioPhysicalDir;

    // Inietta i percorsi per le foto degli artisti
    @Value("${upload.artist.photo.physical-dir}")
    private String artistPhotoPhysicalDir;

    @Value("${upload.artist.photo.url-prefix}")
    private String artistPhotoUrlPrefix;


    @GetMapping("/music/add")
    public String showUploadForm(Model model) {
        model.addAttribute("music", new Music());
        model.addAttribute("artists", artistService.getAllArtists());
        model.addAttribute("newArtist", new Artist());
        model.addAttribute("name", "");
        return "formAddMusic";
    }


    @PostMapping("/music/add")
    public String handleFileUpload(@RequestParam("audioFile") MultipartFile audioFile,
                                   // NUOVO: Aggiungi il parametro per la foto dell'artista.
                                   // 'required = false' è FONDAMENTALE perché la foto non c'è se selezioni un artista esistente.
                                   @RequestParam(name = "photo", required = false) MultipartFile artistPhoto,
                                   @ModelAttribute("music") Music music,
                                   @RequestParam(name = "selectedArtistId", required = false) Long selectedArtistId,
                                   @ModelAttribute(value = "newArtist") Artist newArtist,
                                   @RequestParam(value="name", required = false) String name,
                                   Model model) {
        try {
            String resolvedArtistNameForFile = "unknownartist";
            Artist artistToAssociate;

            if (selectedArtistId != null) {
                // Logica per artista esistente (invariata)
                artistToAssociate = artistService.getArtistById(selectedArtistId)
                        .orElseThrow(() -> new IllegalArgumentException("Artista selezionato non trovato"));
                resolvedArtistNameForFile = artistToAssociate.getName();
            } else {
                // Logica per NUOVO artista
                String newArtistNameFromModal = (newArtist.getName() != null) ? newArtist.getName().trim() : null;
                if (newArtistNameFromModal == null || newArtistNameFromModal.isEmpty()) {
                    throw new IllegalArgumentException("Il nome del nuovo artista non può essere vuoto.");
                }
                newArtist.setName(newArtistNameFromModal);
                artistToAssociate = newArtist;
                resolvedArtistNameForFile = newArtist.getName();

                // =================================================================
                // ===== NUOVO: Logica per salvare la foto del nuovo artista =====
                // =================================================================
                if (artistPhoto != null && !artistPhoto.isEmpty()) {
                    String originalFilename = artistPhoto.getOriginalFilename();
                    String fileExtension = "";
                    if (originalFilename != null && originalFilename.contains(".")) {
                        fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.'));
                    }
                    // Crea un nome file univoco per evitare sovrascritture
                    String uniqueFileName = resolvedArtistNameForFile.replaceAll("[^a-zA-Z0-9.-]", "_")
                            + "_" + UUID.randomUUID().toString().substring(0, 8) + fileExtension;

                    Path uploadPath = Paths.get(artistPhotoPhysicalDir);
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }
                    Path filePath = uploadPath.resolve(uniqueFileName);
                    artistPhoto.transferTo(filePath.toFile());

                    // Imposta l'URL relativo per l'accesso web sull'oggetto artista
                    artistToAssociate.setPhotoUrl(artistPhotoUrlPrefix + "/" + uniqueFileName);
                    logger.info("Foto per il nuovo artista {} salvata in {}", artistToAssociate.getName(), filePath);
                } else {
                    logger.warn("Nessuna foto fornita per il nuovo artista {}", artistToAssociate.getName());
                    // Potresti voler lanciare un errore se la foto è obbligatoria
                    // throw new IllegalArgumentException("La foto è obbligatoria per un nuovo artista.");
                }
            }

            // Logica per salvare il file audio (leggermente modificata per usare la proprietà iniettata)
            String originalAudioFilename = audioFile.getOriginalFilename();
            String audioFileExtension = "";
            if (originalAudioFilename != null && originalAudioFilename.contains(".")) {
                audioFileExtension = originalAudioFilename.substring(originalAudioFilename.lastIndexOf('.'));
            }
            String audioFilename = String.format("%s-%s%s",
                    music.getTitle().replaceAll("[^a-zA-Z0-9.-]", "_"),
                    resolvedArtistNameForFile.replaceAll("[^a-zA-Z0-9.-]", "_"),
                    audioFileExtension);

            Path audioUploadPath = Paths.get(audioPhysicalDir);
            if (!Files.exists(audioUploadPath)) {
                Files.createDirectories(audioUploadPath);
            }
            Path audioFilePath = audioUploadPath.resolve(audioFilename);
            audioFile.transferTo(audioFilePath.toFile());
            music.setAudioFilePath(audioFilePath.toString());
            music.setFileUrl("/audio-files/" + audioFilename);

            // Associa l'artista e salva
            if (artistToAssociate.getId() == null) { // È un nuovo artista
                artistService.save(artistToAssociate); // Prima salva il nuovo artista (ora con photoUrl)
            }
            music.setArtist(artistToAssociate);
            musicService.save(music);

            logger.info("Upload riuscito per la musica: {}", music.getFileUrl());
            model.addAttribute("successMessage", "Caricamento riuscito per " + music.getTitle() + " di " + artistToAssociate.getName() + "!");

        } catch (IOException | IllegalArgumentException e) {
            logger.error("Errore durante il caricamento del file: ", e);
            model.addAttribute("errorMessage", "Errore durante il caricamento: " + e.getMessage());
            // Ricarica i dati necessari per il form in caso di errore
            model.addAttribute("music", music);
            model.addAttribute("newArtist", newArtist);
            model.addAttribute("artists", artistService.getAllArtists());
            return "formAddMusic";
        }
        return "redirect:/music/all";
    }
}