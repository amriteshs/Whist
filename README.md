# Whist
The project simulates the Whist card game, and has been developed in Java by utilizing the JCardGame library addon.  

There are 4 types of players:  
1. **Interactive**: Human player, plays a card by double-clicking on it  
2. **Random**: Randomly plays any card, even if it breaks the rules of the games  
3. **Legal**: Randomly plays any card, as long as it is within the confines of the game rules  
4. **Smart**: Plays a card according to a strategy that aims at winning the round  

### Information Pool
Two types of information pool have been created for each player:  
1. **Historical pool**: This pool stores the cards (by suit and rank) played by each player and whether the player still has a card of a particular suit remaining in his hand. The data structure used for the pool is HashMap<Integer, HashMap<Suit, Pair<Boolean, ArrayList<Integer> > > >. Here, Integer represents the player ID, Suit represents the suit for each player, Boolean represents whether a player has a particular suit (false if player does not have a suit), and ArrayList<Integer> represents the list of ranks for a suit.  
2. **Round pool**: This pool stores the cards (by suit and rank) played by each player in the ongoing (current) round. The data structure used for the pool is HashMap<Suit, ArrayList<Integer> >. Here, Suit represents the suit, and ArrayList<Integer> represents the list of ranks for a suit.  
  
### Smart Player Strategy
Here, players have been referred to by their sequence number for the round. Additionally, a card has been termed here as high or low on the basis of its rank.  
1. **Player 1**  
Play highest unplayed card (still remaining in the deck) of trump suit (if present)  
ELSE play highest unplayed card (still remaining in the deck) of non-trump suit (if present)  
ELSE play lowest card from a non-trump suit  
ELSE play lowest card of trump suit  
2. **Players 2 and 3**  
IF lead suit present  
Play highest unplayed card (still remaining in the deck) of lead suit (if present)  
ELSE play lowest card of lead suit  
ELSE IF trump suit present, play lowest card of trump suit  
ELSE play lowest card from a non-trump suit  
3. **Player 4**  
IF lead suit present  
Play lowest card to win of lead suit  
ELSE play lowest card of lead suit  
ELSE IF trump suit present, play lowest card to win of trump suit  
ELSE play lowest card from a non-trump suit  
