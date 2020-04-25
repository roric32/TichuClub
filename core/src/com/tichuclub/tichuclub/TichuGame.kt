package com.tichuclub.tichuclub

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import kotlin.math.roundToInt

class TichuGame(val WORLD_WIDTH: Int, val WORLD_HEIGHT: Int, stage: Stage, atlas: TextureAtlas) {

    var scoreNS : Int = 0
    var scoreEW : Int = 0
    val CARD_WIDTH : Float = 2f
    val CARD_HEIGHT : Float = 2.9f
    val FANNED_CARD_WIDTH: Float = (CARD_WIDTH/4.2f)
    var eventDispatcher = EventDispatcher(ArrayList())
    val dialogs = DialogOverlord("dialog.json")
    val shuffleSound = Gdx.audio.newMusic(Gdx.files.internal("shuffle.wav"))
    private var endRound = false
    private var delay : Long = 2_000
    var deck = Deck(true, atlas)
    var stage : Stage
    var state : TichuState = StateGameStart(this)
    val isAndroid = Gdx.app.type == Application.ApplicationType.Android

    lateinit var players: PlayerOverlord

    init {
        for(event in TichuEvents.values()) {
            eventDispatcher.addListener(TriggerDialogEventListener(TichuEvent(event), this))
        }
        this.stage = stage
    }

    fun setUp(players: PlayerOverlord) {
        this.players = players
    }

    fun play() {
        state.act()
    }

    /**
     * Player is shown hand and then asked if they want to call Grand Tichu.
     * Each AI character decides if he wants to call Grand Tichu.
     */
    private fun checkForGrandTichu() {


        /*
        askPlayerForCall(true)

        //AI checks if they want to call Grand
        val playersWhoWantToCallGrand = ArrayList<Character>()

        for(character in players.getCharactersAsList()) {
            if (character is Player) continue

            if(character.wantsToCall(true)) {
                playersWhoWantToCallGrand.add(character)
            }
        }

        playersWhoWantToCallGrand.shuffle()

        for(player in playersWhoWantToCallGrand) {

            if(!players.getCharacterFromPosition(player.partner).calledGrand) {
                player.calledGrand = true
                val charName: String = player.name
                val event = TichuEvents.valueOf("GRAND_TICHU_CALL_BY_${charName.toUpperCase()}")
                eventDispatcher.dispatch(TichuEvent(event))
            }

        }
        */

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

            if(!startPlayer.calledGrand) {
                askPlayerForCall()
            }

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

    private fun askPlayerForCall(isGrand: Boolean = false) {

        var answer : String

        do {
            var tichuType : String = ""
            if(isGrand) tichuType = "Grand "
            val message = "Do you wish to call ${tichuType}Tichu? (Y/N): "
            println(message)
            answer = readLine()!!
        } while(!(answer.equals("Y").or(answer.equals("N"))))

        if(answer == "Y") {
            if(isGrand) {
                players.getCharacterFromPosition(Position.SOUTH).calledGrand = true
                eventDispatcher.dispatch(TichuEvent(TichuEvents.GRAND_TICHU_CALL_BY_PLAYER))
            } else {
                players.getCharacterFromPosition(Position.SOUTH).calledTichu = true
                eventDispatcher.dispatch(TichuEvent(TichuEvents.REACT_TO_CALL_BY_PLAYER))
            }
        }


    }

}