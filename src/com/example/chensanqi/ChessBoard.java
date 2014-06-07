package com.example.chensanqi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

/**
 * ����������
 * @author 80070307
 *
 */
public class ChessBoard extends SurfaceView implements Callback, Runnable {
	private static final String TAG = "ChessBoard";

	/**
	 * �������̵��������
	 */
	public final int LINE_WIDTH = 6;

	/**
	 * �������Ҳ�߱߾�
	 */
	public static final int BORDER_W = 40;

	/**
	 * ���̶����߾�
	 */
	public static final int BORDER_H = 240;

	/**
	 * ���̹��
	 */
	public static final int BOARD_SIZE = 6;

	/**
	 * ��С��Ԫ��ߴ�
	 */
	public static int GRID_SIZE = 10;

	/**
	 * SurfaceHolder
	 */
	private SurfaceHolder mSfh;

	/**
	 * ����
	 */
	private Paint mPaint;

	/**
	 * ���´���
	 */
	private Thread mThread;

	/**
	 * ��ʶ
	 */
	private boolean mUpdateFlag;

	/**
	 * ����
	 */
	private Canvas mCanvas;

	/**
	 * ��Ļ�ߴ�
	 */
	private int mScreenW, mScreenH;

	/**
	 * ����������
	 */
	private final int CHESS_COUNT = 9;
	private int mChessStepCount = 0;
	private int mRemainWCCount, mRemainBCCount;
	private int mLooseWCCount, mLooseBCCount;

	/**
	 * ���ӱ�ʶ
	 * 	0	��ʾ����
	 * 	1	��ʾ�Ե�����
	 * 	-1	��ʾ�Ե�����
	 * 	2	��ʾ�ƶ�����
	 * 	-2	��ʾ�ƶ�����
	 */
	private int mLaziFlag = 0;

	/**
	 * ��Դ���
	 */
	private Resources mRes;
	private Bitmap mBkBmp;
	private Bitmap mWhiteChess, mDarkChess, mSelectChess;

	private Context mContext;

	/**
	 * ��������
	 * @author 80070307
	 *
	 */
	private static enum OPERATOR_RESULT{
		SUCCESSFUL,	// �����ɹ�
		WARN_NOT_SPACE,// ���ǿ�λ
		WARN_HILL_CHESS, // ����������
		WARN_OWN_CHESS,	// �Լ�������
		WARN_NOT_ADJPOS,// �������ڵ�λ��
		WARN_ILLEGAL_POSITION,	// ������ȷ�����ӵ�
		WARN_CANNOT_MOVE,	// ���Ӳ����ƶ�
	}

	/**
	 * �������ӵ�λ��
	 */
	private final int BOARD_CIRCLES = 3;
	private final int BOARD_DIMENS = 8;
	ChessPoint[][] mBoardMap = new ChessPoint[BOARD_CIRCLES][BOARD_DIMENS];

	private void initBoardMap(){
		int x,y,div,diff;
		for (int i = 0; i < BOARD_CIRCLES; i++) {
			x= y = i;
			div = 3-i;
			for (int j = 0; j < BOARD_DIMENS; j++) {
				diff = (j*div<=4*div)? j*div : (4*div-j*div);
				if (Math.abs(diff)>0) {					
					if (Math.abs(diff) > 2*div) {
						x = x+(diff*div)/Math.abs(diff);
					} else{
						y = y + (diff*div)/Math.abs(diff);
					}
				}
				mBoardMap[i][j] = new ChessPoint(x, y);
				mBoardMap[i][j].state = ChessState.INITIAL;
				Log.d(TAG, "pos: ("+x+","+y+")");
			}
		}
		mChessStepCount = 0;
		mRemainWCCount = mRemainBCCount = CHESS_COUNT;
		mLooseWCCount = mLooseBCCount = 0;
		mLaziFlag = 0;
	}

	public void restartChess(){
		initBoardMap();
	}

	/**
	 * �Ƿ�Ϊ������Ϸ
	 */
	private boolean mAloneGame = false;

