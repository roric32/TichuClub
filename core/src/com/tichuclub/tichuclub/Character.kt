package com.tichuclub.tichuclub

import com.tichuclub.tichuclub.CardCombination as CardCombination

abstract class Character(open var name : String, open var game: TichuGame) {

    lateinit var position: Position
    lateinit var partner: Position
    lateinit var leftOpponent: Position
    lateinit var rightOpponent: Position

    var calledTichu: Boolean = false
    var calledGrand: Boolean = false

    open val tolerance: Int = 55

    open val isHuman = false
    var isOut = false
    var hand = ArrayList<Card>()
    var passedCards = ArrayList<Card>()
    val cardsWon = ArrayList<Card>()

    open fun wantsToCall(grand : Boolean) : Boolean {
        return if(grand) evaluateGrandTichu() > tolerance else evaluateTichu() > tolerance
    }

    fun evaluateGrandTichu() : Int {

        //Weigh the cards by value
        var handValue : Int = 0

        for(card in hand.take(8)) {
            handValue += card.value
        }

        //TODO: Figure out hand strength with bombs and combinations

        return Math.round(handValue/113.toDouble()*100).toInt()

    }

    /**
     * TODO: Distinct logic
     */
    fun evaluateTichu() : Int {
        return evaluateGrandTichu()
    }

    fun getValidCombinations(type: Combination?, lastValue: Int?) : List<CardCombination> {
        val ca = CardAnalyzer(game.deck)
        return if(type == null) ca.getCombinations(hand).getAll() else ca.getCombinations(hand).get(type).filter{ it.getValue() > lastValue!! }
    }

    open fun evaluatePass(hand: ArrayList<Card>) : List<Card> {
        return listOf(hand[0], hand[13], hand[1])
    }

    open fun speak(dialog: String) {
        println("$name says: \"$dialog\"")
    }

    open fun getAnalysis(hand: ArrayList<Card>) : CardAnalysis {
        val ca = CardAnalyzer(game.deck)
        return ca.getCombinations(hand)
    }

    open fun playFirst(roundStart: Boolean = false) : CardCombination {

        val ret : CardCombination
        val validPlays : List<CardCombination> = getValidCombinations(null, null)
        ret = validPlays.shuffled()[0]
        removeCardsFromHand(ret)

        return ret

    }

    open fun play(type: Combination, lastValue: Int) : CardCombination? {

        var ret : CardCombination? = null

        val validPlays : List<CardCombination> = getValidCombinations(type, lastValue)

        //TODO: Actually do logic here
        if(validPlays.isNotEmpty()) {
            ret = validPlays.shuffled()[0]
            removeCardsFromHand(ret)
        }

        return ret
    }

    fun removeCardsFromHand(cards: CardCombination) {
        hand.removeAll(cards.cards)
    }

}

class Player(override var name : String, override var game: TichuGame) : Character(name, game) {

    override val isHuman = true

    override fun speak(dialog: String) {
        super.speak(dialog)
    }

}

class Zach(override var name : String, override var game: TichuGame) : Character(name, game){

    override val tolerance: Int = 70

    override fun speak(dialog: String) {
        super.speak(dialog)
    }

    override fun evaluatePass(hand: ArrayList<Card>) : List<Card> {
        val analysis = super.getAnalysis(hand)
        return listOf(hand[0], hand[13], hand[1])
        //TODO: analyze pass
    }

}

class Thong(override var name : String, override var game: TichuGame) : Character(name, game){

    override val tolerance: Int = 65

    override fun speak(dialog: String) {
        super.speak(dialog)
    }

    override fun evaluatePass(hand: ArrayList<Card>) : List<Card> {
        val analysis = super.getAnalysis(hand)
        return listOf(hand[0], hand[13], hand[1])
        //TODO: analyze pass
    }

}

class Brandon(override var name : String, override var game: TichuGame) : Character(name, game){

    override val tolerance: Int = 85

    override fun speak(dialog: String) {
        super.speak(dialog)
    }

}

class Nate(override var name : String, override var game: TichuGame) : Character(name, game){

    override val tolerance: Int = 70

    override fun speak(dialog: String) {
        super.speak(dialog)
    }

    override fun evaluatePass(hand: ArrayList<Card>) : List<Card> {
        val analysis = super.getAnalysis(hand)
        return listOf(hand[0], hand[13], hand[1])
        //TODO: analyze pass
    }

}

class Leasha(override var name : String, override var game: TichuGame) : Character(name, game){

    override val tolerance: Int = 70

    override fun speak(dialog: String) {
        super.speak(dialog)
    }

}

class Squire(override var name : String, override var game: TichuGame) : Character(name, game){

    override val tolerance: Int = 68

    override fun speak(dialog: String) {
        super.speak(dialog)
    }

}

class James(override var name : String, override var game: TichuGame) : Character(name, game){

    override val tolerance: Int = 68

    override fun speak(dialog: String) {
        super.speak(dialog)
    }

}

class Rachel(override var name : String, override var game: TichuGame) : Character(name, game){

    override val tolerance: Int = 78

    override fun speak(dialog: String) {
        super.speak(dialog)
    }

}

class Catherine(override var name : String, override var game: TichuGame) : Character(name, game){

    override val tolerance: Int = 78

    override fun speak(dialog: String) {
        super.speak(dialog)
    }

}

class Alex(override var name : String, override var game: TichuGame) : Character(name, game){

    override val tolerance: Int = 80

    override fun speak(dialog: String) {
        super.speak(dialog)
    }

}

class Mary(override var name : String, override var game: TichuGame) : Character(name, game){

    override val tolerance: Int = 80

    override fun speak(dialog: String) {
        super.speak(dialog)
    }

}

