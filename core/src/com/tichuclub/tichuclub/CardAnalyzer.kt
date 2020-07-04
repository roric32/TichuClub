package com.tichuclub.tichuclub

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Image

class CardAnalyzer(private var tichu: TichuGame) {

    fun getCombinations(hand: List<Card>) : CardAnalysis {

        var analysis = CardAnalysis()

        val sortedHand: List<Card> = hand.sortedWith(compareBy({ it.value }))

        //Get multiples and categorize

        for(card in sortedHand) {
            analysis.add(CardCombination(Combination.SINGLETON, listOf(card)))
        }

        for(pair: CardCombination in getPairs(sortedHand)) {
            analysis.add(pair)
        }

        for(trip: CardCombination in getTrips(sortedHand)) {
            analysis.add(trip)
        }

        for(bomb: CardCombination in getBombs(sortedHand)) {
            analysis.add(bomb)
        }

        for(fullHouse: CardCombination in getFullHouses(analysis.get(Combination.PAIR), analysis.get(Combination.TRIPS))) {
            analysis.add(fullHouse)
        }

        for(stairs: CardCombination in getStairs(analysis.get(Combination.PAIR))) {
            analysis.add(stairs)
        }

        for(straight in getDistinctStraights(sortedHand)) {
            analysis.add(straight)
        }

        //Get all potential flush bombs.
        for(straight in analysis.get(Combination.STRAIGHT)) {
            var straightIsBomb = true
            val originalSuit = straight.cards.get(0).suit
            for(card in straight.cards) {
                if(card.suit !== originalSuit) {
                    straightIsBomb = false
                    break
                }
            }

            if(straightIsBomb) {
                analysis.add(CardCombination(Combination.BOMB_FLUSH, straight.cards))
            }
        }

        //TODO: Phoenix as wildcard
        /*
            Phoenix can be a wildcard in any combination.
            Here we're going to loop through all the combinations and add combinations
            substituting the Phoenix for each other card and add them all to an ArrayList.
        */

        if(sortedHand.filter{it.value==15}.count() > 0) {

            val phoenix : Card = sortedHand[sortedHand.indexOfFirst({ it.value == 15 })]

            var combosWithPhoenix = ArrayList<CardCombination>()

            for(combo in analysis.getAll().filter({ !(it.type == Combination.STRAIGHT && it.contains(phoenix)) && it.type !== Combination.BOMB_4 && it.type !== Combination.BOMB_FLUSH })) {

                //Ignore the Sparrow (it could be in a straight)
                if(combo.cards.get(0).value == 1) continue

                var comboWithPhoenix = ArrayList<Card>()
                comboWithPhoenix.addAll(combo.cards)

                for (x in 0..combo.cards.lastIndex) {
                    comboWithPhoenix[x] = phoenix
                    combosWithPhoenix.add(CardCombination(combo.type, comboWithPhoenix.toList()))
                    comboWithPhoenix.clear()
                    comboWithPhoenix.addAll(combo.cards)
                }

                //Any pair can become a trip with the Phoenix
                if(combo.type == Combination.PAIR) {
                    comboWithPhoenix.add(phoenix)
                    combosWithPhoenix.add(CardCombination(Combination.TRIPS, comboWithPhoenix.toList()))
                }

            }

            //Any non-special singleton can become a pair with the Phoenix
            for(card in sortedHand) {
                if(card.suit !== Suit.NONE) {
                    val newPair = listOf(card, phoenix)
                    if(!analysis.contains(Combination.PAIR, newPair)) {
                        analysis.add(CardCombination(Combination.PAIR, newPair))
                    }
                }
            }

            //Add each new combo to the main combo lists.
            for(combo in combosWithPhoenix) {
                if(!analysis.contains(combo.type, combo.cards)) {
                    analysis.add(combo)
                }
            }

            //Now, loop through again to see if we now have full houses that weren't possible before the Phoenix.

            var phoenixFullHouses = ArrayList<CardCombination>()

            for(trip in analysis.get(Combination.TRIPS)) {
                for(pair in analysis.get(Combination.PAIR)) {
                    phoenixFullHouses.add(CardCombination(Combination.FULL_HOUSE, trip.cards + pair.cards))
                }
            }

            for(combo in phoenixFullHouses.filter{!it.containsMultiple(phoenix)}) {
                analysis.add(combo)
            }


            var stairs = analysis.get(Combination.STAIRS).filter({ !it.containsValue(15)})

            var pairsWithPhoenixes = analysis.get(Combination.PAIR).filter({ it.containsValue(15)})

            for(stair in stairs) {
                //Check if stairs can continue higher based on phoenix pair combos.
                val highest = stair.getHighestCardValue()
                for(pair in pairsWithPhoenixes) {
                    if(pair.getLowestCardValue().minus(highest) == 1) {
                        val comboToEvaluate = stair.cards + pair.cards
                        val newCombo = CardCombination(Combination.STAIRS, comboToEvaluate)

                        if(!analysis.contains(Combination.STAIRS, newCombo.cards)) {
                            analysis.add(newCombo)
                        }

                        for(x in (comboToEvaluate.count() -1) downTo 2 step 2) {
                            val combo = ArrayList<Card>()
                            combo.add(comboToEvaluate[x])
                            combo.add(comboToEvaluate[x - 1])
                            combo.add(comboToEvaluate[x - 2])
                            combo.add(comboToEvaluate[x - 3])
                            val curCombo = CardCombination(Combination.STAIRS, combo)
                            if(!analysis.contains(Combination.STAIRS, curCombo.cards)) {
                                analysis.add(curCombo)
                            }
                        }
                    }
                }
            }

        }

        return analysis

    }

