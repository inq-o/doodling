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

        if (gameName.equals("similar")) {
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
        Timer animationTimer = new Timer(50, null); // Executes every 50ms
        int originalFontSize = 25;
        int[] fontSize = {originalFontSize};
        int maxFontSize = 40; // Maximum font size
        AtomicBoolean increasing = new AtomicBoolean(true); // To check whether it's increasing or decreasing
        int shadowOffset = 2; // Shadow offset for text

        animationTimer.addActionListener(e -> {
            // Font size adjustment
            if (increasing.get()) {
                fontSize[0] += 2; // Increase size
            } else {
                fontSize[0] -= 2; // Decrease size
            }

            // Apply font size and create text shadow effect
            comboLabel.setFont(new Font("Arial", Font.BOLD, fontSize[0]));
            comboLabel.setForeground(new Color(255, 0, 0)); // Red color for emphasis
            comboLabel.setText("<html><span style='font-size:" + fontSize[0] + "px; text-shadow: "
                    + shadowOffset + "px " + shadowOffset + "px 5px rgba(0, 0, 0, 0.5);'>"
                    + comboLabel.getText() + "</span></html>");

            // Handle the increase/decrease of font size
            if (fontSize[0] >= maxFontSize) {
                increasing.set(false); // Start decreasing when max font size is reached
            } else if (fontSize[0] <= originalFontSize) {
                animationTimer.stop(); // Stop animation when back to original size
                comboLabel.setForeground(Color.BLACK); // Restore the original color
                comboLabel.setText(comboLabel.getText().replace("<html><span style='font-size:"
                        + fontSize[0] + "px; text-shadow: " + shadowOffset + "px " + shadowOffset + "px 5px rgba(0, 0, 0, 0.5);'>", ""));
            }

            comboLabel.repaint(); // Repaint label to update visuals
        });

        animationTimer.start(); // Start animation
    }

    private void processMatchFailure() {
        errorCount++;
        scoreManager.decreaseScore(); // 매칭 실패 시 점수 감소
        comboCount = 0; // 콤보 초기화
        updateScore(); // 점수 UI 업데이트
        hideCardTimer.start(); // 카드를 숨기는 타이머 시작
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
        gameTimer = new Timer(1000, e -> {
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
        // 게임 종료 시, 남은 시간을 점수에 추가
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