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
package net.esle.sinadura.gui.controller;

import java.io.IOException;
import java.security.KeyStore.PasswordProtection;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import net.esle.sinadura.core.exceptions.PasswordCallbackCanceledException;
import net.esle.sinadura.core.password.PasswordExtractor;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class PasswordCallbackHandlerDialog implements CallbackHandler, PasswordExtractor {

	private Shell sShell = null;
	private PasswordProtection passwordProtection = null;

	public PasswordCallbackHandlerDialog(Shell sShell) {

		super();
		this.sShell = sShell;
	}

	@Override
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {

		for (Callback c : callbacks) {
			if (c instanceof PasswordCallback) {
				PasswordCallback pc = (PasswordCallback) c;
				initPassword();
				if ((passwordProtection != null) && (passwordProtection.getPassword() != null)) {
					pc.setPassword(passwordProtection.getPassword());
				} else {
					throw new IOException(new PasswordCallbackCanceledException());
				}
			}
		}
	}

	private void initPassword() {
		PasswordDialogRunnable runnable = new PasswordDialogRunnable(sShell);

		// por acceder desde el hilo de la progresbar
		Display.getDefault().syncExec(runnable);
		passwordProtection = runnable.getPasswordProtection();
	}

	@Override
	public PasswordProtection getPasswordProtection() {

		return passwordProtection;
	}
}