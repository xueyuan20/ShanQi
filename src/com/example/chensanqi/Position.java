package com.example.chensanqi;
/**
 * ����������
 * @author 80070307
 *
 */
public class Position {
	int pos_x; // ����X����
	int pos_y; // ����Y����

	public Position(int x, int y){
		pos_x = x;
		pos_y = y;
	}

	public int getX(){
		return pos_x;
	}

	public int getY(){
		return pos_y;
	}
}
