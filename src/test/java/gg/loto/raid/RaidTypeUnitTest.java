package gg.loto.raid;

import gg.loto.raid.entity.Difficulty;
import gg.loto.raid.entity.RaidType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RaidTypeUnitTest {
    @Nested
    @DisplayName("레이드 난이도별 요구 레벨 테스트")
    class RequiredItemLevelTest {

        @Test
        @DisplayName("발탄 레이드는 노말 1415, 하드 1445, 헬 1445 레벨이 필요하다")
        void valtanRequiredLevel() {
            assertThat(RaidType.VALTAN.getRequiredItemLevel(Difficulty.NORMAL)).isEqualTo(1415);
            assertThat(RaidType.VALTAN.getRequiredItemLevel(Difficulty.HARD)).isEqualTo(1445);
            assertThat(RaidType.VALTAN.getRequiredItemLevel(Difficulty.HELL)).isEqualTo(1445);
        }

        @Test
        @DisplayName("비아키스 레이드는 노말 1430, 하드 1460, 헬 1460 레벨이 필요하다")
        void vykasRequiredLevel() {
            assertThat(RaidType.VYKAS.getRequiredItemLevel(Difficulty.NORMAL)).isEqualTo(1430);
            assertThat(RaidType.VYKAS.getRequiredItemLevel(Difficulty.HARD)).isEqualTo(1460);
            assertThat(RaidType.VYKAS.getRequiredItemLevel(Difficulty.HELL)).isEqualTo(1460);
        }

        @Test
        @DisplayName("쿠크세이튼 레이드는 노말 1475, 헬 1475 레벨이 필요하며 하드모드는 없다")
        void koukusatonRequiredLevel() {
            assertThat(RaidType.KOUKUSATON.getRequiredItemLevel(Difficulty.NORMAL)).isEqualTo(1475);
            assertThat(RaidType.KOUKUSATON.getRequiredItemLevel(Difficulty.HELL)).isEqualTo(1475);
            assertThatThrownBy(() -> RaidType.KOUKUSATON.getRequiredItemLevel(Difficulty.HARD))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("지원하지 않는 난이도입니다");
        }

        @Test
        @DisplayName("카양겔 레이드는 노말 1540, 하드 1580 레벨이 필요하다.")
        void kayangelRequiredLevel(){
            assertThat(RaidType.KAYANGEL.getRequiredItemLevel(Difficulty.NORMAL)).isEqualTo(1540);
            assertThat(RaidType.KAYANGEL.getRequiredItemLevel(Difficulty.HARD)).isEqualTo(1580);
            assertThatThrownBy(() -> RaidType.KAYANGEL.getRequiredItemLevel(Difficulty.HELL))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("지원하지 않는 난이도입니다");
        }

        @Test
        @DisplayName("일리아칸 레이드는 노말 1580, 하드 1600 레벨이 필요하다.")
        void illiakkanRequiredLevel(){
            assertThat(RaidType.ILLIAKKAN.getRequiredItemLevel(Difficulty.NORMAL)).isEqualTo(1580);
            assertThat(RaidType.ILLIAKKAN.getRequiredItemLevel(Difficulty.HARD)).isEqualTo(1600);
            assertThatThrownBy(() -> RaidType.ILLIAKKAN.getRequiredItemLevel(Difficulty.HELL))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("지원하지 않는 난이도입니다");
        }

        @Test
        @DisplayName("상아탑 레이드는 노말 1600, 하드 1620 레벨이 필요하다.")
        void ivoryTowerRequiredLevel(){
            assertThat(RaidType.IVORYTOWER.getRequiredItemLevel(Difficulty.NORMAL)).isEqualTo(1600);
            assertThat(RaidType.IVORYTOWER.getRequiredItemLevel(Difficulty.HARD)).isEqualTo(1620);
            assertThatThrownBy(() -> RaidType.IVORYTOWER.getRequiredItemLevel(Difficulty.HELL))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("지원하지 않는 난이도입니다");
        }

        @Test
        @DisplayName("카멘 레이드는 노말 1610, 하드 1630 레벨이 필요하다")
        void kamenRequiredLevel(){
            assertThat(RaidType.KAMEN.getRequiredItemLevel(Difficulty.NORMAL)).isEqualTo(1610);
            assertThat(RaidType.KAMEN.getRequiredItemLevel(Difficulty.HARD)).isEqualTo(1630);
            assertThatThrownBy(() -> RaidType.KAMEN.getRequiredItemLevel(Difficulty.HELL))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("지원하지 않는 난이도입니다");
        }

        @Test
        @DisplayName("에키드나 레이드는 노말 1620, 하드 1640 레벨이 필요하다")
        void echidnaRequiredLevel(){
            assertThat(RaidType.ECHIDNA.getRequiredItemLevel(Difficulty.NORMAL)).isEqualTo(1620);
            assertThat(RaidType.ECHIDNA.getRequiredItemLevel(Difficulty.HARD)).isEqualTo(1640);
            assertThatThrownBy(() -> RaidType.ECHIDNA.getRequiredItemLevel(Difficulty.HELL))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("지원하지 않는 난이도입니다");
        }

        @Test
        @DisplayName("베히모스 레이드는 노말 1640 레벨이 필요하며 하드는 없다.")
        void behemothRequiredLevel(){
            assertThat(RaidType.BEHEMOTH.getRequiredItemLevel(Difficulty.NORMAL)).isEqualTo(1640);
            assertThatThrownBy(() -> RaidType.BEHEMOTH.getRequiredItemLevel(Difficulty.HARD))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("지원하지 않는 난이도입니다");
            assertThatThrownBy(() -> RaidType.BEHEMOTH.getRequiredItemLevel(Difficulty.HELL))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("지원하지 않는 난이도입니다");
        }

        @Test
        @DisplayName("에기르 레이드는 노말 1660, 하드 1680 레벨이 필요하다.")
        void EgirRequiredLevel(){
            assertThat(RaidType.KAZEROTH_STAGE1_EGIR.getRequiredItemLevel(Difficulty.NORMAL)).isEqualTo(1660);
            assertThat(RaidType.KAZEROTH_STAGE1_EGIR.getRequiredItemLevel(Difficulty.HARD)).isEqualTo(1680);
            assertThatThrownBy(() -> RaidType.KAZEROTH_STAGE1_EGIR.getRequiredItemLevel(Difficulty.HELL))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("지원하지 않는 난이도입니다");
        }

        @Test
        @DisplayName("아브렐슈드 2막 레이드는 노말 1670, 하드 1690 레벨이 필요하다.")
        void abrelshudAct2RequiredLevel(){
            assertThat(RaidType.KAZEROTH_STAGE2_ABRELSHUD.getRequiredItemLevel(Difficulty.NORMAL)).isEqualTo(1670);
            assertThat(RaidType.KAZEROTH_STAGE2_ABRELSHUD.getRequiredItemLevel(Difficulty.HARD)).isEqualTo(1690);
            assertThatThrownBy(() -> RaidType.KAZEROTH_STAGE2_ABRELSHUD.getRequiredItemLevel(Difficulty.HELL))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("지원하지 않는 난이도입니다");
        }
    }

    @Nested
    @DisplayName("레이드 난이도 진입 가능 여부 테스트")
    class DifficultyAvailabilityTest {

        @Test
        @DisplayName("발탄, 비아키스, 아브렐슈드는 노말, 하드, 헬 난이도가 가능하다")
        void canNormalAndHardAndHellDifficulties() {
            Stream.of(RaidType.VALTAN, RaidType.VYKAS, RaidType.ABRELSHUD).forEach(raidType -> {
                assertThat(raidType.canEnterWithDifficulty(Difficulty.NORMAL)).isTrue();
                assertThat(raidType.canEnterWithDifficulty(Difficulty.HARD)).isTrue();
                assertThat(raidType.canEnterWithDifficulty(Difficulty.HELL)).isTrue();
            });
        }

        @Test
        @DisplayName("쿠크세이튼은 노말과 헬 난이도만 가능하다")
        void canNormalAndHellDifficulties() {
            assertThat(RaidType.KOUKUSATON.canEnterWithDifficulty(Difficulty.NORMAL)).isTrue();
            assertThat(RaidType.KOUKUSATON.canEnterWithDifficulty(Difficulty.HELL)).isTrue();
            assertThat(RaidType.KOUKUSATON.canEnterWithDifficulty(Difficulty.HARD)).isFalse();
        }

        @Test
        @DisplayName("베히모스는 노말 난이도만 가능하다")
        void canNormalDifficulties() {
            assertThat(RaidType.BEHEMOTH.canEnterWithDifficulty(Difficulty.NORMAL)).isTrue();
            assertThat(RaidType.BEHEMOTH.canEnterWithDifficulty(Difficulty.HARD)).isFalse();
            assertThat(RaidType.BEHEMOTH.canEnterWithDifficulty(Difficulty.HELL)).isFalse();
        }

        @Test
        @DisplayName("카양겔, 일리아칸, 상아탑, 카멘, 에키드나, 에기르, 아브렐슈드2막은 노말과 하드 난이도만 가능하다")
        void canNormalAndHardDifficulties() {
            Stream.of(RaidType.KAYANGEL, RaidType.ILLIAKKAN, RaidType.IVORYTOWER, RaidType.KAMEN, RaidType.ECHIDNA, RaidType.KAZEROTH_STAGE1_EGIR, RaidType.KAZEROTH_STAGE2_ABRELSHUD).forEach(raidType -> {
                assertThat(raidType.canEnterWithDifficulty(Difficulty.NORMAL)).isTrue();
                assertThat(raidType.canEnterWithDifficulty(Difficulty.HARD)).isTrue();
                assertThat(raidType.canEnterWithDifficulty(Difficulty.HELL)).isFalse();
            });
        }
    }

    @Nested
    @DisplayName("아브렐슈드 관문별 요구 레벨 테스트")
    class AbrelshudStageRequiredLevelTest {

        @Test
        @DisplayName("노말 난이도의 각 관문별 요구 레벨을 정확히 반환한다")
        void normalDifficultyStages() {
            assertThat(RaidType.ABRELSHUD.getRequiredItemLevelForStage(Difficulty.NORMAL, 1)).isEqualTo(1490);
            assertThat(RaidType.ABRELSHUD.getRequiredItemLevelForStage(Difficulty.NORMAL, 2)).isEqualTo(1500);
            assertThat(RaidType.ABRELSHUD.getRequiredItemLevelForStage(Difficulty.NORMAL, 3)).isEqualTo(1520);
            assertThat(RaidType.ABRELSHUD.getRequiredItemLevelForStage(Difficulty.NORMAL, 4)).isEqualTo(1540);
        }

        @Test
        @DisplayName("하드 난이도의 각 관문별 요구 레벨을 정확히 반환한다")
        void hardDifficultyStages() {
            assertThat(RaidType.ABRELSHUD.getRequiredItemLevelForStage(Difficulty.HARD, 1)).isEqualTo(1540);
            assertThat(RaidType.ABRELSHUD.getRequiredItemLevelForStage(Difficulty.HARD, 2)).isEqualTo(1540);
            assertThat(RaidType.ABRELSHUD.getRequiredItemLevelForStage(Difficulty.HARD, 3)).isEqualTo(1550);
            assertThat(RaidType.ABRELSHUD.getRequiredItemLevelForStage(Difficulty.HARD, 4)).isEqualTo(1560);
        }

        @Test
        @DisplayName("유효하지 않은 관문 번호에 대해 예외를 발생시킨다 ex.아브렐슈드")
        void invalidStageNumber() {
            assertThatThrownBy(() ->
                    RaidType.ABRELSHUD.getRequiredItemLevelForStage(Difficulty.NORMAL, 0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("유효하지 않은 관문입니다.");

            assertThatThrownBy(() ->
                    RaidType.ABRELSHUD.getRequiredItemLevelForStage(Difficulty.NORMAL, 5))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("유효하지 않은 관문입니다.");
        }
    }

    @Nested
    @DisplayName("관문 번호 유효성 검사")
    class StageValidationTest {

        @Test
        @DisplayName("레이드별 관문 수에 따라 유효한 관문 번호를 판단한다")
        void validateStageNumbers() {
            // 2관문 레이드 (발탄, 비아키스, 에키드나, 베히모스, 에기르, 아브렐슈드2막)
            assertThat(RaidType.VALTAN.isValidStage(1)).isTrue();
            assertThat(RaidType.VALTAN.isValidStage(2)).isTrue();
            assertThat(RaidType.VALTAN.isValidStage(3)).isFalse();
            assertThat(RaidType.VALTAN.isValidStage(0)).isFalse();
            assertThat(RaidType.VYKAS.isValidStage(1)).isTrue();
            assertThat(RaidType.VYKAS.isValidStage(2)).isTrue();
            assertThat(RaidType.VYKAS.isValidStage(3)).isFalse();
            assertThat(RaidType.VYKAS.isValidStage(0)).isFalse();
            assertThat(RaidType.ECHIDNA.isValidStage(1)).isTrue();
            assertThat(RaidType.ECHIDNA.isValidStage(2)).isTrue();
            assertThat(RaidType.ECHIDNA.isValidStage(3)).isFalse();
            assertThat(RaidType.ECHIDNA.isValidStage(0)).isFalse();
            assertThat(RaidType.BEHEMOTH.isValidStage(1)).isTrue();
            assertThat(RaidType.BEHEMOTH.isValidStage(2)).isTrue();
            assertThat(RaidType.BEHEMOTH.isValidStage(3)).isFalse();
            assertThat(RaidType.BEHEMOTH.isValidStage(0)).isFalse();
            assertThat(RaidType.KAZEROTH_STAGE1_EGIR.isValidStage(1)).isTrue();
            assertThat(RaidType.KAZEROTH_STAGE1_EGIR.isValidStage(2)).isTrue();
            assertThat(RaidType.KAZEROTH_STAGE1_EGIR.isValidStage(3)).isFalse();
            assertThat(RaidType.KAZEROTH_STAGE1_EGIR.isValidStage(0)).isFalse();
            assertThat(RaidType.KAZEROTH_STAGE2_ABRELSHUD.isValidStage(1)).isTrue();
            assertThat(RaidType.KAZEROTH_STAGE2_ABRELSHUD.isValidStage(2)).isTrue();
            assertThat(RaidType.KAZEROTH_STAGE2_ABRELSHUD.isValidStage(3)).isFalse();
            assertThat(RaidType.KAZEROTH_STAGE2_ABRELSHUD.isValidStage(0)).isFalse();

            // 3관문 레이드 (쿠크세이튼, 일리아칸, 카양겔, 상아탑)
            assertThat(RaidType.KOUKUSATON.isValidStage(1)).isTrue();
            assertThat(RaidType.KOUKUSATON.isValidStage(3)).isTrue();
            assertThat(RaidType.KOUKUSATON.isValidStage(4)).isFalse();
            assertThat(RaidType.ILLIAKKAN.isValidStage(1)).isTrue();
            assertThat(RaidType.ILLIAKKAN.isValidStage(3)).isTrue();
            assertThat(RaidType.ILLIAKKAN.isValidStage(4)).isFalse();
            assertThat(RaidType.KAYANGEL.isValidStage(1)).isTrue();
            assertThat(RaidType.KAYANGEL.isValidStage(3)).isTrue();
            assertThat(RaidType.KAYANGEL.isValidStage(4)).isFalse();
            assertThat(RaidType.IVORYTOWER.isValidStage(1)).isTrue();
            assertThat(RaidType.IVORYTOWER.isValidStage(3)).isTrue();
            assertThat(RaidType.IVORYTOWER.isValidStage(4)).isFalse();

            // 4관문 레이드 (아브렐슈드, 카멘)
            assertThat(RaidType.ABRELSHUD.isValidStage(1)).isTrue();
            assertThat(RaidType.ABRELSHUD.isValidStage(4)).isTrue();
            assertThat(RaidType.ABRELSHUD.isValidStage(5)).isFalse();
            assertThat(RaidType.KAMEN.isValidStage(1)).isTrue();
            assertThat(RaidType.KAMEN.isValidStage(4)).isTrue();
            assertThat(RaidType.KAMEN.isValidStage(5)).isFalse();
        }
    }
}
