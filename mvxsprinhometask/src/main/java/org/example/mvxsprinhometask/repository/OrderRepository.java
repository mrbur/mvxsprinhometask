package org.example.mvxsprinhometask.repository;

import org.example.mvxsprinhometask.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = {"products"})
    @Query("SELECT o FROM Order o")
    Page<Order> findAllOrdersPaged(Pageable pageable);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.products WHERE o.orderId = :id")
    Optional<Order> findByIdWithProducts(@Param("id") Long id);
}