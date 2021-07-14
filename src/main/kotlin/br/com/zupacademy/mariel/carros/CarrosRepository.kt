package br.com.zupacademy.mariel.carros

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface CarrosRepository: JpaRepository<Carro, Long> {
    abstract fun existsByPlaca(placa: String?): Boolean
}