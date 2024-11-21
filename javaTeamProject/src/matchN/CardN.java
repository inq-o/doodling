package matchN;

import javax.swing.*;

public class CardN {
    String cardId;
    ImageIcon cardImageIcon;

    public CardN(int cardId, ImageIcon icon) {
        this.cardId = String.valueOf(cardId);
        this.cardImageIcon = icon;
    }

    public String getCardId() {
        return cardId;
    }
}