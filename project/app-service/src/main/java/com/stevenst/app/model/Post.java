package com.stevenst.app.model;

import java.time.LocalDateTime;

import com.stevenst.lib.model.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "\"post\"")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "\"author_id\"", nullable = false)
	private User author;

	@Column(nullable = false)
	private String title;

	private String description;

	private String mediaName;

	@Column(nullable = false)
	private final LocalDateTime createdAt = LocalDateTime.now();
}
