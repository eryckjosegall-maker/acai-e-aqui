package com.example.acaieaqui.ui.finalizacao

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.acaieaqui.model.DadosEntrega
import com.example.acaieaqui.model.FormaPagamento
import com.example.acaieaqui.ui.components.LinhaValor
import com.example.acaieaqui.ui.components.paraReal
import com.example.acaieaqui.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinalizacaoScreen(
    viewModel: AppViewModel,
    onVoltar: () -> Unit,
    onConfirmado: () -> Unit
) {
    var nome by rememberSaveable { mutableStateOf(viewModel.usuarioLogado?.nome.orEmpty()) }
    var telefone by rememberSaveable { mutableStateOf("") }
    var rua by rememberSaveable { mutableStateOf("") }
    var numero by rememberSaveable { mutableStateOf("") }
    var bairro by rememberSaveable { mutableStateOf("") }
    var complemento by rememberSaveable { mutableStateOf("") }
    var observacao by rememberSaveable { mutableStateOf("") }
    var pagamento by rememberSaveable { mutableStateOf(FormaPagamento.PIX) }
    var tentou by rememberSaveable { mutableStateOf(false) }

    // Fotografia do carrinho ao entrar na tela (evita "piscar" zerado ao confirmar)
    val itens = remember { viewModel.carrinho.toList() }
    val subtotal = remember { viewModel.subtotal }
    val taxa = AppViewModel.TAXA_ENTREGA

    val erroNome = if (nome.isBlank()) "Informe seu nome" else null
    val erroTelefone = if (telefone.count { it.isDigit() } < 10) "Informe DDD + número" else null
    val erroRua = if (rua.isBlank()) "Informe a rua" else null
    val erroNumero = if (numero.isBlank()) "Obrigatório" else null
    val erroBairro = if (bairro.isBlank()) "Informe o bairro" else null
    val valido = listOf(erroNome, erroTelefone, erroRua, erroNumero, erroBairro).all { it == null }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Finalizar pedido") },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Seus dados", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Campo(nome, { nome = it }, "Nome", erroNome, tentou)
            Campo(telefone, { telefone = it }, "Telefone com DDD", erroTelefone, tentou, teclado = KeyboardType.Phone)

            Spacer(Modifier.height(8.dp))
            Text("Endereço de entrega", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Campo(rua, { rua = it }, "Rua / Avenida", erroRua, tentou)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Campo(numero, { numero = it }, "Número", erroNumero, tentou, modifier = Modifier.weight(1f))
                Campo(bairro, { bairro = it }, "Bairro", erroBairro, tentou, modifier = Modifier.weight(2f))
            }
            Campo(complemento, { complemento = it }, "Complemento (opcional)", null, false)

            Spacer(Modifier.height(8.dp))
            Text("Forma de pagamento", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            FormaPagamento.entries.forEach { forma ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = forma == pagamento,
                            onClick = { pagamento = forma },
                            role = Role.RadioButton
                        )
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = forma == pagamento, onClick = null)
                    Spacer(Modifier.width(12.dp))
                    Text(forma.titulo)
                }
            }

            Campo(observacao, { observacao = it }, "Observações (opcional)", null, false, singleLine = false)

            Spacer(Modifier.height(8.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Resumo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    itens.forEach {
                        LinhaValor("${it.quantidade}x ${it.produto.nome} (${it.tamanho.nome})", it.total.paraReal())
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    LinhaValor("Subtotal", subtotal.paraReal())
                    LinhaValor("Taxa de entrega", taxa.paraReal())
                    LinhaValor("Total", (subtotal + taxa).paraReal(), destaque = true)
                }
            }

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    tentou = true
                    if (valido && itens.isNotEmpty()) {
                        viewModel.confirmarPedido(
                            DadosEntrega(
                                nome = nome.trim(),
                                telefone = telefone.trim(),
                                rua = rua.trim(),
                                numero = numero.trim(),
                                bairro = bairro.trim(),
                                complemento = complemento.trim(),
                                pagamento = pagamento,
                                observacao = observacao.trim()
                            )
                        )
                        onConfirmado()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Confirmar pedido")
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun Campo(
    valor: String,
    onValor: (String) -> Unit,
    rotulo: String,
    erro: String?,
    mostrarErro: Boolean,
    modifier: Modifier = Modifier.fillMaxWidth(),
    teclado: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValor,
        label = { Text(rotulo) },
        isError = mostrarErro && erro != null,
        supportingText = { if (mostrarErro && erro != null) Text(erro) },
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = teclado),
        modifier = modifier
    )
}
