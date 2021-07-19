package br.com.zup.academy.pix.busca

import br.com.zup.academy.BuscaChavePixPorChave
import br.com.zup.academy.BuscaChavePixResponse
import br.com.zup.academy.KeymanagerBuscaGrpcServiceGrpc
import br.com.zup.academy.shared.grpc.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Singleton

@Singleton
@ErrorHandler
class BuscaChavePixGrpc(
    val buscaChavePixService: BuscaChavePixService
): KeymanagerBuscaGrpcServiceGrpc.KeymanagerBuscaGrpcServiceImplBase() {

    override fun buscaPorChave(
        request: BuscaChavePixPorChave,
        responseObserver: StreamObserver<BuscaChavePixResponse>
    ) {
        val chavePix = buscaChavePixService.buscaPorChave(request.chave)

        responseObserver.onNext(chavePix.paraBuscaChavePixResponse())
        responseObserver.onCompleted()
    }
}