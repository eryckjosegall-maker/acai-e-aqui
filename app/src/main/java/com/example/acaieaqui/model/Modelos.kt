package com.example.acaieaqui.model

import java.util.UUID

enum class Categoria(val titulo: String) {
    TODOS("Todos"),
    ACAI("Açaí"),
    COMBOS("Combos"),
    BEBIDAS("Bebidas"),
    EXTRAS("Extras")
}

data class Tamanho(
    val nome: String,      // ex: "300ml"
    val preco: Double
)

data class Complemento(
    val id: Int,
    val nome: String,
    val preco: Double
)

data class Produto(
    val id: Int,
    val nome: String,
    val descricao: String,
    val categoria: Categoria,
    val emoji: String,                 // placeholder até termos imagens reais
    val tamanhos: List<Tamanho>,
    val complementosDisponiveis: List<Complemento> = emptyList()
) {
    val precoInicial: Double get() = tamanhos.minOf { it.preco }
}

// RF03 - item do carrinho
data class ItemCarrinho(
    val id: String = UUID.randomUUID().toString(),
    val produto: Produto,
    val tamanho: Tamanho,
    val complementos: List<Complemento>,
    val quantidade: Int
) {
    val precoUnitario: Double get() = tamanho.preco + complementos.sumOf { it.preco }
    val total: Double get() = precoUnitario * quantidade
}

// Login / Cadastro (simulado, em memória)
data class Usuario(
    val nome: String,
    val email: String,
    val senha: String
)

// RF04 - dados do pedido
enum class FormaPagamento(val titulo: String) {
    PIX("Pix"),
    CARTAO("Cartão na entrega"),
    DINHEIRO("Dinheiro")
}

data class DadosEntrega(
    val nome: String,
    val telefone: String,
    val rua: String,
    val numero: String,
    val bairro: String,
    val complemento: String,
    val pagamento: FormaPagamento,
    val observacao: String
)

// RF05 - status do pedido
enum class StatusPedido(val titulo: String, val descricao: String, val emoji: String) {
    RECEBIDO("Pedido recebido", "Recebemos o seu pedido.", "📥"),
    EM_PREPARO("Em preparo", "Estamos montando o seu açaí.", "🍇"),
    SAIU_PARA_ENTREGA("Saiu para entrega", "O entregador está a caminho.", "🛵"),
    ENTREGUE("Entregue", "Bom apetite!", "✅")
}

data class Pedido(
    val numero: Int,
    val itens: List<ItemCarrinho>,
    val entrega: DadosEntrega,
    val subtotal: Double,
    val taxaEntrega: Double,
    val status: StatusPedido
) {
    val total: Double get() = subtotal + taxaEntrega
}
