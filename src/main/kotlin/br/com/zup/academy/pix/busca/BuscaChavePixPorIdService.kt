package br.com.zup.academy.pix.busca

import br.com.zup.academy.pix.*
import io.micronaut.core.annotation.Introspected
import io.micronaut.validation.Validated
import javax.inject.Singleton
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@Singleton
@Validated
class BuscaChavePixPorIdService(
    val repository: ChavePixRepository
) {

    fun busca(@Valid busca: BuscaPorIdForm): ChavePix {

        val chavePix = repository.findByUuid(busca.idPix.toUUID())

        if(chavePix == null){
            throw ChavePixNaoEncontradaException("Chave Pix não encontrada")
        }

        if(!chavePix.pertense(busca.idTitular.toUUID())){
            throw IllegalArgumentException("A chave so pode ser vista pelo seu dono")
        }

        return chavePix
    }
}

@Introspected
data class BuscaPorIdForm(
    @field:NotBlank
    @field:UUIDValido
    val idPix: String,
    @field:NotBlank
    @field:UUIDValido
    val idTitular: String,
)