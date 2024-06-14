package ft.gg.onboarding.sugang.service;

import ft.gg.onboarding.course.service.CourseService;
import ft.gg.onboarding.dto.course.CoursePageRequestDto;
import ft.gg.onboarding.dto.student.StudentRequestDto;
import ft.gg.onboarding.entity.course.Course;
import ft.gg.onboarding.entity.enrollment.Enrollment;
import ft.gg.onboarding.entity.student.Student;
import ft.gg.onboarding.global.exception.custom.BusinessException;
import ft.gg.onboarding.global.exception.custom.NotFoundException;
import ft.gg.onboarding.global.utils.SortParserUtils;
import ft.gg.onboarding.repository.CourseRepository;
import ft.gg.onboarding.repository.EnrollmentRepository;
import ft.gg.onboarding.repository.StudentRepository;
import ft.gg.onboarding.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SugangService {

    public static final String SUGANG_CANCEL_FAILED = "Failed to cancel course";

    private final StudentRepository studentRepository;

    private final CourseRepository courseRepository;

    private final EnrollmentRepository enrollmentRepository;

    @Transactional(readOnly = true)
    public List<Course> findCurrentCourses(CoursePageRequestDto coursePageRequestDto) {
        Sort sort = SortParserUtils.INSTANCE.parse(
                coursePageRequestDto.getSort(), coursePageRequestDto.getOrder());
        return courseRepository.findCoursesByTrueIsTrue(sort);
    }

    @Transactional
    public void enrollCourse(int courseId, StudentRequestDto studentRequestDto) {
        Student findStudent = studentRepository.findByNameAndBirthDate(
                        studentRequestDto.getName(), studentRequestDto.getBirthDate())
                .orElseThrow(() -> new NotFoundException(StudentService.STUDENT_NOT_FOUND));
        Course findCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException(CourseService.COURSE_NOT_FOUND));
        Enrollment newEnrollment = Enrollment.makeEnroll(findStudent, findCourse);
        try {
            findStudent.addCredit(findCourse.getCredit());
            findCourse.addStudent();
            enrollmentRepository.save(newEnrollment);
        } catch (Exception e) {
            throw new NotFoundException(CourseService.COURSE_UPDATE_FAILED);
        }
    }

    @Transactional
    public void cancelCourse(int courseId, StudentRequestDto studentRequestDto) {
        Student findStudent = studentRepository.findByNameAndBirthDate(
                        studentRequestDto.getName(), studentRequestDto.getBirthDate())
                .orElseThrow(() -> new NotFoundException(StudentService.STUDENT_NOT_FOUND));
        Enrollment findEnrollment = enrollmentRepository.findByCourseIdAndStudentId(courseId, findStudent.getId())
                .orElseThrow(() -> new NotFoundException(CourseService.COURSE_NOT_FOUND));
        try{
            findEnrollment.cancelEnrollment();
        } catch (Exception e) {
            throw new BusinessException(SUGANG_CANCEL_FAILED);
        }
    }

    @Transactional(readOnly = true)
    public List<Enrollment> findCourseHistory(StudentRequestDto studentRequestDto) {
        Student findStudent = studentRepository.findByNameAndBirthDate(
                        studentRequestDto.getName(), studentRequestDto.getBirthDate())
                .orElseThrow(() -> new NotFoundException(StudentService.STUDENT_NOT_FOUND));
        return findStudent.getEnrollments();
    }
}
