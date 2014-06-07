package com.example.chensanqi;



/**
 * ���ӵ�
 * @author 80070307
 *
 */
public class ChessPoint {

	private Position mCurrentPos;

	protected int stepId = -1;// �˲�˳��
	/**
	 *  ����״̬��
	 */
	protected int state = ChessState.INITIAL;

	/**
	 * ���캯��
	 * @param x
	 * @param y
	 */
	public ChessPoint(int x, int y){
		mCurrentPos = new Position(x, y);
	}

	/**
	 * 
	 * @param isWhiteChess, ����״̬��true��ʾ���ӣ�false��ʾ����
	 * @param stepInd, ����˳��
	 * @return true, ��ʾ���ӳɹ�
	 * 			false, ��ʾ����ʧ��
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
	 * ����
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
	 * �ж��Ƿ�����λ����˵���ͬ
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isCurrentPoint(int x, int y){
		return (mCurrentPos.pos_x == x) && (mCurrentPos.pos_y == y);
	}

	/**
	 *  �ж��Ƿ�����λ����˵���ͬ
	 * @param step
	 * @return
	 */
	public boolean isCurrentPoint(ChessPoint step){
		return (mCurrentPos.pos_x == step.getX())
				&& (mCurrentPos.pos_y == step.getY());
	}
	/**
	 * ��ȡ��Ӧ��X��Ļ����
	 * @return
	 */
	public float getPosX(){
		return mCurrentPos.pos_x*ChessBoard.GRID_SIZE + ChessBoard.BORDER_W;
	}

	/**
	 * ��ȡ��Ӧ��Y��Ļ����
	 * @return
	 */
	public float getPosY(){
		return mCurrentPos.pos_y*ChessBoard.GRID_SIZE + ChessBoard.BORDER_H;
	}

	/**
	 * ��ȡ��Ӧ����������
	 * @return
	 */
	public int getX(){
		return mCurrentPos.pos_x;
	}

	/**
	 * ��ȡ��Ӧ����������
	 * @return
	 */
	public int getY(){
		return mCurrentPos.pos_y;
	}

}
