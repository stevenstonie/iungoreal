package com.stevenst.app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.stevenst.lib.model.User;

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
@Table(name = "\"friendships\"")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Friendships {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "\"user1_id\"")
	private User user1;

	@ManyToOne(optional = false)
	@JoinColumn(name = "\"user2_id\"")
	private User user2;

	@Column(nullable = false)
	private final LocalDateTime createdAt = LocalDateTime.now();
}
