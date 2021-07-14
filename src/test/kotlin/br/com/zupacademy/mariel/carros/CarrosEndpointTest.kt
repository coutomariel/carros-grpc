package br.com.zupacademy.mariel.carros

import br.com.zupacademy.mariel.CarrosGRpcServiceGrpc
import br.com.zupacademy.mariel.CarrosRequest
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException

import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Test
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class CarrosEndpointTest(
    val repository: CarrosRepository,
    val grpcClient: CarrosGRpcServiceGrpc.CarrosGRpcServiceBlockingStub
) {

    /**
     * 1. happy path
     * 2. quando já existe carro com a placa
     * 3. quando os dados de entrada são inválidos
     */

    /**
     * testar via linha de comando com -> .\gradlew.bat test
     */

    @BeforeEach
    fun setup(){
        repository.deleteAll()
    }

    @Test
    fun `deve adicionar um novo carro`() {
        val response = grpcClient.adicionar(
            CarrosRequest.newBuilder()
                .setModelo("Mercedes Benz").setPlaca("IHV-2871").build()
        )

        with(response) {
            assertNotNull(this.id)
            assertTrue(repository.existsById(this.id))
        }
    }

    @Test
    fun `nao deve adicionar novo carro quando placa ja existente`() {
        val existente = repository.save(Carro("Fiat Pálio", "IPV-4457"))

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.adicionar(
                CarrosRequest.newBuilder()
                    .setModelo("Gol").setPlaca(existente.placa).build()
            )
        }

        with(error){
            assertEquals(Status.ALREADY_EXISTS.code, this.status.code)
            assertEquals("Placa informada já existe na base de dados", this.status.description )
        }

    }

    @Test
    fun `nao deve adicionar novo carro com dados inválidos`() {

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.adicionar(
                CarrosRequest.newBuilder().setPlaca("IHV-2871").build()
            )
        }

        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
            assertEquals("Dados de entrada inválidos", this.status.description )
        }

    }

    @Factory
    class clients {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): CarrosGRpcServiceGrpc.CarrosGRpcServiceBlockingStub {
            return CarrosGRpcServiceGrpc.newBlockingStub(channel)
        }
    }

}