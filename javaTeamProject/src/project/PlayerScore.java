package project;

public class PlayerScore {
    private final String name;
    private final int score;
    private final int matchedCards; // 게임3용
    private final int remainingTime; // 게임3용

    public PlayerScore(String name, int score) {
        this.name = name;
        this.score = score;
        this.matchedCards = 0; // 게임3 외에는 사용되지 않음
        this.remainingTime = 0; // 게임3 외에는 사용되지 않음
    }

    public PlayerScore(String name, int matchedCards, int remainingTime) {
        this.name = name;
        this.score = matchedCards * 10; // 점수 계산 로직
        this.matchedCards = matchedCards;
        this.remainingTime = remainingTime;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public int getMatchedCards() {
        return matchedCards;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    @Override
    public String toString() {
        if (matchedCards > 0 || remainingTime > 0) { // 게임3
            return name + ": " + score + "점 (매치: " + matchedCards + ", 남은 시간: " + remainingTime + "초)";
        }
        return name + ": " + score + "점";
    }
}
