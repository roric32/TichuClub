package com.tichuclub.tichuclub

import com.tichuclub.tichuclub.Card as Card

data class CardCombination(
    val type: Combination,
    val cards: List<Card>
) {
    override fun toString() : String {
        return cards.toString()
    }

    fun contains(card: Card) : Boolean {
        return cards.filter({ it.suit == card.suit && it.value == card.value }).count() > 0
    }

    fun containsValue(value: Int) : Boolean {
        return (cards.filter({ it.value == value }).count() > 0)
    }

    /**
     * Returns true if each card in provided list is in this combination.
     * @param cards - List of provided cards to search the combination for.
     */
    fun containsAll(cards: List<Card>) : Boolean {

        var ret = true

        for(card in cards) {
            if(!contains(card)) {
                ret = false
                break
            }
        }

        return ret
    }

    fun containsMultiple(card: Card) : Boolean {
        return (contains(card) && (cards.filter{it.value == card.value}.size > 1))
    }

    fun containsExactly(justThese: List<Card>) : Boolean {
        return (containsAll(justThese) && cards.size == justThese.size)
    }

    fun getHighestCardValue() : Int {
        return cards.maxBy {it -> it.value}!!.value
    }

    fun getLowestCardValue() : Int {
        return cards.minBy {it -> it.value}!!.value
    }

    fun getValue() : Int {

        var fullHouseValue = 0
        if(type == Combination.FULL_HOUSE) {
            val (highestValue, lowerValue) = cards.filter{ it.value != 15 }.partition{ it.value == cards.maxBy{ it.value }?.value }
            fullHouseValue = if(highestValue.count() > lowerValue.count()) highestValue[0].value else lowerValue[0].value
        }

        val ret = when(type) {
            Combination.FULL_HOUSE -> fullHouseValue
            Combination.STRAIGHT -> if(cards.last().value == 15) cards.dropLast(1).last().value + 1 else cards.last().value
            Combination.SINGLETON -> cards.last().value
            else -> if(cards.last().value == 15) cards.dropLast(1).last().value else cards.last().value
        }

        return ret

    }
}