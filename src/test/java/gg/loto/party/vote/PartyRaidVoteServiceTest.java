package gg.loto.party.vote;

import gg.loto.character.domain.Characters;
import gg.loto.character.service.CharacterFindDao;
import gg.loto.global.auth.dto.SessionUser;
import gg.loto.party.domain.Party;
import gg.loto.party.service.PartyFindDao;
import gg.loto.party.vote.domain.PartyRaidVote;
import gg.loto.party.vote.domain.PartyRaidVoteParticipant;
import gg.loto.party.vote.domain.VoteStatus;
import gg.loto.party.vote.repository.PartyRaidVoteRepository;
import gg.loto.party.vote.service.PartyRaidVoteService;
import gg.loto.party.vote.web.dto.VoteParticipantSaveRequest;
import gg.loto.party.vote.web.dto.VoteResponse;
import gg.loto.party.vote.web.dto.VoteSaveRequest;
import gg.loto.raid.entity.Difficulty;
import gg.loto.raid.entity.RaidType;
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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PartyRaidVoteServiceTest {
    @InjectMocks
    private PartyRaidVoteService voteService;

    @Mock
    private UserFindDao userFindDao;
    @Mock
    private PartyFindDao partyFindDao;
    @Mock
    private CharacterFindDao characterFindDao;
    @Mock
    private PartyRaidVoteRepository voteRepository;

    @Nested
    @DisplayName("투표 생성")
    class CreateVote {
        @Test
        @DisplayName("투표가 정상적으로 생성된다")
        void createVote_Success() {
            // given
            User user = createTestUser(1L);
            Party party = createTestParty(1L, user);
            Characters character = createTestCharacter(1L, user);
            party.addMember(character);  // 파티에 멤버 추가
            SessionUser sessionUser = new SessionUser(user);
            VoteSaveRequest request = createVoteSaveRequest(character.getId());
            PartyRaidVote vote = createTestVote(1L, party, user);

            when(userFindDao.getCurrentUser(any())).thenReturn(user);
            when(partyFindDao.findPartyById(1L)).thenReturn(party);
            when(characterFindDao.findById(character.getId())).thenReturn(character);
            when(voteRepository.save(any())).thenReturn(vote);

            // when
            VoteResponse response = voteService.createVote(sessionUser, 1L, request);

            // then
            assertThat(response.getCreatorName()).isEqualTo(user.getNickname());
            assertThat(response.getVoteStatus()).isEqualTo(VoteStatus.IN_PROGRESS.getDescription());
        }

        @Test
        @DisplayName("파티 멤버가 아니면 투표를 생성할 수 없다")
        void createVote_Fail_WhenNotPartyMember() {
            // given
            User user = createTestUser(1L);
            User partyOwner = createTestUser(2L);
            Party party = createTestParty(1L, partyOwner);  // user는 멤버가 아님
            SessionUser sessionUser = new SessionUser(user);
            VoteSaveRequest request = createVoteSaveRequest(1L);

            when(userFindDao.getCurrentUser(any())).thenReturn(user);
            when(partyFindDao.findPartyById(1L)).thenReturn(party);

            // when & then
            assertThatThrownBy(() -> voteService.createVote(sessionUser, 1L, request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("참여한 공유방만 투표생성이 가능합니다.");
        }
    }

    @Nested
    @DisplayName("투표 참여")
    class JoinVote {
        @Test
        @DisplayName("진행중이 아닌 투표에는 참여할 수 없다")
        void joinVote_Fail_WhenNotInProgress() {
            // given
            User user = createTestUser(1L);
            PartyRaidVote vote = createTestVote(1L, createTestParty(1L, user), user);
            vote.cancel();  // 취소된 투표
            SessionUser sessionUser = new SessionUser(user);
            VoteParticipantSaveRequest request = new VoteParticipantSaveRequest(1L);

            when(userFindDao.getCurrentUser(any())).thenReturn(user);
            when(voteRepository.findById(vote.getId())).thenReturn(Optional.of(vote));

            // when & then
            assertThatThrownBy(() -> voteService.joinVote(sessionUser, vote.getId(), request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("진행 중인 투표만 참여할 수 있습니다.");
        }

        @Test
        @DisplayName("이미 참여한 캐릭터는 다시 참여할 수 없다")
        void joinVote_Fail_WhenAlreadyJoined() {
            // given
            User user = createTestUser(1L);
            Party party = createTestParty(1L, user);
            Characters character = createTestCharacter(1L, user);
            SessionUser sessionUser = new SessionUser(user);
            
            PartyRaidVote vote = createTestVote(1L, party, user);
            VoteParticipantSaveRequest request = new VoteParticipantSaveRequest(character.getId());

            // 먼저 한번 참여시킴
            PartyRaidVoteParticipant participant = new PartyRaidVoteParticipant(vote, character);
            vote.join(participant);

            when(userFindDao.getCurrentUser(any())).thenReturn(user);
            when(voteRepository.findById(1L)).thenReturn(Optional.of(vote));
            when(characterFindDao.findById(1L)).thenReturn(character);

            // when & then
            // 동일한 캐릭터로 다시 참여 시도
            assertThatThrownBy(() -> voteService.joinVote(sessionUser, 1L, request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이미 참여한 캐릭터입니다.");
        }
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

    private Party createTestParty(Long id, User owner) {
        Party party = Party.builder()
                .user(owner)
                .name("테스트 파티")
                .capacity(10)
                .build();
        ReflectionTestUtils.setField(party, "id", id);
        return party;
    }

    private Characters createTestCharacter(Long id, User user) {
        Characters character = Characters.builder()
                .user(user)
                .characterName("테스트캐릭터")
                .build();
        ReflectionTestUtils.setField(character, "id", id);
        return character;
    }

    private VoteSaveRequest createVoteSaveRequest(Long characterId) {
        return VoteSaveRequest.builder()
                .characterId(characterId)
                .name("테스트 투표")
                .raidType(RaidType.VALTAN.name())
                .difficulty(Difficulty.NORMAL.name())
                .targetGateNumber(1)
                .raidDatetime(LocalDateTime.now().plusDays(1))
                .voteExpiresAt(LocalDateTime.now().plusHours(24))
                .build();
    }

    private PartyRaidVote createTestVote(Long id, Party party, User user) {
        PartyRaidVote vote = PartyRaidVote.builder()
                .party(party)
                .creator(user)
                .name("테스트 투표")
                .raidType(RaidType.VALTAN)
                .difficulty(Difficulty.NORMAL)
                .targetGateNumber(1)
                .raidDatetime(LocalDateTime.now().plusDays(1))
                .voteExpiresAt(LocalDateTime.now().plusHours(24))
                .voteStatus(VoteStatus.IN_PROGRESS)
                .build();
        ReflectionTestUtils.setField(vote, "id", id);
        return vote;
    }
}
