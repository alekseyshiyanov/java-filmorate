package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.mpa.MPA;

import java.util.List;

public interface MpaStorage {

    List<MPA> getMpaList();

    MPA getMpa(Long mpaId);
}
