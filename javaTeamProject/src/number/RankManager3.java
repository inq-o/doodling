//package number;
//
//import project.PlayerScore2;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//
//public class RankManager3 {
//    private final ArrayList<PlayerScore2> rankings;
//
//    public RankManager3() {
//        rankings = new ArrayList<>();
//    }
//
//    // 점수 추가
//    public void addScore(PlayerScore2 score) {
//        rankings.add(score);
//    }
//
//    // 랭킹 정렬
//    public void sortRankings() {
//        rankings.sort(new Comparator<PlayerScore2>() {
//            @Override
//            public int compare(PlayerScore2 o1, PlayerScore2 o2) {
//                if (o2.getMatchedCards() != o1.getMatchedCards()) {
//                    return Integer.compare(o2.getMatchedCards(), o1.getMatchedCards()); // 카드 수 기준 정렬
//                }
//                return Integer.compare(o2.getRemainingTime(), o1.getRemainingTime()); // 시간 기준 정렬
//            }
//        });
//    }
//
//    // 랭킹 리스트 반환
//    public ArrayList<PlayerScore2> getRankings() {
//        return new ArrayList<>(rankings); // 복사본 반환
//    }
//
//    // 랭킹 출력
//    public String getFormattedRankings() {
//        StringBuilder sb = new StringBuilder("Game 3 Rankings:\n");
//        for (int i = 0; i < rankings.size(); i++) {
//            PlayerScore2 score = rankings.get(i);
//            sb.append(i + 1).append(". ").append(score.toString()).append("\n");
//        }
//        return sb.toString();
//    }
//}
