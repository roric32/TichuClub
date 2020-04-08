import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture

class Deck(preShuffle: Boolean = false) {

    private val suits = Suit.values().filter{it.toString() !== "NONE"}
    private val values = intArrayOf(2,3,4,5,6,7,8,9,10,11,12,13,14)
    private val cards = ArrayList<Card>()

    /**
     * Create the deck.
     */
    init {
        for(value in values) {
            for(suit in suits) {
                cards.add(NumericCard(suit, value, Texture(Gdx.files.internal("card.png"))))
            }
        }

        //Setup the special cards.
        cards.add(SpecialCard(Suit.NONE, 0, Texture(Gdx.files.internal("card.png")), "DOG"))
        cards.add(SpecialCard(Suit.NONE, 15, Texture(Gdx.files.internal("card.png")), "PHOENIX"))
        cards.add(SpecialCard(Suit.NONE, 16, Texture(Gdx.files.internal("card.png")), "DRAGON"))
        cards.add(SpecialCard(Suit.NONE, 1, Texture(Gdx.files.internal("card.png")), "SPARROW"))

        if(preShuffle) {
            shuffle()
        }
    }

    /**
     * Shuffle the cards.
     */
    fun shuffle() : Unit {
        cards.shuffle()
    }

    /**
     * Deal 14 cards to each player. x here is the player number.
     * @param players - PlayerOverlord
     */
    fun deal(players: PlayerOverlord) : Unit {

        var x = 0

        val playerArray = players.getCharactersAsList()

        while(!cards.isEmpty()) {
            for(i in 0..13) playerArray[x].hand.add(cards.removeAt(0))
            x++
        }

    }

    /**
     * Returns the deck. Useful for Unit Testing.
     */
    fun get() : ArrayList<Card> {
        return cards
    }

}