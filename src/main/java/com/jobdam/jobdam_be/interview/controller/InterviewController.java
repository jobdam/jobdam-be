package com.jobdam.jobdam_be.interview.controller;

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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user/interviews")
public class InterviewController {

    private final InterviewService interviewService;

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

        List<QuestionFeedbackDto> feedbackList = interviewService.getFeedback(interviewId, userId);
        return ResponseEntity.ok(feedbackList);
    }
//  [
//    {
//        "questionId": 1,
//            "question": "팀원과 갈등이 생겼을때 해결방법",
//            "feedbacks": [
//                "해결방식이 구조적으로 설명되어 좋앗어요",
//                "경험은 적절했지만, 말이 너무 길어졌어요"
//        ]
//    }
//  ]

}
