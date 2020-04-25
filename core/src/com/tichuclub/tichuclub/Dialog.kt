package com.tichuclub.tichuclub

data class Dialogs(
    val dialogs: List<Dialog> = listOf<Dialog>()
)

data class Dialog(
    val id: Int,
    val type: TichuEvents,
    val characters: List<String> = listOf<String>(),
    val optional: Boolean = false,
    val text: String
)

