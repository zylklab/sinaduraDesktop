/*
 * # Copyright 2008 zylk.net 
 * # 
 * # This file is part of Sinadura. 
 * # 
 * # Sinadura is free software: you can redistribute it and/or modify 
 * # it under the terms of the GNU General Public License as published by 
 * # the Free Software Foundation, either version 2 of the License, or 
 * # (at your option) any later version. 
 * # 
 * # Sinadura is distributed in the hope that it will be useful, 
 * # but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
 * # GNU General Public License for more details. 
 * # 
 * # You should have received a copy of the GNU General Public License 
 * # along with Sinadura. If not, see <http://www.gnu.org/licenses/>. [^] 
 * # 
 * # See COPYRIGHT.txt for copyright notices and details. 
 * #
 */
package net.esle.sinadura.gui.model;

import net.esle.sinadura.core.model.Status;


public class MessageInfo {

	private Status simpleStatus;
	private String text;
	
	public MessageInfo() {
		this.simpleStatus = null;
		this.text = null;
	}
	
	public MessageInfo(Status simpleStatus, String text) {
		this.simpleStatus = simpleStatus;
		this.text = text;
	}
	
	public void setSimpleStatus(Status simpleStatus) {
		this.simpleStatus = simpleStatus;
	}
	public Status getSimpleStatus() {
		return simpleStatus;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
}
