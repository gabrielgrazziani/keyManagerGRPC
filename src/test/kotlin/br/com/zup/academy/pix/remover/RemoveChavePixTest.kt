package br.com.zup.academy.pix.remover

import br.com.zup.academy.KeymanagerRemoveGrpcServiceGrpc
import br.com.zup.academy.RemoveChavePixRequest
import br.com.zup.academy.integracao.banco_central.BancoCentralClient
import br.com.zup.academy.pix.*
import br.com.zup.academy.util.getViolacao
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class RemoveChavePixTest(
    val grpc: KeymanagerRemoveGrpcServiceGrpc.KeymanagerRemoveGrpcServiceBlockingStub,
    val repository: ChavePixRepository
){
    @Inject
    lateinit var bancoCentralClient: BancoCentralClient

    val idTitular = UUID.randomUUID()
    val idPix = UUID.randomUUID()

    @BeforeEach
    internal fun setUp() {
        repository.deleteAll()
    }

    @Test
    internal fun `deve remover um chave pix`() {
        repository.save(CHAVE_PIX)

        Mockito.`when`(bancoCentralClient.deleta(key = CHAVE_PIX.chave))
            .thenReturn(HttpResponse.status(HttpStatus.OK))

        grpc.remove(RemoveChavePixRequest.newBuilder()
            .setIdPix(idPix.toString())
            .setIdTitular(idTitular.toString())
            .build())

        assertEquals(0,repository.count())
        Mockito.verify(bancoCentralClient,Mockito.times(1)).deleta(key = CHAVE_PIX.chave)
    }

    @Test
    internal fun `nao deve remover chave pix nao encontrada`() {
        repository.save(CHAVE_PIX)

        val error = assertThrows<StatusRuntimeException> {
            grpc.remove(RemoveChavePixRequest.newBuilder()
                .setIdPix(UUID.randomUUID().toString())
                .setIdTitular(idTitular.toString())
                .build())
        }

        assertEquals(Status.NOT_FOUND.code,error.status.code)
        assertTrue(error.message!!.endsWith("Chave não encontrada"))
    }

    @Test
    internal fun `nao deve remover chave pix quando o bcb nao confirmar`() {
        repository.save(CHAVE_PIX)

        Mockito.`when`(bancoCentralClient.deleta(key = CHAVE_PIX.chave))
            .thenReturn(HttpResponse.status(HttpStatus.FORBIDDEN))

        val error = assertThrows<StatusRuntimeException> {
            grpc.remove(RemoveChavePixRequest.newBuilder()
                .setIdPix(idPix.toString())
                .setIdTitular(idTitular.toString())
                .build())
        }

        assertEquals(1,repository.count())
        assertEquals(Status.FAILED_PRECONDITION.code,error.status.code)
        assertEquals("Erro ao remover chave Pix no Banco Central do Brasil (BCB)",error.status.description)
    }

    @Test
    internal fun `deve remover chave pix mesmo quando o bcb nao encontrar a chave`() {
        repository.save(CHAVE_PIX)

        Mockito.`when`(bancoCentralClient.deleta(key = CHAVE_PIX.chave))
            .thenReturn(HttpResponse.status(HttpStatus.NOT_FOUND))

        grpc.remove(RemoveChavePixRequest.newBuilder()
            .setIdPix(idPix.toString())
            .setIdTitular(idTitular.toString())
            .build())

        assertEquals(0,repository.count(),"Quantidade de chave pix no banco")
    }

    @Test
    internal fun `nao deve remover chave pix que nao pertense ao titular`() {
        repository.save(CHAVE_PIX)

        val error = assertThrows<StatusRuntimeException> {
            grpc.remove(RemoveChavePixRequest.newBuilder()
                .setIdPix(idPix.toString())
                .setIdTitular(UUID.randomUUID().toString())
                .build())
        }

        assertEquals(Status.INVALID_ARGUMENT.code,error.status.code)
        assertTrue(error.message!!.endsWith("A chave pode ser removida somente pelo seu dono"))
    }

    @Test
    internal fun `nao deve remover chave pix se o id nao for o UUID valido`() {
        repository.save(CHAVE_PIX)

        val error = assertThrows<StatusRuntimeException> {
            grpc.remove(RemoveChavePixRequest.newBuilder()
                .setIdPix(idPix.toString())
                .setIdTitular("312-123-132")
                .build())
        }

        assertEquals(Status.INVALID_ARGUMENT.code,error.status.code)
        val violation = error.getViolacao(0)
        assertEquals("UUID invalido",violation?.description)
    }

    val CHAVE_PIX = ChavePix(
        idTitular = idTitular,
        tipoChave = TipoChave.EMAIL,
        tipoConta = TipoConta.CONTA_POUPANCA,
        chave = "gabriel.barbosa@zup.com.br",
        uuid = idPix,
        conta = ContaAssociada(
            nomeDoTitular = "Gabriel Grazziani",
            cpfDoTitular = "00801087090",
            instituicao = ContaAssociada.ITAU_UNIBANCO_ISPB,
            numeroDaConta = "123456",
            agencia = "0001"
        )
    )

    @MockBean(BancoCentralClient::class)
    fun bcbClient() = Mockito.mock(BancoCentralClient::class.java)

    @Factory
    class FactoryClass{
        @Singleton
        fun grpc(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeymanagerRemoveGrpcServiceGrpc.KeymanagerRemoveGrpcServiceBlockingStub{
            return KeymanagerRemoveGrpcServiceGrpc.newBlockingStub(channel)
        }
    }
}