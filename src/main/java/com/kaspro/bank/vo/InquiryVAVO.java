package com.kaspro.bank.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InquiryVAVO {
    private String client_id;
    private String trx_id;
    private String type;
}
