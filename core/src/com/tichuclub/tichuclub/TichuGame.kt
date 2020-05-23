package com.tichuclub.tichuclub

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.*
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable
import com.badlogic.gdx.utils.Timer
import com.badlogic.gdx.utils.Timer.Task


enum class TichuType {
    TICHU,
    GRAND_TICHU
}

class TichuGame(val WORLD_WIDTH: Int, val WORLD_HEIGHT: Int, stage: Stage, textStage: Stage, atlas: TextureAtlas) {

    var scoreNS : Int = 0
    var scoreEW : Int = 0
    val CARD_WIDTH : Float = 2f
    val CARD_HEIGHT : Float = 2.9f
    val FANNED_CARD_WIDTH: Float = (CARD_WIDTH/3.7f)
    var eventDispatcher = EventDispatcher(ArrayList())
    val dialogs = DialogOverlord("dialog.json")
    val shuffleSound = Gdx.audio.newMusic(Gdx.files.internal("shuffle.wav"))
    val tichuSound = Gdx.audio.newMusic(Gdx.files.internal("gong.wav"))
    val doubleTichuSound = Gdx.audio.newMusic(Gdx.files.internal("drums.wav"))
    private var endRound = false
    private var delay : Long = 2_000
    var deck = Deck(true, atlas)
    var stage : Stage
    var textStage : Stage
    var state : TichuState = StateGameStart(this)
    val isAndroid = Gdx.app.type == Application.ApplicationType.Android
    val speechBubbleAtlas = TextureAtlas(Gdx.files.internal("speebubb.atlas"))
    val speechBubble = speechBubbleAtlas.createPatch("sbubble")
    val speechBubbleTexture = NinePatchDrawable(speechBubble)
    val bubbles = HashMap<Position, Label>()
    val bannerTexture = Texture(Gdx.files.internal("banner.png"))
    val bannerRegion = TextureRegion(bannerTexture)
    val bannerTile = TiledDrawable(this.bannerRegion)
    val bannerImage = Image(bannerTile)

    lateinit var players: PlayerOverlord
    val WIDTH_UNITS : Float = (Gdx.graphics.width/WORLD_WIDTH).toFloat()
    val HEIGHT_UNITS : Float = (Gdx.graphics.height/WORLD_HEIGHT).toFloat()

    init {
        for(event in TichuEvents.values()) {
            eventDispatcher.addListener(TriggerDialogEventListener(TichuEvent(event), this))
        }
        this.stage = stage
        this.textStage = textStage

        this.bannerImage.width = this.textStage.width
        this.bannerImage.setPosition(-Gdx.graphics.width.toFloat(), (Gdx.graphics.height/2f - this.bannerImage.height/2f))

        this.textStage.addActor(bannerImage)
    }

    fun setUp(players: PlayerOverlord) {

        this.players = players

        val style = Label.LabelStyle(Config.getFont(12, 1, Color.BLACK, "truetypefont/Amble-Regular.ttf"), Color.BLACK)
        style.background = this.speechBubbleTexture
        val northBubble = Label("TEST", style)
        northBubble.pack()

        northBubble.setPosition((Gdx.graphics.width/2f) - northBubble.width/2f, (Gdx.graphics.height/2f + 60f))
        this.bubbles[Position.WEST] = Label("", style)
        this.bubbles[Position.EAST] = Label("", style)

        players.north.setSize(200f, 200f)
        players.north.setPosition((Gdx.graphics.width/2f) - (players.north.width/2f), Gdx.graphics.height - players.north.height)
        players.north.zIndex = 0

        players.east.setSize(200f, 200f)
        players.east.setPosition((Gdx.graphics.width - players.east.width), (players.north.y - players.east.height * 2))
        players.east.zIndex = 0

        players.west.setSize(200f, 200f)
        players.west.setPosition(0f, (players.north.y - players.west.height * 2))
        players.west.zIndex = 0

        textStage.addActor(players.west)
        textStage.addActor(players.east)
        textStage.addActor(players.north)

        val skin = Config.getSkin()
        val font = Config.getFont(WORLD_WIDTH, 4, Color.WHITE, "truetypefont/Brewers Bold Lhf.ttf")
        skin.get(Label.LabelStyle::class.java).font = font

        val lblNorth = Label(players.north.characterName, skin)
        lblNorth.setSize(players.north.width, players.north.height/3)
        lblNorth.setPosition(players.north.x, players.north.y - lblNorth.height)

        val lblWest = Label(players.west.characterName, skin)
        lblWest.setSize(players.west.width, players.west.height/3)
        lblWest.setPosition(players.west.x, players.west.y - lblNorth.height)

        val lblEast = Label(players.east.characterName, skin)
        lblEast.setSize(players.east.width, players.east.height/3)
        lblEast.setPosition(players.east.x, players.east.y - lblNorth.height)

        textStage.addActor(lblNorth)
        textStage.addActor(lblEast)
        textStage.addActor(lblWest)

        //textStage.addActor(northBubble)

    }

