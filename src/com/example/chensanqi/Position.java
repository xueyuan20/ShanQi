package com.example.chensanqi;
/**
 * ÆåÅÌ×ø±êÀà
 * @author 80070307
 *
 */
public class Position {
	int pos_x; // ÆåÅÌX×ø±ê
	int pos_y; // ÆåÅÌY×ø±ê

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
