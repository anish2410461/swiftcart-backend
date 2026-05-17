package ecommerse.project.repository;

import ecommerse.project.model.CartItem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CartRepository extends MongoRepository<CartItem, String> {

    List<CartItem> findByUsername(String username);
}
