package com.order.api01authgateway.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Detalhes da ordenação")
public record SortObject(
        @Schema(description = "Direção", example = "ASC") String direction,
        @Schema(description = "Tratamento de nulos", example = "NATIVE") String nullHandling,
        @Schema(description = "Indica se a ordem é ascendente", example = "true") boolean ascending,
        @Schema(description = "Propriedade ordenada", example = "orderDate") String property,
        @Schema(description = "Indica se ignora caixa", example = "false") boolean ignoreCase
) {
}
