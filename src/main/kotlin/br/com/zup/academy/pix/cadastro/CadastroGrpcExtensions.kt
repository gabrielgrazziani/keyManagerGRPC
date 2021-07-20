package br.com.zup.academy.pix

import br.com.zup.academy.ChavePixRequest
import br.com.zup.academy.pix.cadastro.ChavePixForm

fun ChavePixRequest.paraChavePixForm(): ChavePixForm {
    return ChavePixForm(
        idTitular = idTitular,
        tipoChave = tipoChave.paraMeuEnum(),
        tipoConta = tipoConta.paraMeuEnum(),
        chave = chave
    )
}

fun br.com.zup.academy.TipoConta.paraMeuEnum(): TipoConta? {
    return when(this){
        br.com.zup.academy.TipoConta.DESCONHECIDO_TIPO_CONTA -> null
        else -> TipoConta.valueOf(this.name)
    }
}


fun br.com.zup.academy.TipoChave.paraMeuEnum(): TipoChave? {
    return when(this){
        br.com.zup.academy.TipoChave.DESCONHECIDO_TIPO_CHAVE -> null
        else -> TipoChave.valueOf(this.name)
    }
}

fun TipoConta.paraEnumGrpc(): br.com.zup.academy.TipoConta {
    return br.com.zup.academy.TipoConta.valueOf(this.name)
}


fun TipoChave.paraEnumGrpc(): br.com.zup.academy.TipoChave {
    return br.com.zup.academy.TipoChave.valueOf(this.name)
}