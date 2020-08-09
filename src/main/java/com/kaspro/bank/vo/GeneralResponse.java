package com.kaspro.bank.vo;

import lombok.Data;

@Data
public class GeneralResponse {
    private boolean response_status;

    private String response_code;

    private String response_message;

    private String response_timestamp;
}
