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
package net.esle.sinadura.gui.view.main;

import java.io.File;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import net.esle.sinadura.core.certificate.CertificateUtil;
import net.esle.sinadura.core.model.Status;
import net.esle.sinadura.core.util.FileUtil;
import net.esle.sinadura.gui.model.DocumentInfo;
import net.esle.sinadura.gui.model.SignatureInfo;
import net.esle.sinadura.gui.util.ImagesUtil;
import net.esle.sinadura.gui.util.LanguageUtil;
import net.esle.sinadura.gui.util.LoggingDesktopController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

public class DocumentsTable extends Composite {

	private static Log log = LogFactory.getLog(DocumentsTable.class);

	private Tree tree = null;

	private Cursor oldCursor = null;
	private Cursor newCursor = null;

	public DocumentsTable(Composite parent, int style) {

		super(parent, style);
		initialize();
	}

	private void initialize() {

		GridLayout gridLayout = new GridLayout();
		setLayout(gridLayout);

		this.tree = new Tree(this, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);

		oldCursor = this.getShell().getCursor();
		newCursor = new Cursor(tree.getDisplay(), SWT.CURSOR_HELP);

		this.tree.setHeaderVisible(true);
		this.tree.setLinesVisible(true);

		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		this.tree.setLayoutData(gd);

		TreeColumn tcSigned = new TreeColumn(this.tree, SWT.CENTER);
		tcSigned.setWidth(25);

		TreeColumn tcMime = new TreeColumn(this.tree, SWT.CENTER);
		tcMime.setWidth(25);

		TreeColumn tcArchivoDestino = new TreeColumn(this.tree, SWT.LEFT);
		tcArchivoDestino.setText(LanguageUtil.getLanguage().getString("section.sign.table_pdf.name"));
		tcArchivoDestino.setWidth(250);

		TreeColumn tcSigner = new TreeColumn(this.tree, SWT.LEFT);
		tcSigner.setText(LanguageUtil.getLanguage().getString("section.sign.table_pdf.signer"));
		tcSigner.setWidth(300);

		TreeColumn tcDate = new TreeColumn(this.tree, SWT.LEFT);
		tcDate.setText(LanguageUtil.getLanguage().getString("section.sign.table_pdf.date"));
		tcDate.setWidth(200);

		TreeColumn tcArchivoOrigen = new TreeColumn(this.tree, SWT.LEFT);
		tcArchivoOrigen.setText(LanguageUtil.getLanguage().getString("section.sign.table_pdf.path"));
		tcArchivoOrigen.setWidth(800);

		this.tree.addKeyListener(new SupButtonKeyListener());

		this.tree.addListener(SWT.MouseDown, new Listener() {

			public void handleEvent(Event event) {

				Point pt = new Point(event.x, event.y);

				for (int i = 0; i < tree.getItemCount(); i++) { // recorre los items

					TreeItem item = tree.getItem(i);
					// 3
					if (item.getImage(3) != null && item.getImageBounds(3).contains(pt)) {
						DocumentInfo docInfo = (DocumentInfo) item.getData();
						SignatureInfo sigInfo = ((DocumentInfo) item.getData()).getSignatures().get(0);
						SignaturePropertiesDialog dialog = new SignaturePropertiesDialog(tree.getShell(), docInfo, sigInfo);
						dialog.open();
						return;
					}

					TreeItem[] array = item.getItems();

					for (int j = 0; j < array.length; j++) { // recorre los subitems

						TreeItem subItem = array[j];
						// 3
						if (subItem.getImage(3) != null && subItem.getImageBounds(3).contains(pt)) {
							DocumentInfo docInfo = (DocumentInfo) item.getData();
							SignatureInfo sigInfo = ((DocumentInfo) item.getData()).getSignatures().get(j + 1);
							SignaturePropertiesDialog dialog = new SignaturePropertiesDialog(tree.getShell(), docInfo, sigInfo);
							dialog.open();
							return;
						}
					}
				}
			}
		});

		this.tree.addListener(SWT.MouseMove, new Listener() {

			public void handleEvent(Event event) {

				Point pt = new Point(event.x, event.y);

				for (int i = 0; i < tree.getItemCount(); i++) { // recorre los items

					TreeItem item = tree.getItem(i);

					// 3
					if (item.getImage(3) != null && item.getImageBounds(3).contains(pt)) {

						if (tree.getCursor() == oldCursor) {
							tree.setCursor(newCursor);
						}
						return;
					}
					TreeItem[] array = item.getItems();

					for (int j = 0; j < array.length; j++) { // recorre los subitems

						TreeItem subItem = array[j];
						// 3
						if (subItem.getImage(3) != null && subItem.getImageBounds(3).contains(pt)) {

							if (tree.getCursor() == oldCursor) {
								tree.setCursor(newCursor);
							}
							return;
						}
					}
				}

				if (tree.getCursor() == newCursor) {
					tree.setCursor(oldCursor);
				}
			}
		});
	}

