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
package net.esle.sinadura.gui.util;


public class ValidatorUtil {

	
	public static String formatedTextButton(String s) {
		
		while (s.length() < 22)
			s = s + " ";
		
		return (s);
	}

	
	public static String formatedText(String s) {
		
		if (s == null) {
			return ("");
		}
		else {
			return (s);	
		}
	}
	
	public static String nonFormatedText(String s) {
		
		if (s.equals("")) {
			return (null);
		}
		else {
			return (s);	
		}
	}
}
