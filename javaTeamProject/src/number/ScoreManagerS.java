package number;

public class ScoreManagerS {
    private int matchedCards = 0;
    private int remainingTime = 0;

    public void setMatchedCards(int matchedCards) {
        this.matchedCards = matchedCards;
    }

    public int getMatchedCards() {
        return matchedCards;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public int getRemainingTime() {
        return remainingTime;
    }
}
