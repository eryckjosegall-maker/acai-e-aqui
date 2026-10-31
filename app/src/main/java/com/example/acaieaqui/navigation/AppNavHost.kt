package com.example.acaieaqui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.acaieaqui.ui.acompanhamento.AcompanhamentoScreen
import com.example.acaieaqui.ui.carrinho.CarrinhoScreen
import com.example.acaieaqui.ui.detalhes.DetalhesScreen
import com.example.acaieaqui.ui.finalizacao.FinalizacaoScreen
import com.example.acaieaqui.ui.home.HomeScreen
import com.example.acaieaqui.ui.login.LoginScreen
import com.example.acaieaqui.viewmodel.AppViewModel

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    // Um único ViewModel compartilhado por todas as telas
    val viewModel: AppViewModel = viewModel()

    NavHost(navController = navController, startDestination = Rotas.LOGIN) {

        composable(Rotas.LOGIN) {
            LoginScreen(
                viewModel = viewModel,
                onAutenticado = {
                    navController.navigate(Rotas.HOME) {
                        popUpTo(Rotas.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Rotas.HOME) {
            HomeScreen(
                viewModel = viewModel,
                onProdutoClick = { id -> navController.navigate(Rotas.detalhes(id)) },
                onCarrinhoClick = { navController.navigate(Rotas.CARRINHO) },
                onAcompanharClick = { navController.navigate(Rotas.ACOMPANHAMENTO) },
                onSair = {
                    viewModel.sair()
                    navController.navigate(Rotas.LOGIN) {
                        popUpTo(Rotas.HOME) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Rotas.DETALHES,
            arguments = listOf(navArgument("produtoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("produtoId") ?: 0
            DetalhesScreen(
                produtoId = id,
                viewModel = viewModel,
                onVoltar = { navController.popBackStack() },
                onAdicionado = { navController.popBackStack() }
            )
        }

        composable(Rotas.CARRINHO) {
            CarrinhoScreen(
                viewModel = viewModel,
                onVoltar = { navController.popBackStack() },
                onFinalizar = { navController.navigate(Rotas.FINALIZACAO) }
            )
        }

        composable(Rotas.FINALIZACAO) {
            FinalizacaoScreen(
                viewModel = viewModel,
                onVoltar = { navController.popBackStack() },
                onConfirmado = {
                    navController.navigate(Rotas.ACOMPANHAMENTO) {
                        // remove carrinho e finalização da pilha; o "voltar" cai na Home
                        popUpTo(Rotas.HOME)
                    }
                }
            )
        }

        composable(Rotas.ACOMPANHAMENTO) {
            AcompanhamentoScreen(
                viewModel = viewModel,
                onVoltarInicio = { navController.popBackStack(Rotas.HOME, inclusive = false) }
            )
        }
    }
}
