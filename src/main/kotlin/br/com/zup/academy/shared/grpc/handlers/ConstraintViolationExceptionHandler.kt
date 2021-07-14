package br.com.zup.academy.shared.grpc.handlers

import br.com.zup.academy.shared.grpc.ExceptionHandler
import br.com.zup.academy.shared.grpc.ExceptionHandler.StatusWithDetails
import com.google.protobuf.Any
import com.google.rpc.BadRequest
import com.google.rpc.Code
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

/**
 * Handles the Bean Validation errors adding theirs violations into request trailers (metadata)
 */
@Singleton
class ConstraintViolationExceptionHandler : ExceptionHandler<ConstraintViolationException> {

    override fun handle(e: ConstraintViolationException): StatusWithDetails {

        val details = BadRequest.newBuilder()
            .addAllFieldViolations(e.constraintViolations.map {
                BadRequest.FieldViolation.newBuilder()
                    .setField(it.propertyPath.last().name ?: "?? key ??")
                    .setDescription(it.message)
                    .build()
            })
            .build()

        val statusProto = com.google.rpc.Status.newBuilder()
            .setCode(Code.INVALID_ARGUMENT_VALUE)
            .setMessage("Dados inválidos")
            .addDetails(Any.pack(details))
            .build()

        return StatusWithDetails(statusProto)
    }

    override fun supports(e: Exception): Boolean {
        return e is ConstraintViolationException
    }

}