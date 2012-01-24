package net.esle.sinadura.gui.view.main;

import java.io.File;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.List;

import net.esle.sinadura.core.certificate.CertificateUtil;
import net.esle.sinadura.core.interpreter.MessageInfo;
import net.esle.sinadura.core.interpreter.SignatureInfo;
import net.esle.sinadura.core.model.Status;
import net.esle.sinadura.gui.model.DocumentInfo;
import net.esle.sinadura.gui.util.ImagesUtil;
import net.esle.sinadura.gui.util.LanguageUtil;

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

public class SignaturePropertiesDialog extends Dialog {

	private Shell sShell = null;
	// private List list = null;
	private Tree tree = null;

	private DocumentInfo docInfo = null;
	private SignatureInfo sigInfo = null;

	private Label nombreDocumento = null;

	private Label labelSignedBy = null;
	private Label signer = null;

	private Label labelDate = null;
	private Label date = null;

	private Composite compositeProperties = null;
	private Composite compositeData = null;

	private Button buttonAceptar = null;

	public SignaturePropertiesDialog(Shell parent, DocumentInfo docInfo, SignatureInfo sigInfo) {

		super(parent);
		this.docInfo = docInfo;
		this.sigInfo = sigInfo;
	}

	public void open() {

		this.sShell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
		GridLayout gridLayout = new GridLayout();
		this.sShell.setText(LanguageUtil.getLanguage().getString("validation.windowtitle"));
		this.sShell.setSize(640, this.sShell.getSize().y);
		this.sShell.setImage(new Image(sShell.getDisplay(), ClassLoader.getSystemResourceAsStream(ImagesUtil.SINADURA_LOGO_IMG)));
		this.sShell.setLayout(gridLayout);
		GridData gdShell = new GridData();
//		gdShell.grabExcessHorizontalSpace = true;
//		gdShell.grabExcessHorizontalSpace = true;
//		gdShell.minimumHeight = 0;
//		gdShell.minimumWidth = 0;
		this.sShell.setLayoutData(gdShell);

		// Composite del documento
		Composite compositeDocument = new Composite(this.sShell, SWT.NONE);
		compositeDocument.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout gridLayoutDocument = new GridLayout();
		gridLayoutDocument.numColumns = 2;
		gridLayoutDocument.marginBottom = 10;
		gridLayoutDocument.marginTop = 15;
		gridLayoutDocument.horizontalSpacing = 20;
		compositeDocument.setLayout(gridLayoutDocument);

		this.nombreDocumento = new Label(compositeDocument, SWT.NONE);
		this.nombreDocumento.setText(new File(docInfo.getPath()).getName());
		GridData gdLabelNombreDocumento = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		gdLabelNombreDocumento.grabExcessHorizontalSpace = false;
		gdLabelNombreDocumento.grabExcessVerticalSpace = false;
		this.nombreDocumento.setLayoutData(gdLabelNombreDocumento);

		// Composite de las dos columnas
		Composite compositePrincipal = new Composite(this.sShell, SWT.NONE);
		GridData grPrinc = new GridData(GridData.FILL_BOTH);
		grPrinc.grabExcessHorizontalSpace = true;
		grPrinc.grabExcessVerticalSpace = true;
		compositePrincipal.setLayoutData(grPrinc);
		GridLayout gridLayoutPrincipal = new GridLayout();
		gridLayoutPrincipal.numColumns = 2;
		gridLayoutPrincipal.horizontalSpacing = 20;
		compositePrincipal.setLayout(gridLayoutPrincipal);

		Composite compositeTree = new Composite(compositePrincipal, SWT.NONE);
		GridData grCompositeList = new GridData(GridData.FILL_BOTH);
		grCompositeList.grabExcessHorizontalSpace = false;
		grCompositeList.grabExcessVerticalSpace = true;
		compositeTree.setLayoutData(grCompositeList);
		GridLayout layoutComposite = new GridLayout();
		compositeTree.setLayout(layoutComposite);

		this.tree = new Tree(compositeTree, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gdTree = new GridData();
		gdTree.horizontalAlignment = GridData.FILL;
		gdTree.verticalAlignment = GridData.FILL;
		gdTree.grabExcessHorizontalSpace = true;
		gdTree.grabExcessVerticalSpace = true;
		gdTree.widthHint = 200;
		gdTree.heightHint = 0;
		this.tree.setLayoutData(gdTree);
		
		TreeItem ti = null;
		for (SignatureInfo signature : docInfo.getSignatures()) {
			TreeItem item = new TreeItem(this.tree, SWT.BORDER);
			item.setData(signature);
			if (signature.getStatus().equals(Status.VALID)) {
				item.setImage(new Image(this.tree.getDisplay(), ClassLoader.getSystemResourceAsStream(ImagesUtil.OK_IMG)));
			} else if (signature.getStatus().equals(Status.INVALID)) {
				item.setImage(new Image(this.tree.getDisplay(), ClassLoader.getSystemResourceAsStream(ImagesUtil.CANCEL_IMG)));
			} else if (signature.getStatus().equals(Status.VALID_WARNING)) {
				item.setImage(new Image(this.tree.getDisplay(), ClassLoader.getSystemResourceAsStream(ImagesUtil.WARNING_OK_IMG)));
			} else if (signature.getStatus().equals(Status.UNKNOWN)) {
				item.setImage(new Image(this.tree.getDisplay(), ClassLoader.getSystemResourceAsStream(ImagesUtil.UNKNOWN_IMG)));
			}
			if (sigInfo.equals(signature)) {
				ti = item;
			}
			java.util.List<X509Certificate> chain = signature.getChain();
			if (chain != null && chain.size() != 0) {
				item.setText(CertificateUtil.getFormattedName(chain.get(0)));
			}
		}
		this.tree.select(ti);
		this.tree.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {

				if (tree.getItemCount() != 0 || tree.getSelectionCount() != 0) {
					TreeItem[] array = tree.getSelection();
					tree.setSelection(array[0]);
				}
				disposeChildren(compositeProperties);

				loadProperties(((SignatureInfo) tree.getSelection()[0].getData()).getMessages());
				loadData((SignatureInfo) tree.getSelection()[0].getData());

				compositeProperties.layout(true, true);
				compositeData.layout(true, true);
			}
		});
		this.tree.setSize(200, this.tree.getSize().y);


