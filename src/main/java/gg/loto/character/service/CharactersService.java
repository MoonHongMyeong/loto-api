package gg.loto.character.service;

import gg.loto.character.domain.Characters;
import gg.loto.character.repository.CharactersRepository;
import gg.loto.character.web.dto.CharacterListResponse;
import gg.loto.character.web.dto.CharacterSaveRequest;
import gg.loto.global.auth.dto.SessionUser;
import gg.loto.user.domain.User;
import gg.loto.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CharactersService {
    private final CharactersRepository charactersRepository;
    private final UserService userService;

    @Transactional
    public List<CharacterListResponse> createCharacter(SessionUser sessionUser, CharacterSaveRequest dto) {
        User user = userService.getCurrentUser(sessionUser);
        validateDuplicateCharacter(dto.getCharacterName(), user.getId());

        saveCharacter(dto, user);

        return getUserCharacters(user);
    }

    private void saveCharacter(CharacterSaveRequest dto, User user) {
        Characters character = Characters.builder()
                .user(user)
                .serverName(dto.getServerName())
                .characterName(dto.getCharacterName())
                .characterClassName(dto.getCharacterClassName())
                .itemAvgLevel(dto.getItemAvgLevel())
                .itemMaxLevel(dto.getItemMaxLevel())
                .characterLevel(dto.getCharacterLevel())
                .characterImage(dto.getCharacterImage())
                .build();

        charactersRepository.save(character);
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
}
