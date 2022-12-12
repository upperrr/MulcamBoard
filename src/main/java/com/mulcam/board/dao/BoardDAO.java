package com.mulcam.board.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.mulcam.board.dto.Board;

@Mapper
@Repository
public interface BoardDAO {
	void insertBoard(Board board) throws Exception;
	
	Integer selectBoardMaxNum() throws Exception;
	
	List<Board> selectBoardList(int startrow) throws Exception;
	
	int selectBoardcount() throws Exception;

	void updateReadCount(int boardNum) throws Exception;

	Board selectBoard(int boardNum) throws Exception;

	void updateBoardReseq(Board board) throws Exception;
	
	String selectPassword(int boardNum) throws Exception;
	
	void updateBoard(Board board) throws Exception;

	void deleteBoard(int boardNum) throws Exception;
}