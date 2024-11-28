package gg.loto.character.service;

import gg.loto.character.domain.Characters;
import gg.loto.character.repository.CharactersRepository;
import gg.loto.character.web.dto.CharacterListResponse;
import gg.loto.character.web.dto.CharacterResponse;
import gg.loto.character.web.dto.CharacterSaveRequest;
import gg.loto.character.web.dto.CharacterUpdateRequest;
import gg.loto.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CharactersService {
    private final CharactersRepository charactersRepository;

    @Transactional
    public CharacterResponse createCharacter(User user, CharacterSaveRequest dto) {
        validateDuplicateCharacter(dto.getCharacterName(), user.getId());

        Characters character = dto.toEntity(user);

        Characters savedCharacter = charactersRepository.save(character);

        return CharacterResponse.of(savedCharacter);
    }
   
    private void validateDuplicateCharacter(String characterName, Long userId) {
        charactersRepository.findByCharacterNameAndUserId(characterName, userId)
                .ifPresent(character -> {
                    throw new IllegalArgumentException("이미 존재하는 캐릭터입니다.");
                });
    }

    @Transactional(readOnly = true)
    public List<CharacterListResponse> getUserCharacters(User user) {
        return charactersRepository.findAllByUserIdOrderByItemMaxLevelDesc(user.getId()).stream()
                .map(CharacterListResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public CharacterResponse updateCharacter(User user, Long characterId, CharacterUpdateRequest dto) {

        Characters character = charactersRepository.findByIdAndUserId(characterId, user.getId())
                .orElseThrow(() -> new RuntimeException("잘못된 요청입니다."));

        character.update(dto);

        return CharacterResponse.of(character);
    }
    @Transactional
    public void deleteCharacter(User user, Long characterId) {
        Characters character = charactersRepository.findByIdAndUserId(characterId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));

        charactersRepository.delete(character);
    }

    @Transactional(readOnly = true)
    public CharacterResponse getUserCharacter(User user, Long characterId) {
        Characters character = charactersRepository.findByIdAndUserId(characterId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("캐릭터가 없습니다."));
        return CharacterResponse.of(character);
    }

    @Transactional(readOnly = true)
    public List<Characters> findAllById(Set<Long> charactersId) {
        return charactersRepository.findAllById(charactersId);
    }

    public void validateCharacterOwnership(List<Characters> characters, User user) {
        boolean hasInvalidCharacterOwnership = characters.stream()
                .anyMatch(character -> !character.getUser().equals(user));
        
        if (hasInvalidCharacterOwnership){
            throw new RuntimeException("본인이 등록한 캐릭터만 공유방에 참여, 탈퇴 할 수 있습니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<Characters> findAllByUser(Long targetUserId) {
        return charactersRepository.findAllByUserId(targetUserId);
    }
}
