package rabbitmqdemo.example

import com.rabbitmq.client.Channel
import geb.spock.GebSpec
import grails.core.GrailsApplication
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import rabbitmqdemo.MessageStatus
import rabbitmqdemo.ProducerService
import spock.lang.IgnoreRest

@Integration
@Rollback
class MainSpec extends GebSpec {

    @Autowired
    ProducerService producerService
    @Autowired
    RabbitTemplate rabbitTemplate
    @Autowired
    GrailsApplication grailsApplication

    void testNonTransactionalWithNormalMessage() {
        expect:
        MessageStatus.count() == 0

        when:
        producerService.sendNonTxnMessage("Hello World")

        then: "A MessageStatus message should be saved."
        waitFor(1, 0.5) {
            MessageStatus.count() == 1
        }
    }

    void testNonTransactionalWithError() {
        expect:
        MessageStatus.count() == 0

        when:
        producerService.sendNonTxnMessage('throw exception')

        then: "A MessageStatus message shouldn't be saved."
        waitFor(1) {
            MessageStatus.count() == 0
        }

        when: // Check that the message has *not* remained on the queue.
        Channel response = rabbitTemplate.getChannel(rabbitTemplate.transactionalResourceHolder).basicGet('fooNonTxn', true)

        then:
        response == null
    }

    void cleanup() {
        MessageStatus.withNewSession {
            MessageStatus.executeUpdate('delete MessageStatus ')
        }
    }

}
