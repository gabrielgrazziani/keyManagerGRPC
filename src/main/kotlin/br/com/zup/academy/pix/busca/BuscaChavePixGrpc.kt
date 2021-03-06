package br.com.zup.academy.pix.busca

import br.com.zup.academy.BuscaChavePixPorChave
import br.com.zup.academy.BuscaChavePixPorIdPixRequest
import br.com.zup.academy.BuscaChavePixResponse
import br.com.zup.academy.KeymanagerBuscaGrpcServiceGrpc
import br.com.zup.academy.integracao.banco_central.BancoCentralClient
import br.com.zup.academy.pix.ChavePixNaoEncontradaException
import br.com.zup.academy.pix.ChavePixRepository
import br.com.zup.academy.shared.grpc.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Singleton

@Singleton
@ErrorHandler
class BuscaChavePixGrpc(
    val repository: ChavePixRepository,
    val bcb: BancoCentralClient,
    val buscaChavePixPorIdService: BuscaChavePixPorIdService
): KeymanagerBuscaGrpcServiceGrpc.KeymanagerBuscaGrpcServiceImplBase() {

    override fun buscaPorChave(
        request: BuscaChavePixPorChave,
        responseObserver: StreamObserver<BuscaChavePixResponse>
    ) {
        val chavePix = repository.findByChave(request.chave)

        if(chavePix == null){
            val respostaBcb = bcb.busca(request.chave)
            val dados = respostaBcb.body() ?:  throw ChavePixNaoEncontradaException("Chave Pix não encontrada")
            responseObserver.onNext(dados.paraBuscaChavePixResponse())
        }else{
            responseObserver.onNext(chavePix.paraBuscaChavePixResponse())
        }

        responseObserver.onCompleted()
    }

    override fun buscaPorIdPix(
        request: BuscaChavePixPorIdPixRequest,
        responseObserver: StreamObserver<BuscaChavePixResponse>
    ) {
        val chavePix = buscaChavePixPorIdService.busca(BuscaPorIdForm(
            idTitular = request.idTitular,
            idPix = request.idPix
        ))

        responseObserver.onNext(chavePix.paraBuscaChavePixResponse())
        responseObserver.onCompleted()
    }
}