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
package net.esle.sinadura.gui.events;

import net.esle.sinadura.gui.util.LoggingDesktopController;


/**
 * 
 * para escribir en la consola de la interfaz desde el hilo del progressbar
 *  
 */
public class ProgressWriter implements Runnable {

	private String m;
	private int i;

	public static final int INFO = 0;
	public static final int ERROR = 1;

	public ProgressWriter(int i, String m) {

		this.i = i;
		this.m = m;
	}

	public void run() {

		if (i == INFO) {
			LoggingDesktopController.printInfo(m);
		} else {
			LoggingDesktopController.printError(m);
		}
	}
}