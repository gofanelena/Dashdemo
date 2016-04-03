package com.example.dashdemo2;

public class downObject {
	private byte[] buff;
	private boolean[] isWrite;
	private int hasWritten;
	
	public downObject(byte[] buff,boolean[] isWrite,int hasWritten){
		this.buff=buff;
		this.isWrite=isWrite;
		this.hasWritten=hasWritten;
	}
	
	public byte[] getBuff(){
		return buff;
	}
	public boolean[] getIsWrite(){
		return isWrite;
	}
	public int getHasWritten(){
		return hasWritten;
	}
	public void setBuff(byte[] buff){
		this.buff=buff;
	}
	public void setIsWrite(boolean[] isWrite){
		this.isWrite=isWrite;
	}
	public void setHasWritten(int hasWritten){
		this.hasWritten=hasWritten;
	}
}
