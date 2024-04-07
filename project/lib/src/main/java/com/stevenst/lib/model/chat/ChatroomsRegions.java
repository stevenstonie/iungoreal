package com.stevenst.lib.model.chat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "\"chatrooms_regions\"")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatroomsRegions {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long regionId;

	@Column(nullable = false)
	private Long chatroomId;
}
