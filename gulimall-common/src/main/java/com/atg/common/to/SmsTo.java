package com.atg.common.to;

import lombok.Data;

@Data
public class SmsTo {
    private String phone;
    private String code;
    private String minute;
}