		// Composite que agrupa los de datos del documento y el de propiedades de la firma
		this.compositeData = new Composite(compositePrincipal, SWT.NONE);
		GridData gr = new GridData(GridData.FILL_BOTH);
		gr.grabExcessHorizontalSpace = true;
		gr.grabExcessVerticalSpace = true;
		this.compositeData.setLayoutData(gr);
		GridLayout gdLayoutData = new GridLayout();
		// gdLayoutData.marginBottom = 100;
		this.compositeData.setLayout(gdLayoutData);

		// Composite de los datos del Documento
		Composite compositeDatosFirma = new Composite(this.compositeData, SWT.NONE);
		GridData gdDatosFirma = new GridData(GridData.FILL_HORIZONTAL);
		compositeDatosFirma.setLayoutData(gdDatosFirma);
		GridLayout gridLayoutDatosFirma = new GridLayout();
		gridLayoutDatosFirma.numColumns = 3;
		gridLayoutDatosFirma.marginBottom = 10;
		compositeDatosFirma.setLayout(gridLayoutDatosFirma);
		gridLayoutDatosFirma.horizontalSpacing = 10;

		this.labelSignedBy = new Label(compositeDatosFirma, SWT.NONE);
		this.labelSignedBy.setText(LanguageUtil.getLanguage().getString("validation.signer"));
		GridData gdLabelSignedBy = new GridData(GridData.BEGINNING);
		gdLabelSignedBy.grabExcessHorizontalSpace = false;
		gdLabelSignedBy.grabExcessVerticalSpace = false;
		this.labelSignedBy.setLayoutData(gdLabelSignedBy);

