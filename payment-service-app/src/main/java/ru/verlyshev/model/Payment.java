package ru.verlyshev.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Payment {
    private long id;
    private double value;
}
