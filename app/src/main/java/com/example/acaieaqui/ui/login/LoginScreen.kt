package com.example.acaieaqui.ui.login

import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.acaieaqui.viewmodel.AppViewModel

@Composable
fun LoginScreen(
    viewModel: AppViewModel,
    onAutenticado: () -> Unit
) {
    var modoCadastro by rememberSaveable { mutableStateOf(false) }
    var nome by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var senha by rememberSaveable { mutableStateOf("") }
    var confirmar by rememberSaveable { mutableStateOf("") }
    var mostrarSenha by rememberSaveable { mutableStateOf(false) }
    var tentou by rememberSaveable { mutableStateOf(false) }
    var erroGeral by rememberSaveable { mutableStateOf<String?>(null) }

    val erroNome = if (modoCadastro && nome.isBlank()) "Informe seu nome" else null
    val erroEmail = if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) "Digite um e-mail válido" else null
    val erroSenha = if (senha.length < 6) "Use ao menos 6 caracteres" else null
    val erroConfirmar = if (modoCadastro && confirmar != senha) "As senhas não conferem" else null
    val formularioValido = listOf(erroNome, erroEmail, erroSenha, erroConfirmar).all { it == null }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(Modifier.height(24.dp))
            Text("🍇", fontSize = 64.sp)
            Text(
                "Açaí é Aqui",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                if (modoCadastro) "Crie sua conta para pedir" else "Entre para fazer seu pedido",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))

            if (modoCadastro) {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome") },
                    singleLine = true,
                    isError = tentou && erroNome != null,
                    supportingText = { if (tentou && erroNome != null) Text(erroNome) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("E-mail") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = tentou && erroEmail != null,
                supportingText = { if (tentou && erroEmail != null) Text(erroEmail) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = senha,
                onValueChange = { senha = it },
                label = { Text("Senha") },
                singleLine = true,
                visualTransformation = if (mostrarSenha) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    TextButton(onClick = { mostrarSenha = !mostrarSenha }) {
                        Text(if (mostrarSenha) "Ocultar" else "Mostrar")
                    }
                },
                isError = tentou && erroSenha != null,
                supportingText = { if (tentou && erroSenha != null) Text(erroSenha) },
                modifier = Modifier.fillMaxWidth()
            )

            if (modoCadastro) {
                OutlinedTextField(
                    value = confirmar,
                    onValueChange = { confirmar = it },
                    label = { Text("Confirmar senha") },
                    singleLine = true,
                    visualTransformation = if (mostrarSenha) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = tentou && erroConfirmar != null,
                    supportingText = { if (tentou && erroConfirmar != null) Text(erroConfirmar) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            erroGeral?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }

            Button(
                onClick = {
                    tentou = true
                    if (formularioValido) {
                        val resultado = if (modoCadastro) {
                            viewModel.cadastrar(nome, email, senha)
                        } else {
                            viewModel.entrar(email, senha)
                        }
                        erroGeral = resultado
                        if (resultado == null) onAutenticado()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(if (modoCadastro) "Criar conta" else "Entrar")
            }

            TextButton(onClick = {
                modoCadastro = !modoCadastro
                tentou = false
                erroGeral = null
            }) {
                Text(if (modoCadastro) "Já tenho conta. Entrar" else "Não tenho conta. Cadastrar")
            }

            if (!modoCadastro) {
                TextButton(onClick = {
                    email = "demo@acai.com"
                    senha = "123456"
                    erroGeral = null
                }) {
                    Text("Preencher com a conta de teste")
                }
            }
        }
    }
}
