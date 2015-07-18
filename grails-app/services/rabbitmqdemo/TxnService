package rabbitmqdemo

class TxnService {
    static transactional = true

    static rabbitQueue = "fooTxn"

    void handleMessage(String msg) {
        println "Transactional : "+msg
        if (msg == "throw exception") {
            throw new RuntimeException()
        }
        else {
            try {
                MessageStatus.withTransaction {
                    new MessageStatus(message: msg).save(failOnError: true, flush: true)
                }
            }
            catch (Exception ex) {
                ex.printStackTrace()
                throw ex
            }
        }
    }
}