    private fun getPairs(sortedHand: List<Card>) : ArrayList<CardCombination> {

        var ret = ArrayList<CardCombination>()

        for(i: Int in 2..14) {

            var multiples = sortedHand.filter {it.value == i}

            if(multiples.size == 2) {
                ret.add(CardCombination(Combination.PAIR, multiples))
            }

        }

        return ret

    }

    private fun getTrips(sortedHand: List<Card>) : ArrayList<CardCombination> {

        var ret = ArrayList<CardCombination>()

        for(i: Int in 2..14) {

            var multiples = sortedHand.filter {it.value == i}

            if(multiples.size == 3) {
                ret.add(CardCombination(Combination.TRIPS, multiples))
                for(combo in groupMultiples(Combination.PAIR, multiples)) {
                    ret.add(combo)
                }
            }

        }

        return ret

    }

    private fun getBombs(sortedHand: List<Card>) : ArrayList<CardCombination> {

        val ret = ArrayList<CardCombination>()

        for(i: Int in 2..14) {

            val multiples = sortedHand.filter { it.value == i }

            if (multiples.size == 4) {

                ret.add(CardCombination(Combination.BOMB_4, multiples))

                for (combo in groupMultiples(Combination.TRIPS, multiples)) {
                    ret.add(combo)
                }

                for (combo in groupMultiples(Combination.PAIR, multiples)) {
                    ret.add(combo)
                }

            }

        }

        return ret

    }

    private fun getFullHouses(pairs: List<CardCombination>, trips: List<CardCombination>) : ArrayList<CardCombination> {

        val ret = ArrayList<CardCombination>()

        for(pair in pairs) {
            for(trip in trips) {
                val cards = trip.cards.toMutableList()
                if(!cards.contains(pair.cards.get(0)) && !cards.contains(pair.cards.get(1))) {
                    for (card in pair.cards) {
                        cards.add(card)
                    }
                }
                if(cards.size == 5) {
                    ret.add(CardCombination(Combination.FULL_HOUSE, cards))
                }
            }
        }

        return ret

    }

    private fun getStairs(pairs: List<CardCombination>) : ArrayList<CardCombination> {

        val ret = ArrayList<CardCombination>()

        //Figure out all possible stairs plays.
        for(pair in pairs) {
            var potentialSteps = ArrayList<Card>()
            potentialSteps.add(pair.cards.get(0))
            potentialSteps.add(pair.cards.get(1))
            val curCard = pair.cards.get(0)
            var x: Int = curCard.value
            for(pairAgain in pairs) {
                if(pairAgain.cards.get(0).value.minus(x).equals(1)) {
                    potentialSteps.add(pairAgain.cards.get(0))
                    potentialSteps.add(pairAgain.cards.get(1))
                    x++
                }
            }
            if(potentialSteps.size > 2) {
                ret.add(CardCombination(Combination.STAIRS, potentialSteps.toList()))
            }
        }

        return ret
    }

