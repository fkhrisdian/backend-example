package com.kaspro.bank.persistance.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Data
@Entity
@Table(name = "KASPROBANKAPP_CONFIG")
@AllArgsConstructor
@NoArgsConstructor
public class KasprobankConfig{
    @Id
    @GeneratedValue
    private int id;

    private String param_name;

    private String param_value;
}
