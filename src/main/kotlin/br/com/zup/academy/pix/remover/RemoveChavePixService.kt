package br.com.zup.academy.pix.remover

import br.com.zup.academy.pix.ChavePixNaoEncontradaException
import br.com.zup.academy.pix.ChavePixRepository
import br.com.zup.academy.pix.toUUID
import io.micronaut.validation.Validated
import javax.inject.Singleton
import javax.validation.Valid

@Singleton
@Validated
class RemoveChavePixService(
    val repository: ChavePixRepository
) {

    fun remover(@Valid removeChavePixForm: RemoveChavePixForm){

        val chavePix = repository.findByUuid(removeChavePixForm.idPix!!.toUUID())

        if(chavePix == null){
            throw ChavePixNaoEncontradaException("Chave não encontrada")
        }

        if(chavePix.idTitular != removeChavePixForm.idTitular?.toUUID()){
            throw IllegalArgumentException("A chave pode ser removida somente pelo seu dono")
        }

        repository.delete(chavePix)
    }
}