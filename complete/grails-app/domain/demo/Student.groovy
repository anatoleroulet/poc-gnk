package demo

import groovy.transform.CompileStatic

@CompileStatic
class Student {
    String name
    BigDecimal grade

    Student(String name, BigDecimal grade) {
        this.name = name
        this.grade = grade
    }

    String toString() {
        name
    }
}
