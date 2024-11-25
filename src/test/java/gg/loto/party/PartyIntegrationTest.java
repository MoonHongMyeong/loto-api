package gg.loto.party;

import gg.loto.character.service.CharactersService;
import gg.loto.character.web.dto.CharacterSaveRequest;
import gg.loto.global.auth.dto.SessionUser;
import gg.loto.party.domain.PartyType;
import gg.loto.party.service.PartyService;
import gg.loto.party.web.dto.PartyListResponse;
import gg.loto.party.web.dto.PartyMemberRequest;
import gg.loto.party.web.dto.PartySaveRequest;
import gg.loto.user.service.UserFindDao;
import gg.loto.user.service.UserService;
import gg.loto.user.web.dto.UserResponse;
import gg.loto.user.web.dto.UserSaveRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles({"h2", "local"})
@Transactional
public class PartyIntegrationTest {
    @Autowired
    private PartyService partyService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserFindDao userFindDao;

    @Autowired
    private CharactersService characterService;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @DisplayName("유저1의 공유방 목록 조회 1건 성공")
    void getMyParties_user1_success() {
        // given
        SessionUser user1 = createUser("test1@test.com", "타이탈로스의하수인");
        Long characterId = createCharacter(user1, "타이탈로스의하수인", "인파이터", "1640.8");
        Long partyId = createParty(user1, "테스트 공유방");
        joinParty(user1, partyId, characterId);

        // 영속성 컨텍스트 변경사항 반영 후 초기화
        entityManager.flush();
        entityManager.clear();

        // when
        List<PartyListResponse> result = partyService.getMyParties(user1);

        // then
        assertThat(result).hasSize(1)
                .extracting("partyName")
                .containsExactly("테스트 공유방");
    }

    @Test
    @DisplayName("유저2의 공유방 목록 조회 2건 성공")
    void getMyParties_user2_success() {
        // given
        SessionUser user1 = createUser("test1@test.com", "타이탈로스의하수인");
        SessionUser user2 = createUser("test2@test.com", "라우리엘의하수인");

        // 유저1의 파티 생성
        Long user1CharacterId = createCharacter(user1, "타이탈로스의하수인", "인파이터", "1640.8");
        Long user1PartyId = createParty(user1, "테스트 공유방");
        joinParty(user1, user1PartyId, user1CharacterId);

        // 유저2의 파티 생성 및 참여
        Long user2CharacterId = createCharacter(user2, "라우리엘의하수인", "브레이커", "1680");
        Long user2PartyId = createParty(user2, "테스트 공유방222");
        joinParty(user2, user2PartyId, user2CharacterId);
        joinParty(user2, user1PartyId, user2CharacterId);

        // 영속성 컨텍스트 변경사항 반영 후 초기화
        entityManager.flush();
        entityManager.clear();

        // when
        List<PartyListResponse> result = partyService.getMyParties(user2);

        // then
        assertThat(result).hasSize(2)
                .extracting("partyName")
                .containsExactlyInAnyOrder("테스트 공유방", "테스트 공유방222");
    }

    @Test
    @DisplayName("파티에 참여하지 않은 유저는 상세 조회 시 예외가 발생한다")
    void getPartyWithMembers_throwException_whenNotJoinedUser() {
        // given
        SessionUser partyLeader = createUser("leader@test.com", "방장");
        SessionUser otherUser = createUser("other@test.com", "비참여자");

        Long leaderCharacterId = createCharacter(partyLeader, "방장캐릭터", "디스트로이어", "1670.0");
        Long partyId = createParty(partyLeader, "레이드팟");
        joinParty(partyLeader, partyId, leaderCharacterId);

        entityManager.flush();
        entityManager.clear();

        // when & then
        assertThatThrownBy(() ->
                partyService.getPartyWithMembers(otherUser, partyId)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("참여한 공유방이 아닙니다.");
    }

    private SessionUser createUser(String email, String nickname) {
        UserResponse response = userService.signUp(UserSaveRequest.builder()
                .email(email)
                .nickname(nickname)
                .password("password1234")
                .build());
        return new SessionUser(userFindDao.findById(response.getId()));
    }

    private Long createCharacter(SessionUser user, String characterName, String className, String itemLevel) {
        return characterService.createCharacter(user, CharacterSaveRequest.builder()
                .serverName("카단")
                .characterName(characterName)
                .characterLevel(70)
                .characterClassName(className)
                .itemAvgLevel(itemLevel)
                .itemMaxLevel(itemLevel)
                .build()).getId();
    }

    private Long createParty(SessionUser user, String partyName) {
        return partyService.createParty(user, PartySaveRequest.builder()
                .name(partyName)
                .capacity(8)
                .partyType(String.valueOf(PartyType.FRIENDLY))
                .build()).getId();
    }

    private void joinParty(SessionUser user, Long partyId, Long characterId) {
        partyService.joinParty(user, partyId, PartyMemberRequest.builder()
                .characters(List.of(characterId))
                .build());
    }
}
