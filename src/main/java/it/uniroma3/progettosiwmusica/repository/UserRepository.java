package it.uniroma3.progettosiwmusica.repository;

import it.uniroma3.progettosiwmusica.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
