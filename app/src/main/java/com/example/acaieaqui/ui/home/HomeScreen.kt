package com.example.acaieaqui.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.acaieaqui.data.ProdutosRepository
import com.example.acaieaqui.model.Categoria
import com.example.acaieaqui.model.Produto
import com.example.acaieaqui.ui.components.EmojiBadge
import com.example.acaieaqui.ui.components.paraReal
import com.example.acaieaqui.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    onProdutoClick: (Int) -> Unit,
    onCarrinhoClick: () -> Unit,
    onAcompanharClick: () -> Unit,
    onSair: () -> Unit
) {
    var busca by rememberSaveable { mutableStateOf("") }
    var categoria by rememberSaveable { mutableStateOf(Categoria.TODOS) }

    val produtos = remember(busca, categoria) {
        ProdutosRepository.porCategoria(categoria).filter {
            busca.isBlank() ||
                it.nome.contains(busca.trim(), ignoreCase = true) ||
                it.descricao.contains(busca.trim(), ignoreCase = true)
        }
    }

    val quantidade = viewModel.totalItens
    val primeiroNome = viewModel.usuarioLogado?.nome?.trim()?.substringBefore(" ").orEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            if (primeiroNome.isNotEmpty()) "Olá, $primeiroNome!" else "Olá!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Bora de açaí hoje?", style = MaterialTheme.typography.bodySmall)
                    }
                },
                actions = {
                    IconButton(onClick = onCarrinhoClick) {
                        BadgedBox(badge = { if (quantidade > 0) Badge { Text("$quantidade") } }) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Carrinho")
                        }
                    }
                    IconButton(onClick = onSair) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Sair")
                    }
                }
            )
        },
        bottomBar = {
            if (quantidade > 0) {
                Surface(shadowElevation = 8.dp) {
                    Button(
                        onClick = onCarrinhoClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(16.dp)
                    ) {
                        val rotuloItens = if (quantidade == 1) "item" else "itens"
                        Text("Ver carrinho • $quantidade $rotuloItens • ${viewModel.subtotal.paraReal()}")
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            viewModel.pedidoAtual?.let { pedido ->
                item {
                    Card(
                        onClick = onAcompanharClick,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(pedido.status.emoji, fontSize = 28.sp)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Pedido #${pedido.numero}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "${pedido.status.titulo} • toque para acompanhar",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = busca,
                    onValueChange = { busca = it },
                    placeholder = { Text("Buscar produtos") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (busca.isNotEmpty()) {
                            IconButton(onClick = { busca = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Limpar busca")
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(Categoria.entries) { cat ->
                        FilterChip(
                            selected = cat == categoria,
                            onClick = { categoria = cat },
                            label = { Text(cat.titulo) }
                        )
                    }
                }
            }

            if (produtos.isEmpty()) {
                item {
                    Text(
                        "Nenhum produto encontrado.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                }
            } else {
                items(produtos, key = { it.id }) { produto ->
                    ProdutoCard(produto = produto, onClick = { onProdutoClick(produto.id) })
                }
            }
        }
    }
}

@Composable
private fun ProdutoCard(produto: Produto, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            EmojiBadge(emoji = produto.emoji, tamanho = 72.dp)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    produto.nome,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    produto.descricao,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    if (produto.tamanhos.size > 1) "A partir de ${produto.precoInicial.paraReal()}"
                    else produto.precoInicial.paraReal(),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
