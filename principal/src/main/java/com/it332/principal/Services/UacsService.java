package com.it332.principal.Services;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.it332.principal.Models.Uacs;
import com.it332.principal.Repository.UacsRepository;
import com.it332.principal.Security.NotFoundException;

@Service
public class UacsService {

    @Autowired
    private UacsRepository uacsRepository;

    public Uacs createUacs(Uacs uacs) {
        Uacs existingNameUacs = uacsRepository.findByName(uacs.getName());
        Uacs existingCodeUacs = uacsRepository.findByCode(uacs.getCode());

        if (existingNameUacs != null || existingCodeUacs != null) {
            if (existingNameUacs != null) {
                throw new IllegalArgumentException("Uacs with name " + existingNameUacs.getName() +
                        " already exists");
            }
            throw new IllegalArgumentException("Uacs with code " + existingCodeUacs.getCode() +
                    " already exists");
        }

        return uacsRepository.save(uacs);
    }

    public Uacs getUacsByName(String name) {
        Uacs entity = uacsRepository.findByName(name);

        if (entity == null) {
            throw new NotFoundException("Uacs not found with name: " + name);
        }

        return entity;
    }

    public Uacs getUacsByCode(String code) {
        Uacs entity = uacsRepository.findByCode(code);

        if (entity == null) {
            throw new NotFoundException("Uacs not found with code: " + code);
        }

        return entity;
    }

    public List<Uacs> getAllUacs() {
        return uacsRepository.findAll();
    }

    // Find all UACS except those with the specified objectCode
    public List<Uacs> getAllUacsExceptCashAdv() {
        return uacsRepository.findByCodeNot("1990101000");
    }

    public Uacs getUacsById(String id) {
        // Validate the format of the provided ID
        if (!ObjectId.isValid(id)) {
            throw new IllegalArgumentException("Invalid ID format");
        }
        // Retrieve the school entity by ID from the repository
        return uacsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Uacs not found with ID: " + id));
    }

    public Uacs updateUacs(String id, Uacs updatedUacs) {
        // Check if uacs exists
        Uacs existingUacs = getUacsById(id);

        // Check if uacs name and code is already taken
        Uacs existingNameUacs = uacsRepository.findByName(updatedUacs.getName());
        Uacs existingCodeUacs = uacsRepository.findByCode(updatedUacs.getCode());

        if (existingNameUacs != null || existingCodeUacs != null) {
            if (existingNameUacs != null) {
                throw new IllegalArgumentException("Uacs with name " + existingNameUacs.getName() +
                        " already exists");
            }
            throw new IllegalArgumentException("Uacs with code " + existingCodeUacs.getCode() +
                    " already exists");
        }

        // Check for empty fields
        if (updatedUacs.getName() != null) {
            existingUacs.setName(updatedUacs.getName());
        }
        if (updatedUacs.getCode() != null) {
            existingUacs.setCode(updatedUacs.getCode());
        }

        return uacsRepository.save(existingUacs);
    }

    public void deleteSchoolById(String id) {
        Uacs uacs = getUacsById(id);

        uacsRepository.delete(uacs);
    }

}
