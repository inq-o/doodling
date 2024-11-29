package match;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

public class MatchCards {
    private int size = 4;
    private int cardWidth = 128;
    private int cardHeight = 128;

    private ArrayList<Card> cardSet;
    private ArrayList<JButton> board;
    private ImageIcon cardBackImageIcon;

    private int boardWidth = size * cardWidth;
    private int boardHeight = size * cardHeight;

    private JFrame frame = new JFrame("Match Cards");
    private JLabel errorLabel = new JLabel();
    private JLabel scoreLabel = new JLabel();
    private JLabel timerLabel = new JLabel();
    private JLabel comboLabel = new JLabel();
    private JPanel textPanel = new JPanel();
    private JPanel boardPanel = new JPanel();

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
    }

    public void run() {
        setupCards("color");
        shuffleCards();

        frame.setVisible(true);
        frame.setLayout(new BorderLayout());
        frame.setSize(boardWidth + 300, boardHeight + 200);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        errorLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        errorLabel.setHorizontalAlignment(JLabel.CENTER);
        errorLabel.setText("Errors: " + errorCount);

        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        scoreLabel.setHorizontalAlignment(JLabel.CENTER);
        scoreLabel.setText("Score: " + scoreManager.getFinalScore());

        timerLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        timerLabel.setHorizontalAlignment(JLabel.CENTER);
        timerLabel.setText("Time: " + remainingTime + "s");

        comboLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        comboLabel.setHorizontalAlignment(JLabel.CENTER);
        comboLabel.setText("Combo: " + comboCount);

        textPanel.setLayout(new GridLayout(1, 4));
        textPanel.setPreferredSize(new Dimension(boardWidth, 30));
        textPanel.add(scoreLabel);
        textPanel.add(errorLabel);
        textPanel.add(timerLabel);
        textPanel.add(comboLabel);

        JButton itemButton = new JButton("Use Item");
        itemButton.setFont(new Font("Arial", Font.PLAIN, 16));
        itemButton.setEnabled(true);
        itemButton.addActionListener(new ActionListener() {
            private boolean itemUsed = false;
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!itemUsed) {
                    itemUsed = true;
                    itemButton.setEnabled(false);
                    showUnmatchedCardsTemporarily();
                }
            }
        });

        textPanel.add(itemButton);

        frame.add(textPanel, BorderLayout.NORTH);

        board = new ArrayList<>();
        boardPanel.setLayout(new GridLayout(size, size)); //size == 4 의 보드판 생성
        for (int i = 0; i < cardSet.size(); i++) {
            JButton tile = new JButton();
            tile.setPreferredSize(new Dimension(cardWidth, cardHeight));
            tile.setIcon(cardSet.get(i).cardImageIcon);
            board.add(tile);
            boardPanel.add(tile);
        }
        frame.add(boardPanel);
        frame.pack();
        frame.setVisible(true);

        startShowTimer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (JButton card : board) {
                    card.setIcon(cardBackImageIcon);
                }
                initializeCardListeners();
            }
        });
        startShowTimer.setRepeats(false);
        startShowTimer.start();

        hideCardTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hideCards();
            }
        });
        hideCardTimer.setRepeats(false);

        startGameTimer();
    }

    private void showUnmatchedCardsTemporarily() {

        for (int i = 0; i < board.size(); i++) {
            if (!cardSet.get(i).isMatched) {
                board.get(i).setIcon(cardSet.get(i).cardImageIcon);
            }
        }

        Timer revealTimer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < board.size(); i++) {
                    if (!cardSet.get(i).isMatched) {
                        board.get(i).setIcon(cardBackImageIcon);
                    }
                }
            }
        });
        revealTimer.setRepeats(false);
        revealTimer.start();
    }

    public void setGameEndListener(GameEndListener listener) {
        this.gameEndListener = listener;
    }

    private void startGameTimer() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
        gameTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remainingTime--;
                timerLabel.setText("Time: " + remainingTime + "s");

                if (remainingTime <= 0) {
                    gameTimer.stop();
                    endGame();
                }
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
        hideCardTimer.stop();
        scoreManager.addTimeBonus(remainingTime);
        JOptionPane.showMessageDialog(frame, "Game Over! Final Score (with time bonus): " + scoreManager.getFinalScore());
        frame.dispose();
        if (gameEndListener != null) {
            gameEndListener.onGameEnd(scoreManager.getFinalScore());
        }
    }

    private void setupCards(String imageFileName) {
        cardSet = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            Image cardImg = new ImageIcon(MatchCards.class.getResource("/resource/color/card" + i + ".png")).getImage();
            ImageIcon cardImageIcon = new ImageIcon(cardImg.getScaledInstance(cardWidth, cardHeight, java.awt.Image.SCALE_SMOOTH));
            Card card = new Card("Card " + i, cardImageIcon);
            cardSet.add(card);
        }

        cardSet.addAll(cardSet);

        Image cardBackImg = new ImageIcon(MatchCards.class.getResource("/resource/card_back.png")).getImage();
        cardBackImageIcon = new ImageIcon(cardBackImg.getScaledInstance(cardWidth, cardHeight, java.awt.Image.SCALE_SMOOTH));
    }

    private void shuffleCards() {
        Collections.shuffle(cardSet);
    }

    private void initializeCardListeners() {
        for (int i = 0; i < cardSet.size(); i++) {
            JButton tile = board.get(i);
            tile.setIcon(cardBackImageIcon);
            tile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JButton clickedCard = (JButton) e.getSource();
                    if (clickedCard.getIcon() == cardBackImageIcon) {
                        if (card1Selected == null) {
                            card1Selected = clickedCard;
                            int index = board.indexOf(card1Selected);
                            card1Selected.setIcon(cardSet.get(index).cardImageIcon);
                        } else if (card2Selected == null) {
                            card2Selected = clickedCard;
                            int index = board.indexOf(card2Selected);
                            card2Selected.setIcon(cardSet.get(index).cardImageIcon);

                            if (!card1Selected.getIcon().equals(card2Selected.getIcon())) {
                                errorCount++;
                                errorLabel.setText("Errors: " + errorCount);
                                comboCount = 0;
                                updateScore();
                                scoreManager.decreaseScore();
                                updateScore();
                                hideCardTimer.start();
                            } else {
                                int index1 = board.indexOf(card1Selected);
                                int index2 = board.indexOf(card2Selected);

                                cardSet.get(index1).isMatched = true;
                                cardSet.get(index2).isMatched = true;

                                comboCount++;
                                scoreManager.increaseScore(comboCount);
                                updateScore();
                                if (scoreManager.getMatchSuccessCount() == 8) {
                                    endGame();
                                }

                                card1Selected = null;
                                card2Selected = null;
                            }
                        }
                    }
                }
            });
        }
    }


    private void hideCards() {
        if (card1Selected != null && card2Selected != null) {
            card1Selected.setIcon(cardBackImageIcon);
            card1Selected = null;
            card2Selected.setIcon(cardBackImageIcon);
            card2Selected = null;
        } else {
            for (int i = 0; i < board.size(); i++) {
                board.get(i).setIcon(cardBackImageIcon);
            }
        }
    }

    public interface GameEndListener {
        void onGameEnd(int finalScore);
    }
}
