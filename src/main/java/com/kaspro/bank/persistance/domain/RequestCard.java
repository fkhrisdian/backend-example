package com.kaspro.bank.persistance.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "REQUEST_CARD")
@DynamicUpdate
@Data
public class RequestCard extends Base{
    @ApiModelProperty(value = "Mandatory",required = true)
    @Column(name = "MSISDN",nullable = false)
    private String msisdn;

    @ApiModelProperty(value = "Mandatory",required = true)
    @Column(name = "RECIPIENT_NAME",nullable = false)
    private String name;

    @ApiModelProperty(value = "Mandatory",required = true)
    @Column(name = "RECIPIENT_ADDRESS",nullable = false)
    private String address;

    @ApiModelProperty(value = "Mandatory",required = true)
    @Column(name = "RECIPIENT_CITY",nullable = false)
    private String city;

    @ApiModelProperty(value = "Mandatory",required = true)
    @Column(name = "RECIPIENT_PROVINCE",nullable = false)
    private String province;

    @ApiModelProperty(value = "Optional",required = true)
    @Column(name = "RECIPIENT_ZIP_CODE",nullable = true)
    private String zipCode;

}
