package com.example.imprint.repository.message;

import com.example.imprint.domain.message.MessageEntity;
import com.example.imprint.domain.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

    // 받은 쪽지함: 수신자가 나이고, 수신자가 삭제하지 않은 쪽지들만 조회
    List<MessageEntity> findAllByReceiverAndDeletedByReceiverFalseOrderByCreatedAtDesc(UserEntity receiver);

    // 보낸 쪽지함: 발신자가 나이고, 발신자가 삭제하지 않은 쪽지들만 조회
    List<MessageEntity> findAllBySenderAndDeletedBySenderFalseOrderByCreatedAtDesc(UserEntity sender);

    // 안 읽은 쪽지 개수 확인 (알림용)
    long countByReceiverAndIsReadFalseAndDeletedByReceiverFalse(UserEntity receiver);

    // 사용자가 보낸 메시지중 사용자가 삭제 안 한 것 (발신자 기준)
    List<MessageEntity> findBySenderAndDeletedBySenderFalse(UserEntity sender);

    // 사용자가 받은 메시지 중 사용자가 삭제 안 한 것(수신자 기준)
    List<MessageEntity> findByReceiverAndDeletedByReceiverFalse(UserEntity receiver);
}