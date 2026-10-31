package com.example.acaieaqui.ui.detalhes

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.acaieaqui.data.ProdutosRepository
import com.example.acaieaqui.model.Produto
import com.example.acaieaqui.ui.components.EmojiBadge
import com.example.acaieaqui.ui.components.SeletorQuantidade
import com.example.acaieaqui.ui.components.TelaVazia
import com.example.acaieaqui.ui.components.paraReal
import com.example.acaieaqui.viewmodel.AppViewModel

@Composable
fun DetalhesScreen(
    produtoId: Int,
    viewModel: AppViewModel,
    onVoltar: () -> Unit,
    onAdicionado: () -> Unit
) {
    val produto = ProdutosRepository.buscarPorId(produtoId)
    if (produto == null) {
        TelaVazia(
            emoji = "🤔",
            titulo = "Produto não encontrado",
            texto = "Esse produto não está mais disponível.",
            textoBotao = "Voltar",
            onClick = onVoltar
        )
    } else {
        DetalhesConteudo(produto, viewModel, onVoltar, onAdicionado)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetalhesConteudo(
    produto: Produto,
    viewModel: AppViewModel,
    onVoltar: () -> Unit,
    onAdicionado: () -> Unit
) {
    val context = LocalContext.current

    var tamanhoIdx by rememberSaveable { mutableIntStateOf(0) }
    var selecionados by rememberSaveable { mutableStateOf(listOf<Int>()) }
    var quantidade by rememberSaveable { mutableIntStateOf(1) }

    val tamanho = produto.tamanhos[tamanhoIdx.coerceIn(0, produto.tamanhos.lastIndex)]
    val complementos = produto.complementosDisponiveis.filter { it.id in selecionados }
    val total = (tamanho.preco + complementos.sumOf { it.preco }) * quantidade

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(produto.nome) },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Button(
                    onClick = {
                        viewModel.adicionarAoCarrinho(produto, tamanho, complementos, quantidade)
                        Toast.makeText(context, "Adicionado ao carrinho", Toast.LENGTH_SHORT).show()
                        onAdicionado()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp)
                ) {
                    Text("Adicionar • ${total.paraReal()}")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                EmojiBadge(emoji = produto.emoji, tamanho = 120.dp)
            }
            Text(
                produto.nome,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(produto.descricao, style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(8.dp))
            Text("Tamanho", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            produto.tamanhos.forEachIndexed { indice, opcao ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = indice == tamanhoIdx,
                            onClick = { tamanhoIdx = indice },
                            role = Role.RadioButton
                        )
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = indice == tamanhoIdx, onClick = null)
                    Spacer(Modifier.width(12.dp))
                    Text(opcao.nome, modifier = Modifier.weight(1f))
                    Text(opcao.preco.paraReal())
                }
            }

            if (produto.complementosDisponiveis.isNotEmpty()) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text("Complementos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                produto.complementosDisponiveis.forEach { comp ->
                    val marcado = comp.id in selecionados
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .toggleable(
                                value = marcado,
                                onValueChange = {
                                    selecionados = if (marcado) selecionados - comp.id else selecionados + comp.id
                                },
                                role = Role.Checkbox
                            )
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(checked = marcado, onCheckedChange = null)
                        Spacer(Modifier.width(12.dp))
                        Text(comp.nome, modifier = Modifier.weight(1f))
                        Text("+ ${comp.preco.paraReal()}")
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Quantidade", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                SeletorQuantidade(
                    quantidade = quantidade,
                    onMenos = { if (quantidade > 1) quantidade-- },
                    onMais = { if (quantidade < 20) quantidade++ }
                )
            }
        }
    }
}
