package com.stevenst.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stevenst.app.model.PostMedia;

@Repository
public interface PostMediaRepository extends JpaRepository<PostMedia, Long> {
	
}
