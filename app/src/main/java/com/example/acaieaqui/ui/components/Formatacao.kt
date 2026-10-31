package com.example.acaieaqui.ui.components

import java.text.NumberFormat
import java.util.Locale

private val formatoReal: NumberFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))

fun Double.paraReal(): String = formatoReal.format(this)
