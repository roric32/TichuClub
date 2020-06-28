package com.tichuclub.tichuclub

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.tichuclub.tichuclub.CardCombination as CardCombination

abstract class Character(open val characterName: String, open var game: TichuGame) : Actor() {

    lateinit var position: Position
    lateinit var partner: Position
    lateinit var leftOpponent: Position
    lateinit var rightOpponent: Position

    abstract var expressions: Map<Expression, Sprite>
    open var currentSprite: Sprite = Sprite(Texture(Gdx.files.internal("silhouette.png")))
    open var currentExpression = Expression.NORMAL

    var calledTichu: Boolean = false
    var calledGrand: Boolean = false

    open val tolerance: Int = 55

    open val isHuman = false
    var isOut = false
    var hand = ArrayList<Card>()
    var pendingPassCards = HashMap<Position, Card>()

    var passedCards = ArrayList<Card>()
    val cardsWon = ArrayList<Card>()

    init {
        touchable = Touchable.enabled
    }

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

    open fun evaluatePass(hand: ArrayList<Card>) {
        this.pendingPassCards[leftOpponent] = hand[0]
        this.pendingPassCards[partner] = hand[13]
        this.pendingPassCards[rightOpponent] = hand[1]
    }

    open fun speak(dialog: String) {
        println("$characterName says: \"$dialog\"")
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

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if(batch !== null) {
            batch.draw(currentSprite, currentSprite.x, currentSprite.y, currentSprite.originX, currentSprite.originY, currentSprite.width, currentSprite.height, currentSprite.scaleX, currentSprite.scaleY, currentSprite.rotation)
        }
    }

    override fun setRotation(degrees: Float) {
        currentSprite.rotation = degrees
        super.setRotation(degrees)
    }

    override fun rotateBy(amountInDegrees: Float) {
        currentSprite.rotation += amountInDegrees
    }

    override fun setSize(width: Float, height: Float) {
        currentSprite.setSize(width, height)
        super.setSize(width, height)
    }

    override fun setPosition(x: Float, y: Float) {
        currentSprite.setPosition(x, y)
        super.setPosition(x, y)
    }

    override fun positionChanged() {
        super.positionChanged()
        currentSprite.setPosition(x, y)
    }

}

class Player(override var characterName : String, override var game: TichuGame) : Character(characterName, game) {

    override lateinit var expressions: Map<Expression, Sprite>

    init {
        this.name = characterName
        this.expressions = Config.getCharacterSprites(this)
    }

    override val isHuman = true

}

class Zach(override var characterName : String, override var game: TichuGame) : Character(characterName, game){

    override val tolerance: Int = 65
    override var expressions: Map<Expression, Sprite> = Config.getCharacterSprites(this)
    override var currentSprite: Sprite = expressions[Expression.NORMAL] ?: throw Exception("Exception: Expression NORMAL not found for character: ${this.characterName}}")

    init {
        this.name = characterName
        setBounds(currentSprite.x,currentSprite.y,currentSprite.width,currentSprite.height);
    }

    override fun speak(dialog: String) {
        super.speak(dialog)
    }

    override fun evaluatePass(hand: ArrayList<Card>) {
        super.evaluatePass(hand)
    }

}

class Thong(override var characterName : String, override var game: TichuGame) : Character(characterName, game){

    override val tolerance: Int = 65
    override var expressions: Map<Expression, Sprite> = Config.getCharacterSprites(this)
    override var currentSprite: Sprite = expressions[Expression.NORMAL] ?: throw Exception("Exception: Expression NORMAL not found for character: ${this.characterName}}")

    init {
        this.name = characterName
        setBounds(currentSprite.x,currentSprite.y,currentSprite.width,currentSprite.height);
    }


    override fun speak(dialog: String) {
        super.speak(dialog)
    }


}

class Brandon(override var characterName : String, override var game: TichuGame) : Character(characterName, game){