		this.signer = new Label(compositeDatosFirma, SWT.NONE);
		GridData gdSigner = new GridData(GridData.BEGINNING);
		gdSigner.minimumWidth = 0;
		this.signer.setLayoutData(gdSigner);
		
		Button buttonShowCertificate = new Button(compositeDatosFirma, SWT.NONE);
		buttonShowCertificate.setText(LanguageUtil.getLanguage().getString("validation.show.certificate"));
		GridData gd = new GridData(GridData.BEGINNING);
		gd.grabExcessHorizontalSpace = false;
		gd.grabExcessVerticalSpace = false;
		buttonShowCertificate.setLayoutData(gd);
		buttonShowCertificate.addListener(SWT.MouseUp, new Listener() {
			
			@Override
			public void handleEvent(Event arg0) {
				if(tree.getSelection() != null && tree.getSelection().length == 1 ) {
					List<X509Certificate> chain = ((SignatureInfo)tree.getSelection()[0].getData()).getChain();
					if (chain != null && chain.size() != 0) {
						ChainInfoDialog dialog = new ChainInfoDialog(sShell, chain);
						dialog.open();
					}
				}
			}
		});
		
		this.labelDate = new Label(compositeDatosFirma, SWT.NONE);
		this.labelDate.setText(LanguageUtil.getLanguage().getString("validation.date"));
		GridData gdLabelDate = new GridData(GridData.BEGINNING);
		gdLabelDate.grabExcessHorizontalSpace = false;
		gdLabelDate.grabExcessVerticalSpace = false;
		this.labelDate.setLayoutData(gdLabelDate);

		this.date = new Label(compositeDatosFirma, SWT.NONE);
		GridData gdDate = new GridData(GridData.BEGINNING);
		gdDate.minimumWidth = 0;
		this.date.setLayoutData(gdDate);
		
		Button buttonShowTS = new Button(compositeDatosFirma, SWT.NONE);
		buttonShowTS.setText(LanguageUtil.getLanguage().getString("validation.show.certificate"));
		GridData gd2 = new GridData(GridData.BEGINNING);
		gd2.grabExcessHorizontalSpace = false;
		gd2.grabExcessVerticalSpace = false;
		buttonShowTS.setLayoutData(gd2);
		buttonShowTS.addListener(SWT.MouseUp, new Listener() {
			
			@Override
			public void handleEvent(Event arg0) {
				if (tree.getSelection() != null && tree.getSelection().length == 1) {
					List<X509Certificate> chain = ((SignatureInfo)tree.getSelection()[0].getData()).getTsChain();
					if (chain != null && chain.size() != 0) {
						ChainInfoDialog dialog = new ChainInfoDialog(sShell, chain);
						dialog.open();
					}
				}
			}
		});
		
		initialize(this.compositeData);

		// Button composite
		Composite composite = new Composite(this.sShell, SWT.NONE);
		GridData gdComposite = new GridData(GridData.FILL_HORIZONTAL | GridData.END);
		composite.setLayoutData(gdComposite);
		GridLayout gridLayoutButton = new GridLayout();
		composite.setLayout(gridLayoutButton);

