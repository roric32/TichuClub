interface TichuEventListener {
    val event: TichuEvent
    val game: Game
    fun respond()
}