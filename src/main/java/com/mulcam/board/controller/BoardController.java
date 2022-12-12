package com.mulcam.board.controller;

import java.io.File;
import java.util.List;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.mulcam.board.dto.Board;
import com.mulcam.board.dto.PageInfo;
import com.mulcam.board.service.BoardService;

@Controller
@RequestMapping("/board")
public class BoardController {

	@Autowired
	private BoardService boardService;
	
	@Autowired
	private ServletContext servletContext;
	
	/* 게시글 작성 화면 이동 */
	@GetMapping("writeform")
	public String writeform() {
		return "/board/writeform";
	}

	/* 게시글 작성 기능 */
	@PostMapping("boardwrite")
	public ModelAndView boardwrite(@ModelAttribute Board board) {
		ModelAndView mav = new ModelAndView();
		try {
			// 파일이 있는 경우, 업로드 진행
			if(!board.getFile().isEmpty()) {
				String path = servletContext.getRealPath("/boardupload/");
				File destFile = new File(path+board.getFile().getOriginalFilename());
				board.setBoard_filename(board.getFile().getOriginalFilename());
				board.getFile().transferTo(destFile);
			}
			boardService.regBoard(board);
			// 게시글 작성 성공 시, 게시판 목록 화면으로 이동
			mav.setViewName("redirect:/board/boardlist");
		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject("err", "새글 등록 실패");
			mav.setViewName("/board/err");
		}
		return mav;
	}
	
	/* 게시판 목록 화면 이동 */
	@RequestMapping(value="/boardlist", method= {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView boardlist(@RequestParam(value="page", required=false, defaultValue = "1") int page) {
		ModelAndView mav = new ModelAndView();
		PageInfo pageInfo = new PageInfo();
		try {
			List<Board> articleList = boardService.getBoardList(page, pageInfo);
			mav.addObject("pageInfo", pageInfo);
			mav.addObject("articleList", articleList);
			mav.setViewName("/board/listform");
		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject("err", e.getMessage());
			mav.setViewName("/board/err");
		}
		return mav;
	}
	
	/* 게시글 상세보기 */
	@GetMapping("boarddetail")
	public ModelAndView boarddetail(@RequestParam(value="board_num") int boardNum, @RequestParam(required=false, defaultValue = "1") int page) {
		ModelAndView mav = new ModelAndView();
		try {
			Board board = boardService.getBoard(boardNum);
			mav.addObject("article", board);
			mav.addObject("page", page);
			mav.setViewName("/board/viewform");
		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject("err", e.getMessage());
			mav.setViewName("/board/err");
		}
		return mav;
	}
	
	@GetMapping("replyform")
	public ModelAndView replyform(@RequestParam(value="board_num") int boardNum, @RequestParam int page) {
		ModelAndView mav = new ModelAndView();
		mav.addObject("page", page);
		mav.addObject("boardNum", boardNum);
		mav.setViewName("/board/replyform");
		return mav;
	}
	
	@PostMapping("boardreply")
	public ModelAndView boardreply(@ModelAttribute Board board, @RequestParam int page) {
		ModelAndView mav = new ModelAndView();
		try {
			boardService.regReply(board);
			mav.addObject("page",page);
			mav.setViewName("redirect:/board/boardlist");
		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject("err", e.getMessage());
			mav.setViewName("/board/err");
		}
		return mav;
	}
	
	@GetMapping("modifyform")
	public ModelAndView modifyform(@RequestParam(value="board_num") int boardNum) {
		ModelAndView mav = new ModelAndView();
		Board board;
		try {
			board = boardService.getBoard(boardNum);
			mav.addObject("article", board);
			mav.setViewName("/board/modifyform");
		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject("err", e.getMessage());
			mav.setViewName("/board/err");
		}
		return mav;
	}
	@PostMapping("boardmodify")
	public ModelAndView boardmodify(@ModelAttribute Board board) {
		ModelAndView mav = new ModelAndView();
		try {
			boardService.modifyBoard(board);
			mav.addObject("board_num", board.getBoard_num());
			mav.setViewName("redirect:/board/boarddetail");
		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject("err", e.getMessage());
			mav.setViewName("/board/err");
		}
		return mav;
	}
	
	@GetMapping("/deleteform")
	public ModelAndView deleteform(@RequestParam(value="board_num") int boardNum, @RequestParam(required=false, defaultValue = "1") int page) {
		ModelAndView mav = new ModelAndView();
		mav.addObject("board_num", boardNum);
		mav.addObject("page", page);
		mav.setViewName("/board/deleteform");
		return mav;
	}
	@PostMapping("/boarddelete")
	public ModelAndView deleteform(@RequestParam(value="board_num") int boardNum, @RequestParam(value="BOARD_PASS") String boardPass, @RequestParam(required=true, defaultValue = "1") int page) {
		ModelAndView mav = new ModelAndView();
		try {
			boardService.removeBoard(boardNum, boardPass);
			mav.addObject("page",page);
			mav.setViewName("redirect:/board/boardlist");
		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject("err", e.getMessage());
			mav.setViewName("/board/err");
		}
		return mav;
	}
}