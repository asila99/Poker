import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Deck {
    private final List<Card> cards;

    public Deck() {
        cards = new ArrayList<>();
        for (int i = 0; i < Card.RANKS.length; i++) {
            for (char suit : Card.SUITS) {
                cards.add(new Card(i, suit));
            }
        }
        shuffle();
    }

    public void shuffle() {
        Collections.shuffle(cards, new Random());
    }

    public Card dealCard() {
        return cards.remove(cards.size() - 1);
    }
}
