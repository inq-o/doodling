package match;

import javax.swing.*;

public class Card {
    String cardId;
    ImageIcon cardImageIcon;
    boolean isMatched; // 카드 매칭 여부를 추적

    public Card(String cardId, ImageIcon icon) {
        this.cardId = cardId;
        this.cardImageIcon = icon;
        this.isMatched = false;
    }
}