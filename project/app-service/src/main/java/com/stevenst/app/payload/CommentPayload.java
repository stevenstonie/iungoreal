package com.stevenst.app.payload;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentPayload {
	private Long id;
	private String authorUsername;
	private Long postId;
	private String content;
	private LocalDateTime createdAt;
}
