package net.esle.sinadura.gui.events;

import net.esle.sinadura.gui.view.main.DocumentsPanel;
import net.esle.sinadura.gui.view.main.DocumentsTable;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

public class ButtonRemoveListener implements SelectionListener{

	private DocumentsTable documentsTable = null;

	public ButtonRemoveListener(DocumentsTable documentsTable) {
		this.documentsTable = documentsTable;
	}	
	
	@Override
	public void widgetDefaultSelected(SelectionEvent arg0) {
		widgetSelected(arg0);
	}

	@Override
	public void widgetSelected(SelectionEvent arg0) {
		this.documentsTable.removeDocument();
	}

}
