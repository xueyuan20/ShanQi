package com.example.chensanqi;



/**
 * 落子点
 * @author 80070307
 *
 */
public class ChessPoint {

	private Position mCurrentPos;

	protected int stepId = -1;// 此步顺序
	/**
	 *  落子状态，
	 */
	protected int state = ChessState.INITIAL;

	/**
	 * 构造函数
	 * @param x
	 * @param y
	 */
	public ChessPoint(int x, int y){
		mCurrentPos = new Position(x, y);
	}

	/**
	 * 
	 * @param isWhiteChess, 落子状态，true表示白子，false表示黑子
	 * @param stepInd, 落子顺序
	 * @return true, 表示落子成功
	 * 			false, 表示落子失败
	 */
	public boolean lazi(boolean isWhiteChess, int stepInd){
		if (state != ChessState.INITIAL) {
			return false;				
		}

		if (isWhiteChess) {
			state = ChessState.LAZI_WHITE;
		} else {
			state = ChessState.LAZI_BLACK;
		}

		stepId = stepInd;
		return true;
	}

	/**
	 * 
	 * @param isWhiteChess
	 * @param stepInd
	 * @return
	 */
	public boolean yizi(boolean isWhiteChess, int stepInd){
		if (state == ChessState.CHIZI_BLACK
				|| state == ChessState.CHIZI_WHITE) {
			state = ChessState.INITIAL;
		}
		return lazi(isWhiteChess, stepInd);
	}
	/**
	 * 吃子
	 */
	public boolean chizi(){
		if (state*state!=1) {
			return false;
		}
		state*=3;
		return true;
	}

	public boolean prepareYizi(int chessState) {
		// TODO Auto-generated method stub
		if (state!=chessState) {
			return false;
		}
		state = 2*chessState;
		return true;
	}

	public int getChessState(){
		return state;
	}

	/**
	 * 判断是否棋盘位置与此点相同
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isCurrentPoint(int x, int y){
		return (mCurrentPos.pos_x == x) && (mCurrentPos.pos_y == y);
	}

	/**
	 *  判断是否棋盘位置与此点相同
	 * @param step
	 * @return
	 */
	public boolean isCurrentPoint(ChessPoint step){
		return (mCurrentPos.pos_x == step.getX())
				&& (mCurrentPos.pos_y == step.getY());
	}
	/**
	 * 获取对应的X屏幕坐标
	 * @return
	 */
	public float getPosX(){
		return mCurrentPos.pos_x*ChessBoard.GRID_SIZE + ChessBoard.BORDER_W;
	}

	/**
	 * 获取对应的Y屏幕坐标
	 * @return
	 */
	public float getPosY(){
		return mCurrentPos.pos_y*ChessBoard.GRID_SIZE + ChessBoard.BORDER_H;
	}

	/**
	 * 获取对应的棋盘坐标
	 * @return
	 */
	public int getX(){
		return mCurrentPos.pos_x;
	}

	/**
	 * 获取对应的棋盘坐标
	 * @return
	 */
	public int getY(){
		return mCurrentPos.pos_y;
	}

}
