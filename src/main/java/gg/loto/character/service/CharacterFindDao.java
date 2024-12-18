package gg.loto.character.service;

import gg.loto.character.domain.Characters;
import gg.loto.character.repository.CharactersRepository;
import gg.loto.global.exception.EntityNotFoundException;
import gg.loto.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CharacterFindDao {
    private final CharactersRepository characterRepository;

    public Characters findById(Long id){
        return characterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.CHARACTER_NOT_FOUND));
    }
}
