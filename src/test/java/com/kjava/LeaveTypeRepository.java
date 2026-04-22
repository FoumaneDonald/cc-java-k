package com.kjava;

//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import java.math.BigDecimal;
//import java.util.Optional;
//import static org.assertj.core.api.Assertions.assertThat;
//
//@DataJpaTest
//class LeaveTypeRepositoryTest {
//
//    @Autowired
//    private LeaveTypeRepository repository;
//
//    @Test
//    void shouldSaveAndRetrieveLeaveType() {
//        // Arrange
//        LeaveType annualLeave = LeaveType.builder()
//                .code("CP")
//                .name("Congés Payés")
//                .defaultAnnualBalance(new BigDecimal("25.00"))
//                .requiresJustification(false)
//                .carryOverAllowed(true)
//                .build();
//
//        // Act
//        // Using repository directly instead of TestEntityManager
//        LeaveType saved = repository.saveAndFlush(annualLeave);
//
//        Optional<LeaveType> retrieved = repository.findById(saved.getId());
//
//        // Assert
//        assertThat(retrieved).isPresent();
//        assertThat(retrieved.get().getCode()).isEqualTo("CP");
//        assertThat(retrieved.get().getDefaultAnnualBalance()).isEqualByComparingTo("25.00");
//        assertThat(retrieved.get().getId()).isNotNull(); 
//    }
//}