package com.tichuclub.tichuclub

enum class TrickState {
    WAITING_FOR_HUMAN,
    AI_PLAYING,
    PAUSED,
    COMPLETED
}

class Trick(startPosition: Position) {

    var numPasses = 0
    var state = TrickState.AI_PLAYING
    var hasType = false
    var currentPosition: Position = startPosition
    lateinit var type: Combination

    var cards: MutableList<Pair<Character, CardCombination>> = mutableListOf()

    fun nextPositionToPlay() : Position {
        val positionIterator = Position.values()
        var index = positionIterator.indexOf(currentPosition)
        index = if(index == Position.values().count() - 1) 0 else index + 1
        return positionIterator[index]
    }

    fun playNext(character: Character, combo: CardCombination) {
        numPasses = 0
        currentPosition = character.position
        type = combo.type
        cards.add(Pair(character, combo))
        if(!hasType) hasType = true
    }

    fun pass() {
        numPasses += 1
        currentPosition = nextPositionToPlay()
    }

    fun declareWinner(character: Character) {
        println("${character.name} has won the trick!")
        for(pair in cards) {
            for(card in pair.second.cards) {
                character.cardsWon.add(card)
            }
        }
    }

    fun getNumCombinationsPlayed() : Int {
        return cards.count()
    }

    fun getLastPlayer() : Character {
        return cards.last().first
    }

    fun getLastPlayedCombinationValue() : Int {
        var value = 0
        if(cards.count() > 0) value = cards.last().second.getValue()
        return value
    }

    fun getLastPlayedCombinationCardCount() : Int {
        return when(cards.count()) {
            0 -> 0
            else -> cards.last().second.cards.count()
        }
    }

}