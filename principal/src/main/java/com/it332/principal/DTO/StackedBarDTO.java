package com.it332.principal.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StackedBarDTO {
    String name;
    List<Double> data;
}
