package com.example.imprint.domain;

import com.example.imprint.domain.user.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BoardRequestDto {

    private String name; // 게시판 이름

    // creator를 받아서 엔티티 생성
    public BoardEntity toEntity(UserEntity creator) {
        return BoardEntity.builder()
                .name(name)
                .creator(creator)
                .build();
    }
}
