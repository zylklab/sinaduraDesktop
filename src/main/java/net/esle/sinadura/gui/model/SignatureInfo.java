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

import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;

import net.esle.sinadura.core.model.Status;


public class SignatureInfo {

	private Status status;
	private List<X509Certificate> chain;
	private Date date;
	private List<MessageInfo> messages;
//	private type 
	
	
	public SignatureInfo() {

		this.setStatus(null);
		this.setChain(null);
		this.setDate(null);
		this.setMessages(null);
	}

	public SignatureInfo(Status simpleStatus, List<X509Certificate> chain, Date date, List<MessageInfo> messages) {

		this.setStatus(simpleStatus);
		this.setChain(chain);		
		this.setDate(date);
		this.setMessages(messages);
	}
	
	public List<X509Certificate> getChain() {
		return chain;
	}

	public void setChain(List<X509Certificate> chain) {
		this.chain = chain;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setMessages(List<MessageInfo> messages) {
		this.messages = messages;
	}

	public List<MessageInfo> getMessages() {
		return messages;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Status getStatus() {
		return status;
	}

}