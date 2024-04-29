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
@Table(name = "\"post_interaction\"")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostInteraction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "\"user_id\"", nullable = false)
	private User user;

	@ManyToOne(optional = false)
	@JoinColumn(name = "post_id", nullable = false)
	private Post post;

	private boolean liked;

	private boolean disliked;

	@Builder.Default
	private boolean seen = false;
}
