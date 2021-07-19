package br.com.zup.academy.pix.busca

import br.com.zup.academy.BuscaChavePixPorIdPixRequest
import br.com.zup.academy.KeymanagerBuscaGrpcServiceGrpc
import br.com.zup.academy.integracao.banco_central.*
import br.com.zup.academy.pix.*
import br.com.zup.academy.util.getViolacaons
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class BuscaChavePixPorIdGrpcTest(
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
    internal fun `deve buscar os dados de um chave pix baseado no id`() {
        Mockito.`when`(bancoCentral.busca(CHAVE_PIX.chave))
            .thenReturn(HttpResponse.ok(PIX_KEY_DETAILS_RESPONSE))

        repository.save(CHAVE_PIX)

        val response = grpc.buscaPorIdPix(BuscaChavePixPorIdPixRequest.newBuilder()
            .setIdPix(CHAVE_PIX.uuid.toString())
            .setIdTitular(CHAVE_PIX.idTitular.toString())
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
    internal fun `nao deve encontrar um chave pix`() {
        val error = assertThrows<StatusRuntimeException> {
            grpc.buscaPorIdPix(BuscaChavePixPorIdPixRequest.newBuilder()
                .setIdPix(CHAVE_PIX.uuid.toString())
                .setIdTitular(CHAVE_PIX.idTitular.toString())
                .build())
        }

        assertEquals(Status.NOT_FOUND.code,error.status.code)
        assertEquals("Chave Pix não encontrada",error.status.description)
    }

    @Test
    internal fun `nao deve encontrar um chave pix se nao encontrar no BCB`() {
        val error = assertThrows<StatusRuntimeException> {
            grpc.buscaPorIdPix(BuscaChavePixPorIdPixRequest.newBuilder()
                .setIdPix(CHAVE_PIX.uuid.toString())
                .setIdTitular(CHAVE_PIX.idTitular.toString())
                .build())
        }

        assertEquals(Status.NOT_FOUND.code,error.status.code)
        assertEquals("Chave Pix não encontrada",error.status.description)
    }

    @Test
    internal fun `nao deve buscar os dados quando o titular nao for dono da chave`() {
        Mockito.`when`(bancoCentral.busca(CHAVE_PIX.chave))
            .thenReturn(HttpResponse.notFound())
        repository.save(CHAVE_PIX)

        val error = assertThrows<StatusRuntimeException> {
            grpc.buscaPorIdPix(BuscaChavePixPorIdPixRequest.newBuilder()
                .setIdPix(CHAVE_PIX.uuid.toString())
                .setIdTitular(UUID.randomUUID().toString())
                .build())
        }

        assertEquals(Status.INVALID_ARGUMENT.code,error.status.code)
        assertEquals("A chave so pode ser vista pelo seu dono",error.status.description)
    }

    @Test
    internal fun `nao deve buscar quando os dados do form estiverem incorretos`() {
        val error = assertThrows<StatusRuntimeException> {
            grpc.buscaPorIdPix(BuscaChavePixPorIdPixRequest.newBuilder()
                .setIdPix("632738732")
                .setIdTitular("0976325608923")
                .build())
        }

        assertEquals(Status.INVALID_ARGUMENT.code,error.status.code)
        val violacaons = error.getViolacaons()
        assertThat(violacaons, containsInAnyOrder(
            "idPix" to "UUID invalido",
            "idTitular" to "UUID invalido"
        ))
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