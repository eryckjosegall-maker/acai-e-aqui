package com.example.acaieaqui.ui.acompanhamento

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.acaieaqui.model.Pedido
import com.example.acaieaqui.model.StatusPedido
import com.example.acaieaqui.ui.components.LinhaValor
import com.example.acaieaqui.ui.components.TelaVazia
import com.example.acaieaqui.ui.components.paraReal
import com.example.acaieaqui.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcompanhamentoScreen(
    viewModel: AppViewModel,
    onVoltarInicio: () -> Unit
) {
    val pedido = viewModel.pedidoAtual

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (pedido != null) "Pedido #${pedido.numero}" else "Meu pedido") },
                navigationIcon = {
                    IconButton(onClick = onVoltarInicio) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar ao início")
                    }
                }
            )
        }
    ) { padding ->
        if (pedido == null) {
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                TelaVazia(
                    emoji = "🧾",
                    titulo = "Nenhum pedido ainda",
                    texto = "Quando você fizer um pedido, ele aparece aqui.",
                    textoBotao = "Ver produtos",
                    onClick = onVoltarInicio
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CartaoStatus(pedido)
                LinhaDoTempo(pedido.status)
                CartaoResumo(pedido)

                Text(
                    "O andamento do pedido é simulado para fins de demonstração.",
                    style = MaterialTheme.typography.bodySmall
                )
                Button(onClick = onVoltarInicio, modifier = Modifier.fillMaxWidth()) {
                    Text("Voltar ao início")
                }
            }
        }
    }
}

@Composable
private fun CartaoStatus(pedido: Pedido) {
    val progresso = (pedido.status.ordinal + 1f) / StatusPedido.entries.size
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(pedido.status.emoji, fontSize = 40.sp)
            Text(
                pedido.status.titulo,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(pedido.status.descricao, style = MaterialTheme.typography.bodyMedium)
            LinearProgressIndicator(
                progress = { progresso },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun LinhaDoTempo(statusAtual: StatusPedido) {
    val etapas = StatusPedido.entries
    val corAtiva = MaterialTheme.colorScheme.primary
    val corInativa = MaterialTheme.colorScheme.outlineVariant

    Column {
        etapas.forEachIndexed { indice, etapa ->
            val concluida = indice <= statusAtual.ordinal
            val ultima = indice == etapas.lastIndex

            Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(if (concluida) corAtiva else corInativa),
                        contentAlignment = Alignment.Center
                    ) {
                        if (concluida) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    if (!ultima) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .weight(1f)
                                .background(if (indice < statusAtual.ordinal) corAtiva else corInativa)
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.padding(bottom = if (ultima) 0.dp else 20.dp)) {
                    Text(
                        etapa.titulo,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (indice == statusAtual.ordinal) FontWeight.Bold else FontWeight.Normal
                    )
                    Text(etapa.descricao, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun CartaoResumo(pedido: Pedido) {
    val entrega = pedido.entrega
    val endereco = buildString {
        append("${entrega.rua}, ${entrega.numero} - ${entrega.bairro}")
        if (entrega.complemento.isNotBlank()) append(" (${entrega.complemento})")
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Resumo do pedido", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            pedido.itens.forEach {
                LinhaValor("${it.quantidade}x ${it.produto.nome} (${it.tamanho.nome})", it.total.paraReal())
                if (it.complementos.isNotEmpty()) {
                    Text(
                        "   " + it.complementos.joinToString(", ") { c -> c.nome },
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            LinhaValor("Subtotal", pedido.subtotal.paraReal())
            LinhaValor("Taxa de entrega", pedido.taxaEntrega.paraReal())
            LinhaValor("Total", pedido.total.paraReal(), destaque = true)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text("Entrega", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text(endereco, style = MaterialTheme.typography.bodyMedium)
            Text("${entrega.nome} • ${entrega.telefone}", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
            Text("Pagamento", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text(entrega.pagamento.titulo, style = MaterialTheme.typography.bodyMedium)
            if (entrega.observacao.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text("Observações", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Text(entrega.observacao, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
