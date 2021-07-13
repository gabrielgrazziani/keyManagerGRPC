package br.com.zup.academy

import io.micronaut.runtime.Micronaut.build

fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("br.com.zup.academy")
		.start()
}

