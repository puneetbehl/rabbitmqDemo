package rabbitmqdemo

class MessageStatus {

    String message
    Date dateCreated

    static constraints = {
        message(blank: false)
    }
}
