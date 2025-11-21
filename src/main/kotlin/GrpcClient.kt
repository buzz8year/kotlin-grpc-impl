package main.kotlin

import io.grpc.ManagedChannelBuilder

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.delay

import streaming.StreamServiceGrpc
import streaming.Streaming

fun main(): Unit = runBlocking {

    val channel = ManagedChannelBuilder.forAddress("localhost", 50051)
        .usePlaintext()
        .build()

    val stub = StreamServiceGrpc.newStub(channel)

    val request = Streaming.StreamRequest.newBuilder()
        .setRequestId("ID-test-request!")
        .build()

    val responseObserver = object : io.grpc.stub.StreamObserver<Streaming.StreamResponse>
    {
        override fun onNext(value: Streaming.StreamResponse) {
            println("Received: ${value.message}")
        }

        override fun onError(t: Throwable) {
            println("Error: ${t.message}")
        }

        override fun onCompleted() {
            println("Stream completed")
        }
    }

    stub.streamEvents(request, responseObserver)

    // NOTE: Keep the application running to receive messages
    delay(4000)
    channel.shutdown()
}
