package br.com.zup.academy.pix.remover

import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotNull

@Introspected
data class RemoveChavePixForm (
    @field:NotNull
    val idPix: UUID?,
    @field:NotNull
    val idTitular: UUID?
)
