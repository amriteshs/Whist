import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

//interface for NPCs
public interface NPC {

	public Card move(Hand hand, Whist.Suit lead, int currPlayer, int playerSeqNo, Whist.Suit trump); 
		
}
