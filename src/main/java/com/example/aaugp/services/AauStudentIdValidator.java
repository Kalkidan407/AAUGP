package com.example.aaugp.services;

import java.time.Clock;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class AauStudentIdValidator {

    private static final Pattern STUDENT_ID_PATTERN = Pattern.compile("^(URG|NSE)/\\d{4}/(\\d{2})$");
    private static final int ETHIOPIAN_YEAR_BASE = 2000;
    private static final int REGULAR_FINAL_PROJECT_YEAR_OFFSET = 3;
    private static final int EXTENSION_FINAL_PROJECT_YEAR_OFFSET = 4;

    private final Clock clock;

    public AauStudentIdValidator() {
        this.clock = Clock.systemDefaultZone();
    }

    public StudentAcademicInfo validateFinalProjectEligibility(String studentId) {
        StudentAcademicInfo info = parse(studentId);
        int currentEthiopianYear = currentEthiopianYear();

        if (info.expectedGraduationYearEc() > currentEthiopianYear) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Future graduation students cannot submit final projects yet. Student id "
                            + studentId + " is a " + info.programType()
                            + " student who started in " + info.startYearEc()
                            + " EC and is expected to submit in " + info.expectedGraduationYearEc()
                            + " EC, but the current Ethiopian year is " + currentEthiopianYear + " EC.");
        }

        return info;
    }

    public StudentAcademicInfo parse(String studentId) {
        if (studentId == null || studentId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student id is required");
        }

        Matcher matcher = STUDENT_ID_PATTERN.matcher(studentId.trim().toUpperCase());
        if (!matcher.matches()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid AAU student id format. Use URG/3456/23 for regular students or NSE/3412/15 for extension students.");
        }

        int startYearEc = ETHIOPIAN_YEAR_BASE + Integer.parseInt(matcher.group(2));
        String programCode = matcher.group(1);
        int finalProjectOffset = "NSE".equals(programCode)
                ? EXTENSION_FINAL_PROJECT_YEAR_OFFSET
                : REGULAR_FINAL_PROJECT_YEAR_OFFSET;
        String programType = "NSE".equals(programCode) ? "extension" : "regular";
        return new StudentAcademicInfo(programCode, programType, startYearEc, startYearEc + finalProjectOffset);
    }

    private int currentEthiopianYear() {
        LocalDate today = LocalDate.now(clock);
        int gregorianYear = today.getYear();
        LocalDate ethiopianNewYear = LocalDate.of(gregorianYear, 9, 11);
        return today.isBefore(ethiopianNewYear) ? gregorianYear - 8 : gregorianYear - 7;
    }

    public record StudentAcademicInfo(
            String programCode,
            String programType,
            int startYearEc,
            int expectedGraduationYearEc) {
    }
}
