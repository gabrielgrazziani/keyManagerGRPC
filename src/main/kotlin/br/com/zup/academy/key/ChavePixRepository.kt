package br.com.zup.academy.key

import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface ChavePixRepository: CrudRepository<ChavePix,Long> {
    fun existsByChave(chave: String): Boolean
}