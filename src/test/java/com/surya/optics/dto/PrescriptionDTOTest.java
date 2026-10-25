package com.surya.optics.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class PrescriptionDTOTest {
    @Test
    void positiveDiopterValuesHaveExplicitPlusSign() {
        PrescriptionDTO dto = new PrescriptionDTO();
        dto.setRightSph(new BigDecimal("1.25"));
        dto.setRightCyl(new BigDecimal("0.50"));
        dto.setAddition(new BigDecimal("2.00"));
        assertEquals("+1.25", dto.getRightSphDisplay());
        assertEquals("+0.50", dto.getRightCylDisplay());
        assertEquals("+2.00", dto.getAdditionDisplay());
    }

    @Test
    void negativeZeroAndMissingValuesAreFormattedClearly() {
        PrescriptionDTO dto = new PrescriptionDTO();
        dto.setLeftSph(new BigDecimal("-0.75"));
        dto.setLeftCyl(BigDecimal.ZERO);
        assertEquals("-0.75", dto.getLeftSphDisplay());
        assertEquals("0.00", dto.getLeftCylDisplay());
        assertEquals("-", dto.getRightSphDisplay());
    }
}
