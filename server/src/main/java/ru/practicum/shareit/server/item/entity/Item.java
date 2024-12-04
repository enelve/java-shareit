package ru.practicum.shareit.server.item.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.server.user.entity.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "item", schema = "public")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name_item", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "available", nullable = false)
    @NotNull
    private Boolean available;

    @Column(name = "request_id", nullable = true)
    private Long requestId;

    @ManyToOne
    private User user;
}
