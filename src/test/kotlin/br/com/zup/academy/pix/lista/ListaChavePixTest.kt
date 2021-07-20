package br.com.zup.academy.pix.lista

import br.com.zup.academy.KeymanagerListarGrpcServiceGrpc
import br.com.zup.academy.ListaChavesRequest
import br.com.zup.academy.pix.*
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class ListaChavePixTest(
    val repository: ChavePixRepository,
    val grpc: KeymanagerListarGrpcServiceGrpc.KeymanagerListarGrpcServiceBlockingStub
){

    fun novaChavePix(idTitular: UUID = UUID.randomUUID()) = ChavePix(
        chave = UUID.randomUUID().toString(),
        tipoChave = TipoChave.CHAVE_ALEATORIA,
        idTitular = idTitular,
        tipoConta = TipoConta.CONTA_POUPANCA,
        conta = ContaAssociada(
            numeroDaConta = "123456",
            agencia = "0001",
            cpfDoTitular = "00801087090",
            instituicao = ContaAssociada.ITAU_UNIBANCO_ISPB,
            nomeDoTitular = "Gabriel Grazziani"
        )
    )

    @BeforeEach
    internal fun setUp() {
        repository.deleteAll()
    }

    @Test
    internal fun `deve listar as chaves de um titular`() {
        val chavePixDeFulano = novaChavePix()
        val outraChavePixDeFulano = novaChavePix(chavePixDeFulano.idTitular)
        val chavePixDeCiclano = novaChavePix()
        repository.save(chavePixDeFulano)
        repository.save(outraChavePixDeFulano)
        repository.save(chavePixDeCiclano)

        val chaves = grpc.listar(ListaChavesRequest.newBuilder()
            .setIdTitular(chavePixDeFulano.idTitular.toString())
            .build())
            .chavesList

        assertEquals(2,chaves.size)
        //chavePixDeFulano
        assertEquals(chavePixDeFulano.idTitular.toString(),chaves[0].idTitular)
        assertEquals(chavePixDeFulano.chave,chaves[0].valorChave)
        //outraChavePixDeFulano
        assertEquals(outraChavePixDeFulano.idTitular.toString(),chaves[1].idTitular)
        assertEquals(outraChavePixDeFulano.chave,chaves[1].valorChave)
    }

    @Test
    internal fun `deve carregar uma lista vazia se nao encontrar nenhuma chave`() {
        val chavePixDeCiclano = novaChavePix()
        repository.save(chavePixDeCiclano)

        val chaves = grpc.listar(ListaChavesRequest.newBuilder()
            .setIdTitular(UUID.randomUUID().toString())
            .build())
            .chavesList

        assertEquals(0,chaves.size)
    }

    @Test
    internal fun `deve dar erro quando passar um idTitular que nao for um UUID`() {
        val error = assertThrows<StatusRuntimeException> {
            grpc.listar(ListaChavesRequest.newBuilder()
                .setIdTitular("1234345")
                .build())
        }

        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code,status.code)
            assertEquals("UUID Invalido",status.description)
        }
    }

    @Factory
    class ListaChavePixTestFactory{
        @Singleton
        fun grpc(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeymanagerListarGrpcServiceGrpc.KeymanagerListarGrpcServiceBlockingStub? {
            return KeymanagerListarGrpcServiceGrpc.newBlockingStub(channel)
        }
    }
}