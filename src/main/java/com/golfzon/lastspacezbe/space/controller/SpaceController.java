package com.golfzon.lastspacezbe.space.controller;

import com.golfzon.lastspacezbe.space.dto.SpaceRequestDto;
import com.golfzon.lastspacezbe.space.service.SpaceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@Slf4j
@Api(tags = "Space 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/space")
public class SpaceController {

    private final SpaceService spaceService;

    @ApiOperation(value = "사무공간 등록", notes = "백오피스페이지에서 사무공간 등록기능입니다.")
    @PostMapping(value = "/post")
    public ResponseEntity<String> space(SpaceRequestDto requestDto) {

        spaceService.spaceRegister(requestDto);

        return ResponseEntity.ok()
                .contentType(new MediaType("application", "json", StandardCharsets.UTF_8))
                .body("result : 사무공간 등록완료");
    }

    // 사무공간 수정
    @ApiOperation(value = "사무공간 수정", notes = "백오피스페이지에서 사무공간 수정기능입니다.")
    @PutMapping("/update")
    public ResponseEntity<String> spaceUpdate(
            @RequestParam Long spaceId, @RequestBody SpaceRequestDto requestDto) {

        spaceService.spaceUpdate(spaceId,requestDto);

        return ResponseEntity.ok()
                .contentType(new MediaType("application", "json", StandardCharsets.UTF_8))
                .body("result : 사무공간 수정완료");
    }

    //사무공간 삭제
    @ApiOperation(value = "사무공간 삭제", notes = "백오피스페이지에서 사무공간 삭제기능입니다.")
    @DeleteMapping("/delete")
    public ResponseEntity<String> spaceDelete(
            @RequestParam Long spaceId) {

        spaceService.spaceDelete(spaceId);

        return ResponseEntity.ok()
                .contentType(new MediaType("application", "json", StandardCharsets.UTF_8))
                .body("result : 사무공간 삭제완료");
    }
}
