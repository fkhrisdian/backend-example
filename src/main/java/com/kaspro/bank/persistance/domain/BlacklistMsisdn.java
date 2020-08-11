package com.kaspro.bank.persistance.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Date;

@Entity
@Table(name = "BLACKLIST_MSISDN")
@DynamicUpdate
@Data
public class BlacklistMsisdn extends Base{
    @ApiModelProperty(value = "Mandatory",required = true)
    @Column(name = "MSISDN",nullable = false)
    private String msisdn;

    @ApiModelProperty(value = "Mandatory",required = true)
    @Column(name = "REASON",nullable = false)
    private String reason;

    @ApiModelProperty(value = "Mandatory",required = true)
    @Column(name = "NAME",nullable = false)
    private String name;

    @ApiModelProperty(value = "Mandatory",required = true)
    @Column(name = "EMAIl",nullable = false)
    private String email;

    @ApiModelProperty(value = "Mandatory",required = true)
    @Column(name = "VA",nullable = false)
    private String va;

    @ApiModelProperty(value = "Optional",required = false)
    @Column(name = "STATUS",nullable = false)
    private String status;

}
