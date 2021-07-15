package br.com.zup.academy.pix.cadastro

import br.com.zup.academy.pix.ChavePix
import br.com.zup.academy.pix.ChavePixExistenteException
import br.com.zup.academy.pix.ChavePixRepository
import br.com.zup.academy.pix.ErpItau
import io.micronaut.validation.Validated
import javax.inject.Singleton
import javax.validation.Valid

@Validated
@Singleton
class CadastroChavePixService(
    val repository: ChavePixRepository,
    val erpItau: ErpItau
) {

    fun cadastro(@Valid chavePixForm: ChavePixForm): ChavePix {

        if (repository.existsByChave(chavePixForm.chave!!)) // 1
            throw ChavePixExistenteException("ja existe uma chave com este valor")

        val resposta = erpItau.buscarConta(chavePixForm.idTitular, chavePixForm.tipoConta!!.name)
        resposta.body() ?: throw IllegalArgumentException("idTitilar e/ou tipoConta esta incorreto")

        val chavePix = chavePixForm.paraChavePix()

        return repository.save(chavePix)
    }
}