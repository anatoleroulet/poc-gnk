package demo

import grails.testing.mixin.integration.Integration
import grails.testing.spock.OnceBefore
import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import spock.lang.Shared
import spock.lang.Specification

@SuppressWarnings(['JUnitPublicNonTestMethod', 'JUnitPublicProperty'])
@Integration
class StudentControllerIntSpec extends Specification {

    @Shared HttpClient client

    StudentService studentService
    //StudentController studentController = new StudentController()

    @OnceBefore
    void init() {
        String baseUrl = "http://localhost:$serverPort"
        this.client  = HttpClient.create(baseUrl.toURL())
    }

    def 'test json in URI to return students'() {
        given:


        List<Serializable> ids = []
        Student.withNewTransaction {
            ids << studentService.save('Nirav', 100 as BigDecimal).id
            ids << studentService.save('Jeff', 95 as BigDecimal).id
            ids << studentService.save('Sergio', 90 as BigDecimal).id
        }
        //studentController.show()

        expect:
        studentService.count() == 3

        when:

        HttpRequest request = HttpRequest.GET('/student/index.json') // <1>
        HttpResponse<List<Map>> resp = client.toBlocking().exchange(request, Argument.of(List, Map)) // <2>

        then:
        resp.status == HttpStatus.OK // <3>
        resp.body()
        resp.body().size() == 3
        resp.body().find { it.grade == 100 && it.name == 'Nirav' }
        resp.body().find { it.grade == 95 &&  it.name == 'Jeff' }
        resp.body().find { it.grade == 90 &&  it.name == 'Sergio' }

        when:
        HttpRequest request2 = HttpRequest.GET('/student2.json') // <1>
        HttpResponse<List<Map>> resp2 = client.toBlocking().exchange(request2, Argument.of(List, Map)) // <2>

        then:
        resp2.status == HttpStatus.OK // <3>
        resp2.body()
        resp2.body().size() == 2
        resp2.body().find { it.grade == 100 && it.name == 'ana' }
        resp2.body().find { it.grade == 100 &&  it.name == 'jerem' }


        cleanup:
        Student.withNewTransaction {
            ids.each { id ->
                studentService.delete(id)
            }
        }
    }

    def 'test 2 in URI to return students2'() {
        given:




        expect:
        studentService.count() == 3

        when:

        HttpRequest request = HttpRequest.GET('/student2/index.json') // <1>
        HttpResponse<List<Map>> resp = client.toBlocking().exchange(request, Argument.of(List, Map)) // <2>

        then:
        resp.status == HttpStatus.OK // <3>
        resp.body()
        resp.body().size() == 2
        resp.body().find { it.grade == 100 && it.name == 'ana' }
        resp.body().find { it.grade == 100 &&  it.name == 'jerem' }



        cleanup:
        Student.withNewTransaction {
            ids.each { id ->
                studentService.delete(id)
            }
        }
    }
}
