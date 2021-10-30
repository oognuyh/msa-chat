package com.oognuyh.friendservice.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.IdClass;

import com.oognuyh.friendservice.model.key.FriendPK;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(FriendPK.class)
@EntityListeners(AuditingEntityListener.class)
public class Friend {
    
    @Id
    private String id;

    @Id
    private String userId;

    @CreatedDate
    private Date createdAt;
}
