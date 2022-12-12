package com.mulcam.board.service;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mulcam.board.dao.BoardDAO;
import com.mulcam.board.dto.Board;
import com.mulcam.board.dto.PageInfo;

@Service
public class BoardServiceImpl implements BoardService {

	@Autowired
	BoardDAO boardDAO;
	
	@Override
	public void regBoard(Board board) throws Exception {
		Integer boardNum = boardDAO.selectBoardMaxNum();
		if (boardNum == null) boardNum = 1;
		else boardNum += 1; 
		
		/* 게시판 쓰기에서 입력하지 않는 값들 초기화 */
		
		// 게시판 번호 (게시글 중에서 가장 높은 게시판 번호+1) 
		board.setBoard_num(boardNum);
		// 댓글의 게시글 참조 번호 (게시글 번호와 같게)
		board.setBoard_re_ref(boardNum);
		// 댓글 레벨, 댓글의 댓글 참조 번호, 댓글 번호, 조회수 모두 0으로 초기화 
		board.setBoard_re_lev(0);
		board.setBoard_re_seq(0);
		board.setBoard_date(new Date(System.currentTimeMillis()));
		board.setBoard_readcount(0);
		
		boardDAO.insertBoard(board);
	}

	@Override
	public List<Board> getBoardList(int page, PageInfo pageInfo) throws Exception {
		/* 전체 게시물 수 */
		int listCount = boardDAO.selectBoardcount(); 
		/* 최대 페이지 목록 개수 = 전체 게시물 수 / 10 (게시물 10개 씩 출력) */
		int maxPage = (int) Math.ceil((double)listCount/10);
		
		
		/* 현재 페이지에 보여줄 시작 페이지 수 (1, 11, 21, 31) */
		int startPage = ((   (int)(  (double) page/10 +0.9  )   ) -1)*10+1 ;
		/* 현재 페이지에 보여줄 마지막 페이지 수 (10, 20 ,30 ,40) */
		int endPage = startPage+10-1;
		
		if(endPage>maxPage) endPage = maxPage;
		
		pageInfo.setStartPage(startPage);
		pageInfo.setEndPage(endPage);
		pageInfo.setMaxPage(maxPage);
		pageInfo.setPage(page);
		pageInfo.setListCount(listCount);
		int startrow = (page-1)*10 + 1;
		return boardDAO.selectBoardList(startrow);
	}
	
	@Override
	public Board getBoard(int boardNum) throws Exception {
		boardDAO.updateReadCount(boardNum);
		Board board = boardDAO.selectBoard(boardNum);
		return board;
	}

	@Override
	public void regReply(Board board) throws Exception {
		// 1. board_num으로 원글 조회
		Board src_board = boardDAO.selectBoard(board.getBoard_num());
		// 2. 글번호를 새로 생성해서 할당
		Integer boardNum = boardDAO.selectBoardMaxNum() + 1;
		board.setBoard_num(boardNum);
		// 3. 원글의 ref 번호를 할당
		board.setBoard_re_ref(src_board.getBoard_re_ref());
		// 4. 원글의 lev에 1을 더하여 lev 할당
		board.setBoard_re_lev(src_board.getBoard_re_lev()+1);
		// 5. 원글의 seq보다 큰 seq인 글들을 +1
		boardDAO.updateBoardReseq(src_board);
		// 6. 원글의 seq에 1을 더하여 seq 할당
		board.setBoard_re_seq(src_board.getBoard_re_seq()+1);
		
		boardDAO.insertBoard(board);
	}

	@Override
	public void modifyBoard(Board board) throws Exception {
		// 1. 입력된 비밀번호가 원본의 비밀번호와 같은지 체크
		String password = boardDAO.selectPassword(board.getBoard_num());
		// 2. 비밀번호가 같으면, 글 내용 수정
		if (password.equals(board.getBoard_pass())) {
			boardDAO.updateBoard(board);
		}
		// 3. 비밀번호가 다르면 권한없음 예외처리
		else {
			throw new Exception("수정 권한 없음");
		}
	}

	@Override
	public void removeBoard(int boardNum, String boardPass) throws Exception {
		// 1. 입력된 비밀번호가 원본의 비밀번호와 같은지 체크
		String password = boardDAO.selectPassword(boardNum);
		// 2. 비밀번호가 같으면, 글 삭제
		if (boardPass.equals(password)) {
			boardDAO.deleteBoard(boardNum);
		}
		// 3. 비밀번호가 다르면 권한없음 예외
		else {
			throw new Exception("삭제 권한 없음");
		}
	}
	
	
}
