package br.com.zup.academy.pix

enum class TipoConta(
    val siglaBancoCentral: String
) {
    CONTA_CORRENTE("CACC"),
    CONTA_POUPANCA("SVGS");
}
