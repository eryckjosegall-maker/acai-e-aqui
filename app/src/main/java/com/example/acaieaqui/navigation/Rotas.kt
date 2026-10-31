package com.example.acaieaqui.navigation

object Rotas {
    const val LOGIN = "login"
    const val HOME = "home"
    const val DETALHES = "detalhes/{produtoId}"
    const val CARRINHO = "carrinho"
    const val FINALIZACAO = "finalizacao"
    const val ACOMPANHAMENTO = "acompanhamento"

    fun detalhes(produtoId: Int) = "detalhes/$produtoId"
}
