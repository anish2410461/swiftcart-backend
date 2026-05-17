package ecommerse.project.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String username;
    private String email;
    private String password;
    private String role; // USER or ADMIN

    // Address fields for persistence
    private String fullName;
    private String streetAddress;
    private String city;
    private String state;
    private String zipCode;
}
