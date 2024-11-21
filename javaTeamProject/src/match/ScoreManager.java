package match;

public class ScoreManager {
    private int currentScore;
    private int timeBonus;
    private final int matchPoints = 20;
    private final int missPenalty = 5;
    private int matchSuccessCount;

    public ScoreManager() {
        this.currentScore = 0;
        this.timeBonus = 0;
        this.matchSuccessCount = 0;
    }

    public void increaseScore(int comboCount) {
        this.currentScore += matchPoints + (comboCount * 5);
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

    public int getMatchSuccessCount() {
        return this.matchSuccessCount;
    }
}
