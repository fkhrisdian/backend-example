package com.kaspro.bank.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UpdateStatusVO {
    @NotBlank(message = "status is required")
    private String status;

    @NotBlank(message = "id is required")
    private int id;
}
