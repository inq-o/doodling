package match;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

public class MatchCards {

    int size = 4;
    int cardWidth = 128;
    int cardHeight = 128;

    ArrayList<Card> cardSet;
    ArrayList<JButton> board;
    ImageIcon cardBackImageIcon;

    int boardWidth = size * cardWidth;
    int boardHeight = size * cardHeight;

    JFrame frame = new JFrame("Match Cards");
    JLabel errorLabel = new JLabel();
    JLabel scoreLabel = new JLabel();
    JLabel timerLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();

    int errorCount = 0;
    int remainingTime = 60;
    Timer gameTimer;
    Timer startShowTimer;
    Timer hideCardTimer;
    JButton card1Selected;
    JButton card2Selected;
    ScoreManager scoreManager;

    public MatchCards() {
        scoreManager = new ScoreManager();
    }

    private void run() {
        setupCards("color");
        shuffleCards();

        frame.setVisible(true);
        frame.setLayout(new BorderLayout());
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 에러, 점수 및 타이머 라벨 설정
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        errorLabel.setHorizontalAlignment(JLabel.CENTER);
        errorLabel.setText("Errors: " + errorCount);

        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        scoreLabel.setHorizontalAlignment(JLabel.CENTER);
        scoreLabel.setText("Score: " + scoreManager.getFinalScore());

        timerLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        timerLabel.setHorizontalAlignment(JLabel.CENTER);
        timerLabel.setText("Time: " + remainingTime + "s");

        textPanel.setLayout(new GridLayout(1, 3));
        textPanel.setPreferredSize(new Dimension(boardWidth, 30));
        textPanel.add(scoreLabel);
        textPanel.add(errorLabel);
        textPanel.add(timerLabel);
        frame.add(textPanel, BorderLayout.NORTH);

        board = new ArrayList<>();
        boardPanel.setLayout(new GridLayout(size, size));
        for (int i = 0; i < cardSet.size(); i++) {
            JButton tile = new JButton();
            tile.setPreferredSize(new Dimension(cardWidth, cardHeight));
            tile.setIcon(cardSet.get(i).cardImageIcon);

            tile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JButton tile = (JButton) e.getSource();
                    if (tile.getIcon() == cardBackImageIcon) {
                        if (card1Selected == null) {
                            card1Selected = tile;
                            int index = board.indexOf(card1Selected);
                            card1Selected.setIcon(cardSet.get(index).cardImageIcon);
                        } else if (card2Selected == null) {
                            card2Selected = tile;
                            int index = board.indexOf(card2Selected);
                            card2Selected.setIcon(cardSet.get(index).cardImageIcon);

                            if (card1Selected.getIcon() != card2Selected.getIcon()) {
                                errorCount++;
                                errorLabel.setText("Errors: " + errorCount);
                                scoreManager.decreaseScore();
                                updateScore();
                                hideCardTimer.start();
                            } else {
                                scoreManager.increaseScore();
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
            board.add(tile);
            boardPanel.add(tile);
        }
        frame.add(boardPanel);
        frame.pack();
        frame.setVisible(true);

        startShowTimer = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hideCards();
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

    void startGameTimer() {
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

    void updateScore() {
        scoreLabel.setText("Score: " + scoreManager.getFinalScore());
    }

    void endGame() {
        gameTimer.stop();
        hideCardTimer.stop();
        scoreManager.addTimeBonus(remainingTime);
        JOptionPane.showMessageDialog(frame, "Game Over! Final Score (with time bonus): " + scoreManager.getFinalScore());
        frame.dispose();
    }

    void setupCards(String imageFileName) {
        cardSet = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            Image cardImg = new ImageIcon(getClass().getResource(imageFileName + "/" + Integer.toString(i) + ".png")).getImage();
            ImageIcon cardImageIcon = new ImageIcon(cardImg.getScaledInstance(cardWidth, cardHeight, java.awt.Image.SCALE_SMOOTH));
            Card card = new Card(Integer.toString(i), cardImageIcon);
            cardSet.add(card);
        }

        cardSet.addAll(cardSet);

        Image cardBackImg = new ImageIcon(getClass().getResource("back.png")).getImage();
        cardBackImageIcon = new ImageIcon(cardBackImg.getScaledInstance(cardWidth, cardHeight, java.awt.Image.SCALE_SMOOTH));
    }

    void shuffleCards() {
        Collections.shuffle(cardSet);
    }

    void hideCards() {
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MatchCards matchCards = new MatchCards();
            matchCards.run();
        });
    }
}