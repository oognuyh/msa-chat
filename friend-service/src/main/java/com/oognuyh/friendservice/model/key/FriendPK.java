package com.oognuyh.friendservice.model.key;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendPK implements Serializable {
    
    private String id;

    private String userId;
}