    override val tolerance: Int = 85
    override var expressions: Map<Expression, Sprite> = Config.getCharacterSprites(this)

    init {
        this.name = characterName
        setBounds(currentSprite.x,currentSprite.y,currentSprite.width,currentSprite.height);
    }


    override fun speak(dialog: String) {
        super.speak(dialog)
    }

}

class Nate(override var characterName : String, override var game: TichuGame) : Character(characterName, game){

    override val tolerance: Int = 70
    override var expressions: Map<Expression, Sprite> = Config.getCharacterSprites(this)
    override var currentSprite: Sprite = expressions[Expression.NORMAL] ?: throw Exception("Exception: Expression NORMAL not found for character: ${this.characterName}}")

    init {
        this.name = characterName
        setBounds(currentSprite.x,currentSprite.y,currentSprite.width,currentSprite.height);
    }

    override fun speak(dialog: String) {
        super.speak(dialog)
    }

}

class Leasha(override var characterName : String, override var game: TichuGame) : Character(characterName, game){

    override var expressions: Map<Expression, Sprite> = Config.getCharacterSprites(this)

    init {
        this.name = characterName
        setBounds(currentSprite.x,currentSprite.y,currentSprite.width,currentSprite.height);
    }

    override val tolerance: Int = 70

    override fun speak(dialog: String) {
        super.speak(dialog)
    }

}

class Squire(override var characterName : String, override var game: TichuGame) : Character(characterName, game){

    override var expressions: Map<Expression, Sprite> = Config.getCharacterSprites(this)

    init {
        this.name = characterName
        setBounds(currentSprite.x,currentSprite.y,currentSprite.width,currentSprite.height);
    }

    override val tolerance: Int = 68

    override fun speak(dialog: String) {
        super.speak(dialog)
    }

}

class James(override var characterName : String, override var game: TichuGame) : Character(characterName, game){

    override var expressions: Map<Expression, Sprite> = Config.getCharacterSprites(this)

    init {
        this.name = characterName
        setBounds(currentSprite.x,currentSprite.y,currentSprite.width,currentSprite.height);
    }

    override val tolerance: Int = 68

    override fun speak(dialog: String) {
        super.speak(dialog)
    }

}

class Rachel(override var characterName : String, override var game: TichuGame) : Character(characterName, game){

    override var expressions: Map<Expression, Sprite> = Config.getCharacterSprites(this)

    init {
        this.name = characterName
        setBounds(currentSprite.x,currentSprite.y,currentSprite.width,currentSprite.height);
    }

    override val tolerance: Int = 78

    override fun speak(dialog: String) {
        super.speak(dialog)
    }

}

class Catherine(override var characterName : String, override var game: TichuGame) : Character(characterName, game){

    override var expressions: Map<Expression, Sprite> = Config.getCharacterSprites(this)

    init {
        this.name = characterName
        setBounds(currentSprite.x,currentSprite.y,currentSprite.width,currentSprite.height);
    }

    override val tolerance: Int = 78

    override fun speak(dialog: String) {
        super.speak(dialog)
    }

}

class Alex(override var characterName : String, override var game: TichuGame) : Character(characterName, game){

    override var expressions: Map<Expression, Sprite> = Config.getCharacterSprites(this)

    init {
        this.name = characterName
        setBounds(currentSprite.x,currentSprite.y,currentSprite.width,currentSprite.height);
    }

    override val tolerance: Int = 80

    override fun speak(dialog: String) {
        super.speak(dialog)
    }

}

class Mary(override var characterName : String, override var game: TichuGame) : Character(characterName, game){

    override var expressions: Map<Expression, Sprite> = Config.getCharacterSprites(this)

    init {
        this.name = characterName
        setBounds(currentSprite.x,currentSprite.y,currentSprite.width,currentSprite.height);
    }

    override val tolerance: Int = 80

    override fun speak(dialog: String) {
        super.speak(dialog)
    }

}

