package com.br.soaone.SpringBootWebLogic.service;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.br.soaone.SpringBootWebLogic.gateway.json.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UserService {

    @Autowired
    ReplyingKafkaTemplate<String, String, String> kafkaTemplate;

    @Value("${kafka.topic.request-topic}")
    String requestTopic;

    @Value("${kafka.topic.requestreply-topic}")
    String requestReplyTopic;
    public ResponseEntity<String> execute(User user) throws ExecutionException, InterruptedException, JsonProcessingException {
    	
        // convertendo obj para string
        ObjectMapper mapper = new ObjectMapper();
        byte[] uuid = UUID.randomUUID().toString().getBytes();
        user.setUuid(uuid.toString());
        String jsonString = mapper.writeValueAsString(user);

        // montando o producer que ira ser enviado para o kafka
        ProducerRecord<String, String> record = new ProducerRecord<String, String>(requestTopic, jsonString);
        record.headers().add(new RecordHeader("UUID", uuid));
        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, requestReplyTopic.getBytes()));

        // enviando
        RequestReplyFuture<String, String, String> sendAndReceive = kafkaTemplate.sendAndReceive(record);

        // recebendos o retorno
        SendResult<String, String> sendResult = sendAndReceive.getSendFuture().get();
        sendResult.getProducerRecord().headers().forEach(header -> System.out.println(header.key() + ":" + header.value().toString()));

        //ConsumerRecord<String, String> consumerRecord = sendAndReceive.get();

        //User customerJsonReturn = mapper.readValue(consumerRecord.value(), User.class);

        return new ResponseEntity<>(sendResult.toString(),
        	      HttpStatus.OK);
    
    }	
}
