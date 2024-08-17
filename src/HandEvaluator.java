import java.util.*;
import java.util.stream.Collectors;

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

    public static int compareHands(List<Card> hand1, HandType handType1, List<Card> hand2, HandType handType2) {
        int typeComparison = handType1.compareTo(handType2);
        if (typeComparison != 0) {
            return typeComparison;
        }

        switch (handType1) {
            case STRAIGHT_FLUSH:
                return compareStraightFlush(hand1, hand2);
            case FOUR_OF_A_KIND:
                return compareFourOfAKind(hand1, hand2);
            case FULL_HOUSE:
                return compareFullHouse(hand1, hand2);
            case FLUSH:
                return compareFlush(hand1, hand2);
            case STRAIGHT:
                return compareStraight(hand1, hand2);
            case THREE_OF_A_KIND:
                return compareThreeOfAKind(hand1, hand2);
            case TWO_PAIR:
                return compareTwoPair(hand1, hand2);
            case ONE_PAIR:
                return compareOnePair(hand1, hand2);
            case HIGH_CARD:
                return compareHighestCard(hand1, hand2);
            default:
                return 0;
        }
    }

    private static boolean isStraightFlush(List<Card> hand) {
        return isFlush(hand) && isStraight(hand);
    }

    private static boolean isFourOfAKind(List<Card> hand) {
        return getRankWithFrequency(hand, 4) != -1;
    }

    private static boolean isFullHouse(List<Card> hand) {
        return getRankWithFrequency(hand, 3) != -1 && getRankWithFrequency(hand, 2) != -1;
    }

    private static boolean isFlush(List<Card> hand) {
        return hand.stream().collect(Collectors.groupingBy(Card::getSuit, Collectors.counting())).size() == 1;
    }

    private static boolean isStraight(List<Card> hand) {
        List<Integer> ranks = hand.stream().map(Card::getRank).distinct().sorted().collect(Collectors.toList());
        if (ranks.size() < 5) return false;

        for (int i = 0; i <= ranks.size() - 5; i++) {
            if (ranks.get(i + 4) - ranks.get(i) == 4) return true;
        }

        if (ranks.contains(12) && ranks.subList(0, 4).equals(Arrays.asList(0, 1, 2, 3))) {
            return true;
        }

        return false;
    }

    private static boolean isThreeOfAKind(List<Card> hand) {
        return getRankWithFrequency(hand, 3) != -1;
    }

    private static boolean isTwoPair(List<Card> hand) {
        List<Long> pairs = hand.stream()
                .collect(Collectors.groupingBy(Card::getRank, Collectors.counting()))
                .values().stream().filter(count -> count == 2).collect(Collectors.toList());
        return pairs.size() >= 2;
    }

    private static boolean isOnePair(List<Card> hand) {
        return getRankWithFrequency(hand, 2) != -1;
    }

    private static int compareStraightFlush(List<Card> hand1, List<Card> hand2) {
        return compareStraight(hand2, hand1);
    }

    private static int compareFourOfAKind(List<Card> hand1, List<Card> hand2) {
        int rank1 = getRankWithFrequency(hand1, 4);
        int rank2 = getRankWithFrequency(hand2, 4);
        if (rank1 != rank2) {
            return Integer.compare(rank1, rank2);
        }
        return compareKicker(hand2, hand1, Arrays.asList(rank2));
    }

    private static int compareFullHouse(List<Card> hand1, List<Card> hand2) {
        int threeOfAKindRank1 = getRankWithFrequency(hand1, 3);
        int threeOfAKindRank2 = getRankWithFrequency(hand2, 3);
        if (threeOfAKindRank1 != threeOfAKindRank2) {
            return Integer.compare(threeOfAKindRank1, threeOfAKindRank2);
        }
        int pairRank1 = getRankWithFrequency(hand1, 2);
        int pairRank2 = getRankWithFrequency(hand2, 2);
        return Integer.compare(pairRank2, pairRank1);
    }

    private static int compareFlush(List<Card> hand1, List<Card> hand2) {
        return compareHighestCard(hand2, hand1);
    }

    private static int compareStraight(List<Card> hand1, List<Card> hand2) {
        int highCard1 = getHighestCardInStraight(hand1);
        int highCard2 = getHighestCardInStraight(hand2);
        return Integer.compare(highCard2, highCard1);
    }

    private static int compareThreeOfAKind(List<Card> hand1, List<Card> hand2) {
        return compareByRankFrequency(hand2, hand1, 3);
    }

    private static int compareTwoPair(List<Card> hand1, List<Card> hand2) {
        int highPair1 = getHighestRankByFrequency(hand1, 2);
        int highPair2 = getHighestRankByFrequency(hand2, 2);
        if (highPair1 != highPair2) {
            return Integer.compare(highPair2, highPair1);
        }

        List<Card> remainingHand1 = removeRank(hand1, highPair1);
        List<Card> remainingHand2 = removeRank(hand2, highPair2);

        int lowPair1 = getHighestRankByFrequency(remainingHand1, 2);
        int lowPair2 = getHighestRankByFrequency(remainingHand2, 2);
        if (lowPair1 != lowPair2) {
            return Integer.compare(lowPair2, lowPair1);
        }

        return compareHighestCard(remainingHand1, remainingHand2);
    }

    private static int compareOnePair(List<Card> hand1, List<Card> hand2) {
        return compareByRankFrequency(hand2, hand1, 2);
    }

    private static int compareHighestCard(List<Card> hand1, List<Card> hand2) {
        for (int i = 0; i < hand1.size(); i++) {
            int comparison = Integer.compare(hand2.get(i).getRank(), hand1.get(i).getRank());
            if (comparison != 0) {
                return comparison;
            }
        }
        return 0;
    }

    private static int getRankWithFrequency(List<Card> hand, int frequency) {
        Map<Integer, Integer> rankCount = hand.stream()
                .collect(Collectors.groupingBy(Card::getRank, Collectors.summingInt(e -> 1)));
        return rankCount.entrySet().stream()
                .filter(entry -> entry.getValue() == frequency)
                .map(Map.Entry::getKey)
                .findFirst().orElse(-1);
    }

    private static List<Card> removeRank(List<Card> hand, int rank) {
        return hand.stream().filter(card -> card.getRank() != rank).collect(Collectors.toList());
    }

    private static int compareByRankFrequency(List<Card> hand1, List<Card> hand2, int frequency) {
        int rank1 = getRankWithFrequency(hand1, frequency);
        int rank2 = getRankWithFrequency(hand2, frequency);
        if (rank1 != rank2) {
            return Integer.compare(rank1, rank2);
        }
        List<Card> remainingHand1 = removeRank(hand1, rank1);
        List<Card> remainingHand2 = removeRank(hand2, rank2);
        return compareHighestCard(remainingHand2, remainingHand1);
    }

    private static int getHighestCardInStraight(List<Card> hand) {
        Set<Integer> uniqueRanks = new TreeSet<>();
        for (Card card : hand) {
            uniqueRanks.add(card.getRank());
        }

        List<Integer> sortedRanks = new ArrayList<>(uniqueRanks);

        for (int i = 0; i <= sortedRanks.size() - 5; i++) {
            if (sortedRanks.get(i + 4) - sortedRanks.get(i) == 4) {
                return sortedRanks.get(i + 4);
            }
        }

        if (sortedRanks.contains(12) && sortedRanks.subList(0, 4).equals(Arrays.asList(0, 1, 2, 3))) {
            return 3;
        }

        return sortedRanks.get(sortedRanks.size() - 1);
    }

    private static int getHighestRankByFrequency(List<Card> hand, int frequency) {
        return hand.stream()
                .collect(Collectors.groupingBy(Card::getRank, Collectors.summingInt(e -> 1)))
                .entrySet().stream()
                .filter(entry -> entry.getValue() == frequency)
                .map(Map.Entry::getKey)
                .max(Integer::compareTo)
                .orElse(-1);
    }

    private static int compareKicker(List<Card> hand1, List<Card> hand2, List<Integer> excludedRanks) {
        List<Integer> kickers1 = hand1.stream()
                .filter(card -> !excludedRanks.contains(card.getRank()))
                .map(Card::getRank)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        List<Integer> kickers2 = hand2.stream()
                .filter(card -> !excludedRanks.contains(card.getRank()))
                .map(Card::getRank)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        for (int i = 0; i < Math.min(kickers1.size(), kickers2.size()); i++) {
            int comparison = Integer.compare(kickers1.get(i), kickers2.get(i));
            if (comparison != 0) {
                return comparison;
            }
        }

        return 0;
    }
}
