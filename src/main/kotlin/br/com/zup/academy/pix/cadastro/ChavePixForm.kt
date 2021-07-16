package br.com.zup.academy.pix.cadastro

import br.com.zup.academy.pix.*
import io.micronaut.core.annotation.Introspected
import io.micronaut.core.annotation.NonNull
import javax.validation.constraints.Size

@ValidKeyPix
@Introspected
data class ChavePixForm(
    @field:NonNull
    @field:UUIDValido
    val idTitular: String?,
    @field:NonNull
    val tipoChave: TipoChave?,
    @field:NonNull
    val tipoConta: TipoConta?,
    @field:NonNull
    @field:Size(max = 77)
    val chave: String? = ""
){

    fun paraChavePix(chave: String): ChavePix {
        return ChavePix(
            idTitular = idTitular!!.toUUID(),
            tipoChave = tipoChave!!,
            tipoConta = tipoConta!!,
            chave = chave
        )
    }
}