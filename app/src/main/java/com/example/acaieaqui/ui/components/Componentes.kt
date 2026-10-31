package com.example.acaieaqui.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Quadradinho colorido com um emoji, usado no lugar de fotos dos produtos. */
@Composable
fun EmojiBadge(
    emoji: String,
    modifier: Modifier = Modifier,
    tamanho: Dp = 64.dp
) {
    Box(
        modifier = modifier
            .size(tamanho)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(text = emoji, fontSize = (tamanho.value * 0.5f).sp)
    }
}

@Composable
fun SeletorQuantidade(
    quantidade: Int,
    onMenos: () -> Unit,
    onMais: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        FilledTonalIconButton(onClick = onMenos, modifier = Modifier.size(36.dp)) {
            Text("−", style = MaterialTheme.typography.titleMedium)
        }
        Text(
            text = quantidade.toString(),
            modifier = Modifier.widthIn(min = 36.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium
        )
        FilledTonalIconButton(onClick = onMais, modifier = Modifier.size(36.dp)) {
            Text("+", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun LinhaValor(rotulo: String, valor: String, destaque: Boolean = false) {
    val estilo = if (destaque) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium
    val peso = if (destaque) FontWeight.Bold else FontWeight.Normal
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = rotulo,
            style = estilo,
            fontWeight = peso,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        )
        Text(text = valor, style = estilo, fontWeight = peso)
    }
}

@Composable
fun TelaVazia(
    emoji: String,
    titulo: String,
    texto: String,
    textoBotao: String,
    onClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 56.sp)
            Spacer(Modifier.height(12.dp))
            Text(titulo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(texto, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
            Spacer(Modifier.height(16.dp))
            Button(onClick = onClick) { Text(textoBotao) }
        }
    }
}
