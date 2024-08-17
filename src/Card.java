public class Card {
    private final int rank;
    private final char suit;

    public static final char[] SUITS = {'♠', '♣', '♦', '♥'};
    public static final String[] RANKS = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};

    public Card(int rank, char suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public int getRank() {
        return rank;
    }

    public char getSuit() {
        return suit;
    }

    @Override
    public String toString() {
        return RANKS[rank] + suit;
    }
}