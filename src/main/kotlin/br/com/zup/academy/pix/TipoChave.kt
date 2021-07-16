package br.com.zup.academy.pix

import br.com.zup.academy.integracao.banco_central.keyType
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator

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

        override fun paraKeyType() = keyType.CPF
    },
    TELEFONE_CELULAR {
        override fun validarFormatoChave(chave: String) =
            if (chave.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())) {
                ErroNaValidacao(false)
            } else {
                ErroNaValidacao(true, "Não e um Telefone valido")
            }

        override fun paraKeyType() = keyType.PHONE
    },
    EMAIL {
        override fun validarFormatoChave(chave: String): ErroNaValidacao {
            return if (chave.matches("^[a-z0-9.]+@[a-z0-9]+\\.[a-z]+(\\.[a-z]+)?\$".toRegex())) {
                ErroNaValidacao(false)
            } else {
                ErroNaValidacao(true, "Não e um Email valido")
            }
        }
        override fun paraKeyType() = keyType.EMAIL
    },
    CHAVE_ALEATORIA {
        override fun validarFormatoChave(chave: String) =
            if (chave.isBlank()) {
                ErroNaValidacao(false)
            } else {
                ErroNaValidacao(true, "Para criar uma chave aleatoria o campo 'chave' deve estar em branco")
            }

        override fun paraKeyType() = keyType.RANDOM
    };

    abstract fun validarFormatoChave(chave: String): ErroNaValidacao
    abstract fun paraKeyType(): keyType
}