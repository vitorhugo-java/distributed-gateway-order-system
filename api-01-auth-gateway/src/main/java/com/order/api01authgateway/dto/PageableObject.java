package com.order.api01authgateway.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Detalhes da paginação")
public record PageableObject(
        @Schema(description = "Offset do resultado", example = "0") long offset,
        @Schema(description = "Ordenação aplicada") List<SortObject> sort,
        @Schema(description = "Indica se está sem paginação", example = "false") boolean unpaged,
        @Schema(description = "Tamanho da página", example = "20") int pageSize,
        @Schema(description = "Número da página", example = "0") int pageNumber,
        @Schema(description = "Indica se está paginado", example = "true") boolean paged
) {
}
