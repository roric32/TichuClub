class Trick {

    lateinit var type: Combination

    private var cards: MutableList<Pair<Character, CardCombination>> = mutableListOf()

    fun playNext(character: Character, combo: CardCombination) {
        type = combo.type
        cards.add(Pair(character, combo))
    }

    fun declareWinner(character: Character) {
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
        return cards.last().second.getValue()
    }

}