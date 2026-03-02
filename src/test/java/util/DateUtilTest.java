package util;

import com.prioriza.util.DateUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilTest {

    @Test
    @DisplayName("Formato de fecha")
    void testDateFormat() {
        LocalDate date = LocalDate.of(2026, 3, 2);
        assertEquals("02/03/2026", DateUtil.formatDate(date));
        assertEquals("", DateUtil.formatDate(null));
    }

    @Test
    @DisplayName("Formato de hora")
    void testTimeFormat() {
        LocalTime time = LocalTime.of(14, 30);
        assertEquals("14:30", DateUtil.formatTime(time));
        assertEquals("", DateUtil.formatTime((LocalTime) null));
    }

    @Test
    @DisplayName("Formato de fecha y hora")
    void testDateTimeFormat() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 3, 2, 14, 30);
        assertEquals("02/03/2026 14:30", DateUtil.formatDateTime(dateTime));
    }

    @Test
    @DisplayName("Parseo de hora")
    void testParseTime() {
        assertEquals(LocalTime.of(9, 0), DateUtil.parseTime("09:00"));
        assertEquals(LocalTime.of(23, 59), DateUtil.parseTime("23:59"));
        assertThrows(Exception.class, () -> DateUtil.parseTime("25:00"));
    }

    @Test
    @DisplayName("Combinar fecha y hora")
    void testCombine() {
        LocalDate date = LocalDate.of(2026, 3, 2);
        LocalTime time = LocalTime.of(15, 45);

        LocalDateTime result = DateUtil.combine(date, time);
        assertEquals(LocalDateTime.of(2026, 3, 2, 15, 45), result);

        // Sin hora, debe usar medianoche
        assertEquals(date.atStartOfDay(), DateUtil.combine(date, null));
        assertNull(DateUtil.combine(null, time));
    }
}
