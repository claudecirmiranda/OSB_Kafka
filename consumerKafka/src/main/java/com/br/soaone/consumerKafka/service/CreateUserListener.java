package com.br.soaone.consumerKafka.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.br.soaone.consumerKafka.gateway.json.UserJson;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CreateUserListener {

/*
	@Autowired
    private CreateUserListener createUserService;
*/
	
    @KafkaListener(topics = "${kafka.topic.request-topic}")
    @SendTo
    public String listen(String json) throws InterruptedException, JsonProcessingException {

		final String uri = "http://localhost:8002/SBConsumer/RestCriaPessoa";
		RestTemplate restTemplate = new RestTemplate();

		ObjectMapper mapper = new ObjectMapper();

        UserJson userJson = mapper.readValue(json, UserJson.class);

        restTemplate.put ( uri, userJson, userJson );        

        System.out.println("(Consumer) NOME: " + userJson.getName());
        System.out.println("(Consumer) UUid: " + userJson.getUuid());
        
        return mapper.writeValueAsString(userJson);
    }
    
	
}