		this.buttonAceptar = new Button(composite, SWT.NONE);
		this.buttonAceptar.setText(LanguageUtil.getLanguage().getString("button.accept"));
		this.buttonAceptar.setImage(new Image(this.sShell.getDisplay(), ClassLoader.getSystemResourceAsStream(ImagesUtil.BACK_IMG)));
		GridData gdAceptar = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		gdAceptar.grabExcessHorizontalSpace = true;
		gdAceptar.grabExcessVerticalSpace = true;
		this.buttonAceptar.setLayoutData(gdAceptar);
		this.buttonAceptar.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				sShell.dispose();
			}
		});

		loadData(sigInfo);

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

	private void loadData(SignatureInfo signature) {

		// Nombre del Firmante
		if (signature.getChain() != null && signature.getChain().size() != 0) {
			this.signer.setText(CertificateUtil.getFormattedName(signature.getChain().get(0)));
		} else {
			this.signer.setText("");
		}

		// Fecha de la firma
		if (signature.getDate() != null) {
			SimpleDateFormat dateFormat = LanguageUtil.getFullFormater();
			this.date.setText(dateFormat.format(signature.getDate()));
		} else {
			this.date.setText("");
		}
	}

	private void initialize(Composite comp) {

		if ((this.compositeProperties != null) && (!this.compositeProperties.isDisposed())) {
			this.compositeProperties.dispose();
		}
		if (this.compositeProperties == null || this.compositeProperties.isDisposed()) {
			// Composite de las propiedades de la firma
			this.compositeProperties = new Composite(comp, SWT.BORDER);
			GridData grProperties = new GridData(GridData.FILL_BOTH);
			grProperties.grabExcessHorizontalSpace = true;
			grProperties.grabExcessVerticalSpace= true;
			grProperties.minimumHeight = 0;
			this.compositeProperties.setLayoutData(grProperties);
			GridLayout gridLayoutProperties = new GridLayout();
			gridLayoutProperties.numColumns = 2;
			gridLayoutProperties.marginBottom = 10;
			gridLayoutProperties.marginTop = 10;
			gridLayoutProperties.horizontalSpacing = 20;
			this.compositeProperties.setLayout(gridLayoutProperties);

			loadProperties(sigInfo.getMessages());
			
//			this.compositeData.layout(true);
//			this.compositeProperties.layout(true);
		}
	}

	private void loadProperties(java.util.List<MessageInfo> messages) {

		Image image = null;
		if (messages != null) {
			for (MessageInfo message : messages) {

				image = null;
				if (message.getSimpleStatus().equals(Status.VALID)) {
					image = new Image(this.sShell.getDisplay(), ClassLoader.getSystemResourceAsStream(ImagesUtil.OK_IMG));
				} else if (message.getSimpleStatus().equals(Status.INVALID)) {
					image = new Image(this.sShell.getDisplay(), ClassLoader.getSystemResourceAsStream(ImagesUtil.CANCEL_IMG));
				} else if (message.getSimpleStatus().equals(Status.UNKNOWN)) {
					image = new Image(this.sShell.getDisplay(), ClassLoader.getSystemResourceAsStream(ImagesUtil.UNKNOWN_IMG));
				} else if (message.getSimpleStatus().equals(Status.VALID_WARNING)) {
					image = new Image(this.sShell.getDisplay(), ClassLoader.getSystemResourceAsStream(ImagesUtil.WARNING_OK_IMG));
				}
				Label documentStateProperties = new Label(this.compositeProperties, SWT.NONE);
				documentStateProperties.setImage(image);
				GridData gdLabelDocumentStateProperties = new GridData(GridData.BEGINNING);
				gdLabelDocumentStateProperties.grabExcessHorizontalSpace = false;
				gdLabelDocumentStateProperties.grabExcessVerticalSpace = false;
				gdLabelDocumentStateProperties.minimumWidth = 0;
				gdLabelDocumentStateProperties.minimumHeight = 0;
				documentStateProperties.setLayoutData(gdLabelDocumentStateProperties);

				Label messageProperites = new Label(this.compositeProperties, SWT.WRAP);
				messageProperites.setText(message.getText());
				GridData gdTexto = new GridData(GridData.BEGINNING);
				gdTexto.grabExcessHorizontalSpace = true;
				gdTexto.grabExcessVerticalSpace = false;
				gdTexto.minimumHeight = 0;	
				gdTexto.widthHint = this.sShell.getBounds().width;
				messageProperites.setLayoutData(gdTexto);
			}
		}
	}

	private void disposeChildren(Composite composite) {

		Control[] array = composite.getChildren();
		for (int i = 0; i < array.length; i++) {
			array[i].dispose();
		}
	}
}