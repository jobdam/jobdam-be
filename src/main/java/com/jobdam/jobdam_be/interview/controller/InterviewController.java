package com.jobdam.jobdam_be.interview.controller;

import com.jobdam.jobdam_be.interview.dto.*;
import com.jobdam.jobdam_be.clova.service.ClovaAiService;
import com.jobdam.jobdam_be.interview.dto.QuestionFeedbackDto;
import com.jobdam.jobdam_be.interview.service.InterviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user/interviews")
public class InterviewController {

    private final InterviewService interviewService;
    private final ClovaAiService clovaAiService;

    @GetMapping("/paged")
    public ResponseEntity<List<InterviewDateGroupDTO.Response>> getInterviewsPaged(Authentication authentication,
                                                            @RequestParam(required = false) Long lastId,
                                                            @RequestParam(defaultValue = "5") int limit) {

        Long userId = Long.valueOf(authentication.getName());

        return ResponseEntity.ok(interviewService.getInterviewsPaged(userId,lastId,limit));
    }

    // GET user/interviews/5/feedback
    @GetMapping("/{interviewId}/feedback")
    public ResponseEntity<List<QuestionFeedbackDto>> getFeedbackByInterviewAndUser(@PathVariable Long interviewId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.valueOf(authentication.getName());

        List<QuestionFeedbackDto> feedbackList = interviewService.getFeedbackHistory(interviewId, userId);
        return ResponseEntity.ok(feedbackList);
    }
    //화상채팅 진입전 인터뷰db 초기화()
    @PostMapping("/init")
    public ResponseEntity<Void> initInterview(Authentication authentication,
                                              @RequestBody InterviewDTO.Request request){
        Long userId = Long.valueOf(authentication.getName());
        interviewService.initInterview(userId,request);
        return ResponseEntity.ok().build();
    }
    //userId기준 질문들 전부+이력서 url 가져오기(다른사람ID+가장최신꺼)
    @GetMapping("/data/{userId}")
    public ResponseEntity<InterviewFullDataDTO.Response> getInterviewFullData(
            @PathVariable Long userId){
        return ResponseEntity.ok(interviewService.getInterviewFullData(userId));
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


    /* HACK : 해당 코드는 임시로 보여주기 위한 코드로
              인터뷰가 종료 되었을 때 실행되기를 희망
    */
    @GetMapping("/test")
    public ResponseEntity<String> testGenerateFeedbackReportTEST() throws Exception {
        // 다른 유저들에게 받은 피드백을 String으로 변환하여 받음
        String feedbacks = interviewService.getFeedbacksForSameInterview(4L);

        if(feedbacks == null)
            return ResponseEntity.ok("피드백 리포트 생성 없음");

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
