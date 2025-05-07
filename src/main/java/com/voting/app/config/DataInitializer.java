
package com.voting.app.config;

import com.voting.app.model.Candidate;
import com.voting.app.model.ERole;
import com.voting.app.model.Role;
import com.voting.app.model.User;
import com.voting.app.repository.CandidateRepository;
import com.voting.app.repository.RoleRepository;
import com.voting.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Initialize roles if they don't exist
        initRoles();
        
        // Create admin user if it doesn't exist
        createAdminUser();
        
        // Create sample candidates if none exist
        createSampleCandidates();
    }

    private void initRoles() {
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role(ERole.ROLE_USER));
            roleRepository.save(new Role(ERole.ROLE_ADMIN));
        }
    }

    private void createAdminUser() {
        if (!userRepository.existsByEmail("admin@voting.com")) {
            User admin = new User();
            admin.setName("Administrator");
            admin.setEmail("admin@voting.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            
            Set<Role> roles = new HashSet<>();
            roles.add(roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Admin Role not found.")));
            roles.add(roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: User Role not found.")));
            
            admin.setRoles(roles);
            userRepository.save(admin);
        }
    }

    private void createSampleCandidates() {
        if (candidateRepository.count() == 0) {
            // Create sample candidates
            Candidate candidate1 = new Candidate();
            candidate1.setName("John Smith");
            candidate1.setParty("Democratic Party");
            candidate1.setPosition("President");
            candidate1.setImageUrl("https://via.placeholder.com/150");
            candidateRepository.save(candidate1);
            
            Candidate candidate2 = new Candidate();
            candidate2.setName("Jane Doe");
            candidate2.setParty("Republican Party");
            candidate2.setPosition("President");
            candidate2.setImageUrl("https://via.placeholder.com/150");
            candidateRepository.save(candidate2);
            
            Candidate candidate3 = new Candidate();
            candidate3.setName("Michael Johnson");
            candidate3.setParty("Independent");
            candidate3.setPosition("President");
            candidate3.setImageUrl("https://via.placeholder.com/150");
            candidateRepository.save(candidate3);
        }
    }
}
