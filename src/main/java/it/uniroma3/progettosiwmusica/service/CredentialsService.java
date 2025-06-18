package it.uniroma3.progettosiwmusica.service;

import it.uniroma3.progettosiwmusica.model.Credentials;
import it.uniroma3.progettosiwmusica.repository.CredentialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CredentialsService {
    @Autowired private CredentialsRepository credentialsRepository;
    public void save(Credentials credentials) {credentialsRepository.save(credentials);}

    public Credentials findByUsername(String username) {return credentialsRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Username not found"));}
}
