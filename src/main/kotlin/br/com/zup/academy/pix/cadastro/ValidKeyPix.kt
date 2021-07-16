package br.com.zup.academy.pix

import br.com.zup.academy.pix.cadastro.ChavePixForm
import javax.inject.Singleton
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.TYPE
import kotlin.reflect.KClass

@MustBeDocumented
@Target(CLASS, TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = [ValidPixKeyValidator::class])
annotation class ValidKeyPix(
    val message: String = "chave Pix inválida (\${validatedValue.tipo})",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = [],
)

@Singleton
class ValidPixKeyValidator: javax.validation.ConstraintValidator<ValidKeyPix, ChavePixForm> {

    override fun isValid(value: ChavePixForm, context: javax.validation.ConstraintValidatorContext?): Boolean {

        if (value.tipoChave == null || value.chave == null) {
            return true
        }

        val erro = value.tipoChave.validarFormatoChave(value.chave)

        if(erro.posuiErro){
            context?.disableDefaultConstraintViolation()
            context
                ?.buildConstraintViolationWithTemplate(erro.menssagem)
                ?.addConstraintViolation()
            return false
        }
        return true
    }
}
