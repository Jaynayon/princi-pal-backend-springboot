package com.it332.principal.Services;

import com.it332.principal.Models.Code;
import com.it332.principal.Models.School;
import com.it332.principal.Repository.CodeRepository;
import com.it332.principal.Security.NotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Random;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class CodeService {
    private static final long EXPIRE_CODE = 10080; // Token expiration time in minutes (7 days)
    private static final int CODE_LENGTH = 6;

    @Autowired
    private CodeRepository codeRepository;

    @Autowired
    private SchoolService schoolService;

    // Generate code
    public Code createCode(String schoolId) {
        // Get the existing code by schoolId
        Code existingCode = getCodeBySchoolId(schoolId);

        if (existingCode != null) {
            throw new IllegalArgumentException("Code already exists for this school");
        }

        // Generate a random code
        String code = generateCode(CODE_LENGTH);

        // Save the code to the database
        return codeRepository.save(new Code(code, schoolId));
    }

    public String getReferralCode(String schoolId) {
        // Get the existing code by schoolId
        Code existingCode = getCodeBySchoolId(schoolId);

        if (existingCode == null) {
            // If code doesn't exist, create a new one
            return createCode(schoolId).getCode();
        }

        if (isExpired(existingCode)) {
            // If the existing code is expired, renew it
            return updateNewCode(schoolId).getCode();
        }

        // If the code exists and is not expired
        return existingCode.getCode();
    }

    public School getSchoolByCode(String code) {
        Code existingCode = getCode(code);
        return schoolService.getSchoolById(existingCode.getSchoolId());
    }

    // Get code
    public Code getCode(String code) {
        Code existingCode = codeRepository.findByCode(code);

        if (existingCode == null) {
            throw new NotFoundException("Code not found");
        }

        return existingCode;
    }

    // Get code by schoolId
    public Code getCodeBySchoolId(String schoolId) {
        School school = schoolService.getSchoolById(schoolId);
        return codeRepository.findBySchoolId(school.getId());
    }

    // Update code
    public Code updateNewCode(String schoolId) {
        // Get the existing code by schoolId
        Code existingCode = getCodeBySchoolId(schoolId);

        // Generate a random code
        String newCode = generateCode(CODE_LENGTH);
        existingCode.setTokenCreationDate(LocalDateTime.now());
        existingCode.setCode(newCode);

        return codeRepository.save(existingCode);
    }

    // Delete code
    public void deleteCode(String schoolId) {
        Code existingCode = getCodeBySchoolId(schoolId);
        codeRepository.delete(existingCode);
    }

    // Helper functions
    public static String generateCode(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder code = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            code.append(characters.charAt(index));
        }

        return code.toString();
    }

    public static boolean isExpired(Code code) {
        LocalDateTime now = LocalDateTime.now();
        Duration diff = Duration.between(code.getTokenCreationDate(), now);
        return diff.toMinutes() >= EXPIRE_CODE;
    }
}
