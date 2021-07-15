package br.com.zup.academy.pix.remover

import br.com.zup.academy.KeymanagerRemoveGrpcServiceGrpc
import br.com.zup.academy.RemoveChavePixRequest
import br.com.zup.academy.shared.grpc.ErrorHandler
import com.google.protobuf.Empty
import io.grpc.stub.StreamObserver
import javax.inject.Singleton

@Singleton
@ErrorHandler
class RemoveChavePixGrpc(
    val removeChavePixService: RemoveChavePixService
): KeymanagerRemoveGrpcServiceGrpc.KeymanagerRemoveGrpcServiceImplBase() {

    override fun remove(request: RemoveChavePixRequest, responseObserver: StreamObserver<Empty>) {
        removeChavePixService.remover(request.paraRemoveChavePixForm())
        responseObserver.onNext(Empty.newBuilder().build())
        responseObserver.onCompleted()
    }
}

fun RemoveChavePixRequest.paraRemoveChavePixForm(): RemoveChavePixForm{
    return RemoveChavePixForm(
        idPix = idPix,
        idTitular = idTitular
    )
}