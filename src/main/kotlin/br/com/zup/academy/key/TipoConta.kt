package br.com.zup.academy.key

import br.com.zup.academy.ChavePixRequest

enum class TipoConta {
    CONTA_CORRENTE,
    CONTA_POUPANCA;
}

fun ChavePixRequest.TipoConta.map(): TipoConta {
    return when(this){
        ChavePixRequest.TipoConta.CONTA_CORRENTE -> TipoConta.CONTA_CORRENTE
        ChavePixRequest.TipoConta.CONTA_POUPANCA -> TipoConta.CONTA_POUPANCA
        else -> throw IllegalStateException("Tipo ${this.javaClass.simpleName} não suportado")
    }
}