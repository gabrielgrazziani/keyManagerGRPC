package br.com.zup.academy.pix

import br.com.zup.academy.ChavePixRequest
import br.com.zup.academy.pix.cadastro.ChavePixForm
import java.util.*

fun ChavePixRequest.paraChavePixForm(): ChavePixForm {
    return ChavePixForm(
        idTitilar = UUID.fromString(idTitular),
        tipoChave = tipoChave.map(),
        tipoConta = tipoConta.map(),
        chave = chave
    )
}