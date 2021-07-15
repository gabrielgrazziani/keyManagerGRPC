package br.com.zup.academy.util

import com.google.rpc.BadRequest
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto

fun StatusRuntimeException.getViolacao(index: Int): BadRequest.FieldViolation? {
    val statusProto = StatusProto.fromThrowable(this)
    val badRequest = statusProto?.detailsList?.get(0)?.unpack(BadRequest::class.java)
    return badRequest?.getFieldViolations(index);
}