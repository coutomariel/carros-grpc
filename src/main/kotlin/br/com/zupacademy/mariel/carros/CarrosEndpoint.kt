package br.com.zupacademy.mariel.carros

import br.com.zupacademy.mariel.CarrosGRpcServiceGrpc
import br.com.zupacademy.mariel.CarrosRequest
import br.com.zupacademy.mariel.CarrosResponse
import io.grpc.Status
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
class CarrosEndpoint(@Inject val repository: CarrosRepository) : CarrosGRpcServiceGrpc.CarrosGRpcServiceImplBase() {

    override fun adicionar(request: CarrosRequest, responseObserver: StreamObserver<CarrosResponse>?) {

        if (repository.existsByPlaca(request.placa)) {
            responseObserver
                ?.onError(Status.ALREADY_EXISTS
                    .withDescription("Placa informada já existe na base de dados")
                    .asRuntimeException())
            return
        }

        val carro = Carro(request.modelo, request.placa)

        try {
            repository.save(carro)
        } catch (e: ConstraintViolationException) {
            responseObserver
                ?.onError(Status.INVALID_ARGUMENT
                    .withDescription("Dados de entrada inválidos ")
                    .asRuntimeException())
            return
        }

        responseObserver?.onNext(CarrosResponse.newBuilder().setId(carro.id!!).build())
        responseObserver?.onCompleted()

    }
}