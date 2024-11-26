package gg.loto.character.service;

import gg.loto.character.domain.Characters;
import gg.loto.character.repository.CharactersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CharacterFindDao {
    private final CharactersRepository characterRepository;

    public Characters findById(Long id){
        return characterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 캐릭터입니다."));
    }
}
