package gg.loto.character.service;

import java.util.List;
import java.util.Optional;

import gg.loto.character.domain.Characters;
import gg.loto.character.repository.CharactersRepository;
import gg.loto.character.web.dto.CharacterListResponse;
import gg.loto.character.web.dto.CharacterResponse;
import gg.loto.character.web.dto.CharacterSaveRequest;
import gg.loto.character.web.dto.CharacterUpdateRequest;
import gg.loto.global.auth.dto.SessionUser;
import gg.loto.user.domain.User;
import gg.loto.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CharacterServiceUnitTest {
    @InjectMocks
    private CharactersService charactersService;

    @Mock
    private CharactersRepository charactersRepository;

    @Mock
    private UserService userService;

    @Nested
    @DisplayName("캐릭터 생성 테스트")
    class CreateCharacter{
        @Test
        @DisplayName("캐릭터 생성 성공")
        void createCharacterSuccess() {
            // given
            User user = User.builder()
                    .nickname("기존닉네임")
                    .build();
            ReflectionTestUtils.setField(user, "id", 1L);

            SessionUser sessionUser = new SessionUser(user);

            CharacterSaveRequest request = CharacterSaveRequest.builder()
                    .serverName("테스트서버")
                    .characterName("테스트캐릭터")
                    .characterClassName("버서커")
                    .itemAvgLevel("1600.0")
                    .itemMaxLevel("1650.0")
                    .characterLevel(60)
                    .characterImage("image.jpg")
                    .build();

            Characters savedCharacter = request.toEntity(user);

            given(userService.getCurrentUser(any(SessionUser.class))).willReturn(user);
            given(charactersRepository.findByCharacterNameAndUserId(request.getCharacterName(), user.getId()))
                    .willReturn(Optional.empty());
            given(charactersRepository.save(any(Characters.class))).willReturn(savedCharacter);

            // when
            CharacterResponse result = charactersService.createCharacter(sessionUser, request);

            // then
            assertThat(result.getCharacterName()).isEqualTo(request.getCharacterName());
            verify(charactersRepository).save(any(Characters.class));
        }

        @Test
        @DisplayName("중복된 캐릭터 생성 시 예외 발생")
        void createCharacterDuplicateCharacter() {
            // given
            User user = User.builder()
                    .nickname("기존닉네임")
                    .build();
            ReflectionTestUtils.setField(user, "id", 1L);

            SessionUser sessionUser = new SessionUser(user);

            CharacterSaveRequest request = CharacterSaveRequest.builder()
                    .characterName("테스트캐릭터")
                    .build();

            Characters existingCharacter = Characters.builder()
                    .user(user)
                    .characterName("테스트캐릭터")
                    .build();

            given(userService.getCurrentUser(any(SessionUser.class))).willReturn(user);
            given(charactersRepository.findByCharacterNameAndUserId(request.getCharacterName(), user.getId()))
                    .willReturn(Optional.of(existingCharacter));

            // when & then
            assertThatThrownBy(() -> charactersService.createCharacter(sessionUser, request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이미 존재하는 캐릭터입니다.");
        }
    }

    @Nested
    @DisplayName("캐릭터 수정 테스트")
    class UpdateCharacter{
        @Test
        @DisplayName("성공적으로 캐릭터 정보를 수정한다")
        void success() {
            // given
            User user = User.builder()
                    .nickname("기존닉네임")
                    .build();
            ReflectionTestUtils.setField(user, "id", 1L);
            SessionUser sessionUser = new SessionUser(user);
            Long characterId = 1L;

            Characters character = Characters.builder()
                    .user(user)
                    .itemMaxLevel("1500.0")
                    .build();

            CharacterUpdateRequest request = CharacterUpdateRequest.builder()
                    .itemMaxLevel("1550.0")
                    .build();

            given(userService.getCurrentUser(sessionUser)).willReturn(user);
            given(charactersRepository.findByIdAndUserId(characterId, user.getId()))
                    .willReturn(Optional.of(character));

            // when
            CharacterResponse result = charactersService.updateCharacter(sessionUser, characterId, request);

            // then
            assertThat(result.getItemMaxLevel()).isEqualTo(request.getItemMaxLevel());
        }

        @Test
        @DisplayName("존재하지 않는 캐릭터 수정 시 예외가 발생한다")
        void throwException_WhenCharacterNotFound() {
            // given
            User user = User.builder()
                    .nickname("기존닉네임")
                    .build();
            ReflectionTestUtils.setField(user, "id", 1L);
            SessionUser sessionUser = new SessionUser(user);
            Long characterId = 999L;

            CharacterUpdateRequest request = CharacterUpdateRequest.builder()
                    .itemMaxLevel("1550.0")
                    .build();

            given(userService.getCurrentUser(sessionUser)).willReturn(user);
            given(charactersRepository.findByIdAndUserId(characterId, user.getId()))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> charactersService.updateCharacter(sessionUser, characterId, request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("잘못된 요청입니다.");
        }
    }
    @Nested
    @DisplayName("캐릭터 삭제 테스트")
    class DeleteCharacter {

        @Test
        @DisplayName("성공적으로 캐릭터를 삭제한다")
        void success() {
            // given
            User user = User.builder()
                    .nickname("테스트닉네임")
                    .build();
            ReflectionTestUtils.setField(user, "id", 1L);
            SessionUser sessionUser = new SessionUser(user);
            Long characterId = 1L;

            Characters character = Characters.builder()
                    .user(user)
                    .characterName("테스트캐릭터")
                    .build();

            given(charactersRepository.findByIdAndUserId(characterId, user.getId()))
                    .willReturn(Optional.of(character));

            // when
            charactersService.deleteCharacter(sessionUser, characterId);

            // then
            verify(charactersRepository).delete(character);
        }

        @Test
        @DisplayName("존재하지 않는 캐릭터 삭제 시 예외가 발생한다")
        void throwException_WhenCharacterNotFound() {
            // given
            User user = User.builder()
                    .nickname("테스트닉네임")
                    .build();
            ReflectionTestUtils.setField(user, "id", 1L);
            SessionUser sessionUser = new SessionUser(user);
            Long characterId = 999L;

            given(charactersRepository.findByIdAndUserId(characterId, user.getId()))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> charactersService.deleteCharacter(sessionUser, characterId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("잘못된 요청입니다.");
        }

        @Test
        @DisplayName("다른 사용자의 캐릭터 삭제 시도 시 예외가 발생한다")
        void throwException_WhenDeleteOtherUserCharacter() {
            // given
            User owner = User.builder()
                    .nickname("테스트닉네임")
                    .build();
            ReflectionTestUtils.setField(owner, "id", 1L);

            User otherUser = User.builder()
                    .nickname("테스트닉네임2")
                    .build();
            ReflectionTestUtils.setField(otherUser, "id", 2L);

            SessionUser sessionUser = new SessionUser(otherUser);
            Long characterId = 1L;

            Characters character = Characters.builder()
                    .user(owner)
                    .characterName("테스트캐릭터")
                    .build();

            given(charactersRepository.findByIdAndUserId(characterId, otherUser.getId()))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> charactersService.deleteCharacter(sessionUser, characterId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("잘못된 요청입니다.");
        }
    }
    @Test
    @DisplayName("유저의 캐릭터 목록 조회")
    void getUserCharactersSuccess() {
        // given
        User user = User.builder()
                .nickname("기존닉네임")
                .build();
        ReflectionTestUtils.setField(user, "id", 1L);

        Characters character = Characters.builder()
                .user(user)
                .serverName("테스트서버")
                .characterName("테스트캐릭터")
                .characterClassName("버서커")
                .itemAvgLevel("1500.0")
                .itemMaxLevel("1550.0")
                .characterLevel(60)
                .characterImage("image.jpg")
                .build();

        given(charactersRepository.findAllByUserIdOrderByItemMaxLevelDesc(user.getId()))
                .willReturn(List.of(character));

        // when
        List<CharacterListResponse> result = charactersService.getUserCharacters(user);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCharacterName()).isEqualTo(character.getCharacterName());
        assertThat(result.get(0).getItemMaxLevel()).isEqualTo(character.getItemMaxLevel());
    }
}
