package by.kolbasov.auth_service.service;


import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class KafkaMessageHandler {

    private final KafkaTemplate<String, Boolean> kafkaTemplate;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;


    @KafkaListener(topics = "checkJwtTopic", groupId = "jwtRequest")
    public void checkJwt(String jwt) {
        String userEmail = null;
        jwt=jwt.substring(1, jwt.length() - 1);
       try {
           userEmail = jwtService.extractUsername(jwt);
       }catch (Exception e){
           kafkaTemplate.send("ResponseJwtTopic", false);
       }
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                kafkaTemplate.send("ResponseJwtTopic", true);
            }else  kafkaTemplate.send("ResponseJwtTopic", false);
        }

    }
}