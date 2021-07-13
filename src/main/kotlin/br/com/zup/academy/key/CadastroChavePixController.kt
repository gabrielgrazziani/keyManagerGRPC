package br.com.zup.academy.key

import br.com.zup.academy.ChavePixRequest
import br.com.zup.academy.ChavePixResponse
import br.com.zup.academy.KeyManagerGRPCServiceGrpc
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.http.HttpStatus
import java.util.*
import javax.inject.Singleton
import javax.validation.Validator

@Singleton
class CadastroChavePixController(
    val repository: ChavePixRepository,
    val erpItau: ErpItau,
    val validator: Validator
): KeyManagerGRPCServiceGrpc.KeyManagerGRPCServiceImplBase() {

    override fun cadastro(request: ChavePixRequest, responseObserver: StreamObserver<ChavePixResponse>) {
        val chavePixForm = request.map()

        if(repository.existsByChave(chavePixForm.chave)){
            responseObserver.onError(Status.ALREADY_EXISTS
                .withDescription("ja existe uma chave com este valor")
                .asRuntimeException())
            return
        }

        val erroValidacao = chavePixForm.validar()
        if(erroValidacao.posuiErro){
            responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription(erroValidacao.menssagem)
                .asRuntimeException())
            return
        }

        val resposta = erpItau.buscarConta(chavePixForm.idTitilar,chavePixForm.tipoConta.name)
        if(resposta.status.code == HttpStatus.NOT_FOUND.code){
            responseObserver.onError(Status.NOT_FOUND
                .withDescription("idTitilar e/ou tipoConta esta incorreto")
                .asRuntimeException())
            return
        }else if(resposta.status.code != HttpStatus.OK.code){
            responseObserver.onError(Status.INTERNAL
                .asRuntimeException())
            return
        }

        val chavePix = chavePixForm.map()

        repository.save(chavePix)

        responseObserver.onNext(ChavePixResponse
                                    .newBuilder()
                                    .setId(chavePix.uuid.toString())
                                    .build())
        responseObserver.onCompleted()
    }
}

data class ChavePixForm(
    val idTitilar: UUID,
    val tipoChave: TipoChave,
    val tipoConta: TipoConta,
    val chave: String = ""
){

    fun validar(): ErroNaValidacao{
        return  tipoChave.validarFormatoChave(chave)
    }

    fun map(): ChavePix {
        return ChavePix(
            idTitilar = idTitilar,
            tipoChave = tipoChave,
            tipoConta = tipoConta,
            chave = tipoChave.transformar(chave)
        )
    }
}

fun ChavePixRequest.map(): ChavePixForm{
    return ChavePixForm(
        idTitilar = UUID.fromString(idTitular),
        tipoChave = tipoChave.map(),
        tipoConta = tipoConta.map(),
        chave = chave
    )
}