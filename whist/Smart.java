import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Collectors;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

public class Smart implements NPC {
	// select a card that constitutes a smart move
	public Card move(Hand hand, Whist.Suit lead, int currPlayer, int playerSeqNo, Whist.Suit trump) {
		HashMap<Integer, HashMap<Whist.Suit, Pair<Boolean, ArrayList<Integer>>>> infoPoolHistorical;
		HashMap<Whist.Suit, ArrayList<Integer> > infoPoolRound;
		
		// select the round and historical information pools for the current player
		// each player uses his own information pool
		if (currPlayer == 0) {
			infoPoolHistorical = Whist.player1Info;
			
			infoPoolRound = Whist.currentRoundInfo1;
		} else if (currPlayer == 1) {
			infoPoolHistorical = Whist.player2Info;
			infoPoolRound = Whist.currentRoundInfo2;
		} else if (currPlayer == 2) {
			infoPoolHistorical = Whist.player3Info;
			infoPoolRound = Whist.currentRoundInfo3;
		} else {
			infoPoolHistorical = Whist.player4Info;
			infoPoolRound = Whist.currentRoundInfo4;
		}
		
		// get the list of all cards that have been played in the game till now
		HashMap<Whist.Suit, ArrayList<Integer> > allCardsPlayed = new HashMap<Whist.Suit, ArrayList<Integer> >();
		
		for (int p = 0; p < 4; p++) {
			HashMap<Whist.Suit, Pair<Boolean, ArrayList<Integer>>> playerCardsPlayed = infoPoolHistorical.get(p);
			
			for (Whist.Suit s : playerCardsPlayed.keySet()) {
				ArrayList<Integer> suitRanks = playerCardsPlayed.get(s).getSecond();
				
				if (allCardsPlayed.containsKey(s)) {
					ArrayList<Integer> temp = allCardsPlayed.get(s);
					temp.addAll(suitRanks);
					allCardsPlayed.replace(s, temp);
				} else {
					allCardsPlayed.put(s, suitRanks);
				}
			}
		}
		
		for (Whist.Suit s : allCardsPlayed.keySet()) {
			ArrayList<Integer> temp = new ArrayList<Integer>(allCardsPlayed.get(s).stream().distinct().collect(Collectors.toList()));
			Collections.sort(temp);
			allCardsPlayed.replace(s, temp);
		}
		
		Card c;
		
		if (playerSeqNo == 0) {		// if the current player plays the first move of the round
			// play greatest rank card of trump suit still remaining in the deck (if present)
			c = getCardWithGreatestUnplayedRankforSuit(hand, trump, allCardsPlayed);
			
			if (c != null) {
				return c;
			}
			
			// ELSE play greatest rank card from a non-trump suit still remaining in the deck (if present)
			ArrayList<Whist.Suit> nonTrump = getNonTrumpSuits(trump);
			
			for (Whist.Suit nt : nonTrump) {
				c = getCardWithGreatestUnplayedRankforSuit(hand, nt, allCardsPlayed);
				
				if (c != null) {
					return c;
				}
			}
			
			// ELSE play lowest card from a non-trump suit
			ArrayList<Card> lowRankCards = new ArrayList<Card>();
			ArrayList<Integer> lowRanks = new ArrayList<Integer>();
			for (Whist.Suit nt : nonTrump) {
				c = getCardWithLowestRankforSuit(hand, nt);
				
				if (c != null) {
					lowRankCards.add(c);
					lowRanks.add(c.getRankId());
				}
			}
		
			if (!lowRankCards.isEmpty()) {
				return lowRankCards.get(lowRanks.indexOf(Collections.min(lowRanks)));
			}
			
			// ELSE play lowest card of trump suit
			c = getCardWithLowestRankforSuit(hand, trump);
			
			if (c != null) {
				return c;
			}
		} else if (playerSeqNo == 1 || playerSeqNo == 2) {	// if the current player plays the second or third move of the round
			// IF lead suit present
			if (lead != null) {
				// play greatest rank card of lead suit still remaining in the deck (if present)
				c = getCardWithGreatestUnplayedRankforSuit(hand, lead, allCardsPlayed);

				if (infoPoolRound.containsKey(lead)) {
					if (!infoPoolRound.get(lead).isEmpty() && c != null) {
						if (c.getRankId() > infoPoolRound.get(lead).get(0)) {
							c = null;
						}
					}
				}
				
				if (c != null) {
					return c;
				}
				
				// ELSE play lowest card of lead suit
				c = getCardWithLowestRankforSuit(hand, lead);
				
				if (c != null) {
					return c;
				}
			}
			
			// ELSE IF trump suit present, play lowest card of trump suit
			if (trump != lead) {
				c = getCardWithLowestRankforSuit(hand, trump);
				
				if (c != null) {
					return c;
				}
			}
			
			// ELSE play lowest card from a non-trump suit
			ArrayList<Whist.Suit> nonTrump = getNonTrumpSuits(trump);
			
			ArrayList<Card> lowRankCards = new ArrayList<Card>();
			ArrayList<Integer> lowRanks = new ArrayList<Integer>();
			for (Whist.Suit nt : nonTrump) {
				c = getCardWithLowestRankforSuit(hand, nt);
				
				if (c != null) {
					lowRankCards.add(c);
					lowRanks.add(c.getRankId());
				}
			}
		
			if (!lowRankCards.isEmpty()) {
				return lowRankCards.get(lowRanks.indexOf(Collections.min(lowRanks)));
			}
		} else {	// if the current player plays the last (fourth) move of the round
			// IF lead suit present
			if (lead != null) {
				// play lowest card to win of lead suit 
				c = getCardWithLowestRankToWinforSuit(hand, lead, infoPoolRound);
				
				if (c != null) {
					return c;
				}
				
				// ELSE play lowest card of lead suit
				c = getCardWithLowestRankforSuit(hand, lead);
				
				if (c != null) {
					return c;
				}
			}
			
			// ELSE IF trump suit present, play lowest card to win of trump suit
			if (trump != lead) {
				c = getCardWithLowestRankToWinforSuit(hand, trump, infoPoolRound);
				
				if (c != null) {
					return c;
				}
			}
			
			// ELSE play lowest card from a non-trump suit
			ArrayList<Whist.Suit> nonTrump = getNonTrumpSuits(trump);
			
			ArrayList<Card> lowRankCards = new ArrayList<Card>();
			ArrayList<Integer> lowRanks = new ArrayList<Integer>();
			for (Whist.Suit nt : nonTrump) {
				c = getCardWithLowestRankforSuit(hand, nt);
				
				if (c != null) {
					lowRankCards.add(c);
					lowRanks.add(c.getRankId());
				}
			}
		
			if (!lowRankCards.isEmpty()) {
				return lowRankCards.get(lowRanks.indexOf(Collections.min(lowRanks)));
			}
		}
		
		return playLegalMove(hand, lead);
	}
	
