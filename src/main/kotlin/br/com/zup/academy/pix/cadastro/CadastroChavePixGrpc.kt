package br.com.zup.academy.pix.cadastro

import br.com.zup.academy.ChavePixRequest
import br.com.zup.academy.ChavePixResponse
import br.com.zup.academy.KeyManagerGRPCServiceGrpc
import br.com.zup.academy.pix.paraChavePixForm
import br.com.zup.academy.shared.grpc.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Singleton

@Singleton
@ErrorHandler
class CadastroChavePixGrpc(
    val cadastroChavePixService: CadastroChavePixService
): KeyManagerGRPCServiceGrpc.KeyManagerGRPCServiceImplBase() {

    override fun cadastro(request: ChavePixRequest, responseObserver: StreamObserver<ChavePixResponse>) {
        val chavePixForm = request.paraChavePixForm()

        val chavePix = cadastroChavePixService.cadastro(chavePixForm)

        responseObserver.onNext(ChavePixResponse
                                    .newBuilder()
                                    .setId(chavePix.uuid.toString())
                                    .build())
        responseObserver.onCompleted()
    }
}


//        if(repository.existsByChave(chavePixForm.chave)){
//            responseObserver.onError(Status.ALREADY_EXISTS
//                .withDescription("ja existe uma chave com este valor")
//                .asRuntimeException())
//            return
//        }
//
//        val erroValidacao = chavePixForm.validar()
//        if(erroValidacao.posuiErro){
//            responseObserver.onError(Status.INVALID_ARGUMENT
//                .withDescription(erroValidacao.menssagem)
//                .asRuntimeException())
//            return
//        }
//
//        val resposta = erpItau.buscarConta(chavePixForm.idTitilar,chavePixForm.tipoConta.name)
//        if(resposta.status.code == HttpStatus.NOT_FOUND.code){
//            responseObserver.onError(Status.NOT_FOUND
//                .withDescription("idTitilar e/ou tipoConta esta incorreto")
//                .asRuntimeException())
//            return
//        }