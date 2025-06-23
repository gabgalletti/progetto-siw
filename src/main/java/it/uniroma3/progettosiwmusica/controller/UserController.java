package it.uniroma3.progettosiwmusica.controller;

import it.uniroma3.progettosiwmusica.model.Credentials;
import it.uniroma3.progettosiwmusica.model.User;
import it.uniroma3.progettosiwmusica.model.dto.ProfileUpdateDTO;
import it.uniroma3.progettosiwmusica.service.CredentialsService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/profile") //Per raggruppare tutti gli endpoint relativi al profilo
public class UserController {

    @Autowired
    private CredentialsService credentialsService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public String showProfile(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        Optional<Credentials> credentialsOptional = credentialsService.findByUsername(username);

        if (credentialsOptional.isPresent()) {
            Credentials credentials = credentialsOptional.get();
            User currentUser = credentials.getUser();
            if (currentUser != null) {
                model.addAttribute("user", currentUser);
                model.addAttribute("credentials", credentials);
                model.addAttribute("updateData", new ProfileUpdateDTO());
                return "profile";
            }
        }
        // In caso di errore reindirizza alla home
        return "redirect:/home?error=profileNotFound";
    }


    @PostMapping("/update")
    public String updateProfile(@ModelAttribute("updateData") ProfileUpdateDTO updateData,
                                Principal principal,
                                HttpServletRequest request,
                                RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }
        try {
            credentialsService.updateCredentials(principal.getName(), updateData);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/profile";
        }
        SecurityContextHolder.clearContext();
        request.getSession().invalidate();

        redirectAttributes.addFlashAttribute("successMessage", "Profilo aggiornato con successo! Effettua nuovamente il login.");
        return "redirect:/login";
    }
}
