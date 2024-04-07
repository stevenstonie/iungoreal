package com.stevenst.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.stevenst.lib.model.chat.ChatroomsRegions;

@Repository
public interface ChatroomsRegionsRepository extends JpaRepository<ChatroomsRegions, Long> {
	@Query(value = "SELECT chatroom.chatroomId FROM ChatroomsRegions chatroom WHERE chatroom.regionId = :regionId")
	Long findChatroomIdByRegionId(Long regionId);
}
