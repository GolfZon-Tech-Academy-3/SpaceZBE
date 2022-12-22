package com.golfzon.lastspacezbe.space.controller;

import com.golfzon.lastspacezbe.security.UserDetailsImpl;
import com.golfzon.lastspacezbe.space.dto.SpaceBackOfficeResponseDto;
import com.golfzon.lastspacezbe.space.dto.SpaceRequestDto;
import com.golfzon.lastspacezbe.space.entity.Space;
import com.golfzon.lastspacezbe.space.service.SpaceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Api(tags = "Space 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/space")
public class SpaceBackOfficeController {

    private final SpaceService spaceService;

    // 사무공간 목록 조회
    @ApiOperation(value = "사무공간 목록 조회", notes = "사무공간 목록 조회 기능입니다.")
    @GetMapping("/list/{companyId}")
    public ResponseEntity<List<SpaceBackOfficeResponseDto>> spaceUpdate(@PathVariable(name="companyId") Long companyId) {

        List<SpaceBackOfficeResponseDto> list = spaceService.spaceSelectAll(companyId);

        return ResponseEntity.ok()
                .body(list);
    }
}
