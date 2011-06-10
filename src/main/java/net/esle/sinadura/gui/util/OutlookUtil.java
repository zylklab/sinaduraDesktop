package net.esle.sinadura.gui.util;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.ole.win32.OLE;
import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.OleClientSite;
import org.eclipse.swt.ole.win32.OleFrame;
import org.eclipse.swt.ole.win32.Variant;
import org.eclipse.swt.widgets.Shell;

public class OutlookUtil {
	
	private static Log log = LogFactory.getLog(OutlookUtil.class);

	public static void openOutlook(List<String> attachments) throws SWTException{

		Shell shell = new Shell();
		
		log.info("qwerty --> es windows");
		OleFrame frame = new OleFrame(shell, SWT.NONE);

		OleClientSite site2 = null;
		OleClientSite site = null;
		// This should start outlook if it is not running yet
		site = new OleClientSite(frame, SWT.NONE, "OVCtl.OVCtl");
		site.doVerb(OLE.OLEIVERB_INPLACEACTIVATE);
		// Now get the outlook application
		site2 = new OleClientSite(frame, SWT.NONE, "Outlook.Application");
		OleAutomation outlook = new OleAutomation(site2);
		OleAutomation mail = invoke(outlook, "CreateItem", 0 /* Mail item */)
				.getAutomation();
		setProperty(mail, "To", "");
		setProperty(mail, "Bcc", "");
		setProperty(mail, "BodyFormat", 2 /* HTML */);
		setProperty(mail, "Subject", "");
		setProperty(mail, "HtmlBody", "<html></html>");

		for (String attachment : attachments) {
			File file = new File(attachment);
			if (file.exists()) {
				OleAutomation oleAttach = getProperty(mail, "Attachments");
				invoke(oleAttach, "Add", file.getPath());
			} else {
				System.out.println("Attachment File " + file.getName()
						+ " not found; will send email with attachment");
			}
		}
		invoke(mail, "Display" /* or "Send" */);
	}

	private static OleAutomation getProperty(OleAutomation auto, String name) {
		Variant varResult = auto.getProperty(property(auto, name));
		if (varResult != null && varResult.getType() != OLE.VT_EMPTY) {
			OleAutomation result = varResult.getAutomation();
			varResult.dispose();
			return result;
		}
		return null;
	}

	private static Variant invoke(OleAutomation auto, String command,
			String value) {
		return auto.invoke(property(auto, command),
				new Variant[] { new Variant(value) });
	}

	private static Variant invoke(OleAutomation auto, String command) {
		return auto.invoke(property(auto, command));
	}

	private static Variant invoke(OleAutomation auto, String command, int value) {
		return auto.invoke(property(auto, command),
				new Variant[] { new Variant(value) });
	}

	private static boolean setProperty(OleAutomation auto, String name,
			String value) {
		return auto.setProperty(property(auto, name), new Variant(value));
	}

	private static boolean setProperty(OleAutomation auto, String name,
			int value) {
		return auto.setProperty(property(auto, name), new Variant(value));
	}

	private static int property(OleAutomation auto, String name) {
		return auto.getIDsOfNames(new String[] { name })[0];
	}
}
