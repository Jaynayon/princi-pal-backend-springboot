package com.it332.principal.DTO;

import java.util.ArrayList;
import java.util.List;

import com.it332.principal.Models.Uacs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JEVSummary {
    private String id;
    private String uacsCode;
    private String uacsName;
    private String amountType = "Credit";
    private Double amount = 0.0;
    private List<LRResponse> lrs = new ArrayList<>();

    public JEVSummary(Uacs uacs) {
        this.id = uacs.getId();
        this.uacsCode = uacs.getCode();
        this.uacsName = uacs.getName();
    }

    public JEVSummary(Uacs uacs, List<LRResponse> lr) {
        this.id = uacs.getId();
        this.uacsCode = uacs.getCode();
        this.uacsName = uacs.getName();
        this.lrs = lr;
    }

    public void addLr(LRResponse lr) {
        lrs.add(lr);
    }
}
