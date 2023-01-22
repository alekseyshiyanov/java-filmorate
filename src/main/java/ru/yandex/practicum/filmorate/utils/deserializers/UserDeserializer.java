package ru.yandex.practicum.filmorate.utils.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import ru.yandex.practicum.filmorate.model.User;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UserDeserializer extends StdDeserializer<User> {

    public UserDeserializer() {
        this(null);
    }

    public UserDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public User deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        Long id = null;
        String login = null;
        String email = null;
        String name = null;
        LocalDate birthday = null;

        if (node.hasNonNull("id")) {
            id = node.get("id").asLong();
        }
        if (node.hasNonNull("email")) {
            email = node.get("email").asText();
        }
        if (node.hasNonNull("login")) {
            login = node.get("login").asText();
        }
        if (node.hasNonNull("name")) {
            name = node.get("name").asText();
        }
        if (node.hasNonNull("birthday")) {
            birthday = LocalDate.parse(node.get("birthday").asText(), DateTimeFormatter.ISO_DATE);
        }

        return new User(id, email, login, name, birthday);
    }
}
