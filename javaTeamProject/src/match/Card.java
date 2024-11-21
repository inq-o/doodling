package match;

import javax.swing.*;

public class Card {
    String cardId;
    ImageIcon cardImageIcon;
    boolean isMatched;

    public Card(String cardId, ImageIcon icon) {
        this.cardId = cardId;
        this.cardImageIcon = icon;
        this.isMatched = false;
    }
}