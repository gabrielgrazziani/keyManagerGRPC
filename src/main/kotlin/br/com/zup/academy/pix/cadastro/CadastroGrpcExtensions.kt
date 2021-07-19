package br.com.zup.academy.pix

import br.com.zup.academy.ChavePixRequest
import br.com.zup.academy.pix.cadastro.ChavePixForm

fun ChavePixRequest.paraChavePixForm(): ChavePixForm {
    return ChavePixForm(
        idTitular = idTitular,
        tipoChave = tipoChave.paraTipoChave(),
        tipoConta = tipoConta.paraTipoConta(),
        chave = chave
    )
}

fun ChavePixRequest.TipoConta.paraTipoConta(): TipoConta? {
    return try {
        TipoConta.valueOf(this.name)
    }catch (e: IllegalArgumentException){
        null
    }
}


fun ChavePixRequest.TipoChave.paraTipoChave(): TipoChave? {
    return try {
        TipoChave.valueOf(this.name)
    }catch (e: IllegalArgumentException){
        null
    }
}