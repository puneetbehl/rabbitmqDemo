package rabbitmqdemo.example

import geb.spock.GebSpec
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import org.springframework.beans.factory.annotation.Autowired
import rabbitmqdemo.Person
import rabbitmqdemo.ProducerService

@Integration
@Rollback
class MessageSerializationSpec extends GebSpec {
    @Autowired
    ProducerService producerService

    void testSendingSerializableMessage() {
        expect:
        Person.count() == 0

        when:
        producerService.sendPersonMessage("Peter", 34)
        producerService.sendPersonMessage("Bob", 46)
        producerService.sendPersonMessage("Jill", 26)
        producerService.sendPersonMessage("Amy", 43)
        producerService.sendPersonMessage("Kate", 12)

        then:
        waitFor(10, 0.5) {
            Person.count() == 5
        }
    }
}