    fun play() {
        state.act()
    }

    /**
     * Player is shown hand and then asked if they want to call Grand Tichu.
     * Each AI character decides if he wants to call Grand Tichu.
     */
    fun getAIGrandCalls() : List<Character> {

        val playersWhoWantToCallGrand = ArrayList<Character>()

        for(character in players.getCharactersAsList().filter{!it.isHuman}) {
            if(character.wantsToCall(true)) {
                playersWhoWantToCallGrand.add(character)
            }
        }

        playersWhoWantToCallGrand.shuffle()

        return playersWhoWantToCallGrand

    }

    private fun pass() {

        var playerHand = players.getCharacterFromPosition(Position.SOUTH).hand

        var answer : String

        var totalCards : Int

        for(position in listOf(Position.WEST, Position.NORTH, Position.EAST)) {

             totalCards = playerHand.size

            var sortedPlayerHand: List<Card>

            var x: Int

            do {
                sortedPlayerHand = playerHand.sortedWith(compareBy({ it.value }))
                x = 1
                for(card in sortedPlayerHand) {
                    println("$x) ${card}")
                    x++
                }
                println("Please select a card to pass to the $position player, ${players.getCharacterFromPosition(position).name}.")
                answer = readLine()!!

            } while (answer.toInt() !in 1..totalCards)

            val chosenCard = sortedPlayerHand.get(answer.toInt().minus(1))
            players.getCharacterFromPosition(position).passedCards.add(chosenCard)

            playerHand.remove(chosenCard)

        }

        for(character in players.getAICharacters()) {

            //Print out the hand. For debugging purposes.
            print("\n${character.name}'s hand: \n ${character.hand.sortedWith(compareBy({it.value}))}\n")

            //Evaluate the pass
            var passes: List<Card> = character.evaluatePass(character.hand)

            //Get the characters
            var leftOpponent = players.getCharacterFromPosition(character.leftOpponent)
            var rightOpponent = players.getCharacterFromPosition(character.rightOpponent)
            var partner = players.getCharacterFromPosition(character.partner)

            for(card in passes) {
                character.hand.remove(card)
            }

            print("\n${character.name} passes: ${passes[0]} to ${leftOpponent.name}")
            leftOpponent.passedCards.add(passes[0])
            print("\n${character.name} passes: ${passes[1]} to ${partner.name}")
            partner.passedCards.add(passes[1])
            print("\n${character.name} passes: ${passes[2]} to ${rightOpponent.name}\n")
            rightOpponent.passedCards.add(passes[2])
        }

        for(character in players.getCharactersAsList()) {
            character.hand.addAll(character.passedCards)
            character.passedCards.clear()
            character.hand.sortedWith(compareBy({ it.value }))
            print("\n${character.name}'s hand: \n ${character.hand.sortedWith(compareBy({it.value}))}\n")
        }

    }

    /**
     * Ask who has the bird, basically.
     */
    private fun whoHasTheBird() : Character {

        var startPlayer = players.getCharacterFromPosition(Position.SOUTH)

        for(character in players.getAICharacters()) {
            if(character.hand.filter({ it.value == 1 }).count() > 0) {
                val event = TichuEvents.valueOf("${character.name.toUpperCase()}_HAS_SPARROW")
                eventDispatcher.dispatch(TichuEvent(event))
                startPlayer = character
            }
        }

        return startPlayer

    }

