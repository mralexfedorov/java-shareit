package ru.practicum.shareit.itemRequest.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.itemRequest.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {
    Optional<ItemRequest> findById(int id);

    List<ItemRequest> findAllByRequesterId(int requesterId);

    List<ItemRequest> findAllByRequesterIdNot(int requesterId, Pageable pageable);
}
