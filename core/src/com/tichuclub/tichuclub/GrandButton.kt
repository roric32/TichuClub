package com.tichuclub.tichuclub

data class GrandButtons (
        val buttons: List<GrandText> = listOf()
)

data class GrandText(
        val id: Int,
        val type: String,
        val confidence: Confidence,
        val text: String
)

