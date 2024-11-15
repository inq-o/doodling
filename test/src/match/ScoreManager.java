package match;

public class ScoreManager {
    int currentScore;
    int timeBonus;
    final int matchPoints = 20;
    final int missPenalty = 5;
    int matchSuccessCount;

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

    public int getMatchSuccessCount() {
        return this.matchSuccessCount;
    }
}