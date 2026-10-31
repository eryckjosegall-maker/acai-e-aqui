package com.example.acaieaqui.data

import com.example.acaieaqui.model.Categoria
import com.example.acaieaqui.model.Complemento
import com.example.acaieaqui.model.Produto
import com.example.acaieaqui.model.Tamanho

/**
 * Fonte de dados simulada (RF01). Quando o grupo quiser, dá pra trocar
 * por Room/DataStore sem mexer nas telas, só nesta classe.
 */
object ProdutosRepository {

    val complementos = listOf(
        Complemento(1, "Granola", 2.0),
        Complemento(2, "Leite em pó", 2.5),
        Complemento(3, "Banana", 2.0),
        Complemento(4, "Morango", 3.5),
        Complemento(5, "Leite condensado", 2.5),
        Complemento(6, "Paçoca", 3.0),
        Complemento(7, "Nutella", 5.0)
    )

    private val tamanhosAcai = listOf(
        Tamanho("300ml", 14.0),
        Tamanho("500ml", 20.0),
        Tamanho("700ml", 27.0)
    )

    val produtos = listOf(
        Produto(
            id = 1,
            nome = "Açaí Tradicional",
            descricao = "Açaí puro e cremoso, do jeitinho clássico.",
            categoria = Categoria.ACAI,
            emoji = "🍇",
            tamanhos = tamanhosAcai,
            complementosDisponiveis = complementos
        ),
        Produto(
            id = 2,
            nome = "Açaí com Banana",
            descricao = "Açaí batido com banana, mais docinho.",
            categoria = Categoria.ACAI,
            emoji = "🍌",
            tamanhos = tamanhosAcai,
            complementosDisponiveis = complementos
        ),
        Produto(
            id = 3,
            nome = "Açaí com Morango",
            descricao = "Açaí batido com morango, refrescante.",
            categoria = Categoria.ACAI,
            emoji = "🍓",
            tamanhos = tamanhosAcai,
            complementosDisponiveis = complementos
        ),
        Produto(
            id = 4,
            nome = "Combo Casal",
            descricao = "2 açaís de 500ml com 3 complementos cada.",
            categoria = Categoria.COMBOS,
            emoji = "💜",
            tamanhos = listOf(Tamanho("Único", 36.0)),
            complementosDisponiveis = complementos
        ),
        Produto(
            id = 5,
            nome = "Combo Família",
            descricao = "4 açaís de 500ml para dividir.",
            categoria = Categoria.COMBOS,
            emoji = "👨‍👩‍👧",
            tamanhos = listOf(Tamanho("Único", 68.0)),
            complementosDisponiveis = complementos
        ),
        Produto(
            id = 6,
            nome = "Suco de Laranja",
            descricao = "Natural, gelado, sem açúcar.",
            categoria = Categoria.BEBIDAS,
            emoji = "🍊",
            tamanhos = listOf(Tamanho("300ml", 7.0), Tamanho("500ml", 10.0))
        ),
        Produto(
            id = 7,
            nome = "Água de Coco",
            descricao = "Direto do coco, bem geladinha.",
            categoria = Categoria.BEBIDAS,
            emoji = "🥥",
            tamanhos = listOf(Tamanho("Unidade", 8.0))
        ),
        Produto(
            id = 8,
            nome = "Complemento Extra",
            descricao = "Pote avulso de complemento para levar.",
            categoria = Categoria.EXTRAS,
            emoji = "🥣",
            tamanhos = listOf(Tamanho("100g", 6.0))
        )
    )

    fun porCategoria(categoria: Categoria): List<Produto> =
        if (categoria == Categoria.TODOS) produtos
        else produtos.filter { it.categoria == categoria }

    fun buscarPorId(id: Int): Produto? = produtos.firstOrNull { it.id == id }
}
