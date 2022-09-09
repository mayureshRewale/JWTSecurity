package com.jwt.security.Commons.Constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public enum ErrorMessageConstants {

    NO_DATA_FOUND("No data found"),
    NO_RESULT_FOUND("No result found"),
    TRY_AGAIN("Something went wrong. Please try again.");

    @Getter
    private String value;

    public static List<String> getAllValues() {
        return List.of(ErrorMessageConstants.values()).stream().map(data -> data.value).collect(Collectors.toList());
    }


}