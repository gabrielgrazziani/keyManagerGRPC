package br.com.zup.academy.pix.busca

import br.com.zup.academy.BuscaChavePixPorIdPixRequest
import br.com.zup.academy.KeymanagerBuscaGrpcServiceGrpc
import br.com.zup.academy.pix.*
import br.com.zup.academy.util.getViolacaons
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class BuscaChavePixPorIdGrpcTest(
    val grpc: KeymanagerBuscaGrpcServiceGrpc.KeymanagerBuscaGrpcServiceBlockingStub,
    val repository: ChavePixRepository
){

    val CHAVE_PIX: ChavePix

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
    }

    @BeforeEach
    internal fun setUp() {
        repository.deleteAll()
    }

    @Test
    internal fun `deve buscar os dados de um chave pix baseado no id`() {
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
    internal fun `nao deve buscar os dados quando o titular nao for dono da chave`() {
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

    @Factory
    class BuscaGrpcServiceGrpcFactory{
        @Singleton
        fun grpc(@GrpcChannel(GrpcServerChannel.NAME) chanell: ManagedChannel): KeymanagerBuscaGrpcServiceGrpc.KeymanagerBuscaGrpcServiceBlockingStub{
            return KeymanagerBuscaGrpcServiceGrpc.newBlockingStub(chanell)
        }
    }
}