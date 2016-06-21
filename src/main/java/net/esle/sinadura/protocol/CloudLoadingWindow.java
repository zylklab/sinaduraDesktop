package net.esle.sinadura.protocol;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.xml.utils.URI.MalformedURIException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

import net.esle.sinadura.gui.LoadingOperations;
import net.esle.sinadura.gui.exceptions.FileNotValidException;
import net.esle.sinadura.gui.model.LoggerMessage;
import net.esle.sinadura.gui.util.ImagesUtil;
import net.esle.sinadura.gui.util.LanguageUtil;
import net.esle.sinadura.gui.util.PropertiesUtil;

public class CloudLoadingWindow {

	private String[] args;

	public CloudLoadingWindow(String[] args) throws FileNotValidException, FileSystemException, MalformedURIException {
		
		this.args = args;
		init();
	}

	public void init() throws FileNotValidException, FileSystemException, MalformedURIException {

		Display.setAppName(PropertiesUtil.APPLICATION_NAME);
		Display display = new Display();

		Shell shell = new Shell(SWT.APPLICATION_MODAL | SWT.BORDER);
		shell.setText(LanguageUtil.getLanguage().getString("loading.windowtitle"));
		shell.setImage(new Image(shell.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.SINADURA_LOGO_IMG)));

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.verticalSpacing = 10;
		gridLayout.marginTop = 10;
		shell.setLayout(gridLayout);

		shell.setSize(new Point(400, 250));

		Monitor primary = display.getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 3;
		shell.setLocation(x, y);

		Label labelImage = new Label(shell, SWT.NONE);
		GridData gdImage = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		gdImage.minimumHeight = 0;
		gdImage.grabExcessVerticalSpace = true;
		labelImage.setLayoutData(gdImage);

		Image imageSinadura = new Image(shell.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.SINADURA_FULL_IMG));
		labelImage.setImage(imageSinadura);

		Label messages = new Label(shell, SWT.NONE);
		messages.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		List<LoggerMessage> listMessages = new ArrayList<LoggerMessage>();

		messages.setText(LanguageUtil.getLanguage().getString("loading.checking.conection"));
		messages.pack();

		shell.open();
		
		// Como las funciones internas estan pensada para que haya una Shell, en el modo Cloud mantenemos la ventana de carga.
		// Por eso ademas, se ejecutan directamente en este hilo las operaciones. La ventana se queda congelada en este caso, 
		// pero no deberia ser un problema ya que realmente la ventana no tiene interacion alguna.		
		LoadingOperations.run(listMessages);
		
		// TODO tratar o mostrar "listMessages"
		
		CloudMainWindow.initCloud(shell, args);

		shell.dispose();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		display.dispose();
	}
	
}


