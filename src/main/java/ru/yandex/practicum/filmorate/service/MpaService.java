package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmorateBadRequestException;
import ru.yandex.practicum.filmorate.exceptions.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.model.mpa.MPA;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Service
public class MpaService {

    @Autowired
    @Qualifier("mpaDbStorage")
    private MpaStorage mpaStorage;

    public List<MPA> getMpaList() {
        return mpaStorage.getMpaList();
    }

    public MPA getMpa(Long mpaId) {
        checkMpaId(mpaId);

        MPA mpa = mpaStorage.getMpa(mpaId);
        if (mpa == null) {
            throw new FilmorateNotFoundException("Объект с id = " + mpaId + " не найден");
        }
        return mpa;
    }

    private void checkMpaId(Long mpaId) {
        if (mpaId < 0) {
            throw new FilmorateBadRequestException("Параметр 'id' не может быть отрицательным");
        }
    }

}
