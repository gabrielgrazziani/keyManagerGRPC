package br.com.zup.academy.pix

import br.com.zup.academy.integracao.banco_central.keyType

private object MaperKeyTypeBcb {
    val tipoChaveParakeyType: Map<TipoChave,keyType> = mapOf(
        TipoChave.CPF to  keyType.CPF,
        TipoChave.TELEFONE_CELULAR to  keyType.PHONE,
        TipoChave.EMAIL to  keyType.EMAIL,
        TipoChave.CHAVE_ALEATORIA to  keyType.RANDOM,
    )

    val keyTypeParaTipoChave: Map<keyType,TipoChave> = mapOf(
        keyType.CPF to  TipoChave.CPF,
        keyType.PHONE to  TipoChave.TELEFONE_CELULAR,
        keyType.EMAIL to  TipoChave.EMAIL,
        keyType.RANDOM to  TipoChave.CHAVE_ALEATORIA
    )
}

fun TipoChave.paraKeyType() = MaperKeyTypeBcb.tipoChaveParakeyType.get(this)
fun keyType.paraTipoChave() = MaperKeyTypeBcb.keyTypeParaTipoChave.get(this)