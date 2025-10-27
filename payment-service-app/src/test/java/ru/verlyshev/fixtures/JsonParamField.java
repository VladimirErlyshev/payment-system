package ru.verlyshev.fixtures;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonParamField {
    public static final String CURRENCY = "currency";
    public static final String STATUS = "status";
    public static final String MIN_AMOUNT = "minAmount";
    public static final String MAX_AMOUNT = "maxAmount";
    public static final String CREATED_AFTER = "createdAfter";
    public static final String CREATED_BEFORE = "createdBefore";
    public static final String SORT_BY = "sortBy";
    public static final String SORT_DIRECTION = "sortDirection";
    public static final String PAGE = "page";
    public static final String SIZE = "size";
}
