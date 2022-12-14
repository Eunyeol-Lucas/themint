package com.takealook.db.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@Entity
@DynamicInsert // JPA insert 시 null인 필드 제외
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;
    private String roomId; // 방번호
    private String message; // 메시지
    private Long memberSeq; // 메시지 작성자 번호
    private String nickname; // 작성자 닉네임
    private String date; // 작성 시간
    private int type; // 메시지 타입
    }
