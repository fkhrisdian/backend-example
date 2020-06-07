package com.kaspro.bank.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class KeyValuePairedVO {

    @NotBlank(message = "key is required")
    String key;

    String value;
}
