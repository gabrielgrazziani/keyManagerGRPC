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