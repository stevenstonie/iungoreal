package com.stevenst.app.model;

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
@Table(name = "\"chatroom_participant\"")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatroomParticipant {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "chatroom_id")
	private Chatroom chatroom;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Column(nullable = false)
	@Builder.Default
	private boolean hasLeft = false;
}
