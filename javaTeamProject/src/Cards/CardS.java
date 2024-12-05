package Cards;

import javax.swing.*;
import java.awt.*;

public class CardS {
    private final int number; // 카드 번호
    private final JButton button;
    private boolean isRevealed;
    private final ImageIcon backIcon; // 카드 뒷면 이미지

    public CardS(int number, int width, int height, ImageIcon backIcon) {
        this.number = number;
        this.isRevealed = false;
        this.backIcon = backIcon;

        // 버튼 초기화
        this.button = new JButton(backIcon); // 초기 상태는 뒷면 이미지
        this.button.setPreferredSize(new Dimension(width, height));
        this.button.setFocusPainted(false); // 포커스 표시 제거
        this.button.setBorderPainted(false); // 테두리 제거
        this.button.setContentAreaFilled(false); // 배경색 제거
        this.button.setFont(new Font("Arial", Font.BOLD, 48)); // 폰트 크기 설정
        this.button.setForeground(Color.BLACK); // 텍스트 색상 설정
        this.button.setHorizontalTextPosition(SwingConstants.CENTER); // 텍스트 위치 설정
        this.button.setVerticalTextPosition(SwingConstants.CENTER); // 텍스트 위치 설정
        this.button.setText(""); // 초기 텍스트는 비어 있음
        this.button.setOpaque(true); // 배경색을 적용할 수 있도록 설정
        this.button.setBackground(Color.WHITE); // 배경을 흰색으로 설정
    }

    public int getNumber() {
        return number;
    }

    public JButton getButton() {
        return button;
    }

    public boolean isRevealed() {
        return isRevealed;
    }

    public void reveal() {
        button.setIcon(null); // 뒷면 이미지 제거
        button.setText(String.valueOf(number)); // 텍스트로 번호 표시
        button.setEnabled(false); // 클릭 불가
        isRevealed = true;
    }

    public void hide() {
        button.setIcon(backIcon); // 뒷면 이미지 설정
        button.setText(""); // 텍스트 숨김
        button.setEnabled(true); // 클릭 가능
        isRevealed = false;
    }
}
