package br.com.zup.academy.key

import br.com.zup.academy.ChavePixRequest
import br.com.zup.academy.KeyManagerGRPCServiceGrpc
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.util.*
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class CadastroChavePixControllerTest(
    val repository: ChavePixRepository,
    val erpItau: ErpItau,
    val grpcService: KeyManagerGRPCServiceGrpc.KeyManagerGRPCServiceBlockingStub
) {

    @BeforeEach
    internal fun setUp() {
        repository.deleteAll()
    }

    @Test
    internal fun `deve cadastrar um chava do tipo EMAIL`() {
        val email = "gabriel.barbosa@zup.com.br"
        val idTitular = "18e39430-e4a9-11eb-ba80-0242ac130004"
        Mockito.`when`(erpItau.buscarConta(idTitular.toUUID(),"CONTA_POUPANCA"))
            .thenReturn(HttpResponse.ok())

        val response = grpcService.cadastro(ChavePixRequest.newBuilder()
            .setChave(email)
            .setTipoChave(ChavePixRequest.TipoChave.EMAIL)
            .setTipoConta(ChavePixRequest.TipoConta.CONTA_POUPANCA)
            .setIdTitular(idTitular)
            .build())

        assertNotEquals("",response.id)
        val chave = repository.findByUuid(response.id.toUUID())
        assertNotNull(chave)
        assertEquals(email,chave!!.chave)
    }

    @Test
    internal fun `nao deve cadastrar um chava invalida do tipo EMAIL`() {
        val email = "gabriel.barbosazup.com.br"
        val idTitular = "18e39430-e4a9-11eb-ba80-0242ac130004"
        Mockito.`when`(erpItau.buscarConta(idTitular.toUUID(),"CONTA_POUPANCA"))
            .thenReturn(HttpResponse.ok())


        val error = assertThrows<StatusRuntimeException> {
            grpcService.cadastro(ChavePixRequest.newBuilder()
                .setChave(email)
                .setTipoChave(ChavePixRequest.TipoChave.EMAIL)
                .setTipoConta(ChavePixRequest.TipoConta.CONTA_POUPANCA)
                .setIdTitular(idTitular)
                .build())
        }

        assertEquals(Status.INVALID_ARGUMENT.code,error.status.code)
        assertEquals("INVALID_ARGUMENT: Não e um Email valido",error.message)
        assertEquals(0,repository.count())
    }

    @Test
    internal fun `deve cadastrar um chava do tipo CHAVE_ALEATORIA`() {
        val idTitular = "18e39430-e4a9-11eb-ba80-0242ac130004"
        Mockito.`when`(erpItau.buscarConta(idTitular.toUUID(),"CONTA_POUPANCA"))
            .thenReturn(HttpResponse.ok())

        val response = grpcService.cadastro(ChavePixRequest.newBuilder()
            .setTipoChave(ChavePixRequest.TipoChave.CHAVE_ALEATORIA)
            .setTipoConta(ChavePixRequest.TipoConta.CONTA_POUPANCA)
            .setIdTitular(idTitular)
            .build())

        val chave = repository.findByUuid(response.id.toUUID())
        assertNotNull(chave)
        assertDoesNotThrow(){
            chave!!.chave.toUUID()
        }
    }

    @Test
    internal fun `nao deve cadastrar um chava do tipo CHAVE_ALEATORIA quado o campo chave estiver preenchido`() {
        val idTitular = "18e39430-e4a9-11eb-ba80-0242ac130004"
        Mockito.`when`(erpItau.buscarConta(idTitular.toUUID(),"CONTA_POUPANCA"))
            .thenReturn(HttpResponse.ok())

        val error = assertThrows<StatusRuntimeException> {
            grpcService.cadastro(ChavePixRequest.newBuilder()
                .setChave("teste")
                .setTipoChave(ChavePixRequest.TipoChave.CHAVE_ALEATORIA)
                .setTipoConta(ChavePixRequest.TipoConta.CONTA_POUPANCA)
                .setIdTitular(idTitular)
                .build())
        }

        assertEquals(Status.INVALID_ARGUMENT.code,error.status.code)
        assertEquals("INVALID_ARGUMENT: Para criar uma chave aleatoria o campo 'chave' deve estar em branco",error.message)
        assertEquals(0,repository.count())
    }

    @Test
    internal fun `nao deve cadastrar chaves repetidas`() {
        val email = "gabriel.barbosa@zup.com.br"
        val idTitular = "18e39430-e4a9-11eb-ba80-0242ac130004"
        Mockito.`when`(erpItau.buscarConta(idTitular.toUUID(),"CONTA_POUPANCA"))
            .thenReturn(HttpResponse.ok())
        repository.save(ChavePix(
            tipoChave = TipoChave.EMAIL,
            tipoConta = TipoConta.CONTA_POUPANCA,
            idTitilar = idTitular.toUUID(),
            chave = email
        ))

        val error = assertThrows<StatusRuntimeException> {
            grpcService.cadastro(ChavePixRequest.newBuilder()
                .setChave(email)
                .setTipoChave(ChavePixRequest.TipoChave.EMAIL)
                .setTipoConta(ChavePixRequest.TipoConta.CONTA_POUPANCA)
                .setIdTitular(idTitular)
                .build())
        }

        assertEquals(Status.ALREADY_EXISTS.code,error.status.code)
        assertEquals("ALREADY_EXISTS: ja existe uma chave com este valor",error.message)
        assertEquals(1,repository.count())
    }

    @Test
    internal fun `nao deve cadastrar caso nao encontre no ERP do itau um cliente com o idTitular e com o tipo de conta informado`() {
        val email = "gabriel.barbosa@zup.com.br"
        val idTitular = "18e39430-e4a9-11eb-ba80-0242ac130004"
        Mockito.`when`(erpItau.buscarConta(idTitular.toUUID(),"CONTA_CORRENTE"))
            .thenReturn(HttpResponse.notFound())

        val error = assertThrows<StatusRuntimeException> {
            grpcService.cadastro(ChavePixRequest.newBuilder()
                .setChave(email)
                .setTipoChave(ChavePixRequest.TipoChave.EMAIL)
                .setTipoConta(ChavePixRequest.TipoConta.CONTA_CORRENTE)
                .setIdTitular(idTitular)
                .build())
        }

        assertEquals(Status.NOT_FOUND.code,error.status.code)
        assertEquals("NOT_FOUND: idTitilar e/ou tipoConta esta incorreto",error.message)
        assertEquals(0,repository.count())
    }

    fun String.toUUID() = UUID.fromString(this)

    @Factory
    class FactoryClass{
        @Singleton
        @Replaces(value = ErpItau::class)
        fun erpItau() = Mockito.mock(ErpItau::class.java)

        @Singleton
        fun grpc(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerGRPCServiceGrpc.KeyManagerGRPCServiceBlockingStub? {
            return KeyManagerGRPCServiceGrpc.newBlockingStub(channel)
        }
    }
}