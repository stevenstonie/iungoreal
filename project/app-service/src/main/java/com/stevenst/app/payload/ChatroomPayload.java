package com.stevenst.app.payload;

import java.time.LocalDateTime;
import java.util.List;

import com.stevenst.app.model.chat.ChatroomType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatroomPayload {
	private Long id;
	private String name;
	private ChatroomType type;
	private String adminUsername;
	private List<String> participantsUsernames;
	private LocalDateTime lastMessageTime;
}
