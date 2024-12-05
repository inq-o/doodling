package number;

public class ScoreManagerS {
    private int score;
    private int matchedCards;
    private int remainingTime;

    // 점수 증가
    public void increaseScore(int points) {
        score += points;
    }

    // 점수 감소 (최소 점수 0 유지)
    public void decreaseScore(int points) {
        score = Math.max(0, score - points);
    }

    // 현재 점수 반환
    public int getScore() {
        return score;
    }

    // 매칭된 카드 수 설정
    public void setMatchedCards(int matchedCards) {
        this.matchedCards = matchedCards;
    }

    // 매칭된 카드 수 반환
    public int getMatchedCards() {
        return matchedCards;
    }

    // 남은 시간 설정
    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    // 남은 시간 반환
    public int getRemainingTime() {
        return remainingTime;
    }

    // 점수 초기화
    public void resetScore() {
        score = 0;
        matchedCards = 0;
        remainingTime = 0;
    }
}