	public void setGameType(boolean isAloneGame){
		mAloneGame = isAloneGame;
	}

	public boolean isAloneGame(){
		return mAloneGame;
	}

	/**
	 * ���캯������ʼ������
	 * @param context
	 */
	public ChessBoard(Context context) {
		// TODO Auto-generated constructor stub
		super(context);
		mContext = context;
		init();
	}

	public ChessBoard(Context context, AttributeSet attrs){
		super(context,attrs);
		mContext = context;
		init();
	}


	private void init() {
		// TODO Auto-generated method stub
		Log.d(TAG, "instructor");
		mSfh = this.getHolder();
		mSfh.addCallback(this);
		mPaint = new Paint();
		mPaint.setColor(Color.BLACK);
		mPaint.setAntiAlias(true);
		setFocusable(true);
		mRes = getResources();

		initBoardMap();
	}
	/**
	 * ����SurfaceView
	 */
	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		mScreenH = this.getHeight();
		mScreenW = this.getWidth();
		mUpdateFlag = true;
		Log.d(TAG, "surfaceCreated: Height = " + mScreenH
				+ "; Width = " + mScreenW);
		mBkBmp = BitmapFactory.decodeResource(mRes, R.drawable.bg);
		mWhiteChess = BitmapFactory.decodeResource(mRes, R.drawable.white);
		mDarkChess = BitmapFactory.decodeResource(mRes, R.drawable.black);
		mSelectChess = BitmapFactory.decodeResource(mRes, R.drawable.chess_selector48);
		mThread = new Thread(this);
		mThread.start();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

