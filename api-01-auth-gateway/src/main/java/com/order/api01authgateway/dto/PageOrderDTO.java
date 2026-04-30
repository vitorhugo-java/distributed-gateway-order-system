package com.order.api01authgateway.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Página de pedidos")
public record PageOrderDTO(
        @Schema(description = "Quantidade total de elementos", example = "1") long totalElements,
        @Schema(description = "Quantidade total de páginas", example = "1") int totalPages,
        @Schema(description = "Tamanho da página", example = "20") int size,
        @Schema(description = "Conteúdo da página") List<OrderDTO> content,
        @Schema(description = "Número da página atual", example = "0") int number,
        @Schema(description = "Ordenação aplicada") List<SortObject> sort,
        @Schema(description = "Indica se é a primeira página", example = "true") boolean first,
        @Schema(description = "Indica se é a última página", example = "true") boolean last,
        @Schema(description = "Quantidade de elementos na página atual", example = "1") int numberOfElements,
        @Schema(description = "Metadados de paginação") PageableObject pageable,
        @Schema(description = "Indica se a página está vazia", example = "false") boolean empty
) {
}
