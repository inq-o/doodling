package match;

public class ScoreManager {
    private int currentScore;
    private int timeBonus;
    private final int matchPoints = 20;
    private final int missPenalty = 5;
    private int matchSuccessCount;      // 매칭 성공 횟수

    public ScoreManager() {
        this.currentScore = 0;
        this.timeBonus = 0;
        this.matchSuccessCount = 0;
    }

    public void increaseScore() {
        this.currentScore += matchPoints;
        this.matchSuccessCount++;
    }

    public void decreaseScore() {
        this.currentScore -= missPenalty;
        if (this.currentScore < 0) this.currentScore = 0;
    }

    public void addTimeBonus(int remainingTime) {
        this.timeBonus = remainingTime;
        this.currentScore += this.timeBonus;
    }

    public int getFinalScore() {
        return this.currentScore;
    }

    // 매칭 성공 횟수 반환 메서드
    public int getMatchSuccessCount() {
        return this.matchSuccessCount;
    }
}
