package br.com.zup.academy.pix.cadastro

import br.com.zup.academy.ChavePixRequest
import br.com.zup.academy.pix.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

internal class GrpcExtensionsKtTest{

    @Test
    internal fun `deve conveter ChavePixRequest_TipoChave para TipoChave`() {
        val chavePixRequest_TipoChave = ChavePixRequest.TipoChave.CHAVE_ALEATORIA
        val tipoChave = chavePixRequest_TipoChave.paraTipoChave()

        assertEquals(TipoChave.CHAVE_ALEATORIA,tipoChave)
    }

    @Test
    internal fun `deve conveter ChavePixRequest_TipoConta para TipoConta`() {
        val chavePixRequest_TipoConta = ChavePixRequest.TipoConta.CONTA_CORRENTE
        val tipoConta = chavePixRequest_TipoConta.paraTipoConta()

        assertEquals(TipoConta.CONTA_CORRENTE,tipoConta)
    }

    @Test
    internal fun `deve conveter ChavePixRequest para ChavePixForm`() {
        val idTitular = UUID.randomUUID().toString()
        val chavePixRequest = ChavePixRequest.newBuilder()
            .setChave("teste@email.com")
            .setTipoChave(ChavePixRequest.TipoChave.EMAIL)
            .setTipoConta(ChavePixRequest.TipoConta.CONTA_POUPANCA)
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