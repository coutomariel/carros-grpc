package br.com.zupacademy.mariel

import io.micronaut.runtime.Micronaut.*
fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("br.com.zupacademy.mariel")
		.start()
}

