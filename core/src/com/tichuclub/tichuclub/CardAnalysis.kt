package com.tichuclub.tichuclub

import com.tichuclub.tichuclub.CardCombination as CardCombination
import com.tichuclub.tichuclub.Card as Card

class CardAnalysis {

    private var singletons = ArrayList<CardCombination>()
    private var pairs = ArrayList<CardCombination>()
    private var trips = ArrayList<CardCombination>()
    private var fullHouses = ArrayList<CardCombination>()
    private var straights = ArrayList<CardCombination>()
    private var stairs = ArrayList<CardCombination>()
    private var bombs = ArrayList<CardCombination>()

    fun getAll() : ArrayList<CardCombination> {

        val ret = ArrayList<CardCombination>()

        ret.addAll(singletons)
        ret.addAll(pairs)
        ret.addAll(trips)
        ret.addAll(fullHouses)
        ret.addAll(straights)
        ret.addAll(stairs)
        ret.addAll(bombs)

        return ret

    }

    fun add(combo: CardCombination) {

        val target = when(combo.type) {
            Combination.SINGLETON -> singletons
            Combination.PAIR -> pairs
            Combination.TRIPS -> trips
            Combination.FULL_HOUSE -> fullHouses
            Combination.STAIRS -> stairs
            Combination.STRAIGHT -> straights
            Combination.BOMB_FLUSH -> bombs
            Combination.BOMB_4 -> bombs
        }

        if(!contains(combo.type, combo.cards)) {
            target.add(combo)
        }

    }

    fun get(combinationType: Combination) : List<CardCombination> {

        var ret = ArrayList<CardCombination>()

        ret = when(combinationType) {
            Combination.SINGLETON -> singletons
            Combination.PAIR -> pairs
            Combination.TRIPS -> trips
            Combination.FULL_HOUSE -> fullHouses
            Combination.STAIRS -> stairs
            Combination.STRAIGHT -> straights
            Combination.BOMB_FLUSH -> bombs
            Combination.BOMB_4 -> bombs
        }

        return ret

    }

    fun contains(combinationType: Combination, cards: List<com.tichuclub.tichuclub.Card>) : Boolean {

        var ret = false

        var haystack = ArrayList<CardCombination>()

        when(combinationType) {
            Combination.SINGLETON -> haystack = singletons
            Combination.PAIR -> haystack = pairs
            Combination.TRIPS -> haystack = trips
            Combination.FULL_HOUSE -> haystack = fullHouses
            Combination.STAIRS -> haystack = stairs
            Combination.STRAIGHT -> haystack = straights
            Combination.BOMB_FLUSH -> haystack = bombs
            Combination.BOMB_4 -> haystack = bombs
        }

        for(combination in haystack) {
            if(combination.containsExactly(cards)) {
                ret = true
            }
        }

        return ret

    }

    fun findCombination(cards: List<Card>, type: Combination? = null) : CardCombination? {

        var ret: CardCombination? = null
        val combosToSearch = if(type == null) getAll() else get(type)
        for(combo in combosToSearch) {
            if(combo.containsExactly(cards)) {
                ret = combo
                break
            }
        }

        return ret

    }

}

