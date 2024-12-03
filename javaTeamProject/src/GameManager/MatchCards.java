package GameManager;

import Cards.Card;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

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
    private JButton itemButton = new JButton("Use Item");

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
        Font statusFont = new Font("Arial", Font.BOLD, 20);

        errorLabel.setFont(statusFont);
        errorLabel.setText("Errors: " + errorCount);

        scoreLabel.setFont(statusFont);
        scoreLabel.setText("Score: " + scoreManager.getFinalScore());

        timerLabel.setFont(statusFont);
        timerLabel.setText("Time: " + remainingTime + "s");

        comboLabel.setFont(statusFont);
        comboLabel.setText("Combo: " + comboCount);

        textPanel.setLayout(new GridLayout(1, 5));
        textPanel.add(scoreLabel);
        textPanel.add(errorLabel);
        textPanel.add(timerLabel);
        textPanel.add(comboLabel);

        itemButton.setFont(new Font("Arial", Font.PLAIN, 16)); // 아이템 버튼은 조금 작은 크기 유지
        itemButton.addActionListener(e -> {
            showUnmatchedCardsTemporarily();
            itemButton.setEnabled(false);
        });
        textPanel.add(itemButton);
    }


    private void setupBoardPanel() {
        boardPanel.setLayout(new GridLayout(size, size));
        board = new ArrayList<>();
        for (int i = 0; i < cardSet.size(); i++) {
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
                ImageIcon colorImage = new ImageIcon(Objects.requireNonNull(
                        MatchCards.class.getResource("/resource/" + gameName + "/card" + i + ".png")));
                ImageIcon wordImage = new ImageIcon(Objects.requireNonNull(
                        MatchCards.class.getResource("/resource/" + gameName + "/card" + (i + 8) + ".png")));
                cardSet.add(new Card("Card " + i, colorImage));
                cardSet.add(new Card("Card " + i, wordImage));
            }
        } else {
            for (int i = 1; i <= 8; i++) {
                ImageIcon cardImage = new ImageIcon(Objects.requireNonNull(
                        MatchCards.class.getResource("/resource/" + gameName + "/card" + i + ".png")));
                cardSet.add(new Card("Card " + i, cardImage));
                cardSet.add(new Card("Card " + i, cardImage));
            }
        }
    }

    private void shuffleCards() {
        Collections.shuffle(cardSet);
    }

    private void initializeCardListeners() {
        for (int i = 0; i < board.size(); i++) {
            JButton tile = board.get(i);
            tile.addActionListener(e -> {
                JButton clickedCard = (JButton) e.getSource();
                if (clickedCard.getIcon() == cardBackImageIcon) {
                    handleCardSelection(clickedCard);
                }
            });
        }
    }

    private void handleCardSelection(JButton clickedCard) {
        if (card1Selected == null) {
            card1Selected = clickedCard;
            card1Selected.setIcon(cardSet.get(board.indexOf(card1Selected)).getCardImageIcon());
        } else if (card2Selected == null) {
            card2Selected = clickedCard;
            card2Selected.setIcon(cardSet.get(board.indexOf(card2Selected)).getCardImageIcon());

            if (!isMatched(card1Selected, card2Selected)) {
                errorCount++;
                errorLabel.setText("Errors: " + errorCount);
                comboCount = 0;
                scoreManager.decreaseScore();
                updateScore();
                hideCardTimer.start();
            } else {
                markAsMatched();
                if (scoreManager.getMatchSuccessCount() == 8) {
                    endGame();
                }
            }
        }
    }

    private boolean isMatched(JButton first, JButton second) {
        return cardSet.get(board.indexOf(first)).getCardId().equals(
                cardSet.get(board.indexOf(second)).getCardId()
        );
    }

    private void markAsMatched() {
        int index1 = board.indexOf(card1Selected);
        int index2 = board.indexOf(card2Selected);
        cardSet.get(index1).isMatched = true;
        cardSet.get(index2).isMatched = true;

        comboCount++;
        scoreManager.increaseScore(comboCount);
        updateScore();

        card1Selected = null;
        card2Selected = null;
    }

    private void hideCards() {
        if (card1Selected != null && card2Selected != null) {
            card1Selected.setIcon(cardBackImageIcon);
            card2Selected.setIcon(cardBackImageIcon);
            card1Selected = null;
            card2Selected = null;
        }
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
        comboLabel.setText("Combo: " + comboCount);
    }

    private void endGame() {
        gameTimer.stop();
        JOptionPane.showMessageDialog(null, "Game Over! Final Score: " + scoreManager.getFinalScore());
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
