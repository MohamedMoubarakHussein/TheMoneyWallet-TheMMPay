package com.themoneywallet.transcationservice.utilites;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.GregorianCalendar;

public class DateTimeUtil {

    private static final DatatypeFactory datatypeFactory;

    static {
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException("Failed to initialize DatatypeFactory", e);
        }
    }

    // ✅ Convert from XMLGregorianCalendar → LocalDateTime
    public static LocalDateTime toLocalDateTime(XMLGregorianCalendar xmlCal, LocalDateTime defaultVal) {
        if (xmlCal == null) return defaultVal;
        return xmlCal.toGregorianCalendar()
                     .toZonedDateTime()
                     .toLocalDateTime();
    }

    // ✅ Convert from LocalDateTime → XMLGregorianCalendar
    public static XMLGregorianCalendar toXmlGregorianCalendar(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;

        GregorianCalendar gregorianCalendar = GregorianCalendar.from(localDateTime.atZone(ZoneId.systemDefault()));
        return datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
    }
    public static XMLGregorianCalendar nowAsXmlGregorianCalendar() {
    GregorianCalendar now = GregorianCalendar.from(LocalDateTime.now()
        .atZone(ZoneId.systemDefault()));
    return datatypeFactory.newXMLGregorianCalendar(now);
}

}