    private fun firstTrick() {

        val startPlayer = whoHasTheBird()
        val firstCombo : CardCombination

        if(startPlayer.isHuman) {
            print("\nThis is your hand: \n ${startPlayer.hand.sortedWith(compareBy({it.value}))}\n")

            firstCombo = humanPlaysFirst(startPlayer.hand)
            startPlayer.removeCardsFromHand(firstCombo)

        } else {

            if(!startPlayer.calledGrand) {
                val willCall = startPlayer.wantsToCall(false)
                if(willCall) {
                    startPlayer.calledTichu = true
                    eventDispatcher.dispatch(TichuEvent(TichuEvents.valueOf("TICHU_CALL_BY_${startPlayer.name.toUpperCase()}")))
                    eventDispatcher.dispatch(TichuEvent(TichuEvents.REACT_TO_CALL_BY_PARTNER, players.getCharacterFromPosition(startPlayer.partner)))
                }
            }

            firstCombo = startPlayer.playFirst(true)

        }

        val trick = Trick()
        trick.playNext(startPlayer, firstCombo)
        print("\n${startPlayer.name} has played: ${firstCombo.cards}\n")
        Thread.sleep(delay)

        playTrick(trick)

    }

    private fun playTrick(startingTrick: Trick? = null) {

        var trick = Trick()

        if(startingTrick !== null) {
            trick = startingTrick
        }

        var currentPlayer : Character = if(startingTrick !== null) players.next(trick.getLastPlayer().position) else players.lastTrickWinner()

        if(currentPlayer.isOut) {
            do {
                currentPlayer = this.players.next(currentPlayer.position)
            } while (currentPlayer.isOut)
        }

        var numPasses = 0

        while(numPasses < 3 - players.out()) {

            var nextPlay: CardCombination?

            if(currentPlayer.isHuman) {
                nextPlay = humanPlay(currentPlayer.hand, trick)
                if(nextPlay !== null) {
                    currentPlayer.removeCardsFromHand(nextPlay)
                }
            } else {
                nextPlay = if(trick.getNumCombinationsPlayed() == 0) currentPlayer.playFirst(false) else currentPlayer.play(trick.type, trick.getLastPlayedCombinationValue())
            }

            if(nextPlay == null) {
                numPasses++
                print("\n${currentPlayer.name} has passed!\n")
                Thread.sleep(delay)
            } else {
                trick.playNext(currentPlayer, nextPlay)
                print("\n${currentPlayer.name} has played: ${nextPlay.cards}\n\n")
                numPasses = 0
                Thread.sleep(delay)

                if(currentPlayer.hand.count() < 1) {
                    players.addCharacterOut(currentPlayer)
                    print("\n${currentPlayer.name} is out!\n")
                    if(players.out() > 2) {
                        print("\nThe round has ended!\n")
                        endRound = true
                    }
                }

            }

            do {
                currentPlayer = this.players.next(currentPlayer.position)
            } while(currentPlayer.isOut)
        }

        trick.declareWinner(trick.getLastPlayer())
        players.lastTrickWinner = trick.getLastPlayer()
        print("\n${trick.getLastPlayer().name} has won this trick!\n")
        Thread.sleep(delay)

    }

    /**
     * TODO: ALL THIS
     */
    private fun score() {

    }

    private fun printHand(hand: List<Card>) : Unit {

        for(card in hand.sortedWith(compareBy({ it.value }))) {
            print("[$card] ")
        }

        println()
    }

