package project;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

import GameManager.*;
import number.MatchSequence;
import number.ScoreManagerS;

public class CardMatching extends JFrame {
    private final String GAME1_SCORE_FILE = "./game1.txt";
    private final String GAME2_SCORE_FILE = "./game2.txt";
    private final String GAME3_SCORE_FILE = "./game3.txt";

    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final ArrayList<PlayerScore> game1Scores = new ArrayList<>();
    private final ArrayList<PlayerScore> game2Scores = new ArrayList<>();
    private final ArrayList<PlayerScore> game3Scores = new ArrayList<>();

    String playerName;

    public CardMatching() {
        setTitle("카드 매칭 게임");
        setSize(700, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 점수 파일 로드
        loadScoresFromFile(GAME1_SCORE_FILE, game1Scores);
        loadScoresFromFile(GAME2_SCORE_FILE, game2Scores);
        loadScoresFromFile(GAME3_SCORE_FILE, game3Scores);

        playerName = JOptionPane.showInputDialog(CardMatching.this, "이름을 입력하세요:", "플레이어 이름", JOptionPane.PLAIN_MESSAGE);
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "Unknown";
        }

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // 메뉴 패널 설정
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(CardMatching.class.getResource("/resource/backgrounds/title.png")));
        ImagePanel menuPanel = new ImagePanel(icon.getImage());

        menuPanel.setLayout(new BorderLayout());  // 전체 레이아웃을 BorderLayout으로 설정
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridy = 0; // 같은 행에 배치
        gbc.anchor = GridBagConstraints.CENTER; // 중앙 정렬

        JButton gameButton = new JButton();
        JButton rankButton = new JButton();
        JButton exitButton = new JButton();

        ImageIcon startIcon = new ImageIcon(Objects.requireNonNull(CardMatching.class.getResource("/resource/buttons/start.png")));
        gameButton.setIcon(new ImageIcon(startIcon.getImage().getScaledInstance(150, 100, Image.SCALE_SMOOTH)));

        ImageIcon rankIcon = new ImageIcon(Objects.requireNonNull(CardMatching.class.getResource("/resource/buttons/rank.png")));
        rankButton.setIcon(new ImageIcon(rankIcon.getImage().getScaledInstance(150, 100, Image.SCALE_SMOOTH)));

        ImageIcon exitIcon = new ImageIcon(Objects.requireNonNull(CardMatching.class.getResource("/resource/buttons/exit.png")));
        exitButton.setIcon(new ImageIcon(exitIcon.getImage().getScaledInstance(150, 100, Image.SCALE_SMOOTH)));

        gameButton.setPreferredSize(new Dimension(150, 100));
        rankButton.setPreferredSize(new Dimension(150, 100));
        exitButton.setPreferredSize(new Dimension(150, 100));

        gameButton.addActionListener(e -> cardLayout.show(mainPanel, "gameSelect"));
        rankButton.addActionListener(e -> cardLayout.show(mainPanel, "rankSelect"));
        exitButton.addActionListener(e -> System.exit(0));

        // 버튼들을 GridBagLayout으로 중앙 정렬
        gbc.gridx = 0;
        buttonPanel.add(gameButton, gbc);
        gbc.gridx = 1;
        buttonPanel.add(rankButton, gbc);
        gbc.gridx = 2;
        buttonPanel.add(exitButton, gbc);

