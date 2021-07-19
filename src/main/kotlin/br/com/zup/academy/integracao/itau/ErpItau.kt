package br.com.zup.academy.pix

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client
import java.util.*

@Client("\${endereco_erp_itau}")
interface ErpItau {

    @Get("/clientes/{clienteId}/contas")
    fun buscarConta(@PathVariable clienteId: UUID,@QueryValue tipo: String): HttpResponse<DadosDaContaResponse>
}

data class DadosDaContaResponse(
    val agencia: String,
    val numero: String,
    val titular: TitularResponse
) {
    fun paraContaAssociada(): ContaAssociada {
        return ContaAssociada(
            agencia = agencia,
            numeroDaConta = numero,
            instituicao = ContaAssociada.ITAU_UNIBANCO_ISPB,
            cpfDoTitular = titular.cpf,
            nomeDoTitular = titular.nome
        )
    }
}

data class TitularResponse(
    val nome: String,
    val cpf: String
)