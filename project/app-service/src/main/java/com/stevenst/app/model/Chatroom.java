package com.stevenst.app.model;

import java.time.LocalDateTime;

import com.stevenst.lib.model.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "\"chatroom\"")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chatroom {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ChatroomType type;

	private String adminUsername;

	@Builder.Default
	@Column(nullable = false)
	private LocalDateTime lastMessageTime = LocalDateTime.now();
}