package com.stevenst.app.payload;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostPayload {
	private Long id;
	private String authorUsername;
	private String title;
	private String description;
	private LocalDateTime createdAt;
	private List<String> mediaLinks;
	private Long likes;
	private Long dislikes;
}