    private fun getDistinctStraights(sortedHand: List<Card>) : ArrayList<CardCombination> {

        val straights = ArrayList<CardCombination>()

        for(i in 0..sortedHand.count().minus(1)) {

            //Straights must be 5 cards minimum in length, so 9 is as high as we need to iterate over.
            if(i > 9) {
                break
            }

            var potentialStraight = ArrayList<Card>()
            val duplicateCards = ArrayList<Pair<Int, Card>>()
            val bottomOfStraight: Card = sortedHand[i]

            //The dog is worthless, skip
            if(bottomOfStraight.value == 0) continue

            potentialStraight.add(bottomOfStraight)

            var x = i

            while(x.plus(1) <= sortedHand.count() - 1) {

                val nextCard: Card = sortedHand[x.plus(1)]

                //Ignore the Dragon in straights
                if(nextCard.value == 16) {
                    x++
                    continue
                }

                if(nextCard.value == 15) {
                    if(potentialStraight.filter({ it.value == 15 }).count() == 0 && potentialStraight.last().value < 14) {
                        potentialStraight.add(nextCard)
                    }
                    x++
                    continue
                }

                val difference: Int = nextCard.value.minus(potentialStraight.last().value)

                when(difference) {
                    2 -> if(sortedHand.filter({ it.value == 15}).count() > 0 && potentialStraight.filter({ it.value == 15 }).count() < 1) {
                        potentialStraight.add(tichu.players.searchHandsForCard("PHOENIX")!!)
                        potentialStraight.add(nextCard)
                    }
                    1 -> potentialStraight.add(nextCard)
                    0 -> {
                        duplicateCards.add(Pair(potentialStraight.lastIndex, nextCard))
                    }
                    else -> {
                        //If the bottom card is the Phoenix, we can tolerate this.
                        if(bottomOfStraight.value == 15) {
                            potentialStraight.add(nextCard)
                        }
                    }
                }

                x++
            }

            if(potentialStraight.size >= 5) {


                //Create a backup of the potentialStraight
                val original = ArrayList<Card>()
                for(card in potentialStraight) {
                    original.add(card)
                }

                if(!CombinationExists(straights, potentialStraight)) {
                    straights.add(CardCombination(Combination.STRAIGHT, original.toList()))
                }

                /*
                    Loop back through the cards that were found and add smaller combinations.
                    For example if potentialStraight now has 2W, 3E, 4P, 5E, 6W, 7E, let's go back
                    and add the 2W, 3E, 4P, 5E, 6W now.
                */

                for(counter in potentialStraight.size downTo(6)) {
                    var curStraight = potentialStraight.dropLast(potentialStraight.size - (counter - 1))
                    if(!CombinationExists(straights, curStraight)) {
                        straights.add(CardCombination(Combination.STRAIGHT, curStraight))
                    }
                }

                /*
                    Reconstruct the straight just made with any duplicate cards found.
                    For example, if we just built 2S, 3E, 4P, 5E, 6P but there was a 2W also, now make the 2W through 6P.
                 */

                for(duplicate in duplicateCards) {
                    potentialStraight[duplicate.first] = duplicate.second

                    if(!CombinationExists(straights, potentialStraight)) {
                        straights.add(CardCombination(Combination.STRAIGHT, potentialStraight.toList()))
                    }

                    var filteredCards = duplicateCards.filter { it !== duplicate }

                    for(otherDuplicate in filteredCards) {
                        potentialStraight[otherDuplicate.first] = otherDuplicate.second

                        if(!CombinationExists(straights, potentialStraight)) {
                            straights.add(CardCombination(Combination.STRAIGHT, potentialStraight.toList()))
                        }
                    }

                    potentialStraight.clear()

                    for(card in original) {
                        potentialStraight.add(card)
                    }
                }

                duplicateCards.clear()
            }

        }

        return straights

    }

    private fun groupMultiples(combination: Combination, cards: List<Card>) : ArrayList<CardCombination> {

        var ret = ArrayList<CardCombination>()

        if(combination == Combination.PAIR) {
            cards.forEachIndexed { index, element ->
                cards.forEachIndexed { index2, element2 ->
                    if(cards[index2] !== cards[index]) {
                        ret.add(CardCombination(combination, listOf(cards[index], cards[index2])))
                    }
                }
            }
        }

        if(combination == Combination.TRIPS) {
            for(card in cards) {
                var potentialTrip = mutableListOf<Card>()
                potentialTrip.add(card)
                for(card2 in cards) {
                    if(!potentialTrip.contains(card2) && potentialTrip.count() < 3) {
                        potentialTrip.add(card2)
                    }
                }
                potentialTrip.sortWith(compareBy({it.value}))

                var newPossibleCombo = CardCombination(Combination.TRIPS, potentialTrip)

                if(!ret.contains(newPossibleCombo)) {
                    ret.add(newPossibleCombo)
                }
            }

        }

        return ret

    }

    private fun CombinationExists(straights: List<CardCombination>, potentialStraight: List<Card>) : Boolean {

        var existingStraightFound = false
        for(straight in straights) {
            if(straight.containsExactly(potentialStraight.toList())) {
                existingStraightFound = true
                break
            }
        }
        return existingStraightFound
    }

}