package br.com.zup.academy.pix.cadastro

import br.com.zup.academy.pix.*
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

        val resposta = erpItau.buscarConta(chavePixForm.idTitular!!.toUUID(), chavePixForm.tipoConta!!.name)
        resposta.body() ?: throw IllegalArgumentException("idTitilar e/ou tipoConta esta incorreto")

        val chavePix = chavePixForm.paraChavePix()

        return repository.save(chavePix)
    }
}