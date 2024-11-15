package match;

import javax.swing.*;

public class Card {
    String cardId;
    ImageIcon cardImageIcon;

    public Card(String cardId, ImageIcon icon) {
        this.cardId = cardId;
        this.cardImageIcon = icon;
    }
}