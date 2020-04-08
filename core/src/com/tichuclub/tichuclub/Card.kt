import com.badlogic.gdx.graphics.Texture

enum class Suit {SWORDS, STARS, EMERALDS, PAGODAS, NONE}

abstract class Card(open var suit: Suit, open var value: Int, open var image: Texture)

class NumericCard(override var suit: Suit, override var value: Int, override var image: Texture) : Card(suit, value, image) {

    override fun toString() : String {

        val stringValue : String = when(value) {
            11 -> "Jack"
            12 -> "Queen"
            13 -> "King"
            14 -> "Ace"
            else -> value.toString()
        }

        return "$stringValue of $suit"
    }

}

class SpecialCard(override var suit: Suit, override var value: Int, override var image: Texture, var name: String) : Card(suit, value, image) {

    override fun toString() : String {
        return "The $name"
    }

}