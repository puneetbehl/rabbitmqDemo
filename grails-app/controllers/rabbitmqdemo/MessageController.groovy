package rabbitmqdemo

class MessageController {

    def index() {}

    def sendMessage() {
        def msg = params.msg

        rabbitSend 'foo', "Message: ${msg}"

        def messageMap = [msgBody: msg, msgTime: new Date()]

        rabbitSend 'foo', messageMap

        redirect action: "index"
        return
    }
}
