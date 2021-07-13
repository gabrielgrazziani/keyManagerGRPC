package br.com.zup.academy.key

data class ErroNaValidacao(
    val posuiErro: Boolean,
    val menssagem : String = ""
)