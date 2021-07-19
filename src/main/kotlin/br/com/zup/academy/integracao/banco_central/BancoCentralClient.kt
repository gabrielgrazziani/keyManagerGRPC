package br.com.zup.academy.integracao.banco_central

import br.com.zup.academy.pix.DadosDaContaResponse
import br.com.zup.academy.pix.TitularResponse
import br.com.zup.academy.pix.cadastro.ChavePixForm
import br.com.zup.academy.pix.paraKeyType
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client
import java.time.LocalDateTime


@Client("\${endereco_banco_central}")
interface BancoCentralClient{

    @Post("pix/keys")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    fun cria(@Body createPixKeyRequest: CreatePixKeyRequest): HttpResponse<CreatePixKeyResponse>

    @Delete("pix/keys/{key}")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    fun deleta(@PathVariable key: String, @Body deletePixKey: DeletePixKeyRequest = DeletePixKeyRequest(key = key)): HttpResponse<Any>

    @Get("pix/keys/{key}")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    fun busca(@PathVariable key: String): HttpResponse<PixKeyDetailsResponse>

}

data class DeletePixKeyRequest(
    val key: String,
    val participant: String = "60701190"
)

data class CreatePixKeyRequest(
    val keyType: keyType,
    val key: String,
    val bankAccount: BankAccountRequest,
    val owner: OwnerRequest,
){
    constructor(dadosConta: DadosDaContaResponse,chavePixForm: ChavePixForm): this(
        keyType = chavePixForm.tipoChave?.paraKeyType()!!,
        key = chavePixForm.chave ?: "",
        bankAccount = BankAccountRequest(
            dadosConta = dadosConta,
            accountType = chavePixForm.tipoConta?.siglaBancoCentral ?: ""
        ),
        owner = OwnerRequest(dadosConta.titular)
    )
}

data class CreatePixKeyResponse(
    val key: String
)

data class BankAccountRequest(
    val participant: String = "60701190",
    val branch: String,
    val accountNumber: String,
    val accountType: AccountType,
){
    constructor(dadosConta: DadosDaContaResponse,accountType: String): this(
        branch = dadosConta.agencia,
        accountNumber = dadosConta.numero,
        accountType = AccountType.valueOf(accountType)
    )
}

data class OwnerRequest(
    val type: String,
    val name: String,
    val taxIdNumber: String,
){
    constructor(titular: TitularResponse): this(
        name = titular.nome,
        taxIdNumber = titular.cpf,
        type = "NATURAL_PERSON"
    )
}

enum class AccountType{
    CACC // Conta Corrent
    ,SVGS; // Conta Poupança
}

enum class keyType{
    CPF,CNPJ,EMAIL,RANDOM,PHONE;
}

data class PixKeyDetailsResponse(
    val keyType: keyType,
    val key: String,
    val createdAt: LocalDateTime,
    val bankAccount: BankAccountResponse,
    val owner: OwnerResponse
){

}

data class BankAccountResponse(
    val participant: String,
    val branch: String,
    val accountNumber: String,
    val accountType: AccountType,
)

data class OwnerResponse(
    val type: String,
    val name: String,
    val taxIdNumber: String,
)