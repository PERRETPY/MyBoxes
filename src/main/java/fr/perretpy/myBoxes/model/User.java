package fr.perretpy.myBoxes.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class User {
    private String familyName;
    private String surname;
    private String email;
    private String username;
    private String id;
    private List<String> roles;

    public static User mapAuthenticationToUser(Authentication authentication) throws JsonProcessingException {
        String json = authentication.toString();
        User user = new User();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(json);

        JsonNode principalNode = rootNode.get("principal");
        JsonNode claimsNode = principalNode.get("claims");

        //user.setId(claimsNode.get("sub").asText());
        user.username = claimsNode.get("preferred_username").asText();
        user.email = claimsNode.get("email").asText();
        user.username = claimsNode.get("name").asText();

        List<String> roles = new ArrayList<>();
        JsonNode rolesNode = claimsNode.get("realm_access").get("roles");
        if (rolesNode.isArray()) {
            for (JsonNode role : rolesNode) {
                roles.add(role.asText());
            }
        }
        user.roles = roles;

        return user;
    }
}
