package number;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

public class MatchSequence {
    private int size = 4;
    private int cardWidth = 128;
    private int cardHeight = 128;

    private ArrayList<JButton> board;

    private int boardWidth = size * cardWidth;
    private int boardHeight = size * cardHeight;

    private JFrame frame = new JFrame("Match Sequence");
    private JPanel boardPanel = new JPanel();

    private int nextCardNumber = 1; // 사용자가 눌러야 할 다음 카드 번호

    public MatchSequence() {
    }

    public void run() {
        frame.setVisible(true);
        frame.setLayout(new BorderLayout());
        frame.setSize(boardWidth + 100, boardHeight + 100); // 창 크기 설정
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        boardPanel.setLayout(new GridLayout(size, size));
        board = new ArrayList<>();

        for (int i = 1; i <= 16; i++) {
            JButton tile = new JButton(String.valueOf(i));
            tile.setPreferredSize(new Dimension(cardWidth, cardHeight));
            tile.setFont(new Font("Comic Sans MS", Font.BOLD, 24)); // 귀여운 폰트 설정
            tile.putClientProperty("cardNumber", i);
            tile.addActionListener(new CardClickListener());
            board.add(tile);
        }

        shuffleCards(); // 버튼들을 섞음

        // 섞인 순서대로 버튼을 패널에 추가
        boardPanel.removeAll();
        for (JButton tile : board) {
            boardPanel.add(tile);
        }

        frame.add(boardPanel);
        frame.pack();
        frame.setVisible(true);

        // 5초 동안 모든 카드를 보여주기
        Timer startShowTimer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hideAllCards();
            }
        });
        startShowTimer.setRepeats(false);
        startShowTimer.start();
    }

    private void shuffleCards() {
        Collections.shuffle(board);
    }

    private void hideAllCards() {
        for (JButton card : board) {
            card.setText("");
        }
    }

    private void showAllCards() {
        for (JButton card : board) {
            int cardNumber = (int) card.getClientProperty("cardNumber");
            card.setText(String.valueOf(cardNumber));
        }
    }

    private class CardClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton clickedCard = (JButton) e.getSource();
            int cardNumber = (int) clickedCard.getClientProperty("cardNumber");

            if (cardNumber == nextCardNumber) {
                // 올바른 순서의 카드를 눌렀을 경우
                clickedCard.setText(String.valueOf(cardNumber));
                nextCardNumber++;

                // 모든 카드를 올바른 순서로 클릭한 경우 게임 종료
                if (nextCardNumber > 16) {
                    JOptionPane.showMessageDialog(frame, "축하합니다! 모든 카드를 순서대로 맞췄습니다!");
                    frame.dispose();
                }
            } else {
                // 잘못된 카드를 눌렀을 경우 게임 오버
                showAllCards();
                JOptionPane.showMessageDialog(frame, "게임 오버! 잘못된 카드를 눌렀습니다.");
                frame.dispose();
            }
        }
    }
}
