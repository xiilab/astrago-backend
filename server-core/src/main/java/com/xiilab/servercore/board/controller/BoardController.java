package com.xiilab.servercore.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "BoardController", description = "게시판 api")
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/core/board")
public class BoardController {

}
