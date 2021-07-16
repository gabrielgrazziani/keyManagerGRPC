package br.com.zup.academy.pix.cadastro

import br.com.zup.academy.integracao.banco_central.BancoCentralClient
import br.com.zup.academy.integracao.banco_central.CreatePixKeyRequest
import br.com.zup.academy.pix.*
import io.micronaut.validation.Validated
import javax.inject.Singleton
import javax.validation.Valid

@Validated
@Singleton
class CadastroChavePixService(
    val repository: ChavePixRepository,
    val erpItau: ErpItau,
    val bancoCentral: BancoCentralClient
) {

    fun cadastro(@Valid chavePixForm: ChavePixForm): ChavePix {

        if (repository.existsByChave(chavePixForm.chave!!)) // 1
            throw ChavePixExistenteException("ja existe uma chave com este valor")

        val respostaItau = erpItau.buscarConta(chavePixForm.idTitular!!.toUUID(), chavePixForm.tipoConta!!.name)
        val dadosDaConta = respostaItau.body() ?: throw IllegalArgumentException("idTitilar e/ou tipoConta esta incorreto")

        val respostaBcb = bancoCentral.cria(CreatePixKeyRequest(dadosDaConta,chavePixForm))
        val key = respostaBcb.body()?.key ?: throw IllegalArgumentException("?") //TODO

        val chavePix = chavePixForm.paraChavePix(key)

        return repository.save(chavePix)
    }
}