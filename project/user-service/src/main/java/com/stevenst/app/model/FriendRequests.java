package com.stevenst.app.model;

import com.stevenst.lib.model.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "\"friend_requests\"")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequests {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "\"sender_id\"", nullable = false)
    private User sender;

    @ManyToOne(optional = false)
    @JoinColumn(name = "\"receiver_id\"", nullable = false)
    private User receiver;

    @Column(nullable = false)
    private final LocalDateTime sentAt = LocalDateTime.now();
}
