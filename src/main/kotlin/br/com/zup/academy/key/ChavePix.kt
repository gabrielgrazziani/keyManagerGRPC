package br.com.zup.academy.key

import java.util.*
import javax.persistence.*

@Entity
class ChavePix(
    @Column(length = 16) val idTitilar: UUID,
    @Enumerated(EnumType.STRING) val tipoChave: TipoChave,
    @Enumerated(EnumType.STRING) val tipoConta: TipoConta,
    @Column(length = 77) val chave: String,
    @Column(length = 16) val uuid: UUID = UUID.randomUUID()
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

}