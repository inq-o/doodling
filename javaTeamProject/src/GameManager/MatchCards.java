package GameManager;

import Cards.Card;
import project.CardMatching;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class MatchCards {
    private int size = 4;
    private int cardWidth = 128;
    private int cardHeight = 128;

    private final ArrayList<Card> cardSet = new ArrayList<>();
    private ArrayList<JButton> board;
    private ImageIcon cardBackImageIcon;

    private JLabel errorLabel = new JLabel();
    private JLabel scoreLabel = new JLabel();
    private JLabel timerLabel = new JLabel();
    private JLabel comboLabel = new JLabel();
    private JPanel textPanel = new JPanel();
    private JPanel boardPanel = new JPanel();
    private JButton itemButton = new JButton();

    private int errorCount = 0;
    private int remainingTime = 60;
    private Timer gameTimer;
    private Timer startShowTimer;
    private Timer hideCardTimer;
    private JButton card1Selected;
    private JButton card2Selected;
    private ScoreManager scoreManager;
    private GameEndListener gameEndListener;
    private int comboCount = 0;

    public MatchCards(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
        hideCardTimer = new Timer(1000, e -> hideCards());
        hideCardTimer.setRepeats(false);
    }
    public JPanel createGamePanel(String gameName) {
        setupCards(gameName);
        shuffleCards();

        JPanel gamePanel = new JPanel(new BorderLayout());
        gamePanel.setPreferredSize(new Dimension(size * cardWidth + 300, size * cardHeight + 100));

        setupStatusPanel();
        gamePanel.add(textPanel, BorderLayout.NORTH);

        setupBoardPanel();
        gamePanel.add(boardPanel, BorderLayout.CENTER);

        initializeCardListeners();
        showAllCardsInitially();
        startGameTimer();

        return gamePanel;
    }

    private void setupStatusPanel() {
        Font statusFont = new Font("Arial", Font.BOLD, 23);

        errorLabel.setFont(statusFont);
        errorLabel.setText("Errors: " + errorCount);

        scoreLabel.setFont(statusFont);
        scoreLabel.setText("Score: " + scoreManager.getFinalScore());

        timerLabel.setFont(statusFont);
        timerLabel.setText("Time: " + remainingTime + "s");

        comboLabel.setFont(statusFont);
        comboLabel.setText("Combo: 0");

        textPanel.setLayout(new GridLayout(1, 5));
        textPanel.add(scoreLabel);
        textPanel.add(errorLabel);
        textPanel.add(timerLabel);
        textPanel.add(comboLabel);
        ImageIcon itemIcon = new ImageIcon(Objects.requireNonNull(CardMatching.class.getResource("/resource/buttons/showItem.png")));
        itemButton.setIcon(new ImageIcon(itemIcon.getImage().getScaledInstance(150, 50, Image.SCALE_SMOOTH)));
        itemButton.setPreferredSize(new Dimension(150, 50));


        // itemButton.setFont(new Font("Arial", Font.PLAIN, 10));
        itemButton.addActionListener(e -> {
            showUnmatchedCardsTemporarily();
            itemButton.setEnabled(false);
        });
        textPanel.add(itemButton);
    }

    private void setupBoardPanel() {
        boardPanel.setLayout(new GridLayout(size, size));
        board = new ArrayList<>();
        for (Card card : cardSet) {
            JButton tile = new JButton();
            tile.setPreferredSize(new Dimension(cardWidth, cardHeight));
            tile.setIcon(cardBackImageIcon);
            tile.setBorder(BorderFactory.createEmptyBorder()); ///// // 테두리 제거
            tile.setContentAreaFilled(false);///////////////

            board.add(tile);
            boardPanel.add(tile);
        }
    }

    private void setupCards(String gameName) {
        cardSet.clear();

        Image cardBackImg = new ImageIcon(Objects.requireNonNull(
                MatchCards.class.getResource("/resource/buttons/qm.png"))).getImage();
        cardBackImageIcon = new ImageIcon(cardBackImg.getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH));

        if (gameName.equals("character")) {
            for (int i = 1; i <= 8; i++) {
                Image colorImg = new ImageIcon(Objects.requireNonNull(
                        MatchCards.class.getResource("/resource/"+gameName + "/card" + i + ".png"))).getImage();
                Image wordImg = new ImageIcon(Objects.requireNonNull(
                        MatchCards.class.getResource("/resource/"+gameName + "/card" + (i + 8) + ".png"))).getImage();

                ImageIcon cardColorIcon = new ImageIcon(colorImg.getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH));
                ImageIcon cardWordIcon = new ImageIcon(wordImg.getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH));

                ImageIcon itemIcon = new ImageIcon(Objects.requireNonNull(CardMatching.class.getResource("/resource/buttons/ShowItem.png")));
                Image scaledIconImage = itemIcon.getImage().getScaledInstance(150, 50, Image.SCALE_SMOOTH);
                ImageIcon scaledItemIcon = new ImageIcon(scaledIconImage);
                itemButton.setBorder(BorderFactory.createEmptyBorder());  // 테두리 제거
                itemButton.setContentAreaFilled(false);  // 배경 채우기 제거


                Card card1 = new Card("Card " + i, cardColorIcon);
                Card card2 = new Card("Card " + i, cardWordIcon);

                cardSet.add(card1);
                cardSet.add(card2);
            }
        } else {
            for (int i = 1; i <= 8; i++) {
                Image cardImg = new ImageIcon(Objects.requireNonNull(
                        MatchCards.class.getResource("/resource/"+gameName + "/card" + i + ".png"))).getImage();
                ImageIcon cardIcon = new ImageIcon(cardImg.getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH));
                ImageIcon itemIcon = new ImageIcon(Objects.requireNonNull(CardMatching.class.getResource("/resource/buttons/ShowItem.png")));
                Image scaledIconImage = itemIcon.getImage().getScaledInstance(150, 50, Image.SCALE_SMOOTH);

                ImageIcon scaledItemIcon = new ImageIcon(scaledIconImage);
                itemButton.setBorder(BorderFactory.createEmptyBorder());  // 테두리 제거
                itemButton.setContentAreaFilled(false);  // 배경 채우기 제거



                Card card1 = new Card("Card " + i, cardIcon);
                Card card2 = new Card("Card " + i, cardIcon);

                cardSet.add(card1);
                cardSet.add(card2);
            }
        }
    }

    private void shuffleCards() {
        Collections.shuffle(cardSet);
    }

    private void initializeCardListeners() {
        for (int i = 0; i < board.size(); i++) {
            JButton tile = board.get(i);
            tile.addActionListener(e -> handleCardSelection(tile));
        }
    }

    private void handleCardSelection(JButton clickedCard) {
        if (clickedCard.getIcon() != cardBackImageIcon) return;

        if (card1Selected == null) {
            card1Selected = clickedCard;
            revealCard(card1Selected);
        } else if (card2Selected == null) {
            card2Selected = clickedCard;
            revealCard(card2Selected);

            if (isMatched(card1Selected, card2Selected)) {
                processMatchSuccess();
            } else {
                processMatchFailure();
            }
        }
    }

    private void revealCard(JButton card) {
        int index = board.indexOf(card);
        card.setIcon(cardSet.get(index).getCardImageIcon());
    }

    private boolean isMatched(JButton first, JButton second) {
        String id1 = cardSet.get(board.indexOf(first)).getCardId();
        String id2 = cardSet.get(board.indexOf(second)).getCardId();
        return id1.equals(id2);
    }

    private void processMatchSuccess() {
        markAsMatched(card1Selected, card2Selected);

        if (comboCount == 0) {
            comboCount = 1;  // 처음 성공 시 콤보 시작
        } else {
            comboCount++;  // 콤보 증가
        }

        scoreManager.increaseScore(comboCount - 1);
        updateScore();

        if (comboCount > 1) {
            triggerComboAnimation(); // 콤보 애니메이션은 단순히 시각적 효과
        }

        if (scoreManager.getMatchSuccessCount() == 8) {
            endGame();
        }

        resetCardSelection();
    }

    private void triggerComboAnimation() {
        Timer animationTimer = new Timer(100, null);
        int originalFontSize = 23; // 기본 폰트 크기
        AtomicInteger fontSize = new AtomicInteger(originalFontSize); // AtomicInteger로 변경
        int maxFontSize = 25; // 최대 폰트 크기
        AtomicBoolean increasing = new AtomicBoolean(true); // 폰트 크기 증가/감소 여부 체크
        final Color startColor = comboCount == 0 ? Color.BLACK : new Color(255, 0, 0); // 콤보가 0이면 검은색
        final Color endColor = comboCount == 0 ? Color.BLACK : new Color(255, 255, 0); // 콤보가 0이면 검은색

        // 애니메이션 종료 후 복원할 원본 텍스트
        String originalText = "Combo " + (comboCount - 1); // 콤보 텍스트는 항상 이 형태로 시작
        comboLabel.setText(originalText); // 텍스트 설정

        // 반복 횟수 (2번 반복)
        AtomicInteger cycleCount = new AtomicInteger(0);

        animationTimer.addActionListener(e -> {
            // 콤보가 0일 경우 애니메이션 바로 종료
            if (comboCount == 0) {
                comboLabel.setText("Combo " + (comboCount - 1)); // 콤보 0일 경우 텍스트 바로 설정
                comboLabel.setForeground(Color.BLACK); // 텍스트 색상 검은색으로 설정
                animationTimer.stop(); // 애니메이션 종료
                return; // 애니메이션 종료
            }

            // 폰트 크기 증가/감소 처리
            if (increasing.get()) {
                fontSize.addAndGet(1); // 폰트 크기 증가
            } else {
                fontSize.addAndGet(-1); // 폰트 크기 감소
            }

            // 콤보 텍스트에 애니메이션 효과 적용 (콤보 횟수는 고정)
            comboLabel.setText("<html><span style='font-size:" + fontSize.get() + "px; text-shadow: "
                    + "2px 2px 5px rgba(0, 0, 0, 0.5);'>"
                    + "Combo " + (comboCount - 1) + "</span></html>");
            comboLabel.setFont(new Font("Arial", Font.BOLD, fontSize.get()));

            // 색상 변화 처리 (폰트 크기 변화에 맞춰 색상 변화)
            int red = (int) (startColor.getRed() + (endColor.getRed() - startColor.getRed()) * (fontSize.get() - originalFontSize) / (float) maxFontSize);
            int green = (int) (startColor.getGreen() + (endColor.getGreen() - startColor.getGreen()) * (fontSize.get() - originalFontSize) / (float) maxFontSize);
            int blue = (int) (startColor.getBlue() + (endColor.getBlue() - startColor.getBlue()) * (fontSize.get() - originalFontSize) / (float) maxFontSize);
            comboLabel.setForeground(new Color(red, green, blue));

            // 폰트 크기 최대값 도달 시 크기 감소로 전환
            if (fontSize.get() >= maxFontSize) {
                increasing.set(false); // 폰트 크기 감소
            } else if (fontSize.get() <= originalFontSize) {
                increasing.set(true); // 폰트 크기 증가
            }

            // 1초 동안 폰트 크기 변화 1번 사이클이 끝난 후
            if (fontSize.get() <= originalFontSize && cycleCount.get() < 2) {
                cycleCount.incrementAndGet(); // 사이클 1번 진행
            }

            // 2번 사이클이 끝난 후 애니메이션 종료
            if (cycleCount.get() >= 2 && fontSize.get() == originalFontSize) {
                comboLabel.setText("Combo " + (comboCount - 1)); // 콤보 횟수는 계속 고정
                animationTimer.stop(); // 애니메이션 종료
            }
        });

        animationTimer.start(); // 애니메이션 시작
    }

    private void processMatchFailure() {
        errorCount++;
        scoreManager.decreaseScore(); // 매칭 실패 시 점수 감소
        comboCount = 0; // 콤보 초기화
        updateScore(); // 점수 UI 업데이트
        hideCardTimer.start(); // 카드를 숨기는 타이머 시작

        // 콤보 애니메이션을 종료하도록 설정
        comboLabel.setText("Combo: 0");  // 콤보가 0일 때 텍스트 갱신
        comboLabel.setForeground(Color.BLACK);  // 콤보 텍스트 색상 검정으로 설정
    }

    private void markAsMatched(JButton first, JButton second) {
        int index1 = board.indexOf(first);
        int index2 = board.indexOf(second);
        cardSet.get(index1).isMatched = true;
        cardSet.get(index2).isMatched = true;
    }

    private void resetCardSelection() {
        card1Selected = null;
        card2Selected = null;
    }

    private void hideCards() {
        if (card1Selected != null) card1Selected.setIcon(cardBackImageIcon);
        if (card2Selected != null) card2Selected.setIcon(cardBackImageIcon);
        resetCardSelection();
    }

    private void showUnmatchedCardsTemporarily() {
        for (int i = 0; i < board.size(); i++) {
            if (!cardSet.get(i).isMatched) {
                board.get(i).setIcon(cardSet.get(i).getCardImageIcon());
            }
        }

        Timer revealTimer = new Timer(2000, e -> {
            for (int i = 0; i < board.size(); i++) {
                if (!cardSet.get(i).isMatched) {
                    board.get(i).setIcon(cardBackImageIcon);
                }
            }
        });
        revealTimer.setRepeats(false);
        revealTimer.start();
    }

    private void showAllCardsInitially() {
        for (int i = 0; i < board.size(); i++) {
            board.get(i).setIcon(cardSet.get(i).getCardImageIcon());
        }

        startShowTimer = new Timer(2000, e -> {
            for (JButton card : board) {
                card.setIcon(cardBackImageIcon);
            }
        });
        startShowTimer.setRepeats(false);
        startShowTimer.start();
    }

    private void startGameTimer() {
        Timer startDelayTimer = new Timer(2000, e -> {

            gameTimer = new Timer(1000, event -> {
                remainingTime--;
                timerLabel.setText("Time: " + remainingTime + "s");
                if (remainingTime <= 0) {
                    endGame();
                }
            });
            gameTimer.start();
        });
        startDelayTimer.setRepeats(false);
        startDelayTimer.start();
    }

    private void updateScore() {
        scoreLabel.setText("Score: " + scoreManager.getFinalScore());

        int displayCombo = comboCount > 1 ? comboCount - 1 : 0;
        comboLabel.setText("Combo: " + displayCombo);

        errorLabel.setText("Errors: " + errorCount);
    }

    private void endGame() {
        gameTimer.stop();

        int finalScore = scoreManager.getFinalScore() + remainingTime;
        JOptionPane.showMessageDialog(null, "게임 종료!\n최종 점수 : " + finalScore);

        if (gameEndListener != null) {
            gameEndListener.onGameEnd(finalScore); // 최종 점수 전달
        }
    }

    public void setGameEndListener(GameEndListener listener) {
        this.gameEndListener = listener;
    }

    public interface GameEndListener {
        void onGameEnd(int finalScore);
    }
}