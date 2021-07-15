package br.com.zup.academy.pix.remover

import br.com.zup.academy.KeymanagerRemoveGrpcServiceGrpc
import br.com.zup.academy.RemoveChavePixRequest
import br.com.zup.academy.pix.ChavePix
import br.com.zup.academy.pix.ChavePixRepository
import br.com.zup.academy.pix.TipoChave
import br.com.zup.academy.pix.TipoConta
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class RemoveChavePixTest(
    val grpc: KeymanagerRemoveGrpcServiceGrpc.KeymanagerRemoveGrpcServiceBlockingStub,
    val repository: ChavePixRepository
){

    val idTitular = UUID.randomUUID()
    val idPix = UUID.randomUUID()

    @BeforeEach
    internal fun setUp() {
        repository.deleteAll()
    }

    @Test
    internal fun `deve remover um chave pix`() {
        repository.save(novaChavePix())

        grpc.remove(RemoveChavePixRequest.newBuilder()
            .setIdPix(idPix.toString())
            .setIdTitular(idTitular.toString())
            .build())

        assertEquals(0,repository.count())
    }

    @Test
    internal fun `nao deve remover chave pix nao encontrada`() {
        repository.save(novaChavePix())

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
    internal fun `nao deve remover chave pix que nao pertense ao titular`() {
        repository.save(novaChavePix())

        val error = assertThrows<StatusRuntimeException> {
            grpc.remove(RemoveChavePixRequest.newBuilder()
                .setIdPix(idPix.toString())
                .setIdTitular(UUID.randomUUID().toString())
                .build())
        }

        assertEquals(Status.INVALID_ARGUMENT.code,error.status.code)
        assertTrue(error.message!!.endsWith("A chave pode ser removida somente pelo seu dono"))
    }

    fun novaChavePix() = ChavePix(
        idTitular = idTitular,
        tipoChave = TipoChave.EMAIL,
        tipoConta = TipoConta.CONTA_POUPANCA,
        chave = "gabriel.barbosa@zup.com.br",
        uuid = idPix
    )

    @Factory
    class FactoryClass{
        @Singleton
        fun grpc(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeymanagerRemoveGrpcServiceGrpc.KeymanagerRemoveGrpcServiceBlockingStub{
            return KeymanagerRemoveGrpcServiceGrpc.newBlockingStub(channel)
        }
    }
}