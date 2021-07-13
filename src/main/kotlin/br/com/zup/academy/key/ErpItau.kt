package br.com.zup.academy.key

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client
import java.util.*

@Client("\${endereco_erp_itau}")
interface ErpItau {

    @Get("/clientes/{clienteId}/contas")
    fun buscarConta(@PathVariable clienteId: UUID,@QueryValue tipo: String): HttpResponse<BuscarContaResponse>
}

class BuscarContaResponse