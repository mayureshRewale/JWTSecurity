package com.jwt.security.Dao.Beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceResponseBean {

    private String message;

    private String errorMessage;

    @Builder.Default
    private Boolean status = Boolean.FALSE;

    private Object data = null;

    private Set<String> errors;

    private Set<Object> dataList;
}