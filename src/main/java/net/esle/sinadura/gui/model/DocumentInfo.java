/*
# Copyright 2008 zylk.net
#
# This file is part of Sinadura.
#
# Sinadura is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 2 of the License, or
# (at your option) any later version.
#
# Sinadura is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with Sinadura.  If not, see <http://www.gnu.org/licenses/>. [^]
#
# See COPYRIGHT.txt for copyright notices and details.
#
*/
package net.esle.sinadura.gui.model;

import java.util.List;
import java.util.Map;

import net.esle.sinadura.core.interpreter.SignatureInfo;


public class DocumentInfo {

	// TODO pasarlo a URI
	private String path;
	/**
	 * null cuando no es reconocido
	 */
	private String mimeType;
	
	/**
	 * null si no ha sido validado aun
	 * Lista vacia si el documento no tiene ninguna firma
	 */
	private List<SignatureInfo> signatures;
	
	/**
	 * propiedades adicionales (para el uso desde plugins) 
	 */
	private Map<String, Object> properties;
	
	
	public DocumentInfo () {
		
		this.setPath(null);
		this.signatures = null;
		this.setMimeType(null);
	}

	/**
	 * @param signatures last signer in the first position
	 */
	public void setSignatures(List<SignatureInfo> signatures) {
		this.signatures = signatures;
	}

	/**
	 * @return last signer in the first position
	 */
	public List<SignatureInfo> getSignatures() {
		return signatures;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	@Override
	public boolean equals(Object obj) {

		if (this.getPath().equalsIgnoreCase(((DocumentInfo)obj).getPath())) {
			return true;
		} else {
			return false;
		}
		
	}


}