	public void reloadTable() {

		for (TreeItem item : this.tree.getItems()) {

			DocumentInfo doc = (DocumentInfo) item.getData();

			String extension = FileUtil.getExtension(doc.getPath());
			Program p = Program.findProgram("." + extension);
			if (p != null && p.getImageData() != null) {
				Image image = new Image(this.getDisplay(), p.getImageData());
				item.setImage(1, image);
			} else if (extension.equals(FileUtil.EXTENSION_SAR)) {
				Image image = new Image(this.getDisplay(), ClassLoader.getSystemResourceAsStream(ImagesUtil.SAR_IMG));
				item.setImage(1, image);
			}

			File file = new File(doc.getPath());
			item.setText(2, file.getName());
			item.setText(5, file.getPath());
			boolean expanded = item.getExpanded();
			
			if (doc.getSignatures() != null) {
				for (TreeItem treeItem : item.getItems()) {
					treeItem.dispose();
				}
				if (doc.getSignatures().size() == 0) {
					item.setText(3, LanguageUtil.getLanguage().getString("section.sign.not.signed"));
				} else {
					for (int i = 0; i < doc.getSignatures().size(); i++) {
						SignatureInfo signature = doc.getSignatures().get(i);

						if (i == 0) {
							populateItem(item, signature);
						} else {
							// sub item
							TreeItem subItem = new TreeItem(item, SWT.NONE);
							populateItem(subItem, signature);
							subItem.setText(6, file.getPath());
						}
					}
					item.setExpanded(expanded);
				}
			}
		}
		this.tree.update();
	}

	public DocumentInfo getSelectedDocument() {

		if (this.tree.getItemCount() == 0 || this.tree.getSelectionCount() == 0)
			return (null);
		else {
			TreeItem[] array = this.tree.getSelection();
			 
			if (array[0].getParentItem() == null) {
				return (DocumentInfo)array[0].getData();
			} else {
				return (DocumentInfo)array[0].getParentItem().getData();
			}
		}
	}

	public List<DocumentInfo> getSelectedDocuments() {

		List<DocumentInfo> list = new ArrayList<DocumentInfo>();

		if (!(this.tree.getItemCount() == 0 || this.tree.getSelectionCount() == 0)) {
			TreeItem[] array = this.tree.getSelection();
			for (TreeItem treeItem : array) {
				if ((TreeItem) treeItem.getParentItem() == null) {
					list.add((DocumentInfo) treeItem.getData());
				} else if ((TreeItem) treeItem.getParentItem() != null && !list.contains(treeItem.getParentItem().getData())) {
					list.add((DocumentInfo) treeItem.getParentItem().getData());
				}
			}
		}
		return (list);
	}

	public List<DocumentInfo> getDocuments() {

		List<DocumentInfo> list = new ArrayList<DocumentInfo>();

		if (this.tree.getItemCount() != 0) {
			TreeItem[] array = this.tree.getItems();
			for (TreeItem treeItem : array) {
				list.add((DocumentInfo) treeItem.getData());
			}
		}

		return (list);
	}

	// TODO y esto realmente tambien, pero como asi esta mas encapsulado me queda mas limpio
	public void removeDocument() {

		if (this.tree.getItemCount() == 0 || this.tree.getSelectionCount() == 0) {

			String mensaje = LanguageUtil.getLanguage().getString("error.no_selected_file");
			log.info(mensaje);
			LoggingDesktopController.printError(mensaje);

		} else {
			TreeItem[] selected = this.tree.getSelection();

			for (TreeItem treeItem : selected) {
				// TODO libera los objetos de la memoria? o solo la entrada en si?
				if (!treeItem.isDisposed()) {
					if (treeItem.getParentItem() == null) {
						treeItem.dispose();
					} else {
						treeItem.getParentItem().dispose();
					}
				}
			}
		}
	}

