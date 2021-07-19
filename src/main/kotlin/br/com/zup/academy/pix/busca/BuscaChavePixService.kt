package br.com.zup.academy.pix.busca

import br.com.zup.academy.pix.ChavePix
import br.com.zup.academy.pix.ChavePixNaoEncontradaException
import br.com.zup.academy.pix.ChavePixRepository
import javax.inject.Singleton

@Singleton
class BuscaChavePixService(
    val chavePixRepository: ChavePixRepository
) {

    fun buscaPorChave(chave: String): ChavePix {

        val chavePix = chavePixRepository.findByChave(chave)

        if(chavePix == null){
            throw ChavePixNaoEncontradaException("Chave Pix não encontrada");
        }

        return chavePix
    }

}