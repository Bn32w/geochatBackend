package com.geochat.geochat.repository;

import com.geochat.geochat.domain.Chat;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data MongoDB repository for the Chat entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {

}
