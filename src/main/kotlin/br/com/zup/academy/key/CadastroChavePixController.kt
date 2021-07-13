package br.com.zup.academy.key

import br.com.zup.academy.ChavePixRequest
import br.com.zup.academy.ChavePixResponse
import br.com.zup.academy.KeyManagerGRPCServiceGrpc
import io.grpc.Status
import io.grpc.stub.StreamObserver
import javax.inject.Singleton

@Singleton
class CadastroChavePixController: KeyManagerGRPCServiceGrpc.KeyManagerGRPCServiceImplBase() {

    override fun cadastro(request: ChavePixRequest, responseObserver: StreamObserver<ChavePixResponse>) {
        val erroValidacao = ChavePixForm(request).validar()

        if(erroValidacao.posuiErro){
            responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription(erroValidacao.menssagem)
                .asRuntimeException())
        }

        println(ChavePixForm(request))

        responseObserver.onNext(ChavePixResponse
                                    .newBuilder()
                                    .setId("Teste")
                                    .build())
        responseObserver.onCompleted()
    }
}

data class ChavePixForm(
    val idTitilar: String,
    val tipoChave: TipoChave,
    val tipoConta: TipoConta,
    val chave: String = ""
){

    constructor(request: ChavePixRequest): this(
        idTitilar = request.idTitular,
        tipoChave = request.tipoChave.map(),
        tipoConta = request.tipoConta.map(),
        chave = request.chave
    )

    fun validar(): ErroNaValidacao{
        return if(idTitilar.isBlank()){
            ErroNaValidacao(true,"O compo 'idTitilar' nao pode estar em branco")
        }else{
            tipoChave.validarFormatoChave(chave)
        }
    }
}