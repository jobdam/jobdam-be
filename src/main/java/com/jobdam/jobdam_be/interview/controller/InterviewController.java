package com.jobdam.jobdam_be.interview.controller;

import com.jobdam.jobdam_be.clova.service.ClovaAiService;
import com.jobdam.jobdam_be.interview.dto.QuestionFeedbackDto;
import com.jobdam.jobdam_be.interview.model.Interview;
import com.jobdam.jobdam_be.interview.service.InterviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user/interviews")
public class InterviewController {

    private final InterviewService interviewService;
    private final ClovaAiService clovaAiService;

    @GetMapping
    public ResponseEntity<Map<String, List<Interview>>> getInterview() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.valueOf(authentication.getName());

        return ResponseEntity.ok(interviewService.getInterview(userId));
    }

    // GET user/interviews/5/feedback
    @GetMapping("/{interviewId}/feedback")
    public ResponseEntity<List<QuestionFeedbackDto>> getFeedbackByInterviewAndUser(@PathVariable Long interviewId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.valueOf(authentication.getName());

        List<QuestionFeedbackDto> feedbackList = interviewService.getFeedbackHistory(interviewId, userId);
        return ResponseEntity.ok(feedbackList);
    }

    /* HACK : 해당 코드는 임시로 보여주기 위한 코드로
              인터뷰가 종료 되었을 때 실행되기를 희망
    */
    @GetMapping("/test")
    public ResponseEntity<String> testGenerateFeedbackReportTEST() throws Exception {
        // 다른 유저들에게 받은 피드백을 String으로 변환하여 받음
        String feedbacks = interviewService.getFeedbacksForSameInterview(4L);

        // List로 받을 때, 0번째 인덱스에는 잘한점, 1번째 인덱스에는 개선할 점이 포함
        // 잘한점과, 개선할 점을 하나의 문자열로 받고 있음 - 요청시 수정가능
        CompletableFuture<List<String>> result = clovaAiService.analyzeFeedback(feedbacks);

        result.thenAccept(reports -> {
            // List<String>(2) 으로 0번 1번 분류하여 update 쿼리 호출
            interviewService.insertFeedbackReport(/*인터뷰 아이디*/ 4L, reports);
        });

        return ResponseEntity.ok("피드백 리포트 생성 성공");
    }
}