		Log.d(TAG, "surfaceChanged");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		Log.d(TAG, "surfaceDestroyed");
		mUpdateFlag = false;
		if (mBkBmp!=null) {
			mBkBmp.recycle();
			mBkBmp = null;
		}
		if (mWhiteChess!=null) {
			mWhiteChess.recycle();
			mWhiteChess = null;
		}
		if (mDarkChess!=null) {
			mDarkChess.recycle();
			mDarkChess = null;
		}
		if (mSelectChess != null) {
			mSelectChess.recycle();
			mSelectChess = null;
		}
	}

	/**
	 * �����߳�
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub

		while (mUpdateFlag) {
			myDraw();
			long start = System.currentTimeMillis();
			logic();
			long end = System.currentTimeMillis();
			try {
				if (end-start < 50) {
					Thread.sleep(50- (end - start));
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			
		}
	}

	public void logic() {
		// TODO Auto-generated method stub
	}

	/**
	 * �������̺�����
	 */
	public void myDraw(){
		try {
			mCanvas = mSfh.lockCanvas();
			if (mCanvas != null) {
				/**
				 * ��ʼ������
				 */
				mCanvas.drawColor(Color.WHITE);
				mPaint.setStyle(Style.STROKE);
				int tileW = (mScreenW-BORDER_W*2)/BOARD_SIZE;
				int tileH = (mScreenH-BORDER_H*2)/BOARD_SIZE;
				GRID_SIZE =  (tileH < tileW)? tileH :tileW;

				/**
				 * ���Ʊ���ͼ
				 */
				mCanvas.drawBitmap(mBkBmp, 0, 0, mPaint);

				/**
				 * ��������
				 */
				mPaint.setColor(Color.DKGRAY);
				mPaint.setAlpha(0xCC);
				mPaint.setStyle(Style.STROKE);
				mPaint.setStrokeWidth(LINE_WIDTH);
				int i,j;
				for (i=j=0; i+j<BOARD_SIZE; i++, j++) {
					mCanvas.drawRect(i*GRID_SIZE + BORDER_W, j*GRID_SIZE + BORDER_H,
							(BOARD_SIZE-i)*GRID_SIZE + BORDER_W, (BOARD_SIZE-j)*GRID_SIZE + BORDER_H, mPaint);
				}
				j=BOARD_SIZE/2;
				for (i = 0; i < BOARD_SIZE/2-1; i++) {
					mCanvas.drawLine(i*GRID_SIZE + BORDER_W, j*GRID_SIZE + BORDER_H,
							(i+1)*GRID_SIZE + BORDER_W, j*GRID_SIZE + BORDER_H, mPaint);
				}
				for (i = BOARD_SIZE; i > BOARD_SIZE/2+1; i--) {
					mCanvas.drawLine(i*GRID_SIZE + BORDER_W, j*GRID_SIZE + BORDER_H,
							(i-1)*GRID_SIZE + BORDER_W, j*GRID_SIZE + BORDER_H, mPaint);					
				}

				i=BOARD_SIZE/2;
				for (j = 0; j < BOARD_SIZE/2-1; j++) {
					mCanvas.drawLine(i*GRID_SIZE + BORDER_W, j*GRID_SIZE + BORDER_H,
							i*GRID_SIZE + BORDER_W, (j+1)*GRID_SIZE + BORDER_H, mPaint);
				}
				for (j = BOARD_SIZE; j > BOARD_SIZE/2+1; j--) {
					mCanvas.drawLine(i*GRID_SIZE + BORDER_W, j*GRID_SIZE + BORDER_H,
							i*GRID_SIZE + BORDER_W, (j-1)*GRID_SIZE + BORDER_H, mPaint);					
				}

				/**
				 * ��������
				 */
				mPaint.setAlpha(0xFF);
				ChessPoint step;
				for (i = 0; i < BOARD_CIRCLES; i++) {
					for(j = 0; j < BOARD_DIMENS; j++){
						step = mBoardMap[i][j];
						switch (step.state) {
						case 0:
							mPaint.setColor(Color.DKGRAY);
							mCanvas.drawCircle(step.getPosX(), step.getPosY(), 3, mPaint);
							break;
						case -1:
							mCanvas.drawBitmap(mDarkChess, step.getPosX()-mDarkChess.getWidth()/2,
									step.getPosY()-mDarkChess.getHeight()/2, mPaint);
							break;

						case 1:
							mCanvas.drawBitmap(mWhiteChess, step.getPosX()-mWhiteChess.getWidth()/2,
									step.getPosY()-mWhiteChess.getHeight()/2, mPaint);
							break;
							
						case -2:
							mCanvas.drawBitmap(mDarkChess, step.getPosX()-mDarkChess.getWidth()/2,
									step.getPosY()-mDarkChess.getHeight()/2, mPaint);
							mCanvas.drawBitmap(mSelectChess, step.getPosX()-mSelectChess.getWidth()/2,
									step.getPosY()-mSelectChess.getHeight()/2, mPaint);
							break;

						case 2:
							mCanvas.drawBitmap(mWhiteChess, step.getPosX()-mWhiteChess.getWidth()/2,
									step.getPosY()-mWhiteChess.getHeight()/2, mPaint);
							mCanvas.drawBitmap(mSelectChess, step.getPosX()-mSelectChess.getWidth()/2,
									step.getPosY()-mSelectChess.getHeight()/2, mPaint);
							break;

						case -3:
						case 3:
							mPaint.setColor(Color.RED);
							mCanvas.drawCircle(step.getPosX(), step.getPosY(), 3, mPaint);
							break;
						default:
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (mCanvas != null) {
				mSfh.unlockCanvasAndPost(mCanvas);
			}
		}
	}

	/**
	 * ����¼�
	 */
	private Position mDownPos;
	private MapPos mToMovePos = null;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		int pos_x, pos_y;
		pos_x = Math.round((event.getX()-BORDER_W)/GRID_SIZE);
		pos_y = Math.round((event.getY()-BORDER_H)/GRID_SIZE);
		Log.i(TAG, "click pos: (" + pos_x+", "+pos_y+")");
		/**
		 * ��¼����켣
		 */
		if (checkLocation(pos_x, pos_y)){
			/**
			 * ��¼���µ�λ��
			 */
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				mDownPos = new Position(pos_x, pos_y);
			}
			/**
			 * �ɿ���Ļ�������¼�
			 */
			if ((event.getAction() == MotionEvent.ACTION_UP) && mDownPos!=null){
				/**
				 * ��Ϊ��Ч����¼�
				 */
				if ((pos_x == mDownPos.pos_x) && (pos_y == mDownPos.pos_y)) {
					/**
					 * ����
					 */
					switch (Math.abs(mLaziFlag)) {
					case 0:
						boolean isWhiteChessRound = (mChessStepCount%2 == 0)? true :false;
						if (mToMovePos != null){
							if(yizi(mToMovePos, mDownPos, mBoardMap[mToMovePos.pos_i][mToMovePos.pos_j].getChessState())){
								/**
								 * �ж��Ƿ����
								 */
								Log.d(TAG, "yizi");
								mLaziFlag = checkHill(pos_x, pos_y);
								mToMovePos = null;
							} else {
								break;
							}
						} else {
							if(setChessState(pos_x, pos_y,isWhiteChessRound)){
								/**
								 * �ж��Ƿ����
								 */
								mLaziFlag = checkHill(pos_x, pos_y);
							} else {
								/**
								 * ���ӵ㲻�Ϸ�
								 */
								break;
							}
						}
						resetLaziFlag(isWhiteChessRound);
						if (isWin(isWhiteChessRound)) {
							showResult(isWhiteChessRound?mRes.getString(R.string.result_win_white)
									:mRes.getString(R.string.result_win_black));
						}
						break;

					case 1:
						if (mLaziFlag > 0) {
							/**
							 * ���ӳԵ�����
							 */
							if(chizi(pos_x, pos_y, -1)){
								mLaziFlag = 0;
								mLooseBCCount++;
							}
							if (isWin(true)) {
								showResult(mRes.getString(R.string.result_win_white));
							}
							resetLaziFlag(true);
						} else {
							/**
							 * ���ӳԵ�����
							 */
							if(chizi(pos_x, pos_y, 1)){
								mLaziFlag = 0;
								mLooseWCCount++;
							}
							if (isWin(false)) {
								showResult(mRes.getString(R.string.result_win_black));
							}
							resetLaziFlag(false);
						}
						break;
					case 2:
						Log.i(TAG, "Lazi Flag = "+mLaziFlag);
						if (mLaziFlag > 0) {
							/**
							 * �Ƶ�����
							 */
							mToMovePos = preparedYizi(pos_x, pos_y, 1);
							if (mToMovePos!=null) {
								Log.i(TAG, "white pos("+mToMovePos.pos_i+","+mToMovePos.pos_j+")");
								mLaziFlag = 0;
							}

						} else {
							/**
							 * �Ƶ�����
							 */
							mToMovePos = preparedYizi(pos_x, pos_y, -1);
							if (mToMovePos!=null) {
								mLaziFlag = 0;
								Log.i(TAG, "black pos("+mToMovePos.pos_i+","+mToMovePos.pos_j+")");
							}
						}
						break;
					default:
						break;
					}
				}
			}
		}

		return true;
	}

	private void resetLaziFlag(boolean isWhiteChessRound){
		if (mLaziFlag == 0 && mRemainBCCount<=0 && mRemainWCCount<=0) {
			mLaziFlag = isWhiteChessRound ? -2 : 2;
		}
	}

	/**
	 * �ж��Ƿ�ֳ�ʤ��
	 * @param isWCRound
	 * @return
	 */
	private boolean isWin(boolean isWCRound){
		// ��������δ��
		if ((isWCRound&&(mRemainBCCount>0))
				|| (!isWCRound&&(mRemainWCCount>0))) {
			return false;
		}

		// ����������
		/**
		 * �ж϶���ʣ�������Ƿ񲻶�������
		 */
		if ((isWCRound&&((CHESS_COUNT-mLooseBCCount)<3))
				|| (!isWCRound&&((CHESS_COUNT-mLooseWCCount)<3))) {
			return true;
		}

		/**
		 * �ж϶��������Ƿ��Ѿ�����������
		 * ���һ������Ϊ3���������ƶ�һ���λ������;
		 */
		if ((isWCRound&&((CHESS_COUNT-mLooseBCCount)==3))
				|| (!isWCRound&&((CHESS_COUNT-mLooseWCCount)==3))) {
			return false;
		}
		int state = isWCRound ? -1 : 1;
		int x,y;
		for (int i = 0; i < BOARD_CIRCLES; i++) {
			for (int j = 0; j < BOARD_DIMENS; j++) {
				if (state == mBoardMap[i][j].getChessState()) {
					x = i;
					// pos[i, j-1]
					y = j>1?(j-1):(BOARD_DIMENS-1);
					if (mBoardMap[x][y].getChessState()%3 == 0) {
						return false;
					}
					// pos[i, j+1]
					y = (j+1)%BOARD_DIMENS;
					if (mBoardMap[x][y].getChessState()%3 == 0) {
						return false;
					}
					// pos[i-1,j]
					if (j%2>0 && i>0 && mBoardMap[i-1][j].getChessState()%3==0) {
						return false;
					}
					// pos[i+1,j]
					if (j%2>0 && i<BOARD_CIRCLES-1
							&& mBoardMap[i+1][j].getChessState()%3==0) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * ��ȡ������ʾ
	 * @return
	 */
	public String getChessTip(){
		String tmp;
		if (mLaziFlag == 0) {
			tmp = String.format(
					getResources().getString(R.string.chess_tip), mChessStepCount,
					(mChessStepCount%2 == 0) ? "����" : "����", "����");
		} else if (Math.abs(mLaziFlag)==1){
			tmp = String.format(
					getResources().getString(R.string.chess_tip), mChessStepCount,
					(mChessStepCount%2 == 0) ? "����" : "����", "����");
		} else {
			tmp = String.format(
					getResources().getString(R.string.chess_tip), mChessStepCount,
					(mChessStepCount%2 == 0) ? "����" : "����", "����");
		}
		return tmp;
	}

	/**
	 * �жϵ�������Ƿ���Ч
	 * @param pos_x
	 * @param pos_y
	 * @return
	 */
	private boolean checkLocation(int pos_x, int pos_y){
		boolean flag = false;

		/**
		 * �ж��Ƿ�����Ч����
		 */
		if ((pos_x!=BOARD_SIZE/2 && pos_y!=BOARD_SIZE/2)
				&& ((pos_x == pos_y)||(pos_x + pos_y == BOARD_SIZE))) {
			flag = true;
		} else if ((pos_x!=BOARD_SIZE/2 && pos_y == BOARD_SIZE/2)
				||(pos_x==BOARD_SIZE/2 && pos_y != BOARD_SIZE/2)) {
			flag =true;
		} else {
			flag = false;
		}

		return flag;
	}

	/**
	 * ��Ӧ��ҵĲ���
	 * @param pos_x
	 * @param pos_y
	 */
	private boolean setChessState(int pos_x, int pos_y, boolean isWCRound){

		// ������
		if (mAloneGame) {

			/**
			 * ���ʤ��
			 */

			/**
			 * �����Զ�����
			 */
		} else {

			// ˫�˰�
			for (int i = 0; i < BOARD_CIRCLES; i++) {
				for (int j = 0; j < BOARD_DIMENS; j++) {
					if (mBoardMap[i][j].isCurrentPoint(pos_x, pos_y)){

						if (mBoardMap[i][j].lazi(isWCRound, mChessStepCount)) {							
							mChessStepCount++;
							if (isWCRound) {
								mRemainWCCount--;
							} else {
								mRemainBCCount--;
							}
							return true;
						} else {
							showWarn(OPERATOR_RESULT.WARN_NOT_SPACE);
							return false;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * ����
	 * @param pos_x
	 * @param pos_y
	 * @param chessState
	 * @return
	 */
	private boolean chizi(int pos_x, int pos_y, int chessState){
		// ˫�˰�
		ChessPoint step;
		int pos_i = -1;
		int pos_j = -1;
		int freeChessCount = 0;
		for (int i = 0; i < BOARD_CIRCLES; i++) {
			for (int j = 0; j < BOARD_DIMENS; j++) {
				step = mBoardMap[i][j];
				if (step.getChessState()%3*chessState>0){
					Log.e(TAG, "check free chess");
					if(checkHill(step.getX(), step.getY())==0) {
						freeChessCount++;
					}
					if (step.isCurrentPoint(pos_x, pos_y)) {						
						pos_i = i;
						pos_j = j;
					}
				}
			}
		}
		/**
		 * �ж��Ƿ���ϳ�������
		 */
		if (pos_i>=0&&pos_j>=0) {
			step = mBoardMap[pos_i][pos_j];
			if (freeChessCount>0){
				if (checkHill(step.getX(), step.getY())==0) {
					return step.chizi();
				}
				showWarn(OPERATOR_RESULT.WARN_HILL_CHESS);
				return false;
			}
			return step.chizi();
		} else {
			showWarn(OPERATOR_RESULT.WARN_OWN_CHESS);
		}
		return false;
	}

	/**
	 * Ϊ������׼��
	 * @param pos_x
	 * @param pos_y
	 * @return
	 */
	private MapPos preparedYizi(int pos_x, int pos_y, int chessState){
		ChessPoint step;
		for (int i = 0; i < BOARD_CIRCLES; i++) {
			for (int j = 0; j < BOARD_DIMENS; j++) {
				step = mBoardMap[i][j];
				if (step.isCurrentPoint(pos_x, pos_y)){
					/**
					 * �ж��Ƿ������������
					 */
					if (step.prepareYizi(chessState)) {
						return new MapPos(i, j);
					}
					return null;
				}
			}
		}
		return null;
	}

	/**
	 * �Ƶ�����
	 * @param pos_x
	 * @param pos_y
	 * @param i
	 * @return
	 */
	private boolean yizi(MapPos src_pos, Position dst, int chessState) {
		// TODO Auto-generated method stub
		int div_i,div_j;
		boolean isWCRound = (chessState>0) ? true : false;

		for (int i = 0; i < BOARD_CIRCLES; i++) {
			for (int j = 0; j < BOARD_DIMENS; j++) {
				if (mBoardMap[i][j].isCurrentPoint(dst.pos_x, dst.pos_y)){
					// ���ӵ�ǿգ�������ʧ��
					if (mBoardMap[i][j].getChessState()%3!=0) {
						showWarn(OPERATOR_RESULT.WARN_NOT_SPACE);
						return false;
					}

					// �ж�����λ���Ƿ���ȷ
					if ((isWCRound && (mLooseWCCount<6))
							||(!isWCRound && (mLooseBCCount<6))) {
						div_j = Math.abs(j-src_pos.pos_j);
						div_i = Math.abs(i-src_pos.pos_i);
						if ((src_pos.pos_j%2)*(j%2)==0){
							if(i!=src_pos.pos_i || (div_j!=1&&div_j!=7)){
								showWarn(OPERATOR_RESULT.WARN_NOT_ADJPOS);
								return false;
							}
						} else {
							if (div_i+div_j!=1) {
								showWarn(OPERATOR_RESULT.WARN_NOT_ADJPOS);
								return false;
							}
						}
					}
					// ����
					if (mBoardMap[i][j].yizi(isWCRound, mChessStepCount)) {
						mChessStepCount++;
						mBoardMap[src_pos.pos_i][src_pos.pos_j].state = 0;
						return true;
					}
					return false;
				}
			}
		}
		return false;
	}

	/**
	 * �жϵ�ǰ�Ƿ��hill
	 * @param pos_y 
	 * @param pos_x 
	 * @return
	 */
	private int checkHill(int pos_x, int pos_y){
		int state = 0;
		for (int i = 0; i < BOARD_CIRCLES; i++) {
			for (int j = 1; j < BOARD_DIMENS; j+=2) {
				if (mBoardMap[i][j].isCurrentPoint(pos_x, pos_y)
						|| mBoardMap[i][j-1].isCurrentPoint(pos_x, pos_y)
						|| mBoardMap[i][(j+1)%BOARD_DIMENS].isCurrentPoint(pos_x, pos_y)) {					
					state = mBoardMap[i][j].getChessState()%3;
					if ((state*state>0) && ((mBoardMap[i][j-1].getChessState()%3)*state>0)
							&& ((mBoardMap[i][(j+1)%BOARD_DIMENS].getChessState()%3)*state>0)) {
						return state;
					}
				}
			}
		}
		for (int j = 1; j < BOARD_DIMENS; j+=2) {
			for (int i = 1; i+1 < BOARD_CIRCLES; i++) {
				if (mBoardMap[i][j].isCurrentPoint(pos_x, pos_y)
						|| mBoardMap[i-1][j].isCurrentPoint(pos_x, pos_y)
						|| mBoardMap[i+1][j].isCurrentPoint(pos_x, pos_y)) {					
					state = mBoardMap[i][j].getChessState()%3;
					if ((state*state>0) && ((mBoardMap[i-1][j].getChessState()%3)*state>0)
							&& ((mBoardMap[i+1][j].getChessState()%3)*state>0)) {
						return state;
					}
				}
			}
		}
		return 0;
	}

	/**
	 * ��ʾ�������
	 * @param player
	 */
	private void showResult(String player){
		new AlertDialog.Builder(mContext)
        .setTitle("��������")
        .setMessage(player)
        .setPositiveButton("ȷ��", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dlg, int which) {
            	initBoardMap();
            }
        }).setNegativeButton("ȡ��", null)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .show();
	}

	/**
	 * �쳣����ʱ��ʾ��ʾ��
	 * @param warnType���������ͣ�
	 */
	private void showWarn(OPERATOR_RESULT warnType){
		String msg = "";
		switch (warnType) {
		case WARN_HILL_CHESS:
			msg = mRes.getString(R.string.warn_hill_chess);
			break;

		case WARN_OWN_CHESS:
			msg = mRes.getString(R.string.warn_own_chess);
			break;

		case WARN_NOT_SPACE:
			msg = mRes.getString(R.string.warn_not_space);
			break;

		case WARN_NOT_ADJPOS:
			msg = mRes.getString(R.string.warn_not_adjpos);
			break;

		case WARN_ILLEGAL_POSITION:
			msg = mRes.getString(R.string.warn_illegal_position);
			break;

		case WARN_CANNOT_MOVE:
			msg = mRes.getString(R.string.warn_cannot_move);
			break;

		default:
			break;
		}
		new AlertDialog.Builder(mContext)
        .setTitle("����")
        .setMessage(msg)
        .setPositiveButton("ȷ��", null)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .show();
	}

	public int getPlayerBlackChessCount() {
		// TODO Auto-generated method stub
		return mRemainBCCount;
	}

	public int getPlayerBlackEatCount() {
		// TODO Auto-generated method stub
		return mLooseWCCount;
	}

	public int getPlayerWhiteChessCount() {
		// TODO Auto-generated method stub
		return mRemainWCCount;
	}

	public int getPlayerWhiteEatCount() {
		// TODO Auto-generated method stub
		return mLooseBCCount;
	}

	/**
	 * ����ѡ������û���ѡ�����ӣ������ѡ��
	 * 			���δѡ����������
	 */
	public void reselect() {
		// TODO Auto-generated method stub
		if (mToMovePos != null) {
			mBoardMap[mToMovePos.getI()][mToMovePos.getJ()].state/=2;
			mToMovePos = null;
			mLaziFlag = 0;
			resetLaziFlag((mChessStepCount%2 == 0)? false : true);
		}
	}

	public boolean isSelectMoveChess(){
		if (mToMovePos == null) {
			return false;
		}
		return true;
	}
}