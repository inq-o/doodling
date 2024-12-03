package GameManager;

public class ScoreManager {
    private int currentScore;
    private final int matchPoints = 20;
    private final int missPenalty = 5;
    private int matchSuccessCount;

    public ScoreManager() {
        this.currentScore = 0;
        this.matchSuccessCount = 0;
    }

    public void increaseScore(int comboCount) {
        if (comboCount <= 0) {
            this.currentScore += matchPoints; // 첫 성공 시 기본 점수만 추가
        } else {
            this.currentScore += matchPoints + (comboCount * 5); // 연속 성공 시 추가 점수
        }
        this.matchSuccessCount++;
    }

    public void decreaseScore() {
        this.currentScore -= missPenalty;
        if (this.currentScore < 0) this.currentScore = 0;
    }

    public int getFinalScore() {
        return this.currentScore;
    }

    public int getMatchSuccessCount() {
        return this.matchSuccessCount;
    }
}
