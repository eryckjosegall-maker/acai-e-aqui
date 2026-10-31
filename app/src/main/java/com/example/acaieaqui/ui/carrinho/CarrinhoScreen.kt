package com.example.acaieaqui.ui.carrinho

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.acaieaqui.model.ItemCarrinho
import com.example.acaieaqui.ui.components.EmojiBadge
import com.example.acaieaqui.ui.components.LinhaValor
import com.example.acaieaqui.ui.components.SeletorQuantidade
import com.example.acaieaqui.ui.components.TelaVazia
import com.example.acaieaqui.ui.components.paraReal
import com.example.acaieaqui.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarrinhoScreen(
    viewModel: AppViewModel,
    onVoltar: () -> Unit,
    onFinalizar: () -> Unit
) {
    val itens = viewModel.carrinho
    val subtotal = viewModel.subtotal

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carrinho") },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        bottomBar = {
            if (itens.isNotEmpty()) {
                Surface(shadowElevation = 8.dp) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(16.dp)
                    ) {
                        LinhaValor("Subtotal", subtotal.paraReal())
                        LinhaValor("Taxa de entrega", AppViewModel.TAXA_ENTREGA.paraReal())
                        HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                        LinhaValor("Total", (subtotal + AppViewModel.TAXA_ENTREGA).paraReal(), destaque = true)
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = onFinalizar, modifier = Modifier.fillMaxWidth()) {
                            Text("Finalizar pedido")
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (itens.isEmpty()) {
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                TelaVazia(
                    emoji = "🛒",
                    titulo = "Seu carrinho está vazio",
                    texto = "Escolha um açaí delicioso para começar.",
                    textoBotao = "Ver produtos",
                    onClick = onVoltar
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(itens, key = { it.id }) { item ->
                    ItemCard(
                        item = item,
                        onMenos = { viewModel.alterarQuantidade(item.id, -1) },
                        onMais = { viewModel.alterarQuantidade(item.id, +1) },
                        onRemover = { viewModel.removerDoCarrinho(item.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ItemCard(
    item: ItemCarrinho,
    onMenos: () -> Unit,
    onMais: () -> Unit,
    onRemover: () -> Unit
) {
    val detalhes = buildString {
        append(item.tamanho.nome)
        if (item.complementos.isNotEmpty()) {
            append(" • ")
            append(item.complementos.joinToString(", ") { it.nome })
        }
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
            EmojiBadge(emoji = item.produto.emoji, tamanho = 56.dp)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    item.produto.nome,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(detalhes, style = MaterialTheme.typography.bodySmall)
                Text(
                    item.total.paraReal(),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                SeletorQuantidade(
                    quantidade = item.quantidade,
                    onMenos = onMenos,
                    onMais = onMais
                )
            }
            IconButton(onClick = onRemover) {
                Icon(Icons.Default.Delete, contentDescription = "Remover item")
            }
        }
    }
}
