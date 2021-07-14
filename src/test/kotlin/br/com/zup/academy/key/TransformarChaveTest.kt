package br.com.zup.academy.key

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

internal class TransformarChaveTest{

    @Test
    internal fun `ao trasformar deve manter igual a chave do tipo CPF`() {
        val chave = "12345678901"
        val novaChave = TipoChave.CPF.transformar(chave)

        assertEquals(chave,novaChave)
    }

    @Test
    internal fun `ao trasformar deve manter igual a chave do tipo TELEFONE_CELULAR`() {
        val chave = "+5585988714077"
        val novaChave = TipoChave.TELEFONE_CELULAR.transformar(chave)

        assertEquals(chave,novaChave)
    }

    @Test
    internal fun `ao trasformar deve manter igual a chave do tipo EMAIL`() {
        val chave = "gabriel.barbosa@zup.com.br"
        val novaChave = TipoChave.EMAIL.transformar(chave)

        assertEquals(chave,novaChave)
    }

    @Test
    internal fun `ao trasformar deve Gerar uma Chave ALEATORIA quando o tipo for CHAVE_ALEATORIA`() {
        val chave = ""
        val novaChave = TipoChave.CHAVE_ALEATORIA.transformar(chave)

        assertNotEquals(chave,novaChave)
    }
}