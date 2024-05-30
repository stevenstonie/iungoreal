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
public class CommentDetachedPayload {
	private Long id;
	private String postTitle;
	private String content;
	private LocalDateTime createdAt;
}
