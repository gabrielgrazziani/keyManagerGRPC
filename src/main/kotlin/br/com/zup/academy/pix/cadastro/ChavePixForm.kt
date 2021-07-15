package br.com.zup.academy.pix.cadastro

import br.com.zup.academy.pix.*
import io.micronaut.core.annotation.Introspected
import io.micronaut.core.annotation.NonNull
import java.util.*
import javax.validation.constraints.Size

@ValidKeyPix
@Introspected
data class ChavePixForm(
    @field:NonNull
    val idTitular: UUID,
    @field:NonNull
    val tipoChave: TipoChave?,
    @field:NonNull
    val tipoConta: TipoConta?,
    @field:NonNull
    @field:Size(max = 77)
    val chave: String? = ""
){

    fun validar(): ErroNaValidacao {
        return  tipoChave!!.validarFormatoChave(chave!!)
    }

    fun paraChavePix(): ChavePix {
        return ChavePix(
            idTitular = idTitular!!,
            tipoChave = tipoChave!!,
            tipoConta = tipoConta!!,
            chave = tipoChave.transformar(chave!!)
        )
    }
}