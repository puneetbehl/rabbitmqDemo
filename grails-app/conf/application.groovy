rabbitmq {
    connectionfactory {
        username = 'guest'
        password = 'guest'
        hostname = 'localhost'
    }
    queues = {
        foo autoDelete: true, durable: false, exclusive: true
        fooNonTxn autoDelete: true, durable: false, exclusive: true
        personTestQueue autoDelete: true, durable: false, exclusive: true

        // The integration tests require this to be *not* auto-delete. This is
        // so we can check the messages on the queue after the listener has
        // shutdown. Otherwise, when the listener shuts down the queue would
        // be deleted.
        fooTxn autoDelete: false, durable: false, exclusive: true

        // Test exchange declaration.
        exchange name: "myEx", type: topic, durable: false, {
            sharesConsumer autoDelete: true, exclusive: true, durable: false, binding: "NYSE.#"
        }
    }
}