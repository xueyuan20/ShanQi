package com.example.chensanqi;

public class ChessState {
	/**
	 * 位置初始标识
	 */
	public static final int INITIAL = 0;

	/**
	 * 落子的位置标识
	 */
	public static final int LAZI_WHITE = 1;
	public static final int LAZI_BLACK = -1;

	/**
	 * 将要移到的棋子位置标识
	 */
	public static final int YIZI_WHITE = 2;
	public static final int YIZI_BLACK = -2;

	/**
	 * 被吃子的位置标识，禁止落子到此处
	 */
	public static final int CHIZI_WHITE = 3;
	public static final int CHIZI_BLACK = -3;
}
