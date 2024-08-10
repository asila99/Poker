import java.util.ArrayList;
import java.util.List;

public class PokerGame {

    public static void main(String[] args) {
        Deck deck = new Deck();

        List<Card> player1Hand = new ArrayList<>();
        List<Card> player2Hand = new ArrayList<>();
        List<Card> communityCards = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            player1Hand.add(deck.dealCard());
            player2Hand.add(deck.dealCard());
        }

        for (int i = 0; i < 5; i++) {
            communityCards.add(deck.dealCard());
        }

        HandType player1HandType = HandEvaluator.evaluateHand(player1Hand, communityCards);
        HandType player2HandType = HandEvaluator.evaluateHand(player2Hand, communityCards);

        System.out.println("Player 1's Hand: " + player1Hand);
        System.out.println("Player 2's Hand: " + player2Hand);
        System.out.println("Community Cards: " + communityCards);

        System.out.println("Player 1's Hand Type: " + player1HandType);
        System.out.println("Player 2's Hand Type: " + player2HandType);

        int result = HandEvaluator.compareHands(player1Hand, player1HandType, player2Hand, player2HandType);
        if (result < 0) {
            System.out.println("Player 1 wins!");
        } else if (result > 0) {
            System.out.println("Player 2 wins!");
        } else {
            System.out.println("It's a tie!");
        }
    }
}
