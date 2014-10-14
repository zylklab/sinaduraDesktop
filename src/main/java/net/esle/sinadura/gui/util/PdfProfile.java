package net.esle.sinadura.gui.util;

import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfSignatureAppearance;


public class PdfProfile {
	
	// pdf-profile-name
	private String name;
	
	private boolean visible = false;
	private String acroField = null;
			
	private int page = 1;
	private int startX = 0;
	private int startY = 0;
	private int widht = 0;
	private int height = 0;
	
	private String reason = null;
	private String location = null;
	boolean hasImage;
	private String imagePath;
	
	private int certified = PdfSignatureAppearance.NOT_CERTIFIED;
	
	
	public PdfProfile(){
		
	}
	public PdfProfile(String acroFieldName){
		this.acroField = acroFieldName;
	}
	public PdfProfile(String name, boolean visible, boolean image, String imagePath, String acroField, int width, int height, int startX, int startY, int page, String reason, String location, int certified){
		this.name = name;
		this.visible = visible;
		this.acroField = acroField;
		this.page = page;
		this.startX = startX;
		this.startY = startY;
		this.widht = width;
		this.height = height;
		this.reason = reason;
		this.location = location;
		this.hasImage = image;
		this.imagePath = imagePath;
		this.certified = certified;
		
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	public boolean getVisible() {
		return visible;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getReason() {
		return reason;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getLocation() {
		return location;
	}
	public void setStartX(int startX) {
		this.startX = startX;
	}
	public int getStartX() {
		return startX;
	}
	public void setStartY(int startY) {
		this.startY = startY;
	}
	public int getStartY() {
		return startY;
	}
	public void setWidht(int widht) {
		this.widht = widht;
	}
	public int getWidht() {
		return widht;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getHeight() {
		return height;
	}
	public void setCertified(int certified) {
		this.certified = certified;
	}
	public int getCertified() {
		return certified;
	}

	public void setPage(int page) {
		this.page = page;
	}
	public int getPage() {
		return page;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAcroField() {
		return acroField;
	}
	public void setAcroField(String acroField) {
		this.acroField = acroField;
	}

	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public boolean hasImage() {
		return hasImage;
	}
	public void setHasImage(boolean hasImage) {
		this.hasImage = hasImage;
	}

	/*
	 * renderiza la tabla de preferencias
	 */
	@Override
	public String toString(){
		return name;
	}
}
