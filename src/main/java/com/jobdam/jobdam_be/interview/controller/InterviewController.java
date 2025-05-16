package com.jobdam.jobdam_be.interview.controller;

import com.jobdam.jobdam_be.interview.dto.*;
import com.jobdam.jobdam_be.interview.service.InterviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user/interviews")
public class InterviewController {

    private final InterviewService interviewService;

    // GET user/interviews/5/feedback
    @GetMapping("/{interviewId}/feedback")
    public ResponseEntity<List<QuestionFeedbackDto>> getFeedbackByInterviewAndUser(@PathVariable Long interviewId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.valueOf(authentication.getName());

        List<QuestionFeedbackDto> feedbackList = interviewService.getFeedback(interviewId, userId);
        return ResponseEntity.ok(feedbackList);
    }
    //화상채팅들어갔을떄 초기입력
    @PostMapping("/init")
    public ResponseEntity<VideoChatInterViewDTO.Response> initInterview(Authentication authentication,
                                                                        InterviewDTO.Request request){
        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(interviewService.initInterview(userId,request));
    }
    //인터뷰id기준 질문들 전부가져오기
    @GetMapping("{interviewId}/questions/")
    public ResponseEntity<List<InterviewQuestionDTO.Response>> getInterviewQuestions(
            @PathVariable Long interviewId){
        return ResponseEntity.ok(interviewService.getInterviewQuestions(interviewId));
    }
    //인터뷰id기준 질문 하나 post
    @PostMapping("{interviewId}/question")
    public ResponseEntity<Long> saveQuestion(   @PathVariable Long interviewId,
                                                @RequestBody InterviewQuestionDTO.Request request){
        return ResponseEntity.ok(interviewService.saveQuestion(interviewId,request));
    }
    //질문id기준 피드백 post
    @PostMapping("/questions/{questionId}/feedBack")
    public ResponseEntity<Void> saveFeedBack(@PathVariable Long questionId,
                                          @RequestBody FeedBackDTO.Request request){
        interviewService.saveFeedBack(questionId,request);
        return ResponseEntity.ok().build();
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
