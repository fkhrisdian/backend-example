package com.kaspro.bank.persistance.domain;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "PARTNER")
@DynamicUpdate
@Data
public class Partner extends Base {

    @Column(nullable = false)
    private String name;

    @Override
    public void prePersist() {
        super.prePersist();
    }
}
