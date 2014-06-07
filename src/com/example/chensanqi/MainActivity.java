package com.example.chensanqi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity  extends Activity
	implements OnClickListener, OnTouchListener {
	protected static final int UPDATE_VIEWS = 0;
	private static final String TAG = "XUEYUAN";
	private ChessBoard mChessBoard;
	private TextView mBlackResult, mWhiteResult, mChessTip;
	private TextView mAbout, mTitle;
	private Button mGameType, mReselect;
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case UPDATE_VIEWS:
				updateViews();
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_main);
		initViews();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	private void initViews(){
		mChessBoard = (ChessBoard)findViewById(R.id.chessBoard);
		mChessBoard.setOnTouchListener(this);
		mBlackResult = (TextView)findViewById(R.id.player_black_result);
		mWhiteResult = (TextView)findViewById(R.id.player_white_result);
		mChessTip = (TextView)findViewById(R.id.chess_tip);
		mAbout = (TextView)findViewById(R.id.aboutBt);
		mTitle = (TextView)findViewById(R.id.title_type);
		mGameType = (Button)findViewById(R.id.typeBt);
		mReselect = (Button)findViewById(R.id.reselectBt);

		mAbout.setOnClickListener(this);

		/**
		 * Initialization
		 */
		updateViews();
	}

	public void updateViews(){
		String tmp = String.format(getResources().getString(R.string.player_black_result),
				mChessBoard.getPlayerBlackChessCount(), mChessBoard.getPlayerBlackEatCount());
		mBlackResult.setText(tmp);
		tmp = String.format(getResources().getString(R.string.player_white_result), 
				mChessBoard.getPlayerWhiteChessCount(), mChessBoard.getPlayerWhiteEatCount());
		mWhiteResult.setText(tmp);
		
		mChessTip.setText(mChessBoard.getChessTip());
		Resources res = getResources();
		if (mChessBoard.isAloneGame()) {
			mTitle.setText(res.getString(R.string.sigle_player));
			mGameType.setText(res.getString(R.string.menu_double));
		} else {
			mTitle.setText(res.getString(R.string.double_player));
			mGameType.setText(res.getString(R.string.menu_sigle));
		}

		mReselect.setClickable(mChessBoard.isSelectMoveChess() ? true : false);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		Log.e(TAG, "FunActivity Touch event!");
		return super.onTouchEvent(event);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.aboutBt:
			startActivity(new Intent(MainActivity.this, AboutActivity.class));
			break;

		default:
			break;
		}
	}
	
	public void restartBt(View view){
		mChessBoard.restartChess();
		updateViews();
	}

	/**
	 * 设置游戏模式：单人游戏还是双人游戏
	 * 第一步只支持单人游戏
	 * @param view
	 */
    public void typeBt(View view){
//		mChessBoard.setGameType(!mChessBoard.isAloneGame());
//		updateViews();
    }

    /**
     * 悔棋
     * @param view
     */
    public void undoBt(View view){
    	
    }

    /**
     * 重新选择
     * @param view
     */
    public void reselectBt(View view){
    	mChessBoard.reselect();
    	updateViews();
    }

    /**
     * 查看游戏规则
     * @param view
     */
    public void ruleBt(View view){
		new AlertDialog.Builder(MainActivity.this)
        .setTitle("规则")
        .setMessage(getResources().getString(R.string.about))
        .setPositiveButton("确定", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dlg, int which) {
            }
        }).show();
    }
    
    public void shareBt(View view){
    	Intent intent=new Intent(Intent.ACTION_SEND); 
    	intent.setType("text/plain"); 
    	intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
    	intent.putExtra(Intent.EXTRA_TEXT, "九子棋");
    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
    	startActivity(Intent.createChooser(intent, getTitle()));
    }

    public Handler getUiHandler(){
    	return mHandler;
    }

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		// TODO Auto-generated method stub
		Log.d(TAG, "Chessboard Touched");
		mHandler.sendEmptyMessageDelayed(UPDATE_VIEWS, 50);
		return false;
	}
}
