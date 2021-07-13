package br.com.zup.academy.key

import br.com.zup.academy.ChavePixRequest

enum class TipoChave: ValidadoFomatoChavePix {
    CPF {
        override fun validarFormatoChave(chave: String) =
            if (chave.matches("^[0-9]{11}\$".toRegex())) {
                ErroNaValidacao(false)
            } else {
                ErroNaValidacao(true, "Não e um CPF valido")
            }
    },
    TELEFONE_CELULAR {
        override fun validarFormatoChave(chave: String) =
            if (chave.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())) {
                ErroNaValidacao(false)
            } else {
                ErroNaValidacao(true, "Não e um Telefone valido")
            }
    },
    EMAIL {
        override fun validarFormatoChave(chave: String) =
            if (chave.matches("^[a-z0-9.]+@[a-z0-9]+\\.[a-z]+(\\.[a-z]+)?\$".toRegex())) {
                ErroNaValidacao(false)
            } else {
                ErroNaValidacao(true, "Não e um Email valido")
            }
    },
    CHAVE_ALEATORIA {
        override fun validarFormatoChave(chave: String) =
            if (chave.isBlank()) {
                ErroNaValidacao(false)
            } else {
                ErroNaValidacao(true, "Para criar uma chave aleatoria o campo 'chave' deve estar em branco")
            }

    };

}

interface ValidadoFomatoChavePix{
    fun validarFormatoChave(chave: String): ErroNaValidacao
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
