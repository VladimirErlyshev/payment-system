package ru.verlyshev.fixtures;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JsonPathField {
    public static final String GUID = "$.guid";
    public static final String INQUIRY_REF_ID = "$.inquiryRefId";
    public static final String AMOUNT = "$.amount";
    public static final String CURRENCY = "$.currency";
    public static final String TRANSACTION_REF_ID = "$.transactionRefId";
    public static final String STATUS = "$.status";
    public static final String NOTE = "$.note";
    public static final String CREATED_AT = "$.createdAt";
    public static final String CONTENT = "$.content";
    public static final String TOTAL_ELEMENTS = "$.totalElements";
    public static final String PAGE_NUMBER = "$.number";
    public static final String PAGE_SIZE = "$.size";
    public static final String FIRST = "$.first";
}
