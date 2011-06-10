package net.esle.sinadura.gui.view.main;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.List;

import net.esle.sinadura.core.certificate.CertificateUtil;
import net.esle.sinadura.gui.util.CertificateParserUtil;
import net.esle.sinadura.gui.util.ImagesUtil;
import net.esle.sinadura.gui.util.LanguageUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class ChainInfoDialog extends Dialog {

	private static Log log = LogFactory.getLog(ChainInfoDialog.class);

	private Shell sShell = null;
	private Tree tree = null;
	private Composite compositeData = null;
	private Composite compositePrincipal = null;

	private List<X509Certificate> chain;

	public ChainInfoDialog(Shell parent, List<X509Certificate> chain) {
		super(parent);
		this.chain = chain;
	}

	public void open() {
		this.sShell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
		GridLayout gridLayout = new GridLayout();
		this.sShell.setText(LanguageUtil.getLanguage().getString("chain.info.windowtitle"));
		this.sShell.setImage(new Image(sShell.getDisplay(), ClassLoader.getSystemResourceAsStream(ImagesUtil.SINADURA_LOGO_IMG)));
		this.sShell.setLayout(gridLayout);
		GridData gdShell = new GridData();
		gdShell.grabExcessHorizontalSpace = true;
		gdShell.grabExcessHorizontalSpace = true;
		gdShell.minimumHeight = 0;
		gdShell.minimumWidth = 0;
		this.sShell.setLayoutData(gdShell);

		this.compositePrincipal = new Composite(this.sShell, SWT.NONE);
		GridLayout gridLayoutComposite = new GridLayout();
		gridLayoutComposite.numColumns = 2;
		gridLayoutComposite.marginBottom = 10;
		gridLayoutComposite.marginTop = 15;
		gridLayoutComposite.horizontalSpacing = 20;
		this.compositePrincipal.setLayout(gridLayoutComposite);
		GridData gdPrincipal = new GridData(GridData.FILL_BOTH);
		gdPrincipal.grabExcessHorizontalSpace = true;
		gdPrincipal.grabExcessVerticalSpace = true;
		this.compositePrincipal.setLayoutData(gdPrincipal);

		Composite compositeTree = new Composite(this.compositePrincipal, SWT.NONE);
		GridData grCompositeList = new GridData(GridData.FILL_BOTH);
		grCompositeList.grabExcessHorizontalSpace = false;
		grCompositeList.grabExcessVerticalSpace = true;
		compositeTree.setLayoutData(grCompositeList);
		GridLayout layoutComposite = new GridLayout();
		compositeTree.setLayout(layoutComposite);

		this.tree = new Tree(compositeTree, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gdTree = new GridData(GridData.FILL_BOTH);
		gdTree.grabExcessHorizontalSpace = true;
		gdTree.grabExcessVerticalSpace = true;
		gdTree.widthHint = 200;
		gdTree.heightHint = 0;
		this.tree.setLayoutData(gdTree);

		if (this.chain != null && this.chain.size() > 0) {

			// La lista está en orden inverso por eso se rellena así
			TreeItem item = new TreeItem(this.tree, SWT.BORDER);
			item.setData(this.chain.get(this.chain.size() - 1));
			item.setText(CertificateUtil.getFormattedName(this.chain.get(this.chain.size() - 1)));
			this.tree.setSelection(item);

			for (int i = (this.chain.size() - 1); i > 0; i--) {
				item = new TreeItem(item, SWT.BORDER);
				item.setData(this.chain.get(i - 1));
				item.setText(CertificateUtil.getFormattedName(this.chain.get(i - 1)));
				this.tree.setSelection(item);
			}
			
			this.tree.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {

					disposeChildren(compositeData);

					X509Certificate certificate = (X509Certificate) tree.getSelection()[0].getData();
					initialize(compositeData, certificate);

					compositeData.layout(true, true);
				}
			});
			TreeViewer viewer = new TreeViewer(this.tree);
			viewer.expandToLevel(chain.size() - 1);
			this.tree.setSize(200, this.tree.getSize().y);

			this.compositeData = new Composite(compositePrincipal, SWT.NONE);
			GridData gdData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
			this.compositeData.setLayoutData(gdData);
			GridLayout gridLayoutData = new GridLayout();
			gridLayoutData.numColumns = 2;
			gridLayoutData.marginBottom = 30;
			gridLayoutData.horizontalSpacing = 10;
			this.compositeData.setLayout(gridLayoutData);

			X509Certificate certificate = (X509Certificate) this.tree.getSelection()[0].getData();
			initialize(this.compositeData, certificate);
		}

		// Button composite
		Composite composite = new Composite(this.sShell, SWT.NONE);
		GridData gdComposite = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_END);
		composite.setLayoutData(gdComposite);
		GridLayout gridLayoutButton = new GridLayout();
		composite.setLayout(gridLayoutButton);

		Button buttonAceptar = new Button(composite, SWT.NONE);
		buttonAceptar.setText(LanguageUtil.getLanguage().getString("button.accept"));
		buttonAceptar.setImage(new Image(this.sShell.getDisplay(), ClassLoader.getSystemResourceAsStream(ImagesUtil.BACK_IMG)));
		GridData gdAceptar = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		gdAceptar.grabExcessHorizontalSpace = true;
		gdAceptar.grabExcessVerticalSpace = true;
		buttonAceptar.setLayoutData(gdAceptar);
		buttonAceptar.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				sShell.dispose();
			}
		});
		this.sShell.pack();
		// to center the shell on the screen
		Monitor primary = this.sShell.getDisplay().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = this.sShell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 3;

		this.sShell.setLocation(x, y);
		this.sShell.open();

		while (!this.sShell.isDisposed()) {
			if (!this.sShell.getDisplay().readAndDispatch())
				this.sShell.getDisplay().sleep();
		}
	}

	private void initialize(Composite composite, X509Certificate certificate) {

		Composite compositeSubject = new Composite(this.compositeData, SWT.NONE);
		GridLayout glSubject = new GridLayout();
		glSubject.marginBottom = 20;
		compositeSubject.setLayout(glSubject);
		GridData gdSubject = new GridData(GridData.FILL_HORIZONTAL);
		gdSubject.horizontalSpan = 2;
		compositeSubject.setLayoutData(gdSubject);

		Label dataSubject = new Label(compositeSubject, SWT.WRAP);
		GridData gdDataSubject = new GridData(GridData.FILL_HORIZONTAL);
		gdDataSubject.grabExcessHorizontalSpace = true;
		gdDataSubject.grabExcessVerticalSpace = false;
		gdDataSubject.widthHint = 600;
		dataSubject.setLayoutData(gdDataSubject);

		String s = "";
		// Subject name
		try {
			s = CertificateParserUtil.getSubjectDescription(certificate);
		} catch (IOException e) {
			log.error("", e);
		}
		dataSubject.setText(s);

		SimpleDateFormat dateFormat = LanguageUtil.getFullFormater();
		Label labelValidFrom = new Label(this.compositeData, SWT.NONE);
		labelValidFrom.setText(LanguageUtil.getLanguage().getString("chain.info.valid.from"));
		GridData gdLabelValidFrom = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gdLabelValidFrom.grabExcessHorizontalSpace = false;
		gdLabelValidFrom.grabExcessVerticalSpace = false;
		labelValidFrom.setLayoutData(gdLabelValidFrom);

		Label validFrom = new Label(this.compositeData, SWT.WRAP);
		GridData gdValidFrom = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gdValidFrom.grabExcessHorizontalSpace = true;
		gdValidFrom.grabExcessVerticalSpace = false;
		gdValidFrom.widthHint = 600;
		validFrom.setLayoutData(gdValidFrom);
		validFrom.setText(dateFormat.format(certificate.getNotBefore()));

		Label labelValidUntil = new Label(this.compositeData, SWT.NONE);
		labelValidUntil.setText(LanguageUtil.getLanguage().getString("chain.info.valid.until"));
		GridData gdLabelValidUntil = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gdLabelValidUntil.grabExcessHorizontalSpace = false;
		gdLabelValidUntil.grabExcessVerticalSpace = false;
		labelValidUntil.setLayoutData(gdLabelValidUntil);

		Label validUntil = new Label(this.compositeData, SWT.WRAP);
		GridData gdValidUntil = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gdValidUntil.grabExcessHorizontalSpace = true;
		gdValidUntil.grabExcessVerticalSpace = false;
		gdValidUntil.widthHint = 600;
		validUntil.setLayoutData(gdValidUntil);
		validUntil.setText(dateFormat.format(certificate.getNotAfter()));

		if (CertificateParserUtil.getKeyUsage(certificate) != null && !CertificateParserUtil.getKeyUsage(certificate).trim().equals("")) {

			Label labelKeyUsage = new Label(this.compositeData, SWT.NONE);
			labelKeyUsage.setText(LanguageUtil.getLanguage().getString("chain.info.key.usage"));
			GridData gdLabelKeyUsage = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
			gdLabelKeyUsage.grabExcessHorizontalSpace = false;
			gdLabelKeyUsage.grabExcessVerticalSpace = false;
			labelKeyUsage.setLayoutData(gdLabelKeyUsage);

			Label keyUsage = new Label(this.compositeData, SWT.WRAP);
			GridData gdKeyUsage = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
			gdKeyUsage.grabExcessHorizontalSpace = true;
			gdKeyUsage.grabExcessVerticalSpace = false;
			gdKeyUsage.widthHint = 600;
			keyUsage.setLayoutData(gdKeyUsage);
			keyUsage.setText(CertificateParserUtil.getKeyUsage(certificate));

		}

		Label labelIssuerName = new Label(this.compositeData, SWT.NONE);
		labelIssuerName.setText(LanguageUtil.getLanguage().getString("chain.info.issuer"));
		GridData gdLabelIssuerName = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gdLabelIssuerName.grabExcessHorizontalSpace = false;
		gdLabelIssuerName.grabExcessVerticalSpace = false;
		labelIssuerName.setLayoutData(gdLabelIssuerName);

		Label issuerName = new Label(this.compositeData, SWT.WRAP);
		GridData gdIssuerName = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gdIssuerName.grabExcessHorizontalSpace = true;
		gdIssuerName.grabExcessVerticalSpace = false;
		gdIssuerName.widthHint = 600;
		issuerName.setLayoutData(gdIssuerName);
		try {
			s = CertificateParserUtil.getIssuerDescription(certificate);
		} catch (IOException e) {
			log.error("", e);
		}
		issuerName.setText(s);
	}

	private void disposeChildren(Composite composite) {

		Control[] array = composite.getChildren();
		for (int i = 0; i < array.length; i++) {
			array[i].dispose();
		}
	}
}
