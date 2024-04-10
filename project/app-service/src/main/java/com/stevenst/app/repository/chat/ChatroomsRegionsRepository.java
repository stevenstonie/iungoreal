package com.stevenst.app.repository.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.stevenst.lib.model.chat.ChatroomsRegions;

import jakarta.transaction.Transactional;

public interface ChatroomsRegionsRepository extends JpaRepository<ChatroomsRegions, Long> {
	@Query(value = "SELECT chatroom.chatroomId FROM ChatroomsRegions chatroom WHERE chatroom.regionId = :regionId")
	Long findChatroomIdByRegionId(Long regionId);

	@Modifying
	@Transactional
	void deleteByChatroomId(Long chatroomId);
}