        // 하단 네비게이션 버튼 추가
        JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));  // 좌측 하단에 배치하기 위해 FlowLayout 사용
        bottomButtonPanel.setOpaque(false);  // 투명하게 만들기

        JButton backToMainButton = new JButton("메인 화면");
        backToMainButton.addActionListener(e -> cardLayout.show(mainPanel, "menu"));

        JButton backToGameSelectButton = new JButton("게임 선택 화면");
        backToGameSelectButton.addActionListener(e -> cardLayout.show(mainPanel, "gameSelect"));

        JButton backToRankSelectButton = new JButton("랭킹 선택 화면");
        backToRankSelectButton.addActionListener(e -> cardLayout.show(mainPanel, "rankSelect"));

        JButton backToPreviousButton = new JButton("이전 화면");
        backToPreviousButton.addActionListener(e -> cardLayout.previous(mainPanel));

        bottomButtonPanel.add(backToMainButton);
        bottomButtonPanel.add(backToGameSelectButton);
        bottomButtonPanel.add(backToRankSelectButton);
        bottomButtonPanel.add(backToPreviousButton);

        // 메뉴 패널에 버튼들 추가
        menuPanel.add(buttonPanel, BorderLayout.CENTER);  // 버튼을 가운데 배치
        menuPanel.add(bottomButtonPanel, BorderLayout.SOUTH);  // 하단 버튼을 좌측 하단에 배치

        // 각 화면 설정
        JPanel gameSelectPanel = createGameSelectPanel();
        JPanel rankSelectPanel = createRankSelectPanel();

        JPanel game1RankPanel = createRankPanel(game1Scores);
        JPanel game2RankPanel = createRankPanel(game2Scores);
        JPanel game3RankPanel = createGame3RankPanel(); // 게임3 랭킹 패널 추가

        mainPanel.add(menuPanel, "menu");
        mainPanel.add(gameSelectPanel, "gameSelect");
        mainPanel.add(rankSelectPanel, "rankSelect");
        mainPanel.add(game1RankPanel, "game1Rank");
        mainPanel.add(game2RankPanel, "game2Rank");
        mainPanel.add(game3RankPanel, "game3Rank");

        add(mainPanel);
        cardLayout.show(mainPanel, "menu");

        // 게임 종료 시 점수 저장
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            saveScoresToFile(GAME1_SCORE_FILE, game1Scores);
            saveScoresToFile(GAME2_SCORE_FILE, game2Scores);
            saveScoresToFile(GAME3_SCORE_FILE, game3Scores);
        }));
    }

    // 기존에 있는 기능들은 그대로 두고, 하단 버튼만 추가
    private void addBottomNavigationButtons(JPanel menuPanel, JPanel gameSelectPanel, JPanel rankSelectPanel) {
        JPanel bottomButtonPanel = new JPanel(new GridLayout(1, 4)); // 버튼을 좌측 하단에 나열
        bottomButtonPanel.setOpaque(false);  // 투명하게 만들기

        // 버튼 추가: 메인 화면, 게임 선택 화면, 랭킹 선택 화면, 이전 화면
        JButton backToMainButton = new JButton("메인 화면");
        backToMainButton.addActionListener(e -> cardLayout.show(mainPanel, "menu"));

        JButton backToGameSelectButton = new JButton("게임 선택 화면");
        backToGameSelectButton.addActionListener(e -> cardLayout.show(mainPanel, "gameSelect"));

        JButton backToRankSelectButton = new JButton("랭킹 선택 화면");
        backToRankSelectButton.addActionListener(e -> cardLayout.show(mainPanel, "rankSelect"));

        JButton backToPreviousButton = new JButton("이전 화면");
        backToPreviousButton.addActionListener(e -> cardLayout.previous(mainPanel));

        // 하단 버튼들을 배치
        bottomButtonPanel.add(backToMainButton);
        bottomButtonPanel.add(backToGameSelectButton);
        bottomButtonPanel.add(backToRankSelectButton);
        bottomButtonPanel.add(backToPreviousButton);

        // 기존 메뉴나 각 화면 패널에 하단 버튼 패널 추가
        menuPanel.add(bottomButtonPanel, BorderLayout.SOUTH);
        gameSelectPanel.add(bottomButtonPanel, BorderLayout.SOUTH);
        rankSelectPanel.add(bottomButtonPanel, BorderLayout.SOUTH);
    }

    private void loadScoresFromFile(String fileName, ArrayList<PlayerScore> scores) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String name = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    scores.add(new PlayerScore(name, score));
                }
            }
        } catch (IOException e) {
            System.out.println("파일을 불러오는 중 오류가 발생했습니다: " + fileName);
        }
    }

    private void saveScoresToFile(String fileName, ArrayList<PlayerScore> scores) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (PlayerScore score : scores) {
                writer.write(score.getName() + "," + score.getScore());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("파일을 저장하는 중 오류가 발생했습니다: " + fileName);
        }
    }

    private JPanel createGameSelectPanel() {
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(CardMatching.class.getResource("/resource/backgrounds/gameSelection.png")));
        ImagePanel gameSelectPanel = new ImagePanel(icon.getImage());
        gameSelectPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JButton game1Button = new JButton();
        JButton game2Button = new JButton();
        JButton game3Button = new JButton(); // 게임3 버튼 추가
        JButton backButton = new JButton();

        ImageIcon game1Icon = new ImageIcon(Objects.requireNonNull(CardMatching.class.getResource("/resource/buttons/selectGame1.png")));
        game1Button.setIcon(new ImageIcon(game1Icon.getImage().getScaledInstance(200, 50, Image.SCALE_SMOOTH)));
        game1Button.setPreferredSize(new Dimension(200, 50));

        ImageIcon game2Icon = new ImageIcon(Objects.requireNonNull(CardMatching.class.getResource("/resource/buttons/selectGame2.png")));
        game2Button.setIcon(new ImageIcon(game2Icon.getImage().getScaledInstance(200, 50, Image.SCALE_SMOOTH)));
        game2Button.setPreferredSize(new Dimension(200, 50));

        ImageIcon game3Icon = new ImageIcon(Objects.requireNonNull(CardMatching.class.getResource("/resource/buttons/selectGame3.png")));
        game3Button.setIcon(new ImageIcon(game3Icon.getImage().getScaledInstance(200, 50, Image.SCALE_SMOOTH)));
        game3Button.setPreferredSize(new Dimension(200, 50));

        ImageIcon menuButtonIcon = new ImageIcon(Objects.requireNonNull(CardMatching.class.getResource("/resource/buttons/menu.png")));
        backButton.setIcon(new ImageIcon(menuButtonIcon.getImage().getScaledInstance(200, 50, Image.SCALE_SMOOTH)));
        backButton.setPreferredSize(new Dimension(200, 40));

        game1Button.addActionListener(e -> launchGame("color"));
        game2Button.addActionListener(e -> launchGame("similar"));
        game3Button.addActionListener(e -> launchNumberGame()); // 게임3 실행 로직 연결
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "menu"));

        gbc.gridy = 0;
        gameSelectPanel.add(game1Button, gbc);
        gbc.gridy = 1;
        gameSelectPanel.add(game2Button, gbc);
        gbc.gridy = 2;
        gameSelectPanel.add(game3Button, gbc); // 게임3 버튼 추가
        gbc.gridy = 3;
        gameSelectPanel.add(backButton, gbc);

        return gameSelectPanel;
    }

    private JPanel createRankSelectPanel() {
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(CardMatching.class.getResource("/resource/backgrounds/rankSelect.png")));
        ImagePanel rankSelectPanel = new ImagePanel(icon.getImage());
        rankSelectPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JButton game1RankButton = new JButton();
        JButton game2RankButton = new JButton();
        JButton backButton = new JButton();

        ImageIcon game1RankIcon = new ImageIcon(Objects.requireNonNull(CardMatching.class.getResource("/resource/buttons/game1.png")));
        game1RankButton.setIcon(new ImageIcon(game1RankIcon.getImage().getScaledInstance(200, 50, Image.SCALE_SMOOTH)));
        game1RankButton.setPreferredSize(new Dimension(200, 50));

        ImageIcon game2RankIcon = new ImageIcon(Objects.requireNonNull(CardMatching.class.getResource("/resource/buttons/game2.png")));
        game2RankButton.setIcon(new ImageIcon(game2RankIcon.getImage().getScaledInstance(200, 50, Image.SCALE_SMOOTH)));
        game2RankButton.setPreferredSize(new Dimension(200, 50));

        ImageIcon menuButtonIcon = new ImageIcon(Objects.requireNonNull(CardMatching.class.getResource("/resource/buttons/menu.png")));
        backButton.setIcon(new ImageIcon(menuButtonIcon.getImage().getScaledInstance(200, 50, Image.SCALE_SMOOTH)));
        backButton.setPreferredSize(new Dimension(200, 50));

        game1RankButton.addActionListener(e -> cardLayout.show(mainPanel, "game1Rank"));
        game2RankButton.addActionListener(e -> cardLayout.show(mainPanel, "game2Rank"));
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "menu"));

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        rankSelectPanel.add(game1RankButton, gbc);

        gbc.gridy = 1;
        rankSelectPanel.add(game2RankButton, gbc);

        gbc.gridy = 2;
        rankSelectPanel.add(backButton, gbc);

        return rankSelectPanel;
    }

    private JPanel createRankPanel(ArrayList<PlayerScore> scores) {
        JPanel rankPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                ImageIcon backgroundImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("/resource/backgrounds/title.png")));
                Image img = backgroundImage.getImage();
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                super.paintComponent(g);
            }
        };

        JLabel titleLabel = new JLabel("랭킹");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(Color.BLACK);
        rankPanel.add(titleLabel, BorderLayout.NORTH);

        JEditorPane rankTextPane = new JEditorPane() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setOpaque(false);
            }
        };
        rankTextPane.setContentType("text/html");
        rankTextPane.setEditable(false);
        rankTextPane.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(rankTextPane);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        rankPanel.add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("뒤로 가기");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "rankSelect"));
        rankPanel.add(backButton, BorderLayout.SOUTH);

        rankPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                updateRankArea(rankTextPane, scores);
            }
        });

        return rankPanel;
    }

    private void updateRankArea(JEditorPane rankTextPane, ArrayList<PlayerScore> scores) {
        scores.sort(Comparator.comparingInt(PlayerScore::getScore).reversed());

        StringBuilder rankHtml = new StringBuilder("<html><body style='text-align: center; color: black;'>");
        rankHtml.append("<h2>랭킹</h2>");
        rankHtml.append("<table style='width: 100%; border-collapse: collapse;'>");
        rankHtml.append("<tr style='font-weight: bold; text-align: center;'><td>순위</td><td>이름</td><td>점수</td></tr>");

        for (int i = 0; i < scores.size(); i++) {
            PlayerScore score = scores.get(i);
            rankHtml.append("<tr style='text-align: center;'>")
                    .append("<td>").append(i + 1).append("</td>")
                    .append("<td>").append(score.getName()).append("</td>")
                    .append("<td>").append(score.getScore()).append("</td>")
                    .append("</tr>");
        }

        rankHtml.append("</table></body></html>");

        rankTextPane.setText(rankHtml.toString());
        rankTextPane.setCaretPosition(0);
    }

    private void launchGame(String gameName) {
        System.out.println("Launching game mode: " + gameName);
        ScoreManager scoreManager = new ScoreManager();
        MatchCards matchCards = new MatchCards(scoreManager);

        JPanel gamePanel = matchCards.createGamePanel(gameName);
        mainPanel.add(gamePanel, "gamePanel");

        matchCards.setGameEndListener(finalScore -> {
            System.out.println("Game mode: " + gameName + " ended with score: " + finalScore);
            if (gameName.equals("color")) {
                game1Scores.add(new PlayerScore(playerName, finalScore));
            } else if (gameName.equals("similar")) {
                game2Scores.add(new PlayerScore(playerName, finalScore));
            }
            cardLayout.show(mainPanel, "menu");
        });

        cardLayout.show(mainPanel, "gamePanel");
    }

    private void launchNumberGame() {
        ScoreManagerS scoreManager = new ScoreManagerS();
        MatchSequence matchSequence = new MatchSequence(scoreManager);

        JPanel game3Panel = matchSequence.createGamePanel();
        mainPanel.add(game3Panel, "game3Panel");

        matchSequence.setGameEndListener((matchedCards, remainingTime) -> {
            game3Scores.add(new PlayerScore(playerName, matchedCards * 10));
            JOptionPane.showMessageDialog(this,
                    "게임3이 종료되었습니다!\n점수: " + (matchedCards * 10) + " (남은 시간: " + remainingTime + "초)");
            cardLayout.show(mainPanel, "menu");
        });

        cardLayout.show(mainPanel, "game3Panel");
    }

    private JPanel createGame3RankPanel() {
        return createRankPanel(new ArrayList<>(game3Scores));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CardMatching().setVisible(true));
    }
}
