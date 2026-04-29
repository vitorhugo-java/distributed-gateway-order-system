package com.order.api02orderscrud.service;

import com.order.api02orderscrud.dto.OrderItemDTO;
import com.order.api02orderscrud.entity.Order;
import com.order.api02orderscrud.entity.OrderItem;
import com.order.api02orderscrud.repository.OrderItemRepository;
import com.order.api02orderscrud.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Serviço responsável pelas regras de negócio relacionadas aos itens de pedido.
 */
@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;

    /**
     * Construtor com injeção de dependência dos repositórios necessários.
     * @param orderItemRepository repositório de itens de pedido
     * @param orderRepository repositório de pedidos
     */
    public OrderItemService(OrderItemRepository orderItemRepository, OrderRepository orderRepository) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * Busca todos os itens associados a um pedido específico.
     * @param orderId identificador do pedido
     * @return lista de DTOs dos itens do pedido
     * @throws RuntimeException caso o pedido não seja encontrado
     */
    @Transactional(readOnly = true)
    public List<OrderItemDTO> findByOrderId(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        return order.getItems().stream()
                .map(i -> new OrderItemDTO(i.getId(), i.getProductName(), i.getQuantity(), i.getUnitPrice(), i.getSubtotal()))
                .collect(Collectors.toList());
    }

    /**
     * Adiciona um novo item a um pedido existente.
     * @param orderId identificador do pedido
     * @param dto DTO do item a ser adicionado
     * @return DTO do item criado
     * @throws RuntimeException caso o pedido não seja encontrado
     */
    @Transactional
    public OrderItemDTO addItem(UUID orderId, OrderItemDTO dto) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        OrderItem item = new OrderItem();
        item.setProductName(dto.productName());
        item.setQuantity(dto.quantity());
        item.setUnitPrice(dto.unitPrice());
        order.addItem(item);
        orderRepository.save(order);
        return new OrderItemDTO(item.getId(), item.getProductName(), item.getQuantity(), item.getUnitPrice(), item.getSubtotal());
    }
}
