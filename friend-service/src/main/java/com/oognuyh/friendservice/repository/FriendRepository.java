package com.oognuyh.friendservice.repository;

import java.util.List;

import com.oognuyh.friendservice.model.Friend;
import com.oognuyh.friendservice.model.key.FriendPK;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRepository extends JpaRepository<Friend, FriendPK> {
    
    List<Friend> findFriendsById(String id);
    List<Friend> findFriendsByUserId(String userId);
    void deleteFriendByIdAndUserId(String id, String userId);
}
