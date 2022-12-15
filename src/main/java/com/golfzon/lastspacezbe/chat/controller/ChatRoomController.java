package com.golfzon.lastspacezbe.chat.controller;

import com.golfzon.lastspacezbe.chat.dto.ChatRoomResponseDto;
import com.golfzon.lastspacezbe.chat.entity.ChatRoom;
import com.golfzon.lastspacezbe.chat.repository.ChatRoomRepository;
import com.golfzon.lastspacezbe.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomService chatRoomService;

    // 채팅 리스트 화면
//    @GetMapping("/room")
//    public String rooms(Model model) {
//        return "chat/room";
//    }

    // 모든 채팅방 목록 반환
    @GetMapping("/rooms")
    @ResponseBody
    public ResponseEntity<List<ChatRoomResponseDto>> rooms() {


        List<ChatRoomResponseDto> responseDtos = chatRoomService.findAllRooms();

        return ResponseEntity.ok()
                .body(responseDtos);
    }

//    @GetMapping("/information/{companyId}")
//    public ResponseEntity<CompanyInfoResponseDto> getCompanyInfo(
//            @PathVariable(name = "companyId") Long companyId
//    ) {
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        log.info("principal:{}",principal);
//        Member member = ((UserDetailsImpl)principal).getMember();
//
//        CompanyInfoResponseDto responseDto = companyService.getCompany(companyId);
//
//        return ResponseEntity.ok()
//                .contentType(new MediaType("application", "json", StandardCharsets.UTF_8))
//                .body(responseDto);
//    }
    // 채팅방 생성
    @PostMapping("/room")
    @ResponseBody
    public ResponseEntity<String> createRoom(@RequestParam String name) {

        chatRoomService.createChatRoom(name);

        return ResponseEntity.ok()
                .contentType(new MediaType("application", "json", StandardCharsets.UTF_8))
                .body("result : 방 생성 완료");

    }

//    // 채팅방 입장 화면
//    @GetMapping("/room/enter/{roomId}")
//    public String roomDetail(Model model, @PathVariable String roomId) {
//        model.addAttribute("roomId", roomId);
//        return "chat/roomdetail";
//    }
//
//    // 특정 채팅방 조회
//    @GetMapping("/room/{roomId}")
//    @ResponseBody
//    public ChatRoom roomInfo(@PathVariable String roomId) {
//        return chatRoomRepository.findRoomById(roomId);
//    }
}
