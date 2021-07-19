package br.com.zup.academy.pix

import br.com.zup.academy.integracao.banco_central.AccountType

private object MaperAccountTypeBcb {
    val tipoContaParaAccountType: Map<TipoConta,AccountType> = mapOf(
        TipoConta.CONTA_POUPANCA to  AccountType.SVGS,
        TipoConta.CONTA_CORRENTE to  AccountType.CACC
    )

    val accountTypeParaTipoConta: Map<AccountType,TipoConta> = mapOf(
        AccountType.SVGS to  TipoConta.CONTA_POUPANCA,
        AccountType.CACC to  TipoConta.CONTA_CORRENTE
    )
}

fun TipoConta.paraAccountType() = MaperAccountTypeBcb.tipoContaParaAccountType.get(this)
fun AccountType.paraTipoChave() = MaperAccountTypeBcb.accountTypeParaTipoConta.get(this)