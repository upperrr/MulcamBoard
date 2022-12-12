package com.mulcam.board.service;

import java.util.List;

import com.mulcam.board.dto.Board;
import com.mulcam.board.dto.PageInfo;

public interface BoardService {
	void regBoard(Board board) throws Exception;

	List<Board> getBoardList(int page, PageInfo pageInfo) throws Exception;

	Board getBoard(int boardNum) throws Exception;
	
	void regReply(Board board) throws Exception;

	void modifyBoard(Board board) throws Exception;

	void removeBoard(int boardNum, String boardPass) throws Exception;
}
