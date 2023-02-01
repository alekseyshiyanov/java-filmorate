package ru.yandex.practicum.filmorate.utils.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import ru.yandex.practicum.filmorate.model.Film;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FilmDeserializer extends StdDeserializer<Film> {

    public FilmDeserializer() {
        this(null);
    }

    public FilmDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Film deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        Long id = null;
        String name = null;
        String description = null;
        LocalDate  releaseDate = null;
        Integer duration = null;

        if (node.hasNonNull("id")) {
            id = node.get("id").asLong();
        }
        if (node.hasNonNull("name")) {
            name = node.get("name").asText();
        }
        if (node.hasNonNull("description")) {
            description = node.get("description").asText();
        }
        if (node.hasNonNull("releaseDate")) {
            releaseDate = LocalDate.parse(node.get("releaseDate").asText(), DateTimeFormatter.ISO_DATE);
        }
        if (node.hasNonNull("duration")) {
            duration = node.get("duration").asInt();
        }

        return new Film(id, name, description, releaseDate, duration);
    }
}

