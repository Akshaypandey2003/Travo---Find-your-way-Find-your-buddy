package com.chat.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.chat.Entity.Chat;

import java.util.List;

@Repository
public interface ChatRepo extends MongoRepository<Chat, String> {
  List<Chat> findByParticipantsContaining(String userId);

}
