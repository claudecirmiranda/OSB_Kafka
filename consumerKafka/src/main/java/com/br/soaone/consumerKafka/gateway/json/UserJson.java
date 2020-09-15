package com.br.soaone.consumerKafka.gateway.json;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserJson {
    private String uuid;

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    @NotEmpty
    private String country;

    @NotNull
    @NotEmpty
    private String city;
}