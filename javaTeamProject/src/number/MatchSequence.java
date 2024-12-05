package number;

import Cards.CardS;

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
        Image cardBackImage = new ImageIcon(MatchSequence.class.getResource("/resource/card_back.png")).getImage();
        cardBackIcon = new ImageIcon(cardBackImage.getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH));
    }

    public JPanel createGamePanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // 상단 정보 패널
        infoPanel.setLayout(new GridLayout(1, 2));
        timerLabel.setFont(new Font("Arial", Font.BOLD, 25));
        timerLabel.setHorizontalAlignment(JLabel.CENTER);
        timerLabel.setText("Time: " + remainingTime + "s");

        errorLabel.setFont(new Font("Arial", Font.BOLD, 25));
        errorLabel.setHorizontalAlignment(JLabel.CENTER);
        errorLabel.setText("Errors: " + errorCount);

        infoPanel.add(timerLabel);
        infoPanel.add(errorLabel);
        mainPanel.add(infoPanel, BorderLayout.NORTH);

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
        mainPanel.add(boardPanel, BorderLayout.CENTER);

        revealAllCardsTemporarily();
        startDelayedTimers();

        return mainPanel;
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

    // 타이머 시작을 3초 지연
    private void startDelayedTimers() {
        // 3초 대기 후 게임 타이머와 힌트 타이머 시작
        Timer delayTimer = new Timer(3000, e -> {
            startGameTimer(); // 3초 뒤에 게임 타이머 시작
            startHintTimer(); // 3초 뒤에 힌트 타이머 시작
        });
        delayTimer.setRepeats(false); // 한 번만 실행
        delayTimer.start();
    }

    private void startGameTimer() {
        gameTimer = new Timer(1000, e -> {
            remainingTime--;
            timerLabel.setText("Time: " + remainingTime + "s");

            if (remainingTime <= 0) {
                gameTimer.stop();
                hintTimer.stop(); // 힌트 타이머도 정지
                if (gameEndListener != null) {
                    gameEndListener.onGameEnd(nextCardNumber - 1, 0);
                }
            }
        });
        gameTimer.start();
    }

    private void startHintTimer() {
        Timer delayTimer = new Timer(3000, e -> { // 5초 대기 타이머
            hintTimer = new Timer(3000, ev -> showRandomCards()); // 힌트 타이머
            hintTimer.start();
        });
        delayTimer.setRepeats(false); // 5초 후 한 번만 실행
        delayTimer.start();
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

            Timer hideTimer = new Timer(1500, ev -> {
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
                if (gameEndListener != null) {
                    gameEndListener.onGameEnd(nextCardNumber - 1, remainingTime);
                }
            }
        } else {
            errorCount++;
            errorLabel.setText("Errors: " + errorCount);
        }
    }

    public void setGameEndListener(GameEndListener listener) {
        this.gameEndListener = listener;
    }

    public interface GameEndListener {
        void onGameEnd(int matchedCards, int remainingTime);
    }
}
