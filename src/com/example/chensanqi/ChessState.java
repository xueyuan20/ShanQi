package com.example.chensanqi;

public class ChessState {
	/**
	 * λ�ó�ʼ��ʶ
	 */
	public static final int INITIAL = 0;

	/**
	 * ���ӵ�λ�ñ�ʶ
	 */
	public static final int LAZI_WHITE = 1;
	public static final int LAZI_BLACK = -1;

	/**
	 * ��Ҫ�Ƶ�������λ�ñ�ʶ
	 */
	public static final int YIZI_WHITE = 2;
	public static final int YIZI_BLACK = -2;

	/**
	 * �����ӵ�λ�ñ�ʶ����ֹ���ӵ��˴�
	 */
	public static final int CHIZI_WHITE = 3;
	public static final int CHIZI_BLACK = -3;
}
