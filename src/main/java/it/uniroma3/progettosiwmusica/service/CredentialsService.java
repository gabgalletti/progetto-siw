package it.uniroma3.progettosiwmusica.service;

import it.uniroma3.progettosiwmusica.model.Credentials;
import it.uniroma3.progettosiwmusica.model.dto.ProfileUpdateDTO;
import it.uniroma3.progettosiwmusica.repository.CredentialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CredentialsService {
    @Autowired
    private CredentialsRepository credentialsRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void save(Credentials credentials) {credentialsRepository.save(credentials);}

    @Transactional(readOnly = true)
    public Optional<Credentials> findByUsername(String username) {
        return credentialsRepository.findByUsername(username);
    }


    @Transactional
    public void updateCredentials(String currentUsername, ProfileUpdateDTO dto) throws IllegalArgumentException {
        Credentials credentials = this.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalStateException("Credenziali non trovate per l'utente: " + currentUsername));

        String newUsername = dto.getNewUsername();
        if (newUsername != null && !newUsername.trim().isEmpty() && !newUsername.equals(currentUsername)) {
            if (credentialsRepository.findByUsername(newUsername).isPresent()) {
                throw new IllegalArgumentException("Lo username '" + newUsername + "' è già in uso. Scegline un altro.");
            }
            credentials.setUsername(newUsername);
        }
        String newPassword = dto.getNewPassword();
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            credentials.setPassword(passwordEncoder.encode(newPassword));
        }
    }
}