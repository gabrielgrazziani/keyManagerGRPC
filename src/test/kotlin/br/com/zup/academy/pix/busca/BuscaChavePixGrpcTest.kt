package br.com.zup.academy.pix.busca

import br.com.zup.academy.BuscaChavePixPorChave
import br.com.zup.academy.KeymanagerBuscaGrpcServiceGrpc
import br.com.zup.academy.integracao.banco_central.*
import br.com.zup.academy.pix.*
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class BuscaChavePixGrpcTest(
    val grpc: KeymanagerBuscaGrpcServiceGrpc.KeymanagerBuscaGrpcServiceBlockingStub,
    val repository: ChavePixRepository
){

    @Inject
    lateinit var bancoCentral: BancoCentralClient

    val CHAVE_PIX: ChavePix
    val PIX_KEY_DETAILS_RESPONSE: PixKeyDetailsResponse

    init {
        CHAVE_PIX = ChavePix(
            chave = UUID.randomUUID().toString(),
            tipoChave = TipoChave.CHAVE_ALEATORIA,
            idTitular = UUID.randomUUID(),
            tipoConta = TipoConta.CONTA_POUPANCA,
            conta = ContaAssociada(
                numeroDaConta = "123456",
                agencia = "0001",
                cpfDoTitular = "00801087090",
                instituicao = ContaAssociada.ITAU_UNIBANCO_ISPB,
                nomeDoTitular = "Gabriel Grazziani"
            )
        )

        PIX_KEY_DETAILS_RESPONSE = PixKeyDetailsResponse(
            keyType = keyType.RANDOM,
            key = CHAVE_PIX.chave,
            createdAt = CHAVE_PIX.criadoEm,
            bankAccount = BankAccountResponse(
                accountNumber = CHAVE_PIX.conta.numeroDaConta,
                accountType = AccountType.SVGS,
                branch = CHAVE_PIX.conta.agencia,
                participant = CHAVE_PIX.conta.instituicao
            ),
            owner = OwnerResponse(
                type = "",
                name = CHAVE_PIX.conta.nomeDoTitular,
                taxIdNumber = CHAVE_PIX.conta.cpfDoTitular
            )
        )
    }

    @BeforeEach
    internal fun setUp() {
        repository.deleteAll()
    }

    @Test
    internal fun `deve buscar os dados de um chave pix baseado na chave`() {
        repository.save(CHAVE_PIX)

        val response = grpc.buscaPorChave(BuscaChavePixPorChave.newBuilder()
            .setChave(CHAVE_PIX.chave)
            .build())

        assertEquals(CHAVE_PIX.idTitular.toString(),response.idTitular,"idTitular")
        assertEquals(CHAVE_PIX.uuid.toString(),response.idPix,"idPix")
        assertEquals(CHAVE_PIX.chave,response.chave.chave,"chave pix")
        assertEquals(CHAVE_PIX.tipoChave.name,response.chave.tipo.name,"tipo chave")
        assertEquals(CHAVE_PIX.criadoEm.paraTimestamp().seconds,response.chave.criadaEm.seconds,"criadaEm")
        assertEquals(CHAVE_PIX.tipoConta.name,response.chave.conta.tipo.name,"tipo conta")
        assertEquals(CHAVE_PIX.conta.instituicao,response.chave.conta.instituicao,"instituicao")
        assertEquals(CHAVE_PIX.conta.nomeDoTitular,response.chave.conta.nomeDoTitular,"nome do titular")
        assertEquals(CHAVE_PIX.conta.cpfDoTitular,response.chave.conta.cpfDoTitular,"cpf")
        assertEquals(CHAVE_PIX.conta.agencia,response.chave.conta.agencia,"agencia")
        assertEquals(CHAVE_PIX.conta.numeroDaConta,response.chave.conta.numeroDaConta,"numero da conta")
    }

    @Test
    internal fun `nao deve encontrar um cahve pix`() {
        Mockito.`when`(bancoCentral.busca(CHAVE_PIX.chave))
            .thenReturn(HttpResponse.notFound())

        val error = assertThrows<StatusRuntimeException> {
            grpc.buscaPorChave(BuscaChavePixPorChave.newBuilder()
                .setChave(CHAVE_PIX.chave)
                .build())
        }

        assertEquals(Status.NOT_FOUND.code,error.status.code)
        assertEquals("Chave Pix não encontrada",error.status.description)
    }

    @Test
    internal fun `deve buscar os dados de um chave pix baseado na chave no bcb`() {
        Mockito.`when`(bancoCentral.busca(CHAVE_PIX.chave))
            .thenReturn(HttpResponse.ok(PIX_KEY_DETAILS_RESPONSE))

        val response = grpc.buscaPorChave(BuscaChavePixPorChave.newBuilder()
            .setChave(CHAVE_PIX.chave)
            .build())


        assertEquals(CHAVE_PIX.chave,response.chave.chave,"chave pix")
        assertEquals(CHAVE_PIX.tipoChave.name,response.chave.tipo.name,"tipo chave")
        assertEquals(CHAVE_PIX.criadoEm.paraTimestamp().seconds,response.chave.criadaEm.seconds,"criadaEm")
        assertEquals(CHAVE_PIX.tipoConta.name,response.chave.conta.tipo.name,"tipo conta")
        assertEquals(CHAVE_PIX.conta.instituicao,response.chave.conta.instituicao,"instituicao")
        assertEquals(CHAVE_PIX.conta.nomeDoTitular,response.chave.conta.nomeDoTitular,"nome do titular")
        assertEquals(CHAVE_PIX.conta.cpfDoTitular,response.chave.conta.cpfDoTitular,"cpf")
        assertEquals(CHAVE_PIX.conta.agencia,response.chave.conta.agencia,"agencia")
        assertEquals(CHAVE_PIX.conta.numeroDaConta,response.chave.conta.numeroDaConta,"numero da conta")
    }

    @MockBean(BancoCentralClient::class)
    fun bcb() = Mockito.mock(BancoCentralClient::class.java)

    @Factory
    class BuscaGrpcServiceGrpcFactory{
        @Singleton
        fun grpc(@GrpcChannel(GrpcServerChannel.NAME) chanell: ManagedChannel): KeymanagerBuscaGrpcServiceGrpc.KeymanagerBuscaGrpcServiceBlockingStub{
            return KeymanagerBuscaGrpcServiceGrpc.newBlockingStub(chanell)
        }
    }
}