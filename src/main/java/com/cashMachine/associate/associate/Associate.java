package com.cashMachine.associate.associate;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Data
@Table(name = "associate")
public class Associate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "document_number")
    private String documentNumber;
    @Column(name = "salary")
    private BigDecimal salary;
}
