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
    val tipo: String,
    val agencia: String,
    val numero: String,
    val instituicao: InstituicaoResponse,
    val titular: TitularResponse
)

data class DeletePixKeyRequest(
    val key: String,
    val participant: String = "60701190"
)

data class InstituicaoResponse(
    val nome: String,
    val ispb: String
)

data class TitularResponse(
    val id: String,
    val nome: String,
    val cpf: String
)