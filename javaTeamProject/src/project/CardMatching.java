package project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import match.MatchCards;
import match.ScoreManager;
import matchN.MatchName;
import matchN.ScoreManagerN;
import number.MatchSequence;

public class CardMatching extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private ArrayList<PlayerScore> game1Scores = new ArrayList<>();
    private ArrayList<PlayerScore> game2Scores = new ArrayList<>();
    private MatchCards matchCards;
    private MatchName matchName;

    public CardMatching() {
        setTitle("카드 매칭 게임");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("resource/card.jpg"));
        ImagePanel menuPanel = new ImagePanel(icon.getImage());

        menuPanel.setLayout(new GridBagLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        JButton gameButton = new JButton("게임 시작");
        JButton rankButton = new JButton("등수 확인");
        JButton exitButton = new JButton("게임 종료");

        gameButton.setPreferredSize(new Dimension(120, 50));
        rankButton.setPreferredSize(new Dimension(120, 50));
        exitButton.setPreferredSize(new Dimension(120, 50));

        gameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "gameSelect");
            }
        });

        rankButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "rankSelect");
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        buttonPanel.add(gameButton);
        buttonPanel.add(rankButton);
        buttonPanel.add(exitButton);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        menuPanel.add(buttonPanel, gbc);

        JPanel gameSelectPanel = createGameSelectPanel();
        JPanel rankSelectPanel = createRankSelectPanel();

        JPanel game1RankPanel = createRankPanel("게임 1 등수", game1Scores);
        JPanel game2RankPanel = createRankPanel("게임 2 등수", game2Scores); // 게임 2의 랭킹 패널 생성

        mainPanel.add(menuPanel, "menu");
        mainPanel.add(gameSelectPanel, "gameSelect");
        mainPanel.add(rankSelectPanel, "rankSelect");
        mainPanel.add(game1RankPanel, "game1Rank");
        mainPanel.add(game2RankPanel, "game2Rank"); // 게임 2 랭킹 패널 추가

        add(mainPanel);
        cardLayout.show(mainPanel, "menu");
    }

    private JPanel createGameSelectPanel() {
        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("resource/card2.jpg"));
        ImagePanel gameSelectPanel = new ImagePanel(icon.getImage());
        gameSelectPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JButton game1Button = new JButton("게임 1");
        JButton game2Button = new JButton("게임 2");
        JButton game3Button = new JButton("게임 3");
        JButton backButton = new JButton("메뉴로 돌아가기");

        game1Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                launchMatchColorGame();
            }
        });

        game2Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                launchMatchNameGame();
            }
        });

        game3Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                launchNumberGame();
            }
        });

        gbc.gridy = 0;
        gameSelectPanel.add(game1Button, gbc);
        gbc.gridy = 1;
        gameSelectPanel.add(game2Button, gbc);
        gbc.gridy = 2;
        gameSelectPanel.add(game3Button, gbc);
        gbc.gridy = 3;
        gameSelectPanel.add(backButton, gbc);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "menu");
            }
        });

        return gameSelectPanel;
    }

    private JPanel createRankSelectPanel() {
        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("resource/rank.jpg"));
        ImagePanel rankSelectPanel = new ImagePanel(icon.getImage());
        rankSelectPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JButton game1RankButton = new JButton("게임 1 등수");
        JButton game2RankButton = new JButton("게임 2 등수");
        JButton backButton = new JButton("메뉴로 돌아가기");

        game1RankButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "game1Rank");
            }
        });

        game2RankButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "game2Rank");
            }
        });

        gbc.gridy = 0;
        rankSelectPanel.add(game1RankButton, gbc);
        gbc.gridy = 1;
        rankSelectPanel.add(game2RankButton, gbc);
        gbc.gridy = 2;
        rankSelectPanel.add(backButton, gbc);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "menu");
            }
        });

        return rankSelectPanel;
    }

    private JPanel createRankPanel(String title, ArrayList<PlayerScore> scores) {
        JPanel rankPanel = new JPanel(new BorderLayout());
        JTextArea rankArea = new JTextArea();
        rankArea.setEditable(false);
        rankArea.setFont(new Font("Arial", Font.PLAIN, 16));
        rankPanel.add(new JScrollPane(rankArea), BorderLayout.CENTER);

        JButton backButton = new JButton("등수 선택 화면으로 돌아가기");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "rankSelect");
            }
        });
        rankPanel.add(backButton, BorderLayout.SOUTH);

        rankPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                updateRankArea(rankArea, scores);
            }
        });

        return rankPanel;
    }

    private void updateRankArea(JTextArea rankArea, ArrayList<PlayerScore> scores) {
        Collections.sort(scores, Comparator.comparingInt(PlayerScore::getScore).reversed());
        StringBuilder rankText = new StringBuilder();
        for (int i = 0; i < scores.size(); i++) {
            rankText.append(i + 1).append(". ").append(scores.get(i).getName())
                    .append(" - ").append(scores.get(i).getScore()).append("\n");
        }
        rankArea.setText(rankText.toString());
    }

    private void launchMatchColorGame() {
        ScoreManager scoreManager = new ScoreManager();
        matchCards = new MatchCards(scoreManager);
        matchCards.setGameEndListener(new MatchCards.GameEndListener() {
            @Override
            public void onGameEnd(int finalScore) {
                String playerName = JOptionPane.showInputDialog(CardMatching.this, "이름을 입력하세요:", "게임 종료", JOptionPane.PLAIN_MESSAGE);
                if (playerName != null && !playerName.trim().isEmpty()) {
                    game1Scores.add(new PlayerScore(playerName, finalScore));
                    JOptionPane.showMessageDialog(CardMatching.this, "점수가 저장되었습니다!");
                }
                cardLayout.show(mainPanel, "menu");
            }
        });
        matchCards.run();
    }

    private void launchMatchNameGame() {
        ScoreManagerN scoreManagerN = new ScoreManagerN();
        matchName = new MatchName(scoreManagerN);
        matchName.setGameEndListener(new MatchName.GameEndListener() {
            @Override
            public void onGameEnd(int finalScore) {
                String playerName = JOptionPane.showInputDialog(CardMatching.this, "이름을 입력하세요:", "게임 종료", JOptionPane.PLAIN_MESSAGE);
                if (playerName != null && !playerName.trim().isEmpty()) {
                    game2Scores.add(new PlayerScore(playerName, finalScore)); // 게임 2 점수 저장
                    JOptionPane.showMessageDialog(CardMatching.this, "점수가 저장되었습니다!");
                }
                cardLayout.show(mainPanel, "menu");
            }
        });
        matchName.run();
    }

    private void launchNumberGame() {
        MatchSequence matchSequence = new MatchSequence();
        matchSequence.run();
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CardMatching().setVisible(true));
    }
}

class PlayerScore {
    private String name;
    private int score;

    public PlayerScore(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }
}
