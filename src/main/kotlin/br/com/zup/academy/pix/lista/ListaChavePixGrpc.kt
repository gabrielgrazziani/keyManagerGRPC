package br.com.zup.academy.pix.lista

import br.com.zup.academy.KeymanagerListarGrpcServiceGrpc
import br.com.zup.academy.ListaChavesRequest
import br.com.zup.academy.ListaChavesResponse
import br.com.zup.academy.pix.*
import br.com.zup.academy.pix.busca.paraTimestamp
import br.com.zup.academy.shared.grpc.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Singleton

@Singleton
@ErrorHandler
class ListaChavePixGrpc(
    val repository: ChavePixRepository,
    val uuidValidator: UUIDValidator
): KeymanagerListarGrpcServiceGrpc.KeymanagerListarGrpcServiceImplBase() {

    override fun listar(request: ListaChavesRequest, responseObserver: StreamObserver<ListaChavesResponse>) {
        if (!uuidValidator.isValid(request.idTitular,null)){
            throw IllegalArgumentException("UUID Invalido")
        }

        val chaves = repository
            .findByIdTitular(idTitular = request.idTitular.toUUID())
            .map { it.paraChavesResponse() }
            .let {
                ListaChavesResponse.newBuilder()
                    .addAllChaves(it)
                    .build()
            }
        responseObserver.onNext(chaves)
        responseObserver.onCompleted()
    }
}

fun ChavePix.paraChavesResponse(): ListaChavesResponse.ChavesResponse{
    return ListaChavesResponse.ChavesResponse.newBuilder()
        .setTipoChave(tipoChave.paraEnumGrpc())
        .setValorChave(chave)
        .setIdTitular(idTitular.toString())
        .setIdPix(uuid.toString())
        .setTipoConta(tipoConta.paraEnumGrpc())
        .setCriadoEm(criadoEm.paraTimestamp())
        .build()
}