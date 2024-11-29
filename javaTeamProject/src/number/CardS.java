package number;

import javax.swing.*;
import java.awt.*;

public class CardS {
    private final int number;
    private final JButton button;
    private final ImageIcon backIcon;

    public CardS(int number, int width, int height, ImageIcon backIcon) {
        this.number = number;
        this.backIcon = backIcon;
        this.button = new JButton(backIcon);
        this.button.setPreferredSize(new Dimension(width, height));
        this.button.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
    }

    public JButton getButton() {
        return button;
    }

    public int getNumber() {
        return number;
    }

    public void reveal() {
        button.setText(String.valueOf(number));
        button.setIcon(null);
    }

    public void hide() {
        button.setText("");
        button.setIcon(backIcon);
    }
}
