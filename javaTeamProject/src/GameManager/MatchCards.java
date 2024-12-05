package GameManager;

import Cards.Card;
import project.CardMatching;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private JButton itemButton = new JButton("Item");

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
        Font statusFont = new Font("Arial", Font.BOLD, 25);

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
            board.add(tile);
            boardPanel.add(tile);
        }
    }

    private void setupCards(String gameName) {
        cardSet.clear();

        Image cardBackImg = new ImageIcon(Objects.requireNonNull(
                MatchCards.class.getResource("/resource/card_back.png"))).getImage();
        cardBackImageIcon = new ImageIcon(cardBackImg.getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH));

        if (gameName.equals("similar")) {
            for (int i = 1; i <= 8; i++) {
                Image colorImg = new ImageIcon(Objects.requireNonNull(
                        MatchCards.class.getResource("/resource/"+gameName + "/card" + i + ".png"))).getImage();
                Image wordImg = new ImageIcon(Objects.requireNonNull(
                        MatchCards.class.getResource("/resource/"+gameName + "/card" + (i + 8) + ".png"))).getImage();

                ImageIcon cardColorIcon = new ImageIcon(colorImg.getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH));
                ImageIcon cardWordIcon = new ImageIcon(wordImg.getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH));

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

        if (comboCount > 0) {
            comboCount++;
        } else {
            comboCount = 1;
        }

        scoreManager.increaseScore(comboCount - 1);
        updateScore();

        if (comboCount > 1) {
            triggerComboAnimation();
        }

        if (scoreManager.getMatchSuccessCount() == 8) {
            endGame();
        }

        resetCardSelection();
    }
    private void triggerComboAnimation() {
        Timer animationTimer = new Timer(50, null); // 50ms마다 실행
        int originalFontSize = 25;
        int[] fontSize = {originalFontSize};
        int maxFontSize = 40; // 최대 폰트 크기
        AtomicBoolean increasing = new AtomicBoolean(true); // 증가 여부를 AtomicBoolean으로 변경

        animationTimer.addActionListener(e -> {
            // 폰트 크기 변경
            if (increasing.get()) {
                fontSize[0] += 2;
            } else {
                fontSize[0] -= 2;
            }

            // 폰트 크기 적용
            comboLabel.setFont(new Font("Arial", Font.BOLD, fontSize[0]));
            comboLabel.setForeground(new Color(255, 0, 0)); // 빨간색 강조

            // 애니메이션의 증가 및 감소 처리
            if (fontSize[0] >= maxFontSize) {
                increasing.set(false); // 증가 종료, 감소 시작
            } else if (fontSize[0] <= originalFontSize) {
                animationTimer.stop(); // 애니메이션 종료
                comboLabel.setForeground(Color.BLACK); // 원래 색상 복원
            }

            comboLabel.repaint(); // 레이블 다시 그리기
        });

        animationTimer.start(); // 애니메이션 시작
    }
    private void processMatchFailure() {
        errorCount++;
        comboCount = 0;
        updateScore();
        hideCardTimer.start();
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
        gameTimer = new Timer(3000, e -> {
            remainingTime--;
            timerLabel.setText("Time: " + remainingTime + "s");
            if (remainingTime <= 0) {
                endGame();
            }
        });
        gameTimer.start();
    }

    private void updateScore() {
        scoreLabel.setText("Score: " + scoreManager.getFinalScore());

        int displayCombo = comboCount > 1 ? comboCount - 1 : 0;
        comboLabel.setText("Combo: " + displayCombo);

        errorLabel.setText("Errors: " + errorCount);
    }

    private void endGame() {
        gameTimer.stop();
        JOptionPane.showMessageDialog(null, "게임 종료!\n최종 점수 : " + scoreManager.getFinalScore());
        if (gameEndListener != null) {
            gameEndListener.onGameEnd(scoreManager.getFinalScore());
        }
    }

    public void setGameEndListener(GameEndListener listener) {
        this.gameEndListener = listener;
    }

    public interface GameEndListener {
        void onGameEnd(int finalScore);
    }
}