package com.db.ms.shelfie.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum OrderEnum {

    PENDING(1,"Order is being processed"),
    SHIPPED(2,"Order has been shipped"),
    DELIVERED(3,"Order delivered successfully");

    private  final  int statusCode;
    private  final String statusDetail;
}
