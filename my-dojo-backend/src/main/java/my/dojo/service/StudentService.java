/*
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <https://unlicense.org>
 */

package my.dojo.service;

import com.google.common.base.Strings;
import my.dojo.exception.BusinessException;
import my.dojo.exception.StudentNotFoundException;
import my.dojo.model.Student;
import my.dojo.model.StudentPage;
import my.dojo.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

@Service
public class StudentService {
    private static final int MINIMUM_AGE = 6;
    private static final int AGE_OF_MAJORITY = 18;

    @Autowired
    private StudentRepository studentRepository;

    public Student create(@NotNull @Valid Student student) throws BusinessException {
        int age = Period.between(student.getDateOfBirth(), LocalDate.now()).getYears();
        if (age < MINIMUM_AGE) {
            throw new BusinessException("Student must have minimum age of 6 years");
        } else if (age < AGE_OF_MAJORITY) {
            if (Strings.isNullOrEmpty(student.getGuardianName())) {
                throw new BusinessException("Guardian name is mandatory for students under 18 years");
            }

            if (Strings.isNullOrEmpty(student.getGuardianPhone())) {
                throw new BusinessException("Guardian phone is mandatory for students under 18 years");
            }

            if (Strings.isNullOrEmpty(student.getGuardianEmail())) {
                throw new BusinessException("Guardian email is mandatory for students under 18 years");
            }
        }

        switch (student.getGradeType()) {
            case DAN -> {
                if (student.getGradeRank() < 1 || student.getGradeRank() > 10) {
                    throw new BusinessException("DAN grades must be between 1st and 10th.");
                }
            }

            case KYU -> {
                if (student.getGradeRank() < 1 || student.getGradeRank() > 7) {
                    throw new BusinessException("KYU grades must be between 7th and 1st.");
                }
            }
        }

        if (studentRepository.existsByEmail(student.getEmail())) {
            throw new BusinessException(student.getEmail() + "is already registered.");
        }

        return studentRepository.save(student);
    }

    public Student findById(Long id) throws StudentNotFoundException {
        return studentRepository
                .findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student " + id + " not found."));
    }

    public StudentPage findAllByNameStartsWith(String name, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Slice<Student> slice = studentRepository.findAllByNameStartsWith(name, pageable);
        return new StudentPage(slice.getContent(), slice.getNumber(), slice.getSize(), slice.hasNext());
    }

    public Student update(Long id, Student updatedStudent) throws StudentNotFoundException {
        Student student = studentRepository
                .findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student " + id + " not found."));

        Optional.ofNullable(updatedStudent.getPhoneNumber()).ifPresent(student::setPhoneNumber);
        Optional.ofNullable(updatedStudent.getGradeRank()).ifPresent(student::setGradeRank);
        Optional.ofNullable(updatedStudent.getGradeType()).ifPresent(student::setGradeType);
        Optional.ofNullable(updatedStudent.getGuardianPhone()).ifPresent(student::setGuardianPhone);

        return studentRepository.save(student);
    }

    public void deleteById(Long id) throws StudentNotFoundException {
        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException("Student " + id + " not found.");
        }
        studentRepository.deleteById(id);
    }
}
