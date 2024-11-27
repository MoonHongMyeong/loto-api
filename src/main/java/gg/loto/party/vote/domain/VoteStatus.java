package gg.loto.party.vote.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum VoteStatus {
    IN_PROGRESS("진행중"),
    COMPLETE("완료"),
    CANCEL("취소"),
    EXPIRED("만료");

    private String description;

    VoteStatus(String description) {
        this.description = description;
    }
}
