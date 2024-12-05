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
        ImageIcon houseIcon = new ImageIcon(Objects.requireNonNull(CardMatching.class.getResource("/resource/buttons/mainNav.png")));
        backToMainButton.setIcon(new ImageIcon(houseIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
        backToMainButton.setPreferredSize(new Dimension(50, 50));
        backToMainButton.setBorder(BorderFactory.createEmptyBorder()); // 테두리 제거
        backToMainButton.setContentAreaFilled(false);
        backToMainButton.addActionListener(e -> cardLayout.show(mainPanel, "menu"));

        JButton backToGameSelectButton = new JButton("게임 선택 화면");
        ImageIcon gameIcon = new ImageIcon(Objects.requireNonNull(CardMatching.class.getResource("/resource/buttons/gameNav.png")));
        backToGameSelectButton.setIcon(new ImageIcon(gameIcon.getImage().getScaledInstance(50,50, Image.SCALE_SMOOTH)));
        backToGameSelectButton.setPreferredSize(new Dimension(50, 50));
        backToGameSelectButton.setBorder(BorderFactory.createEmptyBorder()); // 테두리 제거
        backToGameSelectButton.setContentAreaFilled(false); // 배경 제거
        backToGameSelectButton.addActionListener(e -> cardLayout.show(mainPanel, "gameSelect"));

        JButton backToRankSelectButton = new JButton("랭킹 선택 화면");
        ImageIcon rank2Icon = new ImageIcon(Objects.requireNonNull(CardMatching.class.getResource("/resource/buttons/rankNav.png")));
        backToRankSelectButton.setIcon(new ImageIcon(rank2Icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
        backToRankSelectButton.setPreferredSize(new Dimension(50, 50));
        backToRankSelectButton.setBorder(BorderFactory.createEmptyBorder()); // 테두리 제거
        backToRankSelectButton.setContentAreaFilled(false); // 배경 제거
        backToRankSelectButton.addActionListener(e -> cardLayout.show(mainPanel, "rankSelect"));

        JButton backToPreviousButton = new JButton("이전 화면");
        ImageIcon backIcon = new ImageIcon(Objects.requireNonNull(CardMatching.class.getResource("/resource/buttons/backNav.png")));
        backToPreviousButton.setIcon(new ImageIcon(backIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
        backToPreviousButton.setPreferredSize(new Dimension(50, 50));
        backToPreviousButton.setBorder(BorderFactory.createEmptyBorder()); // 테두리 제거
        backToPreviousButton.setContentAreaFilled(false); // 배경 제거
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
        gameSelectPanel.setLayout(new BorderLayout()); // BorderLayout으로 변경

        // 중앙 버튼 패널
        JPanel centerButtonPanel = new JPanel(new GridBagLayout());
        centerButtonPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 0, 20, 0); // 위아래 간격 설정
        gbc.gridx = 0; // 같은 열에 배치
        gbc.anchor = GridBagConstraints.CENTER; // 중앙 정렬

        JButton game1Button = new JButton();
        JButton game2Button = new JButton();
        JButton game3Button = new JButton();
        JButton helpButton = new JButton();  // HELP 버튼 추가

        ImageIcon game1Icon = new ImageIcon(Objects.requireNonNull(CardMatching.class.getResource("/resource/buttons/selectGame1.png")));
        game1Button.setIcon(new ImageIcon(game1Icon.getImage().getScaledInstance(200, 50, Image.SCALE_SMOOTH)));
        game1Button.setPreferredSize(new Dimension(200, 50));

        ImageIcon game2Icon = new ImageIcon(Objects.requireNonNull(CardMatching.class.getResource("/resource/buttons/selectGame2.png")));
        game2Button.setIcon(new ImageIcon(game2Icon.getImage().getScaledInstance(200, 50, Image.SCALE_SMOOTH)));
        game2Button.setPreferredSize(new Dimension(200, 50));

        ImageIcon game3Icon = new ImageIcon(Objects.requireNonNull(CardMatching.class.getResource("/resource/buttons/selectGame3.png")));
        game3Button.setIcon(new ImageIcon(game3Icon.getImage().getScaledInstance(200, 50, Image.SCALE_SMOOTH)));
        game3Button.setPreferredSize(new Dimension(200, 50));

        // HELP 버튼 아이콘 설정
        ImageIcon helpIcon = new ImageIcon(Objects.requireNonNull(CardMatching.class.getResource("/resource/buttons/qm.png")));
        helpButton.setIcon(new ImageIcon(helpIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));  // 크기 설정
        helpButton.setPreferredSize(new Dimension(50, 50));
        helpButton.setBorder(BorderFactory.createEmptyBorder());
        helpButton.setContentAreaFilled(false);


        game1Button.addActionListener(e -> launchGame("color"));
        game2Button.addActionListener(e -> launchGame("character"));
        game3Button.addActionListener(e -> launchNumberGame());
        helpButton.addActionListener(e -> showHelpDialog());



        gbc.gridy = 0;
        centerButtonPanel.add(game1Button, gbc);
        gbc.gridy = 1;
        centerButtonPanel.add(game2Button, gbc);
        gbc.gridy = 2;
        centerButtonPanel.add(game3Button, gbc);

        // HELP 버튼을 우측 상단에 배치
        gbc.gridx = 1; // 우측 열로 설정
        gbc.gridy = 0; // 첫 번째 행
        gbc.anchor = GridBagConstraints.NORTHEAST; // 우측 상단에 배치
        centerButtonPanel.add(helpButton, gbc);

        // 하단 버튼 패널
        JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // 좌측 정렬
        bottomButtonPanel.setOpaque(false);

        JButton backToMainButton = new JButton();
        JButton backToGameSelectButton = new JButton();
        JButton backToRankSelectButton = new JButton();
        JButton backToPreviousButton = new JButton();

        ImageIcon houseIcon = new ImageIcon(Objects.requireNonNull(CardMatching.class.getResource("/resource/buttons/mainNav.png")));
        backToMainButton.setIcon(new ImageIcon(houseIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
        backToMainButton.setPreferredSize(new Dimension(50, 50));
        backToMainButton.setBorder(BorderFactory.createEmptyBorder());
        backToMainButton.setContentAreaFilled(false);

        ImageIcon gameIcon = new ImageIcon(Objects.requireNonNull(CardMatching.class.getResource("/resource/buttons/gameNav.png")));
        backToGameSelectButton.setIcon(new ImageIcon(gameIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
        backToGameSelectButton.setPreferredSize(new Dimension(50, 50));
        backToGameSelectButton.setBorder(BorderFactory.createEmptyBorder());
        backToGameSelectButton.setContentAreaFilled(false);

        ImageIcon rankIcon = new ImageIcon(Objects.requireNonNull(CardMatching.class.getResource("/resource/buttons/rankNav.png")));
        backToRankSelectButton.setIcon(new ImageIcon(rankIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
        backToRankSelectButton.setPreferredSize(new Dimension(50, 50));
        backToRankSelectButton.setBorder(BorderFactory.createEmptyBorder());
        backToRankSelectButton.setContentAreaFilled(false);

        ImageIcon backIcon = new ImageIcon(Objects.requireNonNull(CardMatching.class.getResource("/resource/buttons/backNav.png")));
        backToPreviousButton.setIcon(new ImageIcon(backIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
        backToPreviousButton.setPreferredSize(new Dimension(50, 50));
        backToPreviousButton.setBorder(BorderFactory.createEmptyBorder());
        backToPreviousButton.setContentAreaFilled(false);

        // 하단 버튼 이벤트 추가
        backToMainButton.addActionListener(e -> cardLayout.show(mainPanel, "menu"));
        backToGameSelectButton.addActionListener(e -> cardLayout.show(mainPanel, "gameSelect"));
        backToRankSelectButton.addActionListener(e -> cardLayout.show(mainPanel, "rankSelect"));
        backToPreviousButton.addActionListener(e -> cardLayout.previous(mainPanel));

        // 하단 버튼 패널에 추가
        bottomButtonPanel.add(backToMainButton);
        bottomButtonPanel.add(backToGameSelectButton);
        bottomButtonPanel.add(backToRankSelectButton);
        bottomButtonPanel.add(backToPreviousButton);

        // 패널에 구성 추가
        gameSelectPanel.add(centerButtonPanel, BorderLayout.CENTER); // 중앙
        gameSelectPanel.add(bottomButtonPanel, BorderLayout.SOUTH);  // 하단

        return gameSelectPanel;
    }

    // 설명서 팝업창 메서드
    private void showHelpDialog() {
        String helpMessage = "게임 설명:\n" +
                "game1: 같은 색깔 맞추기.\n" +
                "game2: 이미지와 단어 맞추기.\n" +
                "game3: 숫자 순서대로 맞추기.\n\n" +
                "점수 및 아이템 설명:\n"+
                "카드를 매칭하면 30점을 얻습니다.\n"+
                "제한 시간: 60초\n"+
                "콤보: 연속해서 카드 매칭 시, 더 많은 점수를 얻을 수 있습니다.\n"+
                "눈 모양 아이콘: 모든 카드를 뒤집을 수 있는 아이템으로 1회 사용 가능합니다\n"+
                "카드를 맞추지 못 할 때마다, 5점씩 감점합니다\n"+
                "모든 카드를 매칭 한 후, 남은 시간은 점수로 들어갑니다.\n"+
                "최대한 틀리지 않고, 연속해서 빠르게 맞출수록 높은 점수를 얻을 수 있습니다.";
        JOptionPane.showMessageDialog(mainPanel, helpMessage, "게임 설명", JOptionPane.INFORMATION_MESSAGE);
    }


    private JPanel createRankSelectPanel() {
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(CardMatching.class.getResource("/resource/backgrounds/rankSelect.png")));
        ImagePanel rankSelectPanel = new ImagePanel(icon.getImage());
        rankSelectPanel.setLayout(new BorderLayout()); // BorderLayout으로 변경

        // 중앙 버튼 패널
        JPanel centerButtonPanel = new JPanel(new GridBagLayout());
        centerButtonPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 0, 20, 0); // 위아래 간격 설정
        gbc.gridx = 0; // 같은 열에 배치
        gbc.anchor = GridBagConstraints.CENTER; // 중앙 정렬

        JButton game1RankButton = new JButton();
        JButton game2RankButton = new JButton();
        JButton game3RankButton = new JButton();

        ImageIcon game1RankIcon = new ImageIcon(Objects.requireNonNull(CardMatching.class.getResource("/resource/buttons/game1.png")));
        game1RankButton.setIcon(new ImageIcon(game1RankIcon.getImage().getScaledInstance(200, 50, Image.SCALE_SMOOTH)));
        game1RankButton.setPreferredSize(new Dimension(200, 50));

        ImageIcon game2RankIcon = new ImageIcon(Objects.requireNonNull(CardMatching.class.getResource("/resource/buttons/game2.png")));
        game2RankButton.setIcon(new ImageIcon(game2RankIcon.getImage().getScaledInstance(200, 50, Image.SCALE_SMOOTH)));
        game2RankButton.setPreferredSize(new Dimension(200, 50));

        ImageIcon game3RankIcon = new ImageIcon(Objects.requireNonNull(CardMatching.class.getResource("/resource/buttons/game3.png")));
        game3RankButton.setIcon(new ImageIcon(game3RankIcon.getImage().getScaledInstance(200, 50, Image.SCALE_SMOOTH)));
        game3RankButton.setPreferredSize(new Dimension(200, 50));

        // 랭킹 버튼 이벤트 추가
        game1RankButton.addActionListener(e -> cardLayout.show(mainPanel, "game1Rank"));
        game2RankButton.addActionListener(e -> cardLayout.show(mainPanel, "game2Rank"));
        game3RankButton.addActionListener(e -> cardLayout.show(mainPanel, "game3Rank"));

        // 버튼 추가
        gbc.gridy = 0;
        centerButtonPanel.add(game1RankButton, gbc);
        gbc.gridy = 1;
        centerButtonPanel.add(game2RankButton, gbc);
        gbc.gridy = 2;
        centerButtonPanel.add(game3RankButton, gbc);

        // 하단 버튼 패널
        JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // 좌측 정렬
        bottomButtonPanel.setOpaque(false);

        JButton backToMainButton = new JButton();
        JButton backToGameSelectButton = new JButton();
        JButton backToRankSelectButton = new JButton();
        JButton backToPreviousButton = new JButton();

        ImageIcon houseIcon = new ImageIcon(Objects.requireNonNull(CardMatching.class.getResource("/resource/buttons/mainNav.png")));
        backToMainButton.setIcon(new ImageIcon(houseIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
        backToMainButton.setPreferredSize(new Dimension(50, 50));
        backToMainButton.setBorder(BorderFactory.createEmptyBorder());
        backToMainButton.setContentAreaFilled(false);

        ImageIcon gameIcon = new ImageIcon(Objects.requireNonNull(CardMatching.class.getResource("/resource/buttons/gameNav.png")));
        backToGameSelectButton.setIcon(new ImageIcon(gameIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
        backToGameSelectButton.setPreferredSize(new Dimension(50, 50));
        backToGameSelectButton.setBorder(BorderFactory.createEmptyBorder());
        backToGameSelectButton.setContentAreaFilled(false);

        ImageIcon rank2Icon = new ImageIcon(Objects.requireNonNull(CardMatching.class.getResource("/resource/buttons/rankNav.png")));
        backToRankSelectButton.setIcon(new ImageIcon(rank2Icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
        backToRankSelectButton.setPreferredSize(new Dimension(50, 50));
        backToRankSelectButton.setBorder(BorderFactory.createEmptyBorder());
        backToRankSelectButton.setContentAreaFilled(false);

        ImageIcon backIcon = new ImageIcon(Objects.requireNonNull(CardMatching.class.getResource("/resource/buttons/backNav.png")));
        backToPreviousButton.setIcon(new ImageIcon(backIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
        backToPreviousButton.setPreferredSize(new Dimension(50, 50));
        backToPreviousButton.setBorder(BorderFactory.createEmptyBorder());
        backToPreviousButton.setContentAreaFilled(false);

        // 하단 버튼 이벤트 추가
        backToMainButton.addActionListener(e -> cardLayout.show(mainPanel, "menu"));
        backToGameSelectButton.addActionListener(e -> cardLayout.show(mainPanel, "gameSelect"));
        backToRankSelectButton.addActionListener(e -> cardLayout.show(mainPanel, "rankSelect"));
        backToPreviousButton.addActionListener(e -> cardLayout.previous(mainPanel));

        // 하단 버튼 패널에 추가
        bottomButtonPanel.add(backToMainButton);
        bottomButtonPanel.add(backToGameSelectButton);
        bottomButtonPanel.add(backToRankSelectButton);
        bottomButtonPanel.add(backToPreviousButton);

        // 패널에 구성 추가
        rankSelectPanel.add(centerButtonPanel, BorderLayout.CENTER); // 중앙
        rankSelectPanel.add(bottomButtonPanel, BorderLayout.SOUTH);  // 하단

        return rankSelectPanel;
    }

    private JPanel createRankPanel(ArrayList<PlayerScore> scores) {

        // 랭킹 텍스트 표시를 위한 JEditorPane
        JPanel rankPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("/resource/backgrounds/rankSelect.png")));
                Image img = backgroundImage.getImage();
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        };

        // 랭킹 텍스트 표시를 위한 JEditorPane
        JEditorPane rankTextPane = new JEditorPane() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setOpaque(false); // 투명하게 설정
            }
        };
        rankTextPane.setContentType("text/html");
        rankTextPane.setEditable(false);
        rankTextPane.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(rankTextPane);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBounds(0, 80, getWidth(), getHeight() - 200);  // 스크롤 영역 크기와 위치 설정

        // 테두리 제거
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        rankPanel.add(scrollPane);

        // 백 버튼 설정 및 위치 지정
        JButton backButton = new JButton();
        ImageIcon startIcon = new ImageIcon(Objects.requireNonNull(CardMatching.class.getResource("/resource/buttons/backNav.png")));
        backButton.setIcon(new ImageIcon(startIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));  // 버튼 아이콘 크기 설정
        backButton.setBorder(BorderFactory.createEmptyBorder());  // 테두리 제거
        backButton.setContentAreaFilled(false);  // 배경 제거
        backButton.setBounds(getWidth() - 70, 20, 50, 50);  // 우측 상단에 배치
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "rankSelect"));
        rankPanel.add(backButton);

        // 랭킹 업데이트 이벤트 리스너 추가
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

        StringBuilder rankHtml = new StringBuilder("<html><body style='text-align: center; color: black; font-size: 24;'>"); // 글자 크기 키움
        rankHtml.append("<h2 style='font-size: 36px;'></h2>");
        rankHtml.append("<table style='width: 100%; border-collapse: collapse; font-size: 24px;'>"); // 테이블의 글자 크기 키움
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