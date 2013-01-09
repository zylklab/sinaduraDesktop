package net.esle.sinadura.gui.exceptions;

public class FileNotValidException extends Exception {

	String path;
	
	public FileNotValidException(String path){
		this.path = path;
	}
	
	public String getFilePath(){
		return path;
	}
}
