package com.example.imprint.board;

import com.example.imprint.domain.BoardEntity;
import com.example.imprint.domain.user.UserEntity;
import com.example.imprint.repository.BoardRepository;
import com.example.imprint.repository.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    private UserEntity testUser;

    @BeforeEach
    void setup() {
        // 테스트용 유저 생성
        testUser = UserEntity.builder()
                .email("test@test.com")
                .password("password")
                .name("테스트유저")
                .nickname("테스터")
                .build();
        userRepository.save(testUser);
    }

    @Test
    @DisplayName("게시판 저장 테스트")
    void saveBoard() {
        // given
        BoardEntity board= BoardEntity.builder()
                .name("테스트 게시판")
                .creator(testUser)
                .build();

        // when
        BoardEntity saved = boardRepository.save(board);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("테스트 게시판");
        assertThat(saved.getCreator().getId()).isEqualTo(testUser.getId());
    }

    @Test
    @DisplayName("게시판 이름으로 조회 테스트")
    void findName() {
        // given
        BoardEntity board = BoardEntity.builder()
                .name("개발 게시판")
                .creator(testUser)
                .build();
        boardRepository.save(board);

        // when
        Optional<BoardEntity> found = boardRepository.findByName("개발 게시판");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("개발 게시판");
    }

    @Test
    @DisplayName("게시판 삭제 테스트")
    void findCreator() {
        // given
        BoardEntity board = BoardEntity.builder()
                .name("삭제될 게시판")
                .creator(testUser)
                .build();

        // 저장
        BoardEntity saved = boardRepository.save(board);

        // when
        boardRepository.delete(saved);

        // then
        Optional<BoardEntity> deleted = boardRepository.findById(saved.getId());
        assertThat(deleted).isEmpty();
    }
}
