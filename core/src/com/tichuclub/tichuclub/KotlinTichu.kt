class KotlinTichu {

    companion object {
        @JvmStatic fun main(args: Array<String>) {

            val players = PlayerOverlord(
                    Zach("Zach"),
                    Thong("Thong"),
                    Player("Brandon"),
                    Nate("Nate"))

            val game = Game(players)

            game.play()

        }
    }

}
