package br.com.zup.academy.pix.cadastro

import br.com.zup.academy.ChavePixRequest
import br.com.zup.academy.KeyManagerGRPCServiceGrpc
import br.com.zup.academy.pix.*
import com.google.rpc.BadRequest
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
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
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class CadastroChavePixTest(
    val repository: ChavePixRepository,
    val erpItau: ErpItau,
    val grpcService: KeyManagerGRPCServiceGrpc.KeyManagerGRPCServiceBlockingStub
) {

    @BeforeEach
    internal fun setUp() {
        repository.deleteAll()
    }

    @Test
    internal fun `deve cadastrar um chave do tipo EMAIL`() {
        val email = "gabriel.barbosa@zup.com.br"
        val idTitular = "18e39430-e4a9-11eb-ba80-0242ac130004"
        Mockito.`when`(erpItau.buscarConta(idTitular.toUUID(),"CONTA_POUPANCA"))
            .thenReturn(HttpResponse.ok(BuscarContaResponse()))

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
    internal fun `nao deve cadastrar um chave invalida do tipo EMAIL`() {
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
        assertEquals(0,repository.count())
        val violation = error.getViolacao(0)
        assertEquals("Não e um Email valido",violation?.description)
    }

    @Test
    internal fun `deve cadastrar um chave do tipo CHAVE_ALEATORIA`() {
        val idTitular = "18e39430-e4a9-11eb-ba80-0242ac130004"
        Mockito.`when`(erpItau.buscarConta(idTitular.toUUID(),"CONTA_POUPANCA"))
            .thenReturn(HttpResponse.ok(BuscarContaResponse()))

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
    internal fun `nao deve cadastrar um chave do tipo CHAVE_ALEATORIA quado o campo chave estiver preenchido`() {
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
        assertEquals(0,repository.count())
        val violation = error.getViolacao(0)
        assertEquals("Para criar uma chave aleatoria o campo 'chave' deve estar em branco",violation?.description)
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

        assertEquals(Status.INVALID_ARGUMENT.code,error.status.code)
        assertEquals("INVALID_ARGUMENT: idTitilar e/ou tipoConta esta incorreto",error.message)
        assertEquals(0,repository.count())
    }

    fun StatusRuntimeException.getViolacao(index: Int): BadRequest.FieldViolation? {
        val statusProto = StatusProto.fromThrowable(this)
        val badRequest = statusProto?.detailsList?.get(0)?.unpack(BadRequest::class.java)
        return badRequest?.getFieldViolations(index);
    }

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