	public void addDocuments(List<DocumentInfo> list) {
		List<TreeItem> selection = new ArrayList<TreeItem>();
		for (DocumentInfo doc : list) {
			TreeItem ti = new TreeItem(this.tree, SWT.NONE);
			ti.setData(doc);

			// TODO pasar esto a image util
			String extension = FileUtil.getExtension(doc.getPath());
			Program p = Program.findProgram("." + extension);
			if (p != null && p.getImageData() != null) {
				Image image = new Image(this.getDisplay(), p.getImageData());
				ti.setImage(1, image);
			} else if (extension.equals(FileUtil.EXTENSION_SAR)) {
				Image image = new Image(this.getDisplay(), ClassLoader.getSystemResourceAsStream(ImagesUtil.SAR_IMG));
				ti.setImage(1, image);
			}

			File file = new File(doc.getPath());
			ti.setText(2, file.getName());
			ti.setText(5, file.getPath());
			selection.add(ti);
		}
		TreeItem[] array = (TreeItem[]) selection.toArray(new TreeItem[selection.size()]);
		this.tree.setSelection(array);
		
		this.tree.update();
	}

	public void setDocuments(List<DocumentInfo> list) {

		this.tree.removeAll();

		for (DocumentInfo pdfParameter : list) {

			TreeItem item = new TreeItem(this.tree, SWT.NONE);
			item.setData(pdfParameter);

			// TODO pasar esto a image util
			String extension = FileUtil.getExtension(pdfParameter.getPath());
			Program p = Program.findProgram("." + extension);
			if (p != null && p.getImageData() != null) {
				Image image = new Image(this.getDisplay(), p.getImageData());
				item.setImage(1, image);
			} else if (extension.equals(FileUtil.EXTENSION_SAR)) {
				Image image = new Image(this.getDisplay(), ClassLoader.getSystemResourceAsStream(ImagesUtil.SAR_IMG));
				item.setImage(1, image);
			}

			File file = new File(pdfParameter.getPath());
			item.setText(2, file.getName());
			item.setText(5, file.getPath());

			if (pdfParameter.getSignatures() != null) {
				if (pdfParameter.getSignatures().size() == 0) {
					item.setText(3, LanguageUtil.getLanguage().getString("section.sign.not.signed"));
				} else {
					for (int i = 0; i < pdfParameter.getSignatures().size(); i++) {
						SignatureInfo signature = pdfParameter.getSignatures().get(i);
						if (i == 0) {
							populateItem(item, signature);
						} else {
							// sub item
							TreeItem subItem = new TreeItem(item, SWT.NONE);
							populateItem(subItem, signature);
							subItem.setText(6, file.getPath());
						}
					}
				}
			}
		}
	}

	private void populateItem(TreeItem item, SignatureInfo signature) {

		if (signature.getStatus().equals(Status.VALID)) {
			item.setImage(3, new Image(this.getDisplay(), ClassLoader.getSystemResourceAsStream(ImagesUtil.OK_IMG)));
		} else if (signature.getStatus().equals(Status.INVALID)) {
			item.setImage(3, new Image(this.getDisplay(), ClassLoader.getSystemResourceAsStream(ImagesUtil.CANCEL_IMG)));
		} else if (signature.getStatus().equals(Status.VALID_WARNING)) {
			item.setImage(3, new Image(this.getDisplay(), ClassLoader.getSystemResourceAsStream(ImagesUtil.OK_WARNING_IMG)));
		} else if (signature.getStatus().equals(Status.UNKNOWN)) {
			item.setImage(3, new Image(this.getDisplay(), ClassLoader.getSystemResourceAsStream(ImagesUtil.WARNING_IMG)));
		}

		// signer chain
		List<X509Certificate> chain = signature.getChain();
		if (chain != null && chain.size() != 0) {
			item.setText(3, " " + CertificateUtil.getFormattedName(signature.getChain().get(0)));
		}

		// date
		if (signature.getDate() != null) {

			SimpleDateFormat dateFormat = LanguageUtil.getFullFormater();
			item.setText(4, " " + dateFormat.format(signature.getDate()));
		}

	}

	public void addSelectionListener(SelectionListener s) {

		this.tree.addSelectionListener(s);
	}

	public void expandAllItems() {
		for (TreeItem item : tree.getItems()) {
			item.setExpanded(true);
		}
	}

	class SupButtonKeyListener implements KeyListener {

		public void keyPressed(KeyEvent e) {
		}

		public void keyReleased(KeyEvent e) {
			if (SWT.DEL == e.character) {
				removeDocument();
			}
		}
	}

}