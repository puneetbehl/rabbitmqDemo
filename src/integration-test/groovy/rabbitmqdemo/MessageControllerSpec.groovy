package rabbitmqdemo

import geb.spock.GebSpec
import grails.test.mixin.integration.Integration
import org.springframework.amqp.rabbit.core.RabbitTemplate
import spock.util.mop.ConfineMetaClassChanges

@Integration
@ConfineMetaClassChanges([RabbitTemplate])
class MessageControllerSpec extends GebSpec {

    MessageController controller

    void setup() {
        controller = new MessageController()
    }
    void "canary test"() {
        expect:
        true
    }

    void testRabbitSendCallsConvertAndSend() {
        setup:
        def stringMessageQueueName
        def stringMessage

        def mapMessageQueueName
        def mapMessage

        RabbitTemplate.metaClass.convertAndSend = {String queue, String message->
            stringMessageQueueName = queue
            stringMessage = message
        }
        RabbitTemplate.metaClass.convertAndSend = { String queue, Map message ->
            mapMessage = message
            mapMessageQueueName = queue
        }

        when:
        go '/message/sendMessage?msg=Hello+World'

        then:
        'Message: Hello World' == stringMessage
        'foo' == stringMessageQueueName

        'foo' == mapMessageQueueName
        2 == mapMessage?.size()
        'Hello World' == mapMessage.msgBody
        mapMessage.msgTime instanceof Date

    }
}
