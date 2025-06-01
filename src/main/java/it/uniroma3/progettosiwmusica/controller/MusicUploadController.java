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
        return "formAddMusic";
    }

    /**
     * Gestisce l'upload di una traccia musicale, assegna un nome dinamico
     * (titolo_musicista-artista.estensione) e salva il file sul server.
     */
    @PostMapping("/music/add")
    public String handleFileUpload(@RequestParam("audioFile") MultipartFile file,
                                   @ModelAttribute("music") Music music,
                                   @RequestParam(value = "selectedArtistId", required = false) Long selectedArtistId,
                                   @ModelAttribute(value = "newArtist") Artist newArtist,
                                   @ModelAttribute("name") String name,
                                   Model model) {


        try {
            String artistName = "unknownartist";
            if(selectedArtistId == null) {
                newArtist.setName(name);

            } else {
                Optional<Artist> artist = (artistService.getArtistById(selectedArtistId));
                if (artist.isPresent()) {
                    music.setArtist(artist.get());
                }
            }

            // Controlla se newArtist è nullo e assegna un nome alternativo



            if(selectedArtistId == null)
                artistName = newArtist.getName();
            else
                artistName = artistService.getArtistById(selectedArtistId).get().getName();


            // Genera il nome del file dinamico
            String filename = String.format("%s-%s.%s", music.getTitle(), artistName, file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.') + 1));


            // Crea il percorso fisico per salvare il file
            Path uploadPath = Paths.get("C:/Users/Gabriele/Desktop/uploads/audio-files/");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath); // Crea directory se non esiste
            }
            Path filePath = uploadPath.resolve(filename);
            file.transferTo(filePath.toFile()); // Salva il file
            music.setAudioFilePath(filePath.toString());
            // Imposta il percorso accessibile da Internet
            String fileUrl = String.format("/audio-files/%s", filename); // Usa uploadUrl per generare URL pubblico
            music.setFileUrl(fileUrl); // Salva l'URL nel modello dell'entità Musica

            if(selectedArtistId == null){
                artistService.save(newArtist);
                newArtist.getMusics().add(music);
                music.setArtist(artistService.getArtistById(newArtist.getId()).get());
            } else {
                artistService.getArtistById(selectedArtistId).get().getMusics().add(music);
                music.setArtist(artistService.getArtistById(selectedArtistId).get());
            }
            // Salva l'entità Music
            musicService.save(music);


            logger.info("Upload riuscito: {}", fileUrl);
            model.addAttribute("successMessage", "Caricamento riuscito!");
        } catch (IOException e) {
            logger.error("Errore durante il caricamento del file: ", e);
            model.addAttribute("errorMessage", "Errore durante il caricamento del file.");
        }

        return "formAddMusic"; // Torna alla pagina del form
    }
}