package br.com.zup.academy.pix.cadastro

import br.com.zup.academy.ChavePixRequest
import br.com.zup.academy.pix.TipoChave
import br.com.zup.academy.pix.TipoConta
import br.com.zup.academy.pix.paraChavePixForm
import br.com.zup.academy.pix.paraMeuEnum
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

internal class GrpcExtensionsKtTest{

    @Test
    internal fun `deve conveter ChavePixRequest_TipoChave para TipoChave`() {
        val chavePixRequest_TipoChave = br.com.zup.academy.TipoChave.CHAVE_ALEATORIA
        val tipoChave = chavePixRequest_TipoChave.paraMeuEnum()

        assertEquals(TipoChave.CHAVE_ALEATORIA,tipoChave)
    }

    @Test
    internal fun `deve conveter ChavePixRequest_TipoConta para TipoConta`() {
        val chavePixRequest_TipoConta = br.com.zup.academy.TipoConta.CONTA_CORRENTE
        val tipoConta = chavePixRequest_TipoConta.paraMeuEnum()

        assertEquals(TipoConta.CONTA_CORRENTE,tipoConta)
    }

    @Test
    internal fun `deve conveter ChavePixRequest para ChavePixForm`() {
        val idTitular = UUID.randomUUID().toString()
        val chavePixRequest = ChavePixRequest.newBuilder()
            .setChave("teste@email.com")
            .setTipoChave(br.com.zup.academy.TipoChave.EMAIL)
            .setTipoConta(br.com.zup.academy.TipoConta.CONTA_POUPANCA)
            .setIdTitular(idTitular)
            .build()
        val chavePixForm = chavePixRequest.paraChavePixForm()

        val chavePixFormEsperado = ChavePixForm(
            idTitular = idTitular,
            tipoChave = TipoChave.EMAIL,
            tipoConta = TipoConta.CONTA_POUPANCA,
            chave = "teste@email.com"
        )
        assertEquals(chavePixFormEsperado,chavePixForm)
    }
}