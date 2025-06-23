package it.uniroma3.progettosiwmusica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import it.uniroma3.progettosiwmusica.model.Credentials;
import java.util.Optional;


public interface CredentialsRepository extends JpaRepository<Credentials, Long> {
    Optional<Credentials> findByUsername(String username);
}
