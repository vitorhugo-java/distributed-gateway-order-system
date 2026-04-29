package com.order.api02orderscrud.service;

import com.order.api02orderscrud.dto.OrderDTO;
import com.order.api02orderscrud.entity.Order;
import com.order.api02orderscrud.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Serviço responsável pelas regras de negócio relacionadas aos pedidos.
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;

    /**
     * Construtor com injeção de dependência do repositório de pedidos.
     * @param orderRepository repositório de pedidos
     */
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Busca uma lista paginada de todos os pedidos.
     * @param pageable informações de paginação
     * @return página de DTOs de pedido
     */
    @Transactional(readOnly = true)
    public Page<OrderDTO> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable).map(o -> new OrderDTO(o.getId(), o.getCustomerName(), o.getCustomerEmail(), o.getOrderDate(), o.getStatus(), o.getTotalAmount()));
    }

    /**
     * Busca um pedido pelo seu identificador único.
     * @param id identificador do pedido
     * @return DTO do pedido encontrado
     * @throws RuntimeException caso o pedido não seja encontrado
     */
    @Transactional(readOnly = true)
    public OrderDTO findById(UUID id) {
        Order o = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        return new OrderDTO(o.getId(), o.getCustomerName(), o.getCustomerEmail(), o.getOrderDate(), o.getStatus(), o.getTotalAmount());
    }

    /**
     * Salva um novo pedido no sistema.
     * @param dto DTO contendo os dados do pedido a ser criado
     * @return DTO do pedido criado
     */
    @Transactional
    public OrderDTO save(OrderDTO dto) {
        Order o = new Order();
        o.setCustomerName(dto.customerName());
        o.setCustomerEmail(dto.customerEmail());
        o.setOrderDate(dto.orderDate());
        o.setStatus(dto.status());
        o.setTotalAmount(java.math.BigDecimal.ZERO);
        o = orderRepository.save(o);
        return new OrderDTO(o.getId(), o.getCustomerName(), o.getCustomerEmail(), o.getOrderDate(), o.getStatus(), o.getTotalAmount());
    }

    /**
     * Exclui um pedido pelo seu identificador único.
     * @param id identificador do pedido a ser excluído
     */
    @Transactional
    public void delete(UUID id) {
        orderRepository.deleteById(id);
    }
}
