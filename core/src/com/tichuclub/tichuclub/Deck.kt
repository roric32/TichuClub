package com.tichuclub.tichuclub

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.actions.*

class Deck(preShuffle: Boolean = false, atlas: TextureAtlas, tichu: TichuGame) {

    private val suits = Suit.values().filter{it.toString() !== "NONE"}
    private val values = intArrayOf(2,3,4,5,6,7,8,9,10,11,12,13,14)
    private val cards = ArrayList<Card>()
    private var backImage = Sprite()
    var atlas = TextureAtlas()
    var tichu : TichuGame

    /**
     * Create the deck.
     */
    init {
        this.atlas = atlas
        this.tichu = tichu
        for(value in values) {
            for(suit in suits) {
                val cardName = suit.getAbbreviation(suit) + value.toString()
                cards.add(NumericCard(suit, value, Sprite(atlas.findRegion(cardName)), Sprite(atlas.findRegion("CardBack")), tichu))
            }
        }

        //Setup the special cards.
        cards.add(SpecialCard(Suit.NONE, 0, Sprite(atlas.findRegion("DOG")), Sprite(atlas.findRegion("CardBack")), tichu, "DOG"))
        cards.add(SpecialCard(Suit.NONE, 15, Sprite(atlas.findRegion("PHOENIX")), Sprite(atlas.findRegion("CardBack")), tichu, "PHOENIX"))
        cards.add(SpecialCard(Suit.NONE, 16, Sprite(atlas.findRegion("DRAGON")), Sprite(atlas.findRegion("CardBack")), tichu, "DRAGON"))
        cards.add(SpecialCard(Suit.NONE, 1, Sprite(atlas.findRegion("SPARROW")), Sprite(atlas.findRegion("CardBack")), tichu, "SPARROW"))

        if(preShuffle) {
            shuffle()
        }
    }

    /**
     * Shuffle the cards.
     */
    fun shuffle() {
        cards.shuffle()
    }

    fun readyDeck() {

        for(card: Card in cards) {
            card.setPosition((tichu.WORLD_WIDTH/2f) - (tichu.CARD_WIDTH * .5f), (tichu.WORLD_HEIGHT/2f) - (tichu.CARD_HEIGHT * .5f))
            card.setSize(tichu.CARD_WIDTH, tichu.CARD_HEIGHT)
            tichu.stage.addActor(card)
        }

    }

    fun deal() {

        var cardMovesCompleted = 0

        val currentHand = tichu.players.south.hand

        val fourthCard = tichu.players.south.hand.first()

        val startX = fourthCard.x - (tichu.FANNED_CARD_WIDTH * 3)

        val cardGrid = MutableList(14){index -> startX + (index * tichu.FANNED_CARD_WIDTH)}

        val duration = 1.0f

        while(cards.size > 0) {
            for (player in tichu.players.getCharactersAsList()) {
                val card = cards.first()
                player.hand.add(card)
                cards.removeAt(0)
                player.hand.sortBy { it.value }
            }
        }

        for(player in tichu.players.getCharactersAsList()) {

            var x = 0f
            var y = 0f

            for((index, card) in player.hand.withIndex()) {

                when(player.position) {
                    Position.NORTH -> {
                        x = card.x
                        y = 50f
                    }
                    Position.WEST -> {
                        x = -50f
                        y = card.y
                    }
                    Position.EAST -> {
                        x = 50f
                        y = card.y
                    }
                    Position.SOUTH -> {
                        x = cardGrid[index]
                        y = fourthCard.y
                    }
                }

                val moveAction = MoveToAction()
                moveAction.setPosition(x, y)
                moveAction.duration = duration
                val actionChain = SequenceAction(moveAction, (object : RunnableAction() {
                    override fun run() {
                        cardMovesCompleted++
                        if(cardMovesCompleted == 56) {
                            tichu.state.act()
                        }
                    }
                }))
                card.addAction(actionChain)

            }

        }


    }

    /**
     * Deal 8 cards to each player.
     * @param tichu - TichuGame
     */
    fun dealEight() {

        val playerArray = tichu.players.getCharactersAsList()

        //TODO: Look at this when more awake.
        val totalHorizontalPixelsUsed = (tichu.FANNED_CARD_WIDTH) * 7 + tichu.CARD_WIDTH

        //Set up the resolution and stuff.
        val halfScreen: Float = tichu.WORLD_WIDTH / 2f
        var tally : Float = halfScreen - (totalHorizontalPixelsUsed * 0.5f)

        var cardMovesCompleted = 0

        //Each player is dealt 8 cards and then each must decide whether or not to call Grand Tichu.
        while(cards.size > 24) {
            for (player in playerArray) {
                player.hand.add(cards.first())
                cards.removeAt(0)
                player.hand.sortBy { it.value }
            }
        }

        for(player in playerArray) {

            var z = 0

            for(card: Card in player.hand) {

                var x = 0f
                var y = 0f
                var duration = 1f

                card.setSize(tichu.CARD_WIDTH, tichu.CARD_HEIGHT)

                when(player.position) {
                    Position.NORTH -> {
                        x = card.x
                        y = 50f
                    }
                    Position.WEST -> {
                        x = -50f
                        y = card.y
                    }
                    Position.EAST -> {
                        x = 50f
                        y = card.y
                    }
                    Position.SOUTH -> {
                        x = tally
                        y = 0f
                        duration = 0.8f
                        tally += tichu.FANNED_CARD_WIDTH
                        card.zIndex = z++
                    }
                }

                val moveAction = MoveToAction()
                moveAction.setPosition(x, y)
                moveAction.duration = duration

                val actionChain = SequenceAction(moveAction, (object : RunnableAction() {
                    override fun run() {
                        cardMovesCompleted++
                        if(cardMovesCompleted == 24) {

                            tichu.state.act()
                        }
                    }
                }))
                card.addAction(actionChain)

            }

        }

    }


    /**
     * Returns the deck. Useful for Unit Testing.
     */
    fun get() : ArrayList<Card> {
        return cards
    }

    fun createNumericCard(suit: Suit, value: Int) : NumericCard {
        val suitFirstLetter = suit.name.toUpperCase().substring(0,1)
        return NumericCard(suit, value, Sprite(atlas.findRegion(suitFirstLetter + value.toString())), backImage, tichu)
    }

    fun createSpecialCard(suit: Suit, value: Int, name: String) : SpecialCard {
        return SpecialCard(suit, value, Sprite(atlas.findRegion(name)), backImage, tichu, name)
    }

}