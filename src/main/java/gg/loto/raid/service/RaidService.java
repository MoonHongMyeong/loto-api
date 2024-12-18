package gg.loto.raid.service;

import gg.loto.character.domain.Characters;
import gg.loto.character.service.CharacterFindDao;
import gg.loto.character.web.dto.CharacterListResponse;
import gg.loto.global.exception.ErrorCode;
import gg.loto.raid.entity.CharacterWeeklyRaid;
import gg.loto.raid.entity.Difficulty;
import gg.loto.raid.entity.RaidType;
import gg.loto.raid.exception.RaidException;
import gg.loto.raid.web.dto.RaidSaveRequest;
import gg.loto.raid.web.dto.RaidUpdateRequest;
import gg.loto.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RaidService {
    private final CharacterFindDao characterFindDao;
    @Transactional
    public CharacterListResponse saveWeeklyRaid(User user, Long characterId, RaidSaveRequest dto) {
        Characters character = characterFindDao.findById(characterId);

        if (!character.isOwnership(user)) {
            throw new RaidException(ErrorCode.NOT_CHARACTER_OWNER);
        }

        RaidType raidType = RaidType.valueOf(dto.getRaidType().toUpperCase());
        int requiredLevel = raidType.getRequiredItemLevel(
                Difficulty.valueOf(dto.getDifficulty().toUpperCase())
        );

        if (Integer.parseInt(character.getItemMaxLevel()) < requiredLevel) {
            throw new RaidException(ErrorCode.INSUFFICIENT_ITEM_LEVEL);
        }

        character.addWeeklyRaid(dto.toEntity(character));

        return CharacterListResponse.of(character);
    }

    @Transactional
    public CharacterListResponse updateWeeklyRaid(User user, Long characterId, Long raidId, RaidUpdateRequest dto) {
        Characters character = characterFindDao.findById(characterId);

        if (!character.isOwnership(user)) {
            throw new RaidException(ErrorCode.NOT_CHARACTER_OWNER);
        }

        CharacterWeeklyRaid weeklyRaid = character.getWeeklyRaids().stream()
                .filter(raid -> raid.getId().equals(raidId))
                .findFirst()
                .orElseThrow(() -> new RaidException(ErrorCode.RAID_RECORD_NOT_FOUND));

        weeklyRaid.update(dto);

        return CharacterListResponse.of(character);
    }

    @Transactional
    public CharacterListResponse removeWeeklyRaid(User user, Long characterId, Long raidId) {
        Characters character = characterFindDao.findById(characterId);

        if (!character.isOwnership(user)) {
            throw new RaidException(ErrorCode.NOT_CHARACTER_OWNER);
        }

        character.removeWeeklyRaid(raidId);

        return CharacterListResponse.of(character);
    }
}
