package br.com.zup.academy.pix.busca

import br.com.zup.academy.BuscaChavePixResponse
import br.com.zup.academy.pix.ChavePix
import com.google.protobuf.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId

fun ChavePix.paraBuscaChavePixResponse(): BuscaChavePixResponse {
    return BuscaChavePixResponse.newBuilder()
        .setChave(BuscaChavePixResponse.ChavePix.newBuilder()
            .setChave(this.chave)
            .setTipo(BuscaChavePixResponse.TipoChave.valueOf(this.tipoChave.name))
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

fun LocalDateTime.paraTimestamp(): Timestamp {
    val instant = this.atZone(ZoneId.of("UTC")).toInstant()
    return Timestamp.newBuilder()
        .setSeconds(instant.epochSecond)
        .setNanos(instant.nano)
        .build()
}