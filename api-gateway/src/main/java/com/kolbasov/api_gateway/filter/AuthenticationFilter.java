package com.kolbasov.api_gateway.filter;


import org.apache.hc.core5.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component

public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouteValidator validator;
    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    private CountDownLatch latch = new CountDownLatch(1);

    private boolean bool;

    public AuthenticationFilter() {
        super (Config.class);
    }
    String jwt;

    @Override
    public GatewayFilter apply(Config config) {
        return (((exchange, chain) -> {

            if(validator.isSecured.test(exchange.getRequest())){

                if(!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
                    throw new RuntimeException("Authorization header not present");
                }

                String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                jwt = authHeader.substring(7);
                }
                kafkaTemplate.send("checkJwtTopic", jwt);

                try {
                    latch.await();
                }catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                }

                if(!bool){
                    exchange.getResponse().setStatusCode(HttpStatusCode.valueOf(HttpStatus.SC_FORBIDDEN));
                    latch = new CountDownLatch(1);
                    return exchange.getResponse().setComplete();
                }
                latch = new CountDownLatch(1);

            }


            return chain.filter(exchange);
        }));
    }
    public void setResponseMessage(boolean bool) {
        this.bool = bool;
        latch.countDown();
    }




    public static class Config{

    }

}
