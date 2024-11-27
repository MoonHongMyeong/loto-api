package gg.loto.raid;

import gg.loto.character.domain.Characters;
import gg.loto.character.service.CharacterFindDao;
import gg.loto.character.web.dto.CharacterListResponse;
import gg.loto.global.auth.dto.SessionUser;
import gg.loto.raid.entity.RaidType;
import gg.loto.raid.service.RaidService;
import gg.loto.raid.web.dto.RaidSaveRequest;
import gg.loto.raid.web.dto.RaidUpdateRequest;
import gg.loto.user.domain.User;
import gg.loto.user.service.UserFindDao;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RaidServiceUnitTest {
    @InjectMocks
    private RaidService raidService;

    @Mock
    private UserFindDao userFindDao;

    @Mock
    private CharacterFindDao characterFindDao;

    @Nested
    @DisplayName("주간 레이드 저장")
    class SaveWeeklyRaid{
        @Test
        @DisplayName("주간 레이드 저장 시 캐릭터의 레벨이 충분하면 성공한다")
        void saveWeeklyRaid_Success_WithSufficientLevel() {
            // given
            User owner = createTestUser(1L);
            Characters character = createTestCharacter(1L, "1490", owner);
            SessionUser sessionUser = new SessionUser(owner);
            RaidSaveRequest request = createRaidSaveRequest("ABRELSHUD", "NORMAL", 1);

            // when
            when(userFindDao.getCurrentUser(any())).thenReturn(owner);
            when(characterFindDao.findById(1L)).thenReturn(character);
            CharacterListResponse response = raidService.saveWeeklyRaid(sessionUser, 1L, request);

            // then
            assertThat(response).isNotNull();
            assertThat(character.getWeeklyRaids()).hasSize(1);
            assertThat(character.getWeeklyRaids().get(0).getRaidType()).isEqualTo(RaidType.ABRELSHUD);
        }

        @Test
        @DisplayName("주간 레이드 저장 시 캐릭터의 레벨이 부족하면 실패한다")
        void saveWeeklyRaid_Fail_WithInsufficientLevel() {
            // given
            User owner = createTestUser(1L);
            Characters character = createTestCharacter(1L, "1415", owner);
            SessionUser sessionUser = new SessionUser(owner);
            RaidSaveRequest request = createRaidSaveRequest("ABRELSHUD", "NORMAL", 1);

            // when
            when(userFindDao.getCurrentUser(any())).thenReturn(owner);
            when(characterFindDao.findById(1L)).thenReturn(character);

            // then
            assertThatThrownBy(() -> raidService.saveWeeklyRaid(sessionUser, 1L, request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("아이템 레벨이 부족합니다.");
        }

        @Test
        @DisplayName("다른 유저의 캐릭터에 레이드를 저장하려고 하면 실패한다")
        void saveWeeklyRaid_Fail_WithWrongOwnership() {
            // given
            User owner = createTestUser(1L);
            User wrongUser = createTestUser(2L);
            Characters character = createTestCharacter(1L, "1490", owner);
            SessionUser wrongSessionUser = new SessionUser(wrongUser);
            RaidSaveRequest request = createRaidSaveRequest("ABRELSHUD", "NORMAL", 1);

            // when
            when(userFindDao.getCurrentUser(any())).thenReturn(wrongUser);
            when(characterFindDao.findById(1L)).thenReturn(character);

            // then
            assertThatThrownBy(() -> raidService.saveWeeklyRaid(wrongSessionUser, 1L, request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("본인이 소유한 캐릭터만 가능한 요청입니다.");
        }

        @Test
        @DisplayName("존재하지 않는 캐릭터 ID로 요청하면 실패한다")
        void saveWeeklyRaid_Fail_WithNonExistentCharacter() {
            // given
            User owner = createTestUser(1L);
            SessionUser sessionUser = new SessionUser(owner);
            RaidSaveRequest request = createRaidSaveRequest("ABRELSHUD", "NORMAL", 1);
            Long nonExistentCharacterId = 999L;

            // when
            when(userFindDao.getCurrentUser(any())).thenReturn(owner);
            when(characterFindDao.findById(nonExistentCharacterId))
                    .thenThrow(new IllegalArgumentException("존재하지 않는 캐릭터입니다."));

            // then
            assertThatThrownBy(() -> raidService.saveWeeklyRaid(sessionUser, nonExistentCharacterId, request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("존재하지 않는 캐릭터입니다.");
        }
    }

    @Nested
    @DisplayName("주간 레이드 수정")
    class UpdateWeeklyRaidTest {

        @Test
        @DisplayName("본인 캐릭터의 레이드 기록을 수정할 수 있다")
        void updateWeeklyRaid_Success() {
            // given
            User owner = createTestUser(1L);
            Characters character = createTestCharacter(1L, "1490", owner);
            SessionUser sessionUser = new SessionUser(owner);

            // 기존 레이드 추가
            RaidSaveRequest saveRequest = createRaidSaveRequest("VALTAN", "NORMAL", 1);
            character.addWeeklyRaid(saveRequest.toEntity(character));
            ReflectionTestUtils.setField(character.getWeeklyRaids().get(0), "id", 1L);
            Long raidId = character.getWeeklyRaids().get(0).getId();


            RaidUpdateRequest updateRequest = RaidUpdateRequest.builder()
                    .difficulty("HARD")
                    .stage(2)
                    .build();

            // when
            when(userFindDao.getCurrentUser(any())).thenReturn(owner);
            when(characterFindDao.findById(1L)).thenReturn(character);

            CharacterListResponse response = raidService.updateWeeklyRaid(sessionUser, 1L, raidId, updateRequest);

            // then
            assertThat(response).isNotNull();
            assertThat(character.getWeeklyRaids()).hasSize(1);
            assertThat(character.getWeeklyRaids().get(0).getDifficulty().name()).isEqualTo("HARD");
            assertThat(character.getWeeklyRaids().get(0).getStage()).isEqualTo(2);
        }

        @Test
        @DisplayName("존재하지 않는 레이드 ID로 수정 요청하면 실패한다")
        void updateWeeklyRaid_Fail_WithNonExistentRaid() {
            // given
            User owner = createTestUser(1L);
            Characters character = createTestCharacter(1L, "1490", owner);
            SessionUser sessionUser = new SessionUser(owner);
            Long nonExistentRaidId = 999L;

            RaidUpdateRequest updateRequest = RaidUpdateRequest.builder()
                    .difficulty("HARD")
                    .stage(2)
                    .build();

            // when
            when(userFindDao.getCurrentUser(any())).thenReturn(owner);
            when(characterFindDao.findById(1L)).thenReturn(character);

            // then
            assertThatThrownBy(() ->
                    raidService.updateWeeklyRaid(sessionUser, 1L, nonExistentRaidId, updateRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("존재하지 않는 레이드 기록입니다.");
        }

        @Test
        @DisplayName("다른 유저의 레이드 기록을 수정하려고 하면 실패한다")
        void updateWeeklyRaid_Fail_WithWrongOwnership() {
            // given
            User owner = createTestUser(1L);
            User wrongUser = createTestUser(2L);
            Characters character = createTestCharacter(1L, "1490", owner);
            SessionUser wrongSessionUser = new SessionUser(wrongUser);

            // 레이드 추가
            RaidSaveRequest saveRequest = createRaidSaveRequest("VALTAN", "NORMAL", 1);
            character.addWeeklyRaid(saveRequest.toEntity(character));
            Long raidId = 1L;
            ReflectionTestUtils.setField(character.getWeeklyRaids().get(0), "id", raidId);

            RaidUpdateRequest updateRequest = RaidUpdateRequest.builder()
                    .difficulty("HARD")
                    .stage(2)
                    .build();

            // when
            when(userFindDao.getCurrentUser(any())).thenReturn(wrongUser);
            when(characterFindDao.findById(1L)).thenReturn(character);

            // then
            assertThatThrownBy(() ->
                    raidService.updateWeeklyRaid(wrongSessionUser, 1L, raidId, updateRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("본인이 소유한 캐릭터만 가능한 요청입니다.");
        }
    }

    @Nested
    @DisplayName("주간 레이드 삭제")
    class RemoveWeeklyRaidTest {

        @Test
        @DisplayName("본인 캐릭터의 레이드 기록을 삭제할 수 있다")
        void removeWeeklyRaid_Success() {
            // given
            User owner = createTestUser(1L);
            Characters character = createTestCharacter(1L, "1490", owner);
            SessionUser sessionUser = new SessionUser(owner);

            // 레이드 추가
            RaidSaveRequest saveRequest = createRaidSaveRequest("VALTAN", "NORMAL", 1);
            character.addWeeklyRaid(saveRequest.toEntity(character));
            Long raidId = 1L;
            ReflectionTestUtils.setField(character.getWeeklyRaids().get(0), "id", raidId);

            // when
            when(userFindDao.getCurrentUser(any())).thenReturn(owner);
            when(characterFindDao.findById(1L)).thenReturn(character);

            CharacterListResponse response = raidService.removeWeeklyRaid(sessionUser, 1L, raidId);

            // then
            assertThat(response).isNotNull();
            assertThat(character.getWeeklyRaids()).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 레이드 ID로 삭제 요청하면 실패한다")
        void removeWeeklyRaid_Fail_WithNonExistentRaid() {
            // given
            User owner = createTestUser(1L);
            Characters character = createTestCharacter(1L, "1490", owner);
            SessionUser sessionUser = new SessionUser(owner);
            Long nonExistentRaidId = 999L;

            // when
            when(userFindDao.getCurrentUser(any())).thenReturn(owner);
            when(characterFindDao.findById(1L)).thenReturn(character);

            // then
            assertThatThrownBy(() ->
                    raidService.removeWeeklyRaid(sessionUser, 1L, nonExistentRaidId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("삭제할 레이드가 존재하지 않습니다.");
        }

        @Test
        @DisplayName("다른 유저의 레이드 기록을 삭제하려고 하면 실패한다")
        void removeWeeklyRaid_Fail_WithWrongOwnership() {
            // given
            User owner = createTestUser(1L);
            User wrongUser = createTestUser(2L);
            Characters character = createTestCharacter(1L, "1490", owner);
            SessionUser wrongSessionUser = new SessionUser(wrongUser);

            // 레이드 추가
            RaidSaveRequest saveRequest = createRaidSaveRequest("VALTAN", "NORMAL", 1);
            character.addWeeklyRaid(saveRequest.toEntity(character));
            Long raidId = 1L;
            ReflectionTestUtils.setField(character.getWeeklyRaids().get(0), "id", raidId);

            // when
            when(userFindDao.getCurrentUser(any())).thenReturn(wrongUser);
            when(characterFindDao.findById(1L)).thenReturn(character);

            // then
            assertThatThrownBy(() ->
                    raidService.removeWeeklyRaid(wrongSessionUser, 1L, raidId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("본인이 소유한 캐릭터만 가능한 요청입니다.");
        }
    }

    private Characters createTestCharacter(Long id, String itemLevel, User owner) {
        Characters character = Characters.builder()
                .user(owner)
                .itemMaxLevel(itemLevel)
                .build();
        ReflectionTestUtils.setField(character, "id", id);
        return character;
    }

    private User createTestUser(Long id) {
        User user = User.builder()
                .email("test@test.com")
                .password("password")
                .nickname("tester")
                .build();
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private RaidSaveRequest createRaidSaveRequest(String raidType, String difficulty, int stage) {
        return RaidSaveRequest.builder()
                .raidType(raidType)
                .difficulty(difficulty)
                .stage(stage)
                .build();
    }
}
