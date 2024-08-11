import java.util.*;

public class HandEvaluator {

    public static HandType evaluateHand(List<Card> hand, List<Card> communityCards) {
        List<Card> combined = new ArrayList<>(hand);
        combined.addAll(communityCards);
        Collections.sort(combined, (a, b) -> b.getRank() - a.getRank());

        if (isStraightFlush(combined)) return HandType.STRAIGHT_FLUSH;
        if (isFourOfAKind(combined)) return HandType.FOUR_OF_A_KIND;
        if (isFullHouse(combined)) return HandType.FULL_HOUSE;
        if (isFlush(combined)) return HandType.FLUSH;
        if (isStraight(combined)) return HandType.STRAIGHT;
        if (isThreeOfAKind(combined)) return HandType.THREE_OF_A_KIND;
        if (isTwoPair(combined)) return HandType.TWO_PAIR;
        if (isOnePair(combined)) return HandType.ONE_PAIR;

        return HandType.HIGH_CARD;
    }

    private static boolean isStraightFlush(List<Card> cards) {
        return isFlush(cards) && isStraight(cards);
    }

    private static boolean isFourOfAKind(List<Card> cards) {
        return hasSameRank(cards, 4);
    }

    private static boolean isFullHouse(List<Card> cards) {
        return hasSameRank(cards, 3) && hasSameRank(cards, 2);
    }

    private static boolean isFlush(List<Card> cards) {
        Map<Character, List<Card>> suitMap = new HashMap<>();
        for (Card card : cards) {
            suitMap.putIfAbsent(card.getSuit(), new ArrayList<>());
            suitMap.get(card.getSuit()).add(card);
            if (suitMap.get(card.getSuit()).size() >= 5) {
                return true;
            }
        }
        return false;
    }

    private static boolean isStraight(List<Card> cards) {
        Set<Integer> uniqueRanks = new TreeSet<>();
        for (Card card : cards) {
            uniqueRanks.add(card.getRank());
        }

        if (uniqueRanks.size() < 5) {
            return false;
        }

        List<Integer> sortedRanks = new ArrayList<>(uniqueRanks);

        for (int i = 0; i <= sortedRanks.size() - 5; i++) {
            if (sortedRanks.get(i + 4) - sortedRanks.get(i) == 4) {
                return true;
            }
        }

        if (sortedRanks.contains(12) && sortedRanks.subList(0, 4).equals(Arrays.asList(0, 1, 2, 3))) {
            return true;
        }

        return false;
    }

    private static boolean isThreeOfAKind(List<Card> cards) {
        return hasSameRank(cards, 3);
    }

    private static boolean isTwoPair(List<Card> cards) {
        int pairs = 0;
        Map<Integer, Integer> rankMap = new HashMap<>();
        for (Card card : cards) {
            rankMap.put(card.getRank(), rankMap.getOrDefault(card.getRank(), 0) + 1);
        }
        for (int count : rankMap.values()) {
            if (count == 2) {
                pairs++;
            }
        }
        return pairs >= 2;
    }

    private static boolean isOnePair(List<Card> cards) {
        return hasSameRank(cards, 2);
    }

    private static boolean hasSameRank(List<Card> cards, int count) {
        Map<Integer, Integer> rankMap = new HashMap<>();
        for (Card card : cards) {
            rankMap.put(card.getRank(), rankMap.getOrDefault(card.getRank(), 0) + 1);
        }
        return rankMap.containsValue(count);
    }

    public static int compareHands(List<Card> hand1, HandType handType1, List<Card> hand2, HandType handType2) {
        int comparison = handType1.compareTo(handType2);
        if (comparison != 0) {
            return comparison;
        }

        switch (handType1) {
            case STRAIGHT_FLUSH:
                return compareStraightFlush(hand1, hand2);
            case FOUR_OF_A_KIND:
                return compareByRankFrequency(hand1, hand2, 4);
            case FULL_HOUSE:
                return compareByRankFrequency(hand1, hand2, 3);
            case FLUSH:
                return compareHighestCard(hand1, hand2);
            case STRAIGHT:
                return compareStraight(hand1, hand2);
            case THREE_OF_A_KIND:
                return compareByRankFrequency(hand1, hand2, 3);
            case TWO_PAIR:
            case ONE_PAIR:
                return compareByRankFrequency(hand1, hand2, 2);
            case HIGH_CARD:
                return compareHighestCard(hand1, hand2);
            default:
                return 0;
        }
    }

    private static int compareStraightFlush(List<Card> hand1, List<Card> hand2) {
        return compareStraight(hand1, hand2);
    }

    private static int compareStraight(List<Card> hand1, List<Card> hand2) {
        int highCard1 = getHighestCardInStraight(hand1);
        int highCard2 = getHighestCardInStraight(hand2);
        return Integer.compare(highCard1, highCard2);
    }

    private static int getHighestCardInStraight(List<Card> hand) {
        Set<Integer> uniqueRanks = new TreeSet<>();
        for (Card card : hand) {
            uniqueRanks.add(card.getRank());
        }

        List<Integer> sortedRanks = new ArrayList<>(uniqueRanks);

        for (int i = sortedRanks.size() - 1; i >= 4; i--) {
            if (sortedRanks.get(i) - sortedRanks.get(i - 4) == 4) {
                return sortedRanks.get(i);
            }
        }

        if (sortedRanks.contains(12) && sortedRanks.subList(0, 4).equals(Arrays.asList(0, 1, 2, 3))) {
            return 3;
        }

        return sortedRanks.get(sortedRanks.size() - 1);
    }

    private static int compareHighestCard(List<Card> hand1, List<Card> hand2) {
        for (int i = 0; i < hand1.size(); i++) {
            int comparison = hand1.get(i).getRank() - hand2.get(i).getRank();
            if (comparison != 0) {
                return comparison;
            }
        }
        return 0;
    }

    private static int compareByRankFrequency(List<Card> hand1, List<Card> hand2, int frequency) {
        int rank1 = getHighestRankByFrequency(hand1, frequency);
        int rank2 = getHighestRankByFrequency(hand2, frequency);
        return Integer.compare(rank1, rank2);
    }

    private static int getHighestRankByFrequency(List<Card> hand, int frequency) {
        return hand.stream().filter(c -> Collections.frequency(hand, c.getRank()) == frequency)
                .map(Card::getRank).max(Integer::compare).orElse(0);
    }
}
