package com.kolbasov.api_gateway.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class CheckJwtResponse {

    @Autowired
    private AuthenticationFilter authenticationFilter;

    @KafkaListener(topics = "ResponseJwtTopic", groupId = "jwtResponse")
    public void listen(boolean bool) {
        authenticationFilter.setResponseMessage(bool);
    }


}
