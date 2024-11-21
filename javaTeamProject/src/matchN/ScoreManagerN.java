package matchN;

public class ScoreManagerN {
    private int currentScore;
    private int timeBonus;
    private final int matchPoints = 20;
    private final int missPenalty = 5;
    private int matchSuccessCount;

    public ScoreManagerN() {
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
