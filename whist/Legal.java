import java.util.ArrayList;
import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

public class Legal implements NPC {
	// select a card that constitutes a legal move, i.e., a move that does not violate the constraints of the game
	public Card move(Hand hand, Whist.Suit lead, int currPlayer, int playerSeqNo, Whist.Suit trump) {
		// for the player starting the round, play a random card
		if (lead == null) {
			return playRandomMove(hand);
		}
		
		// if lead suit present in hand
		if (hand.getNumberOfCardsWithSuit(lead) > 0) {
			// get all cards that belong to the lead suit
			ArrayList<Card> cardsWithLeadSuit = new ArrayList<Card>();
			for (int i = 0; i < hand.getNumberOfCards(); i++) {
				Card c = hand.get(i);
				
				if (c.getSuit() == lead) {
					cardsWithLeadSuit.add(c);
				}
			}
			
			// play a random card from the lead suit cards
			return cardsWithLeadSuit.get(Whist.random.nextInt(cardsWithLeadSuit.size()));
		}
		
		// if lead suit not present in hand, play a random card
		return playRandomMove(hand);
	}
	
	// select a random card from hand
	private Card playRandomMove(Hand hand) {
		return Whist.randomCard(hand);
	}
}