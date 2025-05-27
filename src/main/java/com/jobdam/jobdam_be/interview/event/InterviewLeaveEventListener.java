package com.jobdam.jobdam_be.interview.event;

import com.jobdam.jobdam_be.chat.dto.ChatStatusMessageDTO;
import com.jobdam.jobdam_be.chat.event.ChatSessionEvent;
import com.jobdam.jobdam_be.clova.service.ClovaAiService;
import com.jobdam.jobdam_be.interview.exception.InterviewErrorCode;
import com.jobdam.jobdam_be.interview.exception.InterviewException;
import com.jobdam.jobdam_be.interview.model.Interview;
import com.jobdam.jobdam_be.interview.service.InterviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class InterviewLeaveEventListener {
    private final InterviewService interviewService;
    private final ClovaAiService clovaAiService;

    //인터뷰 나가면 발생하는 리스너
    @EventListener
    public void handleInterviewLeave(InterviewLeaveEvent event) throws Exception {
        log.info("[이벤트리너]userId{}", event.userId());
        Interview interview = interviewService.findOneLatestInterviewByUserId(event.userId());

        // 다른 유저들에게 받은 피드백을 String으로 변환하여 받음
        String feedbacks = interviewService.getFeedbacksForSameInterview(interview.getId());

        ArrayList<String> tmpReports = new ArrayList<>();
        if (feedbacks == null || feedbacks.length() < 30) {
            tmpReports.add("Ai 분석에 실패하였습니다.");
            tmpReports.add("Ai 분석에 실패하였습니다.");
            interviewService.insertFeedbackReport(interview.getId(), tmpReports);
            return;
        }

        tmpReports.add("Ai 분석 중입니다. 잠시 후에 다시 접근해주세요.");
        tmpReports.add("Ai 분석 중입니다. 잠시 후에 다시 시도해주세요.");
        interviewService.insertFeedbackReport(interview.getId(), tmpReports);

        // List로 받을 때, 0번째 인덱스에는 잘한점, 1번째 인덱스에는 개선할 점이 포함
        // 잘한점과, 개선할 점을 하나의 문자열로 받고 있음 - 요청시 수정가능
        CompletableFuture<List<String>> result = clovaAiService.analyzeFeedback(feedbacks);

        result.thenAccept(reports -> {
            // List<String>(2) 으로 0번 1번 분류하여 update 쿼리 호출
            interviewService.insertFeedbackReport(/*인터뷰 아이디*/ interview.getId(), reports);
        });

        log.info("[화상채팅종료] userId = {} interviewId = {}", event.userId(), interview.getId());
    }
}
