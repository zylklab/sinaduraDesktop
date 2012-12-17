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

import java.lang.reflect.InvocationTargetException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import net.esle.sinadura.core.exceptions.CoreException;
import net.esle.sinadura.core.exceptions.CorePKCS12Exception;
import net.esle.sinadura.core.exceptions.NoSunPkcs11ProviderException;
import net.esle.sinadura.core.exceptions.PKCS11Exception;
import net.esle.sinadura.core.model.KsSignaturePreferences;
import net.esle.sinadura.gui.controller.SignController;
import net.esle.sinadura.gui.exceptions.DriversNotFoundException;
import net.esle.sinadura.gui.util.LanguageUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;


class LoadKeyStoreProgress implements IRunnableWithProgress {
	
	private static Log log = LogFactory.getLog(LoadKeyStoreProgress.class);
	
	private Shell sShell = null;
	private KsSignaturePreferences ksSignaturePreferences = null;
	private String slot = null;

	public LoadKeyStoreProgress(Shell sShell,String slot) {
		
		this.sShell = sShell;
		this.slot = slot;
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		
		monitor.beginTask(LanguageUtil.getLanguage().getString("info.loading.certificate"), IProgressMonitor.UNKNOWN);
		
		try {
			ksSignaturePreferences = SignController.loadKeyStore(sShell, slot);
			
		} catch (KeyStoreException e) {
			throw new InvocationTargetException(e);
		} catch (PKCS11Exception e) {
			throw new InvocationTargetException(e);
		} catch (NoSuchAlgorithmException e) {			
			throw new InvocationTargetException(e);
		} catch (CoreException e) {
			throw new InvocationTargetException(e);	
		} catch (CorePKCS12Exception e) {
			throw new InvocationTargetException(e);
		} catch (DriversNotFoundException e) {
			throw new InvocationTargetException(e);
		} catch (NoSunPkcs11ProviderException e) {
			throw new InvocationTargetException(e);
		}		
		monitor.done();
	}

	public KsSignaturePreferences getKsSignaturePreferences() {
		return ksSignaturePreferences;
	}

}