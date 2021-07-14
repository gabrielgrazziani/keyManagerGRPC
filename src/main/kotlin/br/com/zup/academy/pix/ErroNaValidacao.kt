package br.com.zup.academy.pix

data class ErroNaValidacao(
    val posuiErro: Boolean,
    val menssagem : String = ""
)