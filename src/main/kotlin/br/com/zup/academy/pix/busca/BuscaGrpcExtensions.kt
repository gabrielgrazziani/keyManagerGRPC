package br.com.zup.academy.pix.busca

import br.com.zup.academy.BuscaChavePixResponse
import br.com.zup.academy.integracao.banco_central.PixKeyDetailsResponse
import br.com.zup.academy.integracao.banco_central.keyType
import br.com.zup.academy.pix.ChavePix
import br.com.zup.academy.pix.TipoChave
import br.com.zup.academy.pix.paraTipoChave
import com.google.protobuf.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId

fun ChavePix.paraBuscaChavePixResponse(): BuscaChavePixResponse {
    return BuscaChavePixResponse.newBuilder()
        .setChave(BuscaChavePixResponse.ChavePix.newBuilder()
            .setChave(this.chave)
            .setTipo(this.tipoChave.paraTipoChave())
            .setCriadaEm(this.criadoEm.paraTimestamp())
            .setConta(BuscaChavePixResponse.ContaInfo.newBuilder()
                .setTipo(BuscaChavePixResponse.TipoConta.valueOf(this.tipoConta.name))
                .setAgencia(this.conta.agencia)
                .setNumeroDaConta(this.conta.numeroDaConta)
                .setInstituicao(this.conta.instituicao)
                .setCpfDoTitular(this.conta.cpfDoTitular)
                .setNomeDoTitular(this.conta.nomeDoTitular)
                .build()
            )
            .build())
        .setIdPix(this.uuid.toString())
        .setIdTitular(this.idTitular.toString())
        .build()
}

fun PixKeyDetailsResponse.paraBuscaChavePixResponse(): BuscaChavePixResponse {
    return BuscaChavePixResponse.newBuilder()
        .setChave(BuscaChavePixResponse.ChavePix.newBuilder()
            .setChave(this.key)
            .setTipo(this.keyType.paraTipoChave()!!.paraTipoChave())
            .setCriadaEm(this.createdAt.paraTimestamp())
            .setConta(BuscaChavePixResponse.ContaInfo.newBuilder()
                .setTipo(BuscaChavePixResponse.TipoConta.valueOf(this.bankAccount.accountType.paraTipoChave()!!.name))
                .setAgencia(this.bankAccount.branch)
                .setNumeroDaConta(this.bankAccount.accountNumber)
                .setInstituicao(this.bankAccount.participant)
                .setCpfDoTitular(this.owner.taxIdNumber)
                .setNomeDoTitular(this.owner.name)
                .build()
            )
            .build())
        .build()
}

fun keyType.paraTipoChave(): BuscaChavePixResponse.TipoChave{
    return BuscaChavePixResponse.TipoChave.valueOf(this.name)
}

fun TipoChave.paraTipoChave(): BuscaChavePixResponse.TipoChave{
    return BuscaChavePixResponse.TipoChave.valueOf(this.name)
}

fun LocalDateTime.paraTimestamp(): Timestamp {
    val instant = this.atZone(ZoneId.of("UTC")).toInstant()
    return Timestamp.newBuilder()
        .setSeconds(instant.epochSecond)
        .setNanos(instant.nano)
        .build()
}