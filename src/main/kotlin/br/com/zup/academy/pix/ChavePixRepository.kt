package br.com.zup.academy.pix

import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import java.util.*

@Repository
interface ChavePixRepository: CrudRepository<ChavePix,Long> {
    fun existsByChave(chave: String): Boolean
    fun findByUuid(uuid: UUID): ChavePix?
    fun findByChave(chave: String): ChavePix?
    fun findByIdTitular(idTitular: UUID): List<ChavePix>
}