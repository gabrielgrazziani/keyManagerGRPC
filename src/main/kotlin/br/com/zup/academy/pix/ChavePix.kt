package br.com.zup.academy.pix

import java.util.*
import javax.persistence.*

@Entity
class ChavePix(
    @field:Column(length = 16) val idTitilar: UUID,
    @field:Enumerated(EnumType.STRING) val tipoChave: TipoChave,
    @field:Enumerated(EnumType.STRING) val tipoConta: TipoConta,
    @field:Column(length = 77) val chave: String,
    @field:Column(length = 16) val uuid: UUID = UUID.randomUUID()
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

}