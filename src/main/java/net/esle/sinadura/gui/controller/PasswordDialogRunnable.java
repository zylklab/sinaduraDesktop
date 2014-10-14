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
/**
 * Excmo. Cabildo Insular de Tenerife
 * Instituto Insular de Informática y Comunicaciones
 *
 * sinaduraDesktop
 * martind - 13/06/2011
 */
package net.esle.sinadura.gui.controller;

import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;

import net.esle.sinadura.gui.util.LanguageUtil;
import net.esle.sinadura.gui.view.main.PasswordDialog;

import org.eclipse.swt.widgets.Shell;

/**
 * Runnable para abrir el diálogo de petición de contraseñas de forma desacoplada del diálogo de
 * PasswordCallbackHanlderDialog.
 * 
 */
public class PasswordDialogRunnable implements Runnable {

	private String dialogMessage;
	private Shell sShell;
	private PasswordProtection passwordProtection;

	PasswordDialogRunnable(Shell shell) {
		this(shell, null);
	}

	PasswordDialogRunnable(Shell shell, String customDialogMessage) {
		sShell = shell;
		if (customDialogMessage != null) {
			dialogMessage = customDialogMessage;
		} else {
			dialogMessage = LanguageUtil.getLanguage().getString("password.dialog.message");
		}
	}

	@Override
	public void run() {
		PasswordDialog passwordDialog = new PasswordDialog(sShell);
		String password = passwordDialog.open(dialogMessage);
		if (password != null) {
			passwordProtection = new KeyStore.PasswordProtection(password.toCharArray());
		} else {
			passwordProtection = null;
		}
	}

	public PasswordProtection getPasswordProtection() {
		return passwordProtection;
	}
}
