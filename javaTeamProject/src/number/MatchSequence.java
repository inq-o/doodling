package number;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MatchSequence {
    private int size = 4; // 보드 크기 (4x4)
    private int cardWidth = 128;
    private int cardHeight = 128;

    private ArrayList<CardS> cardSet;
    private JFrame frame = new JFrame("Match Sequence");
    private JPanel boardPanel = new JPanel();
    private JPanel infoPanel = new JPanel();

    private int nextCardNumber = 1;
    private int errorCount = 0;
    private int remainingTime = 60;

    private JLabel timerLabel = new JLabel();
    private JLabel errorLabel = new JLabel();
    private Timer gameTimer; // javax.swing.Timer
    private Timer hintTimer; // javax.swing.Timer
    private ImageIcon cardBackIcon;

    private GameEndListener gameEndListener;
    private ScoreManagerS scoreManager;

    public MatchSequence(ScoreManagerS scoreManager) {
        this.scoreManager = scoreManager;

        // 카드 뒷면 이미지 설정 및 크기 조정
        Image cardBackImage = new ImageIcon(getClass().getClassLoader().getResource("resource/card_back.png")).getImage();
        cardBackIcon = new ImageIcon(cardBackImage.getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH));
    }

    public void run() {
        // 프레임 설정
        frame.setVisible(true);
        frame.setLayout(new BorderLayout());
        frame.setSize(size * cardWidth + 100, size * cardHeight + 200);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 상단 정보 패널
        infoPanel.setLayout(new GridLayout(1, 2));
        timerLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        timerLabel.setHorizontalAlignment(JLabel.CENTER);
        timerLabel.setText("Time: " + remainingTime + "s");

        errorLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        errorLabel.setHorizontalAlignment(JLabel.CENTER);
        errorLabel.setText("Errors: " + errorCount);

        infoPanel.add(timerLabel);
        infoPanel.add(errorLabel);
        frame.add(infoPanel, BorderLayout.NORTH);

        // 보드 패널
        boardPanel.setLayout(new GridLayout(size, size));
        cardSet = new ArrayList<>();
        setupCards();
        shuffleCards();

        for (CardS card : cardSet) {
            JButton tile = card.getButton();
            boardPanel.add(tile);
            tile.addActionListener(e -> handleCardClick(card));
        }
        frame.add(boardPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

        // 초기 카드 공개 및 타이머 시작
        revealAllCardsTemporarily();
        startGameTimer();
        startHintTimer(); // 힌트 타이머 시작
    }

    private void setupCards() {
        for (int i = 1; i <= size * size; i++) {
            CardS card = new CardS(i, cardWidth, cardHeight, cardBackIcon);
            cardSet.add(card);
        }
    }

    private void shuffleCards() {
        Collections.shuffle(cardSet);
    }

    private void revealAllCardsTemporarily() {
        for (CardS card : cardSet) {
            card.reveal();
        }
        Timer revealTimer = new Timer(3000, e -> hideAllCards());
        revealTimer.setRepeats(false);
        revealTimer.start();
    }

    private void hideAllCards() {
        for (CardS card : cardSet) {
            card.hide();
        }
    }

    private void startGameTimer() {
        gameTimer = new Timer(1000, e -> {
            remainingTime--;
            timerLabel.setText("Time: " + remainingTime + "s");

            if (remainingTime <= 0) {
                gameTimer.stop();
                hintTimer.stop(); // 힌트 타이머도 정지
                endGame("Time's up! You matched " + (nextCardNumber - 1) + " cards.");
            }
        });
        gameTimer.start();
    }

    private void startHintTimer() {
        hintTimer = new Timer(3000, e -> showRandomCards());
        hintTimer.start();
    }

    private void showRandomCards() {
        ArrayList<CardS> availableCards = new ArrayList<>();
        for (CardS card : cardSet) {
            if (card.getNumber() >= nextCardNumber) { // 아직 맞추지 않은 카드
                availableCards.add(card);
            }
        }

        if (availableCards.size() >= 2) {
            Random random = new Random();
            int firstIndex = random.nextInt(availableCards.size());
            int secondIndex;
            do {
                secondIndex = random.nextInt(availableCards.size());
            } while (secondIndex == firstIndex);

            CardS firstCard = availableCards.get(firstIndex);
            CardS secondCard = availableCards.get(secondIndex);

            firstCard.reveal();
            secondCard.reveal();

            Timer hideTimer = new Timer(1000, ev -> {
                firstCard.hide();
                secondCard.hide();
            });
            hideTimer.setRepeats(false);
            hideTimer.start();
        }
    }

    private void handleCardClick(CardS card) {
        if (card.getNumber() == nextCardNumber) {
            card.reveal();
            nextCardNumber++;

            if (nextCardNumber > size * size) {
                gameTimer.stop();
                hintTimer.stop();
                endGame("Congratulations! You matched all cards.");
            }
        } else {
            gameTimer.stop();
            hintTimer.stop();
            endGame("Game Over! You clicked the wrong card.");
        }
    }

    private void endGame(String message) {
        JOptionPane.showMessageDialog(frame, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);

        scoreManager.setMatchedCards(nextCardNumber - 1);
        scoreManager.setRemainingTime(remainingTime);

        if (gameEndListener != null) {
            gameEndListener.onGameEnd(scoreManager.getMatchedCards(), scoreManager.getRemainingTime());
        }

        frame.dispose();
    }

    public void setGameEndListener(GameEndListener listener) {
        this.gameEndListener = listener;
    }

    public interface GameEndListener {
        void onGameEnd(int matchedCards, int remainingTime);
    }
}
