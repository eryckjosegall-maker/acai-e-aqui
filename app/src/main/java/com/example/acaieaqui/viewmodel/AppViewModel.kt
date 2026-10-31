package com.example.acaieaqui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acaieaqui.model.Complemento
import com.example.acaieaqui.model.DadosEntrega
import com.example.acaieaqui.model.ItemCarrinho
import com.example.acaieaqui.model.Pedido
import com.example.acaieaqui.model.Produto
import com.example.acaieaqui.model.StatusPedido
import com.example.acaieaqui.model.Tamanho
import com.example.acaieaqui.model.Usuario
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Estado compartilhado do app (usuário, carrinho e pedido).
 * Tudo em memória: ao fechar o app, os dados somem. Para a entrega
 * do trabalho isso é suficiente; persistência fica como melhoria.
 */
class AppViewModel : ViewModel() {

    companion object {
        const val TAXA_ENTREGA = 5.0
        private const val INTERVALO_STATUS_MS = 10_000L
    }

    // ---------- Login / Cadastro ----------
    private val usuarios = mutableListOf(
        Usuario("Cliente Demo", "demo@acai.com", "123456")
    )

    var usuarioLogado by mutableStateOf<Usuario?>(null)
        private set

    /** Retorna a mensagem de erro, ou null se deu certo. */
    fun entrar(email: String, senha: String): String? {
        val usuario = usuarios.firstOrNull { it.email.equals(email.trim(), ignoreCase = true) }
        return when {
            usuario == null -> "Nenhuma conta encontrada com esse e-mail."
            usuario.senha != senha -> "Senha incorreta."
            else -> {
                usuarioLogado = usuario
                null
            }
        }
    }

    /** Retorna a mensagem de erro, ou null se deu certo. */
    fun cadastrar(nome: String, email: String, senha: String): String? {
        if (usuarios.any { it.email.equals(email.trim(), ignoreCase = true) }) {
            return "Esse e-mail já está cadastrado."
        }
        val novo = Usuario(nome.trim(), email.trim(), senha)
        usuarios.add(novo)
        usuarioLogado = novo
        return null
    }

    fun sair() {
        jobAcompanhamento?.cancel()
        usuarioLogado = null
        carrinho.clear()
        pedidoAtual = null
    }

    // ---------- Carrinho (RF03) ----------
    val carrinho = mutableStateListOf<ItemCarrinho>()

    val totalItens: Int get() = carrinho.sumOf { it.quantidade }
    val subtotal: Double get() = carrinho.sumOf { it.total }

    fun adicionarAoCarrinho(
        produto: Produto,
        tamanho: Tamanho,
        complementos: List<Complemento>,
        quantidade: Int
    ) {
        val idsNovos = complementos.map { it.id }.toSet()
        val indice = carrinho.indexOfFirst {
            it.produto.id == produto.id &&
                it.tamanho == tamanho &&
                it.complementos.map { c -> c.id }.toSet() == idsNovos
        }
        if (indice >= 0) {
            val atual = carrinho[indice]
            carrinho[indice] = atual.copy(quantidade = atual.quantidade + quantidade)
        } else {
            carrinho.add(
                ItemCarrinho(
                    produto = produto,
                    tamanho = tamanho,
                    complementos = complementos,
                    quantidade = quantidade
                )
            )
        }
    }

    fun alterarQuantidade(idItem: String, delta: Int) {
        val indice = carrinho.indexOfFirst { it.id == idItem }
        if (indice < 0) return
        val novaQuantidade = carrinho[indice].quantidade + delta
        if (novaQuantidade <= 0) {
            carrinho.removeAt(indice)
        } else {
            carrinho[indice] = carrinho[indice].copy(quantidade = novaQuantidade)
        }
    }

    fun removerDoCarrinho(idItem: String) {
        carrinho.removeAll { it.id == idItem }
    }

    // ---------- Pedido (RF04 e RF05) ----------
    var pedidoAtual by mutableStateOf<Pedido?>(null)
        private set

    private var contadorPedido = 1000
    private var jobAcompanhamento: Job? = null

    fun confirmarPedido(entrega: DadosEntrega) {
        if (carrinho.isEmpty()) return
        pedidoAtual = Pedido(
            numero = ++contadorPedido,
            itens = carrinho.toList(),
            entrega = entrega,
            subtotal = subtotal,
            taxaEntrega = TAXA_ENTREGA,
            status = StatusPedido.RECEBIDO
        )
        carrinho.clear()
        simularAndamento()
    }

    /** Avança o status a cada 10 segundos, só para demonstrar o RF05. */
    private fun simularAndamento() {
        jobAcompanhamento?.cancel()
        jobAcompanhamento = viewModelScope.launch {
            for (proximo in StatusPedido.entries.drop(1)) {
                delay(INTERVALO_STATUS_MS)
                pedidoAtual = pedidoAtual?.copy(status = proximo)
            }
        }
    }
}
