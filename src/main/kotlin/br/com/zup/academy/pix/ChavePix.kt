package br.com.zup.academy.pix

import java.util.*
import javax.persistence.*
import javax.validation.Valid

@Entity
class ChavePix(
    @field:Column(length = 16) val idTitular: UUID,
    @field:Enumerated(EnumType.STRING) val tipoChave: TipoChave,
    @field:Enumerated(EnumType.STRING) val tipoConta: TipoConta,
    @field:Column(length = 77) val chave: String,
    @field:Column(length = 16) val uuid: UUID = UUID.randomUUID(),
    @field:Valid
    @Embedded
    val conta: ContaAssociada
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

}