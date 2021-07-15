package br.com.zup.academy.pix

import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator
import java.util.*

enum class TipoChave {
    CPF {
        override fun validarFormatoChave(chave: String): ErroNaValidacao {
            val valido = CPFValidator().run {
                initialize(null)
                isValid(chave, null)
            }

            if (valido && chave.matches("^[0-9]{11}\$".toRegex())) {
                return ErroNaValidacao(false)
            } else {
                return ErroNaValidacao(true, "Não e um CPF valido ou esta mal formatado")
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
        override fun validarFormatoChave(chave: String): ErroNaValidacao {
            return if (chave.matches("^[a-z0-9.]+@[a-z0-9]+\\.[a-z]+(\\.[a-z]+)?\$".toRegex())) {
                ErroNaValidacao(false)
            } else {
                ErroNaValidacao(true, "Não e um Email valido")
            }
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

    abstract fun transformar(chave: String): String;
    abstract fun validarFormatoChave(chave: String): ErroNaValidacao
}