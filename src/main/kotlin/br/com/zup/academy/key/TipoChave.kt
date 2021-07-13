package br.com.zup.academy.key

import br.com.zup.academy.ChavePixRequest
import java.util.*

enum class TipoChave: ValidadoFomatoChavePix, TransformadorChavePix {
    CPF {
        override fun validarFormatoChave(chave: String) {
            if (chave.matches("^[0-9]{11}\$".toRegex())) {
                ErroNaValidacao(false)
            } else {
                ErroNaValidacao(true, "Não e um CPF valido")
            }
        }
        override fun transformar(chave: String) = chave
    },
    TELEFONE_CELULAR {
        override fun validarFormatoChave(chave: String) =
            if (chave.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())) {
                ErroNaValidacao(false)
            } else {
                ErroNaValidacao(true, "Não e um Telefone valido")
            }

        override fun transformar(chave: String) = chave
    },
    EMAIL {
        override fun validarFormatoChave(chave: String) =
            if (chave.matches("^[a-z0-9.]+@[a-z0-9]+\\.[a-z]+(\\.[a-z]+)?\$".toRegex())) {
                ErroNaValidacao(false)
            } else {
                ErroNaValidacao(true, "Não e um Email valido")
            }

        override fun transformar(chave: String) = chave
    },
    CHAVE_ALEATORIA {
        override fun validarFormatoChave(chave: String) =
            if (chave.isBlank()) {
                ErroNaValidacao(false)
            } else {
                ErroNaValidacao(true, "Para criar uma chave aleatoria o campo 'chave' deve estar em branco")
            }

        override fun transformar(chave: String) = UUID.randomUUID().toString()
    };

}

interface ValidadoFomatoChavePix{
    fun validarFormatoChave(chave: String): ErroNaValidacao
}

interface TransformadorChavePix{
    fun transformar(chave: String): String
}

fun ChavePixRequest.TipoChave.map(): TipoChave {
    return when(this){
        ChavePixRequest.TipoChave.CPF -> TipoChave.CPF
        ChavePixRequest.TipoChave.EMAIL -> TipoChave.EMAIL
        ChavePixRequest.TipoChave.TELEFONE_CELULAR -> TipoChave.TELEFONE_CELULAR
        ChavePixRequest.TipoChave.CHAVE_ALEATORIA -> TipoChave.CHAVE_ALEATORIA
        else -> throw IllegalStateException("Tipo ${this.javaClass.simpleName}  não suportado")
    }
}
