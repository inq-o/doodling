package Cards;

import javax.swing.*;

public class Card {
    String cardId;
    ImageIcon cardImageIcon;
    public boolean isMatched;

    public Card(String cardId, ImageIcon icon) {
        this.cardId = cardId;
        this.cardImageIcon = icon;
        this.isMatched = false;
    }

    public Card(int CardId, ImageIcon icon) {
        this.cardId = "" + CardId;
        this.cardImageIcon = icon;
        this.isMatched = false;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public ImageIcon getCardImageIcon() {
        return cardImageIcon;
    }

    public void setCardImageIcon(ImageIcon cardImageIcon) {
        this.cardImageIcon = cardImageIcon;
    }
}