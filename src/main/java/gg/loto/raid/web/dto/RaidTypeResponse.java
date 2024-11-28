package gg.loto.raid.web.dto;

import gg.loto.raid.entity.Difficulty;
import gg.loto.raid.entity.RaidType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class RaidTypeResponse {
    private String name;
    private String bossNameKor;
    private List<String> difficulties;

    @Builder
    public RaidTypeResponse(String name, String bossNameKor, List<String> difficulties) {
        this.name = name;
        this.bossNameKor = bossNameKor;
        this.difficulties = difficulties;
    }

    public static RaidTypeResponse from(RaidType raidType) {
        List<String> availableDifficulties = Arrays.stream(Difficulty.values())
                .filter(raidType::canEnterWithDifficulty)
                .map(Difficulty::name)
                .collect(Collectors.toList());

        return RaidTypeResponse.builder()
                .name(raidType.name())
                .bossNameKor(raidType.getBossNameKor())
                .difficulties(availableDifficulties)
                .build();
    }
}
