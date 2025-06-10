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

@Controller
public class MusicUploadController {

    @Autowired
    private MusicService musicService;

    @Autowired
    private ArtistService artistService;

    // Logger static final
    private static final Logger logger = LoggerFactory.getLogger(MusicUploadController.class);

    // Modifica: percorso fisico per upload
    //@Value("${upload.audio.dir:uploads/audio_files}")
    private String uploadDir;

    // URL base per accedere ai file caricati
    //@Value("${upload.audio.url:/uploads/audio_files}")
    private String uploadUrl;

    /**
     * Mostra il form per caricare una nuova traccia musicale.
     */
    @GetMapping("/music/add")
    public String showUploadForm(Model model) {
        model.addAttribute("music", new Music());
        model.addAttribute("artists", artistService.getAllArtists());
        model.addAttribute("newArtist", new Artist());
        model.addAttribute("name", "");
        return "formAddMusic";
    }

    /**
     * Gestisce l'upload di una traccia musicale, assegna un nome dinamico
     * (titolo_musicista-artista.estensione) e salva il file sul server.
     */
    // ...
    @PostMapping("/music/add")
    public String handleFileUpload(@RequestParam("audioFile") MultipartFile file,
                                   @ModelAttribute("music") Music music, // Music object from form
                                   @RequestParam(name = "selectedArtistId", required = false) Long selectedArtistId,
                                   @ModelAttribute(value = "newArtist") Artist newArtist, // newArtist object from modal fields (newArtistName, newArtistDescription)
                                   @RequestParam(value="name", required = false) String name, // << PROBLEMA QUI
                                   Model model) {
        try {
            String resolvedArtistNameForFile = "unknownartist"; // Per il nome del file

            Artist artistToAssociate = null; // L'artista che verrà associato alla musica

            if (selectedArtistId != null) {
                Optional<Artist> artistOptional = artistService.getArtistById(selectedArtistId);
                if (artistOptional.isPresent()) {
                    artistToAssociate = artistOptional.get();
                    resolvedArtistNameForFile = artistToAssociate.getName();
                } else {
                    // Artista selezionato non trovato, errore!
                    model.addAttribute("errorMessage", "Selected artist not found.");
                    // Ricarica i dati necessari per il form
                    model.addAttribute("music", music);
                    model.addAttribute("newArtist", newArtist); // Ripassa l'oggetto newArtist per il modale
                    model.addAttribute("artists", artistService.getAllArtists()); // Per la ricerca
                    return "formAddMusic";
                }
            } else {
                // Nessun artista selezionato, si presume un nuovo artista.
                // Il nome del nuovo artista dovrebbe essere già in newArtist.getName()
                // grazie al binding di Spring con th:field="*{name}" nel modale.
                // La riga newArtist.setName(name) che c'era prima era il problema,
                // perché 'name' (da @RequestParam) è probabilmente null o non quello che ti aspetti.

                // Trimmiamo il nome per sicurezza
                String newArtistNameFromModal = (newArtist.getName() != null) ? newArtist.getName().trim() : null;

                if (newArtistNameFromModal == null || newArtistNameFromModal.isEmpty()) {
                    // Questo non dovrebbe succedere se la validazione client funziona
                    // e se il campo 'name' in Artist ha @NotBlank.
                    // Ma è una buona difesa.
                    model.addAttribute("errorMessage", "Artist name field cannot be empty.");
                    model.addAttribute("music", music);
                    model.addAttribute("newArtist", newArtist);
                    model.addAttribute("artists", artistService.getAllArtists());
                    return "formAddMusic";
                }
                newArtist.setName(newArtistNameFromModal); // Assicura che il nome sia trimmato

                // Qui newArtist contiene i dati dal modale (nome e descrizione)
                // Verrà salvato più avanti se tutto va bene.
                artistToAssociate = newArtist; // L'artista da associare sarà questo nuovo artista
                resolvedArtistNameForFile = newArtist.getName();
            }

            // Genera il nome del file dinamico
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
            }
            String filename = String.format("%s-%s.%s", music.getTitle().replaceAll("[^a-zA-Z0-9.-]", "_"), resolvedArtistNameForFile.replaceAll("[^a-zA-Z0-9.-]", "_"), fileExtension);

            // Crea il percorso fisico per salvare il file
            Path uploadPath = Paths.get("C:/Users/Gabriele/Desktop/uploads/audio-files/");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(filename);
            file.transferTo(filePath.toFile());
            music.setAudioFilePath(filePath.toString());

            String fileUrl = String.format("/audio-files/%s", filename);
            music.setFileUrl(fileUrl);

            // Associa l'artista e salva
            if (artistToAssociate != null) {
                if (artistToAssociate.getId() == null) { // È un nuovo artista
                    artistService.save(artistToAssociate); // Salva il nuovo artista PRIMA
                }
                music.setArtist(artistToAssociate); // Associa l'artista (nuovo o esistente)
                // artistToAssociate.getMusics().add(music); // JPA dovrebbe gestire questo se Cascade è corretto
                // e se music.setArtist() è il lato proprietario.
                // È più sicuro farlo dopo aver salvato la musica se necessario,
                // o lasciare che JPA lo gestisca.
            } else {
                // Questo non dovrebbe accadere se la logica sopra è corretta
                logger.error("Artist to associate is null, this should not happen.");
                model.addAttribute("errorMessage", "An error occurred with artist association.");
                model.addAttribute("music", music);
                model.addAttribute("newArtist", newArtist);
                model.addAttribute("artists", artistService.getAllArtists());
                return "formAddMusic";
            }

            musicService.save(music); // Salva l'entità Music

            logger.info("Upload riuscito: {}", fileUrl);
            model.addAttribute("successMessage", "Caricamento riuscito per " + music.getTitle() + " di " + artistToAssociate.getName() + "!");
        } catch (IOException e) {
            logger.error("Errore durante il caricamento del file: ", e);
            model.addAttribute("errorMessage", "Errore durante il caricamento del file: " + e.getMessage());
            // Ricarica i dati necessari per il form in caso di errore IO
            model.addAttribute("music", music);
            model.addAttribute("newArtist", newArtist);
            model.addAttribute("artists", artistService.getAllArtists());
            return "formAddMusic"; // Torna al form invece di redirect, per mostrare l'errore e conservare i dati
        } catch (jakarta.validation.ConstraintViolationException e) {
            logger.error("Errore di validazione: ", e);
            model.addAttribute("errorMessage", "Errore di validazione: " + e.getConstraintViolations().iterator().next().getMessage());
            // Ricarica i dati necessari per il form in caso di errore di validazione
            model.addAttribute("music", music);
            model.addAttribute("newArtist", newArtist); // Ripassa l'oggetto newArtist per il modale
            model.addAttribute("artists", artistService.getAllArtists()); // Per la ricerca
            return "formAddMusic"; // Torna al form
        }


        return "redirect:/music/all";
    }
}