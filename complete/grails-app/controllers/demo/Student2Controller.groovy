// tag::controllerPackageImport[]
package demo

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.springframework.context.MessageSource

import static org.springframework.http.HttpStatus.*

// end::controllerPackageImport[]

// tag::controllerImports[]

// end::controllerImports[]

@SuppressWarnings('LineLength')
// tag::classDeclaration[]
@CompileStatic
class Student2Controller {
// end::classDeclaration[]

// tag::allowedMethods[]
    static allowedMethods = [save: 'POST',
                             update: 'PUT',
                             delete: 'DELETE',]
// end::allowedMethods[]

    // tag::injectedStudentService[]
    StudentService studentService

    MessageSource messageSource
    // end::injectedStudentService[]

    // tag::indexAction[]
    def index(Integer max) {
        Student Anatole = new Student('ana', 100 as BigDecimal);
        Student Jerem = new Student('jerem', 100 as BigDecimal);

        params.max = Math.min(max ?: 10, 100)

        List<Student> studentList = new ArrayList<>()
        studentList.add(Anatole);
        studentList.add(Jerem);
        respond studentList, model: [studentCount: studentService.count()]
    }
    // end::indexAction[]

    // tag::showAction[]
    def show(Long id) {
        if (!id) {
            notFound()
            return
        }
        respond studentService.read(id)
    }
    // end::showAction[]

    // tag::createAction[]
    def create() {
        respond studentService.create(params)
    }
    // end::createAction[]

    // tag::editAction[]
    def edit(Long id) {
        if (!id) {
            notFound()
            return
        }
        respond studentService.read(id)
    }
    // end::editAction[]

    // tag::saveAction[]
    @CompileDynamic
    def save(StudentSaveCommand cmd) {
        if (cmd.hasErrors()) { // <1>
            respond cmd.errors, [model: [student: cmd], view: 'create']
            return
        }

        Student student = studentService.save(cmd) // <2>
        if (student.hasErrors()) { // <3>
            respond student.errors, view:'create'
            return
        }

        request.withFormat {  // <4>
            form multipartForm {  // <5>
                String msg = messageSource.getMessage('student.label', [] as Object[], 'Student', request.locale)
                flash.message = messageSource.getMessage('default.created.message', [msg, student.id] as Object[], 'Student created', request.locale)
                redirect(action: 'show', id: student.id)
            }
            '*' { respond student, [status: CREATED] }
        }
    }
    // end::saveAction[]

    // tag::updateAction[]
    @CompileDynamic
    def update(StudentUpdateCommand cmd) {
        if ( !cmd.id ) {
            notFound()
            return
        }

        if (cmd.hasErrors()) {
            respond cmd.errors, [model: [student: cmd], view: 'edit']
            return
        }

        Student student = studentService.update(cmd)
        if ( student == null ) {
            notFound()
            return
        }
        request.withFormat {
            form multipartForm {
                String msg = messageSource.getMessage('student.label', [] as Object[], 'Student', request.locale)
                flash.message = messageSource.getMessage('default.updated.message', [msg, student.id] as Object[], 'Student updated', request.locale)
                redirect(action: 'show', id: student.id)
            }
            '*' { respond student, [status: OK] }
        }
    }
    // end::updateAction[]

    // tag::deleteAction[]
    @CompileDynamic
    def delete(Long id) {
        if (!id) {
            notFound()
            return
        }

        Student student = studentService.delete(id)
        if (!student) {
            notFound()
            return
        }

        request.withFormat {
            form multipartForm {
                String msg = messageSource.getMessage('student.label', [] as Object[], 'Student', request.locale)
                flash.message = messageSource.getMessage('default.deleted.message', [msg, student.id] as Object[], 'Student Deleted', request.locale)
                redirect(action: 'index', method: 'GET')
            }
            '*' { render status: NO_CONTENT }
        }
    }
    // end::deleteAction[]

    // tag::calculateAvgGradeAction[]
    def calculateAvgGrade() {
        BigDecimal avgGrade = studentService.calculateAvgGrade()
        render messageSource.getMessage('student.average.grade', [avgGrade] as Object[], "Avg Grade is ${avgGrade}", request.locale)
    }
    // end::calculateAvgGradeAction[]

    // tag::notFoundMethod[]
    @CompileDynamic
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                String msg = messageSource.getMessage('student.label', [] as Object[], 'Student', request.locale)
                flash.message = messageSource.getMessage('default.not.found.message', [msg, params.id] as Object[], 'Student not found', request.locale)
                redirect(action: 'index', method: 'GET')
            }
            '*' { render status: NOT_FOUND }
        }
    }
    // tag::notFoundMethod[]
//tag::close[]
}
//end::close[]
