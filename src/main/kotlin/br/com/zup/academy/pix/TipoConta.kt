package br.com.zup.academy.pix

import br.com.zup.academy.ChavePixRequest

enum class TipoConta {
    CONTA_CORRENTE,
    CONTA_POUPANCA;
}

fun ChavePixRequest.TipoConta.map(): TipoConta? {
    return try {
        TipoConta.valueOf(this.name)
    }catch (e: IllegalArgumentException){
        null
    }
}