import java.util.*

class TriggerDialogEventListener(override val event: TichuEvent, override val game: Game) : TichuEventListener {

    override fun respond() {

        val dialog: Dialog? = game.dialogs.getRandomDialogForEvent(event)

        dialog?.let {

            //Decide if this dialog is absolutely necessary, or just fluff
            val randomObject = Random()
            var needDialog: Boolean = true

            if(dialog.optional) {
                needDialog = randomObject.nextBoolean()
            }

            if(needDialog) {

                val chosenCharacter : Character
                if(event.specificCharacter == null) {

                    val gameCharacters: List<Character> = game.players.getCharactersAsList()

                    val foundCharacters = ArrayList<Character>()

                    for (character in gameCharacters) {
                        if (character.javaClass.name in dialog.characters) {
                            foundCharacters.add(character)
                        }
                    }

                    if (foundCharacters.count() > 0) {
                        val randomNumber = randomObject.nextInt(foundCharacters.size)
                        chosenCharacter = foundCharacters[randomNumber]
                        chosenCharacter.speak(dialog.text)

                    }

                } else {
                    chosenCharacter = event.specificCharacter
                    chosenCharacter.speak(dialog.text)
                }

            }

        }

    }

}