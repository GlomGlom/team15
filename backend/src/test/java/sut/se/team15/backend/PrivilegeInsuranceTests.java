package sut.se.team15.backend;

import sut.se.team15.Repository.*;
import sut.se.team15.Entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class PrivilegeInsuranceTests {

    private Validator validator;

    @Autowired
    private PrivilegeInsuranceRepository privilegeInsuranceRepository;
    @Autowired
    private PurposeRequestRepository purposeRequestRepository;
    @Autowired
    private HospitalRepository hospitalRepository;
    @Autowired
    private RegisterInsuranceRepository registerInsuranceRepository;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void B5917099_testPrivilegeDateOKFuture() { // ใส่ข้อมูลปกติ

        RegisterInsurance registerInsurance = registerInsuranceRepository.findById(1);
        Hospital hospital = hospitalRepository.findById(2);
        PurposeRequest purposeRequest = purposeRequestRepository.findById(3);

        PrivilegeInsurance privilegeinsurance = new PrivilegeInsurance();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate privilegeDate = LocalDate.parse("2021-01-01", formatter);

        privilegeinsurance.setRegisterInsurance(registerInsurance);
        privilegeinsurance.setHospital(hospital);
        privilegeinsurance.setPrivilegeDate(privilegeDate);
        privilegeinsurance.setPurposeRequest(purposeRequest);
        privilegeinsurance = privilegeInsuranceRepository.saveAndFlush(privilegeinsurance);

        Optional<PrivilegeInsurance> found = privilegeInsuranceRepository.findById(privilegeinsurance.getId());
        assertEquals(privilegeDate, found.get().getPrivilegeDate());
    }

    @Test
    void B5917099_testPrivilegeDateOKPresent() { // ใส่ข้อมูลปกติ

        RegisterInsurance registerInsurance = registerInsuranceRepository.findById(1);
        Hospital hospital = hospitalRepository.findById(2);
        PurposeRequest purposeRequest = purposeRequestRepository.findById(3);

        PrivilegeInsurance privilegeinsurance = new PrivilegeInsurance();

        LocalDate privilegeDate = LocalDate.now();

        privilegeinsurance.setRegisterInsurance(registerInsurance);
        privilegeinsurance.setHospital(hospital);
        privilegeinsurance.setPrivilegeDate(privilegeDate);
        privilegeinsurance.setPurposeRequest(purposeRequest);
        privilegeinsurance = privilegeInsuranceRepository.saveAndFlush(privilegeinsurance);

        Optional<PrivilegeInsurance> found = privilegeInsuranceRepository.findById(privilegeinsurance.getId());
        assertEquals(privilegeDate, found.get().getPrivilegeDate());
    }

    @Test
    void B5917099_testPrivilegeDateMustNotBeNull() {

        RegisterInsurance registerInsurance = registerInsuranceRepository.findById(1);
        Hospital hospital = hospitalRepository.findById(2);
        PurposeRequest purposeRequest = purposeRequestRepository.findById(3);

        PrivilegeInsurance privilegeinsurance = new PrivilegeInsurance();

        privilegeinsurance.setRegisterInsurance(registerInsurance);
        privilegeinsurance.setHospital(hospital);
        privilegeinsurance.setPrivilegeDate(null);
        privilegeinsurance.setPurposeRequest(purposeRequest);

        Set<ConstraintViolation<PrivilegeInsurance>> result = validator.validate(privilegeinsurance);

        // result ต้องมี error 1 ค่าเท่านั้น
        assertEquals(1, result.size());

        // error message ตรงชนิด และถูก field
        ConstraintViolation<PrivilegeInsurance> v = result.iterator().next();
        assertEquals("must not be null", v.getMessage());
        assertEquals("privilegeDate", v.getPropertyPath().toString());
    }

    @Test
    void B5917099_testPrivilegeDateWrongFormatPast() {

        RegisterInsurance registerInsurance = registerInsuranceRepository.findById(1);
        Hospital hospital = hospitalRepository.findById(2);
        PurposeRequest purposeRequest = purposeRequestRepository.findById(3);

        PrivilegeInsurance privilegeinsurance = new PrivilegeInsurance();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate privilegeDate = LocalDate.parse("2020-01-01", formatter);

        privilegeinsurance.setRegisterInsurance(registerInsurance);
        privilegeinsurance.setHospital(hospital);
        privilegeinsurance.setPrivilegeDate(privilegeDate);
        privilegeinsurance.setPurposeRequest(purposeRequest);

        Set<ConstraintViolation<PrivilegeInsurance>> result = validator.validate(privilegeinsurance);

        // result ต้องมี error 1 ค่าเท่านั้น
        assertEquals(1, result.size());

        // error message ตรงชนิด และถูก field
        ConstraintViolation<PrivilegeInsurance> v = result.iterator().next();
        assertEquals("must be a date in the present or in the future", v.getMessage());
        assertEquals("privilegeDate", v.getPropertyPath().toString());
    }

    @Test
    void B5917099_testPurposeRequestMustBeUnique() {
        // สร้าง PurposeRequest object
        PurposeRequest p1 = new PurposeRequest();

        p1.setPurposeRequest("ค่ารักษาอุบัติเหตุ");
        purposeRequestRepository.saveAndFlush(p1);

        // คาดหวังว่า DataIntegrityViolationException จะถูก throw
        assertThrows(DataIntegrityViolationException.class, () -> {
            // สร้าง PurposeRequest object ตัวที่ 2
            PurposeRequest p2 = new PurposeRequest();

            p2.setPurposeRequest("ค่ารักษาอุบัติเหตุ");
            purposeRequestRepository.saveAndFlush(p2);
        });
    }
}