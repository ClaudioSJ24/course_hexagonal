package com.sanchez.juarez.infrastructure.jpa.adapters;

import com.juarez.domain.entities.order.OrderId;
import com.juarez.domain.entities.order.OrderItem;
import com.juarez.domain.entities.order.OrderNumber;
import com.juarez.domain.entities.order.OrderRoot;
import com.juarez.domain.ports.repositories.OrderRepositoryPort;
import com.juarez.domain.shared.CustomerId;
import com.sanchez.juarez.infrastructure.jpa.entities.OrderEntity;
import com.sanchez.juarez.infrastructure.jpa.entities.OrderProductEntity;
import com.sanchez.juarez.infrastructure.jpa.mappers.OrderJpaMapper;
import com.sanchez.juarez.infrastructure.jpa.repositories.OrderRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

@Repository
public class OrderRepositoryAdapter implements OrderRepositoryPort {

    private static final Logger log = LogManager.getLogger(OrderRepositoryAdapter.class);
    private final OrderRepository orderRepository;
    private OrderJpaMapper orderJpaMapper;

    public OrderRepositoryAdapter(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public OrderRoot save(OrderRoot order) {

        log.debug("Saving order {}", order);

        try {

            OrderEntity orderEntity = this.orderJpaMapper.toEntity(order);

            orderEntity.setId(order.getId().value());
            this.zipOrderItemsIds(order, orderEntity);
            OrderEntity savedOrderEntity = this.orderRepository.save(orderEntity);

            log.debug("Saved order success {}", savedOrderEntity);

            return this.orderJpaMapper.toDomain(savedOrderEntity);


        }catch (Exception e) {
            log.error("Error on persist order", e);
            throw new IllegalStateException(e);
        }

    }

    @Override
    public Optional<OrderRoot> findById(OrderId id) {

        log.debug("Finding all orders by id {}", id);

        try {

            Optional<OrderEntity> entityId = this.orderRepository.findById(id.value());

            if (entityId.isEmpty()) {
                log.debug("No order found with id {}", id);
                return Optional.empty();
            }

            OrderEntity orderEntity = entityId.get();

            return Optional.of(this.orderJpaMapper.toDomain(orderEntity));

        } catch (Exception e) {
            log.error("Error on findAllById order", e);
            return Optional.empty();
        }

    }

    @Override
    public Optional<OrderRoot> findAllByOrderNumber(OrderNumber orderNumber) {

        log.debug("Finding order by number: {}", orderNumber.value());

        try {

            Optional<OrderEntity> optionalOrderEntity = this.orderRepository.findByOrderNumber(orderNumber.value());

            if (optionalOrderEntity.isEmpty()) {
                log.debug("Order not found with number: {}", orderNumber.value());
                return Optional.empty();
            }

            OrderEntity entity = optionalOrderEntity.get();
            log.debug("Order found with number: {} (ID: {})",
                    entity.getOrderNumber(),
                    entity.getId());

            OrderRoot domain = this.orderJpaMapper.toDomain(entity);

            return Optional.of(domain);


        }catch (Exception ex) {
            log.error("Failed to find order by number: {}", orderNumber.value(), ex);
            return Optional.empty();
        }


    }

    @Override
    public List<OrderRoot> findCustomerId(CustomerId customerId) {

        log.debug("Finding orders by customer ID: {}", customerId.value());

        try {

            List<OrderEntity> orderEntityList = this.orderRepository.findAllByCustomerId(customerId.value());
            log.debug("Found {} orders for customer ID: {}", orderEntityList.size(), customerId.value());

            List<OrderRoot> orderRootList = orderEntityList.stream()
                    .map(entity -> {
                        try {
                            return  this.orderJpaMapper.toDomain(entity);
                        }catch (Exception ex) {
                            log.error("Failed to map order entity to domain: {}", entity.getId(), ex);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList();

            log.debug("Successfully rehydrated {} orders", orderRootList.size());

            return orderRootList;


        }catch (Exception ex) {
            log.error("Failed to find orders by customer ID: {}", customerId.value(), ex);
            return List.of();
        }

    }

    @Override
    public void delete(OrderRoot order) {

        log.info("Deleting order {}", order);

        try {

            this.orderRepository.deleteById(order.getId().value());
            log.info("Order deleted successfully");

        } catch (Exception e) {
            log.error("Failed to delete order with ID: {}", order.getId().value(), e);
            throw new IllegalStateException(e);
        }

    }

    private void zipOrderItemsIds(OrderRoot root, OrderEntity entity){

        if (root.getItems().isEmpty() || root.getItems() == null) {
            log.debug("Items EMPTY");
            return;
        }

        List<OrderProductEntity> itemsEntity = entity.getItems();
        List<OrderItem> itesDomain = root.getItems();

        IntStream.range(0, itesDomain.size())
                .forEach(i -> itemsEntity.get(i).setId(itesDomain.get(i).getId().value()));
    }
}
