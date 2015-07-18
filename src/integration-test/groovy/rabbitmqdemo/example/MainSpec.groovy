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


    void testTransactionalWithNormalMessage() {
        expect:
        MessageStatus.count() == 0

        when:
        producerService.sendTxnMessage("Hello world")

        then:
        waitFor(1) {
            MessageStatus.count() == 0
        }
    }

    void testTransactionalWithError() {
        expect:
        MessageStatus.count() == 0

        when:
        producerService.sendTxnMessage("throw exception")

        then: "A MessageStatus message shouldn't be saved."
        waitFor(1, 0.5) {
            MessageStatus.count() == 0
        }

        when:
        // Stop the service that's consuming the messages so we can check that
        // the message is still on the queue.
        def listener = grailsApplication.mainContext.getBean('txnService_MessageListenerContainer')
        listener.stop()

        then:
        // Check that the message is back on the queue.
        //
        // Note: the current retry handler behaviour means that messages are
        // dropped. This is probably fine for poisoned messages, i.e. ones where
        // the content of the message is malformed or otherwise causes an
        // exception in the listener, but it's not good for listeners that simply
        // have a bug in them.
        waitFor(1, 0.5) {
            rabbitTemplate.receiveAndConvert('fooTxn') == null
        }

    }

    void cleanup() {
        MessageStatus.withNewSession {
            MessageStatus.executeUpdate('delete MessageStatus ')
        }
    }

}
