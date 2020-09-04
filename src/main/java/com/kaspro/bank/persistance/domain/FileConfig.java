package com.kaspro.bank.persistance.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Blob;


@Data
@Entity
@Table(name = "FILE_CONFIG")
public class FileConfig {
    @Id
    @GeneratedValue
    private int id;

    private String param_name;

    private Blob param_value;
}
