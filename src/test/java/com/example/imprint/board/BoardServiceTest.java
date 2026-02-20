package com.example.imprint.board;

import com.example.imprint.domain.BoardEntity;
import com.example.imprint.domain.BoardRequestDto;
import com.example.imprint.domain.BoardResponseDto;
import com.example.imprint.domain.user.UserEntity;
import com.example.imprint.repository.BoardRepository;
import com.example.imprint.repository.user.UserRepository;
import com.example.imprint.service.BoardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BoardService boardService;

    private UserEntity testUser;
    private BoardEntity testBoard;

    @BeforeEach
    void setUp() {
        testUser = UserEntity.builder()
                .email("test@test.com")
                .password("password")
                .name("테스트유저")
                .nickname("테스터")
                .build();
        // Reflection으로 ID 설정 (테스트용)
        setId(testUser, 1L);

        testBoard = BoardEntity.builder()
                .name("테스트 게시판")
                .creator(testUser)
                .build();
        setId(testBoard, 1L);
    }

    @Test
    @DisplayName("게시판 생성 성공 테스트")
    void createBoard() {
        // given
        BoardRequestDto requestDto = new BoardRequestDto("새 게시판");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(boardRepository.save(any(BoardEntity.class))).thenReturn(testBoard);

        // when
        BoardResponseDto result = boardService.save(requestDto, 1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCreatorId()).isEqualTo(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(boardRepository, times(1)).save(any(BoardEntity.class));
    }

    @Test
    @DisplayName("존재하지 않는 유저로 게시판 생성 시 예외 발생")
    void createBoardWithInvalidUser() {
        // given
        BoardRequestDto requestDto = new BoardRequestDto("새 게시판");

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> boardService.save(requestDto, 999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 유저가 없습니다");
    }

    @Test
    @DisplayName("전체 게시판 조회 테스트")
    void findAllBoards() {
        // given
        List<BoardEntity> boards = Arrays.asList(testBoard);
        when(boardRepository.findAll()).thenReturn(boards);

        // when
        List<BoardResponseDto> result = boardService.findAll();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("테스트 게시판");
    }

    @Test
    @DisplayName("게시판 수정 성공 테스트")
    void updateBoard() {
        // given
        BoardRequestDto requestDto = new BoardRequestDto("새 게시판");

        when(boardRepository.findById(1L)).thenReturn(Optional.of(testBoard));

        // when
        Long updatedId = boardService.update(1L, requestDto, 1L);

        // then
        assertThat(updatedId).isEqualTo(1L);
        assertThat(testBoard.getName()).isEqualTo("수정된 게시판");
    }

    @Test
    @DisplayName("권한 없는 유저가 게시판 수정 시 예외 발생")
    void updateBoardWithoutPermission() {
        // given
        BoardRequestDto requestDto = new BoardRequestDto("새 게시판");

        when(boardRepository.findById(1L)).thenReturn(Optional.of(testBoard));

        // when & then
        assertThatThrownBy(() -> boardService.update(1L, requestDto, 999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("게시판을 수정할 권한이 없습니다");
    }

    @Test
    @DisplayName("게시판 삭제 성공 테스트")
    void deleteBoard() {
        // given
        when(boardRepository.findById(1L)).thenReturn(Optional.of(testBoard));

        // when
        boardService.delete(1L, 1L);

        // then
        verify(boardRepository, times(1)).delete(testBoard);
    }

    @Test
    @DisplayName("권한 없는 유저가 게시판 삭제 시 예외 발생")
    void deleteBoardWithoutPermission() {
        // given
        when(boardRepository.findById(1L)).thenReturn(Optional.of(testBoard));

        // when & then
        assertThatThrownBy(() -> boardService.delete(1L, 999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("게시판을 삭제할 권한이 없습니다");
    }

    // 테스트용 헬퍼 메서드
    private void setId(Object entity, Long id) {
        try {
            var field = entity.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