	// select a card from hand that constitutes a legal move
	private Card playLegalMove(Hand hand, Whist.Suit lead) {
		if (lead == null) {
			return playRandomMove(hand);
		}
		
		if (hand.getNumberOfCardsWithSuit(lead) > 0) {
			ArrayList<Card> cardsWithLeadSuit = new ArrayList<Card>();
			for (int i = 0; i < hand.getNumberOfCards(); i++) {
				Card c = hand.get(i);
				
				if (c.getSuit() == lead) {
					cardsWithLeadSuit.add(c);
				}
			}
			
			return cardsWithLeadSuit.get(Whist.random.nextInt(cardsWithLeadSuit.size()));
		}
		
		return playRandomMove(hand);
	}
	
	// select a card at random from hand
	private Card playRandomMove(Hand hand) {
		return Whist.randomCard(hand);
	}
	
	// select the card, with the greatest rank still remaining in the deck, from hand for the given suit
	private Card getCardWithGreatestUnplayedRankforSuit(Hand hand, Whist.Suit suit, HashMap<Whist.Suit, ArrayList<Integer> > allCardsPlayed) {
		if (hand.getNumberOfCardsWithSuit(suit) > 0) {
			Card c = hand.getCardsWithSuit(suit).get(0);
			
			if (c.getRankId() == 0) {
				return c;
			}
			
			for (int i = 1; i < 13; i++) {
				if (allCardsPlayed.containsKey(suit)) {
					if (!allCardsPlayed.get(suit).contains(i)) {
						if (c.getRankId() == i) {
							return c;
						}
					
						break;
					}
				}
			}
		}

		return null;
	}
	
	// select the card, with the lowest rank that would still win the round, from hand for the given suit
	public Card getCardWithLowestRankToWinforSuit(Hand hand, Whist.Suit suit, HashMap<Whist.Suit, ArrayList<Integer> > roundCardsPlayed) {
		if (hand.getNumberOfCardsWithSuit(suit) > 0 && roundCardsPlayed.containsKey(suit)) {
			int roundGreatestRank = roundCardsPlayed.get(suit).get(0);
			ArrayList<Card> cardList = hand.getCardsWithSuit(suit);
			
			for (int i = cardList.size() - 1; i >= 0; i--) {
				if (cardList.get(i).getRankId() < roundGreatestRank) {
					return cardList.get(i);
				}
			}
		}

		return null;
	}
	
	// select the card, with the greatest rank, from hand for the given suit
	private Card getCardWithGreatestRankforSuit(Hand hand, Whist.Suit suit) {
		if (hand.getNumberOfCardsWithSuit(suit) > 0) {
			return hand.getCardsWithSuit(suit).get(0);
		}
		
		return null;
	}
	
	// select the card, with the lowest rank, from hand for the given suit
	private Card getCardWithLowestRankforSuit(Hand hand, Whist.Suit suit) {
		if (hand.getNumberOfCardsWithSuit(suit) > 0) {
			ArrayList<Card> cardList = hand.getCardsWithSuit(suit);
		
			return cardList.get(cardList.size() - 1);
		}
		
		return null;
	}
	
	// get the list of suits that are not the trump suit
	private ArrayList<Whist.Suit> getNonTrumpSuits(Whist.Suit trump) {
		ArrayList<Whist.Suit> suits = new ArrayList<Whist.Suit>();
		
		for (Whist.Suit s : Whist.Suit.class.getEnumConstants()) {
			if (s != trump) {
				suits.add(s);
			}
		}
		
		return suits;
	}
}
