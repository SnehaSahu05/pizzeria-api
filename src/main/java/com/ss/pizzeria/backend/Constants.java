package com.ss.pizzeria.backend;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Sneha
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Tags {
        public static final String AUTH = "Auth";
        public static final String ORDERS = "Orders";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Paths {
        public static final String AUTH = "/auth";
        public static final String ORDERS = "/orders";
        public static final String PARAM_ORDER_ID = "Order_ID";
    }

    // TODO: 2-security implementation
    public static final String AUTH_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2MjkzMDAxNTgsIm5iZiI6MTYyOTMwMDE1OCwianRpIjoiNDc2M2JjZmUtMzM5Yy00MWFjLTkxY2EtZGE1MDk5Yjg3NmUzIiwiZXhwIjoxNjI5MzAxMDU4LCJpZGVudGl0eSI6InRlc3QiLCJmcmVzaCI6ZmFsc2UsInR5cGUiOiJhY2Nlc3MifQ.E6XL5Ese6yG1VmoVu8cl-sXThjz4TCSYCpi1QmtwdkQ";
}
