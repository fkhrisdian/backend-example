package com.kaspro.bank.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by yukibuwana on 1/24/17.
 */

@Data
@AllArgsConstructor
public class K2KBResultVO {

    private GeneralResponse general_response;
    private Object data;

    public K2KBResultVO() { }
}
