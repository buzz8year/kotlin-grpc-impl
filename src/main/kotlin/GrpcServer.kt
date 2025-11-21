package main.kotlin

import io.grpc.Server
import io.grpc.ServerBuilder

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

import streaming.StreamServiceGrpcKt
import streaming.Streaming

class GrpcServer
{
    val port: Int = 50051
    val server: Server = ServerBuilder.forPort(port)
        .addService(HelloService())
        .build()

    val hook: Thread = Thread {
        println("Shutting down gRPC server since JVM is shutting down")
        stop()
    }

    fun start() {
        server.start()
        Runtime.getRuntime().addShutdownHook(hook)
        println("Server started, listening on $port")
    }

    fun stop() {
        server.shutdown()
        println("Server shut down")
    }

    fun blockUntilShutdown() {
        server.awaitTermination()
    }
}

class HelloService : StreamServiceGrpcKt.StreamServiceCoroutineImplBase()
{
    override fun streamEvents(request: Streaming.StreamRequest): Flow<Streaming.StreamResponse> = flow {

        for (i in 1..5)
        {
            val response = Streaming.StreamResponse.newBuilder()
                .setMessage("Hello #$i ${request.requestId}")
                .build()

            // NOTE: Do not forget to emit() response within flow
            emit(response)

            // NOTE: Delay for imitation of lasting process
            delay(1000)
        }
    }
}

fun main()
{
    val server = GrpcServer()
    server.start()

    server.blockUntilShutdown()
}