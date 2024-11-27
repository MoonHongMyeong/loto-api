package gg.loto.raid.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum Difficulty {
    NORMAL("노멀"), HARD("하드"), HELL("헬");
    private String DifficultyKor;

    Difficulty(String difficultyKor){
        this.DifficultyKor = difficultyKor;
    }
}
