package rabbitmqdemo

import grails.test.mixin.integration.Integration
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

@Integration
class ConnectionFactoryInitializationSpec extends Specification{

    @Autowired ConnectionFactory rabbitMQConnectionFactory

    void testConnectionFactoryInitialization() {
        expect:
        10 == rabbitMQConnectionFactory.channelCacheSize
    }
}