    private fun humanPlay(hand: List<Card>, trick: Trick) : CardCombination? {

        var sortedPlayerHand : List<Card>
        var x: Int
        val totalCards = hand.size
        var answer: String
        val cards = ArrayList<Card>()
        val ca = CardAnalyzer(deck)
        val analysis: CardAnalysis = ca.getCombinations(hand)
        var combination: CardCombination? = null
        var foundCombination = false

        if(trick.getNumCombinationsPlayed() == 0) {
            combination = humanPlaysFirst(hand)
        } else {

            while (!foundCombination) {
                sortedPlayerHand = hand.sortedWith(compareBy({ it.value }))
                x = 1
                for (card in sortedPlayerHand) {
                    println("$x) ${card}")
                    x++
                }
                println("\nPlease type the number for each card you wish to play (with a space between each) or type \"P\" for PASS and hit ENTER when complete..")
                answer = readLine()!!

                if (answer.equals("P")) {
                    break
                }

                try {
                    for (number in answer.split(" ")) {
                        if (number.toIntOrNull() == null) {
                            throw Exception("This is not a number.")
                        } else {
                            cards.add(sortedPlayerHand[number.toInt() - 1])
                        }
                    }
                } catch (e: Exception) {
                    println("\nInvalid response. Please retry your selection.\n")
                    cards.clear()
                    Thread.sleep(1_000)
                }

                combination = analysis.findCombination(cards, trick.type)

                if (combination !== null) {
                    //Check if play is valid.
                    if (combination.getValue() > trick.getLastPlayedCombinationValue()) {
                        foundCombination = true
                    } else {
                        println("Combination must be higher. Please retry your selection.\n")
                    }
                }

            }
        }

        return combination
    }

    private fun humanPlaysFirst(hand: List<Card>) : CardCombination {

        var sortedPlayerHand : List<Card>
        var x: Int
        var answer: String
        val cards = ArrayList<Card>()
        val ca = CardAnalyzer(deck)
        val analysis: CardAnalysis = ca.getCombinations(hand)
        var combination: CardCombination? = null
        var foundCombination = false

        while (!foundCombination) {
            sortedPlayerHand = hand.sortedWith(compareBy({ it.value }))
            x = 1
            for(card in sortedPlayerHand) {
                println("$x) ${card}")
                x++
            }
            println("\nPlease type the number for each card you wish to play (with a space between each) and hit ENTER when complete..")
            answer = readLine()!!

            try {
                for(number in answer.split(" ")) {
                    if(number.toIntOrNull() == null) {
                        throw Exception("This is not a number.")
                    } else {
                        cards.add(sortedPlayerHand[number.toInt() - 1])
                    }
                }
                combination = analysis.findCombination(cards)
            } catch(e: Exception) {
                println("\nInvalid response. Please retry your selection.\n")
                cards.clear()
                Thread.sleep(1_000)
            }

            if(combination !== null) {
                foundCombination = true
            }
        }

        return combination!!

    }

    fun showTichuAnimation(type: TichuType, character: Character, useDoubleTichuSound: Boolean = false) {

        this.bannerImage.setPosition(-Gdx.graphics.width.toFloat(), (Gdx.graphics.height/2f - this.bannerImage.height/2f))
        this.bannerImage.toFront()

        //Play a cool sound.
        if(useDoubleTichuSound) doubleTichuSound.play() else tichuSound.play()

        //Show our cool banner.
        val moveAction = MoveToAction()
        moveAction.setPosition(0f, this.bannerImage.y)
        moveAction.duration = 0.3f

        val waitAction = DelayAction()
        waitAction.duration = 2.0f

        val font = Config.getFont(WORLD_WIDTH, Color.WHITE, 1f, Color.BLACK)
        val skin = Config.getSkin()
        skin.get(Label.LabelStyle::class.java).font = font
        val labelText = when(type) {
            TichuType.GRAND_TICHU ->
                "${character.name} calls \n GRAND TICHU!"
            TichuType.TICHU ->
                "${character.name} calls \n TICHU!"
        }

        val callText = Label(labelText, skin)

        callText.x = Gdx.graphics.width/2f - (callText.width/2f)
        callText.y = stage.camera.project(Vector3(0f, WORLD_HEIGHT/2.1f, 0f)).y
        callText.addAction(Actions.fadeIn(0.3f))

        val removeAction = MoveToAction()
        removeAction.setPosition(Gdx.graphics.width + 1f, this.bannerImage.y)
        removeAction.duration = 0.3f

        val actionChain = SequenceAction(moveAction, (object : RunnableAction() {
            override fun run() {
                textStage.addActor(callText)
            }
        }), waitAction, (object: RunnableAction() {
            override fun run() {
                callText.remove()
            }
        }), removeAction)

        this.bannerImage.addAction(actionChain)

    }

}