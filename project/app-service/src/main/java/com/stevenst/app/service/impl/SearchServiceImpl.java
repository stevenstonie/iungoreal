package com.stevenst.app.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.stevenst.app.payload.UserPublicPayload;
import com.stevenst.app.repository.UserRepository;
import com.stevenst.app.service.SearchService;
import com.stevenst.lib.model.User;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
	private final UserRepository userRepository;
	private final WebClient webClient;

	@Override
	public List<UserPublicPayload> getUsersMatching(String input) {
		if (input == null || input.isEmpty()) {
			throw new IllegalArgumentException("input cannot be null or empty.");
		}
		if (input.length() < 3) {
			throw new IllegalArgumentException("input must be at least 3 characters long.");
		}

		List<User> matchingUsers = userRepository.findUsersByUsernameContaining(input);

		for (User user : matchingUsers) {
			Mono<String> userPfpLinkMono = webClient.get()
					.uri(uriBuilder -> uriBuilder.path("/api/user/getProfilePictureLink")
							.queryParam("username", user.getUsername())
							.build())
					.retrieve()
					.bodyToMono(new ParameterizedTypeReference<String>() {
					});

			String userPfpLink = userPfpLinkMono.block();
			if (userPfpLink != null) {
				user.setProfilePictureName(userPfpLink
						.replace("{\"string\":\"", "")
						.replace("\"}", ""));
			}

		}

		return matchingUsers.stream()
				.map(user -> new UserPublicPayload(user.getId(), user.getUsername(), user.getProfilePictureName()))
				.collect(Collectors.toList());
	}
}
