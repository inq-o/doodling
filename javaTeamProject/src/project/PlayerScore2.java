package project;

public class PlayerScore2 {
    private final String playerName;
    private final int matchedCards;
    private final int remainingTime;

    public PlayerScore2(String playerName, int matchedCards, int remainingTime) {
        this.playerName = playerName;
        this.matchedCards = matchedCards;
        this.remainingTime = remainingTime;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getMatchedCards() {
        return matchedCards;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    @Override
    public String toString() {
        return playerName + " - Cards: " + matchedCards + ", Time: " + remainingTime + "s";
    }
}
