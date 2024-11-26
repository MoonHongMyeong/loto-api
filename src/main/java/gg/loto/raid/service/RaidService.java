package gg.loto.raid.service;

import gg.loto.character.domain.Characters;
import gg.loto.character.service.CharacterFindDao;
import gg.loto.character.web.dto.CharacterListResponse;
import gg.loto.global.auth.dto.SessionUser;
import gg.loto.raid.entity.CharacterWeeklyRaid;
import gg.loto.raid.entity.Difficulty;
import gg.loto.raid.entity.RaidType;
import gg.loto.raid.web.dto.RaidSaveRequest;
import gg.loto.raid.web.dto.RaidUpdateRequest;
import gg.loto.user.domain.User;
import gg.loto.user.service.UserFindDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RaidService {
    private final UserFindDao userFindDao;
    private final CharacterFindDao characterFindDao;
    @Transactional
    public CharacterListResponse saveWeeklyRaid(SessionUser sessionUser, Long characterId, RaidSaveRequest dto) {
        User user = userFindDao.getCurrentUser(sessionUser);
        Characters character = characterFindDao.findById(characterId);

        if (!character.isOwnership(user)) {
            throw new IllegalArgumentException("본인이 소유한 캐릭터만 가능한 요청입니다.");
        }

        RaidType raidType = RaidType.valueOf(dto.getRaidType().toUpperCase());
        int requiredLevel = raidType.getRequiredItemLevel(
                Difficulty.valueOf(dto.getDifficulty().toUpperCase())
        );

        if (Integer.parseInt(character.getItemMaxLevel()) < requiredLevel) {
            throw new IllegalArgumentException("아이템 레벨이 부족합니다.");
        }

        character.addWeeklyRaid(dto.toEntity(character));

        return CharacterListResponse.of(character);
    }

    @Transactional
    public CharacterListResponse updateWeeklyRaid(SessionUser sessionUser, Long characterId, Long raidId, RaidUpdateRequest dto) {
        User user = userFindDao.getCurrentUser(sessionUser);
        Characters character = characterFindDao.findById(characterId);

        if (!character.isOwnership(user)) {
            throw new IllegalArgumentException("본인이 소유한 캐릭터만 가능한 요청입니다.");
        }

        CharacterWeeklyRaid weeklyRaid = character.getWeeklyRaids().stream()
                .filter(raid -> raid.getId().equals(raidId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 레이드 기록입니다."));

        weeklyRaid.update(dto);

        return CharacterListResponse.of(character);
    }
}
