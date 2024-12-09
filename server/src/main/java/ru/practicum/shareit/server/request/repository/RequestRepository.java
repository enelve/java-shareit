package ru.practicum.shareit.server.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.server.request.entity.Request;
import ru.practicum.shareit.server.user.entity.User;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByUserOrderByCreatedDesc(User user);

    List<Request> findAllByUserNot(User user);
}
