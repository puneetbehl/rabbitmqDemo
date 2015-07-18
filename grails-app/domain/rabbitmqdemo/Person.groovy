package rabbitmqdemo

class Person {

    String name
    Integer age

    static constraints = {
        name(blank: false)
        age()
    }
}
