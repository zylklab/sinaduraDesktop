package net.esle.sinadura.gui.util;


public class PdfProfile {
	
	private String name;
	
	private boolean visible;
	private String acroField;
			
	private boolean askPosition;
	private int page;
	private float startX;
	private float startY;
	private float widht;
	private float height;
	
	private boolean hasImage;
	private String imagePath;
	
	private String reason;
	private String location;
	
	private int certified;
	
	
	public PdfProfile() {
	}
	
	public PdfProfile(PdfProfile pdfProfile) {
		
		this.name = pdfProfile.getName();
		this.visible = pdfProfile.getVisible();
		this.acroField = pdfProfile.getAcroField();
		this.askPosition = pdfProfile.getAskPosition();
		this.page = pdfProfile.getPage();
		this.startX = pdfProfile.getStartX();
		this.startY = pdfProfile.getStartY();
		this.widht = pdfProfile.getWidht();
		this.height = pdfProfile.getHeight();
		this.hasImage = pdfProfile.hasImage();
		this.imagePath = pdfProfile.getImagePath();
		this.reason = pdfProfile.getReason();
		this.location = pdfProfile.getLocation();
		this.certified = pdfProfile.getCertified();
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
	public void setStartX(float startX) {
		this.startX = startX;
	}
	public float getStartX() {
		return startX;
	}
	public void setStartY(float startY) {
		this.startY = startY;
	}
	public float getStartY() {
		return startY;
	}
	public void setWidht(float widht) {
		this.widht = widht;
	}
	public float getWidht() {
		return widht;
	}
	public void setHeight(float height) {
		this.height = height;
	}
	public float getHeight() {
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

	public void setAskPosition(boolean askPosition) {
		this.askPosition = askPosition;
	}

	public boolean getAskPosition() {
		return askPosition;
	}

}
