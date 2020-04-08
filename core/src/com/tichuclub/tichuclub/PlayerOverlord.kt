class PlayerOverlord(private var north : Character,
                     private var east : Character,
                     private var south : Character,
                     private var west : Character) {

    private val playersOut = ArrayList<Character>()
    lateinit var lastTrickWinner : Character

    init {
        north.position = Position.NORTH
        north.partner = Position.SOUTH
        north.leftOpponent = Position.EAST
        north.rightOpponent = Position.WEST

        east.position = Position.EAST
        east.partner = Position.WEST
        east.leftOpponent = Position.SOUTH
        east.rightOpponent = Position.NORTH

        south.position = Position.SOUTH
        south.partner = Position.NORTH
        south.leftOpponent = Position.WEST
        south.rightOpponent = Position.EAST

        west.position = Position.WEST
        west.partner = Position.EAST
        west.leftOpponent = Position.NORTH
        west.rightOpponent = Position.SOUTH
    }

    fun getCharactersAsList() : List<Character> {
        return listOf(north, east, south, west)
    }

    fun getAICharacters() : List<Character> {
        return listOf(north, east, west)
    }

    fun getCharacterFromPosition(position: Position) : Character {

        val character: Character

        character = when(position) {
            Position.NORTH -> north
            Position.EAST -> east
            Position.SOUTH -> south
            Position.WEST -> west
        }

        return character
    }

    fun getCharacterPositionByName(requestedCharacter: String) : Position? {

        var ret: Position? = null

        for(character in getCharactersAsList()) {
            if(character.name == requestedCharacter) {
                ret = character.position
            }
        }

        return ret

    }

    fun next(position: Position) : Character {

        val index = Position.values().indexOf(position)
        var newIndex = index + 1

        if(index == Position.values().size - 1) {
            newIndex = 0
        }

        return getCharacterFromPosition(Position.values()[newIndex])
    }

    fun addCharacterOut(character: Character) {
        playersOut.add(character)
        character.isOut = true
    }

    fun whoWasOutFirst() : Character {
        return playersOut.first()
    }

    fun whoWasOutLast() : Character {
        return playersOut.last()
    }

    fun out() : Int {
        return playersOut.count()
    }

    fun lastTrickWinner() : Character {
        return lastTrickWinner
    }

}