package net.esle.sinadura.gui.view.documentation;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

import net.esle.sinadura.gui.util.DesktopUtil;
import net.esle.sinadura.gui.util.ImagesUtil;
import net.esle.sinadura.gui.util.LanguageUtil;
import net.esle.sinadura.gui.util.PropertiesUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndLinkImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class NewsWindow extends Dialog {

	private static Log log = LogFactory.getLog(NewsWindow.class);
	private Shell sShell = null;
	private Label labelImage = null;
	private Button buttonClose = null;

	private String url = null;
	private SyndFeed feed = null;

	public NewsWindow(Shell parent, SyndFeed feed) {
		super(parent);
		this.feed = feed;
	}

	@Override
	public int open() {

		Shell parent = getParentShell();

		this.sShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		this.sShell.setSize(new Point(800, 600));
		this.sShell.setImage(new Image(this.sShell.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.SINADURA_LOGO_IMG)));
		this.sShell.setText(LanguageUtil.getLanguage().getString("news.windowtitle"));

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.verticalSpacing = 10;
		gridLayout.marginTop = 10;
		this.sShell.setLayout(gridLayout);

		this.labelImage = new Label(sShell, SWT.CENTER);
		this.labelImage.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));

		Image imageSinadura = new Image(sShell.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.SINADURA_FULL_IMG));
		this.labelImage.setImage(imageSinadura);
		
		Link urlSinadura = new Link(sShell, SWT.NONE);
		urlSinadura.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));

		String text = "<a>" + PropertiesUtil.getConfiguration().getProperty(PropertiesUtil.SINADURA_MAIN_URL) + "</a>";
		urlSinadura.setText(text);
		urlSinadura.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				DesktopUtil.openDefaultBrowser(PropertiesUtil.getConfiguration().getProperty(PropertiesUtil.SINADURA_MAIN_URL));
			}
		});

			List<SyndEntry> list = this.feed.getEntries();

			SyndLinkImpl o = (SyndLinkImpl) list.get(0).getLinks().get(0);
			String html = "<html>" + list.get(0).getDescription().getValue().replace("src=\"/image/image_gallery?uuid=", "src=\"http://www.sinadura.net/image/image_gallery?uuid=") + "</html>";
			url = o.getHref();

			Browser browser = new Browser(sShell, SWT.BORDER);
			browser.setLayoutData(new GridData(GridData.FILL_BOTH));
			browser.setText(html);

			new Label(sShell, SWT.NONE);
			for (int i = 0; i < 5; i++) {
				o = (SyndLinkImpl) list.get(i).getLinks().get(0);
				url = o.getHref();

				Link link = new Link(sShell, SWT.NONE);
				link.setText("<a>" + list.get(i).getTitle() + "</a>");
				link.addMouseListener(new UrlListener(url));
			}

		// buttons composite
		Composite composite = new Composite(this.sShell, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		GridLayout gridLayoutButtons = new GridLayout();
		gridLayoutButtons.numColumns = 1;
		composite.setLayout(gridLayoutButtons);

		this.buttonClose = new Button(composite, SWT.NONE);
		this.buttonClose.setText(LanguageUtil.getLanguage().getString("button.back"));
		this.buttonClose.setImage(new Image(this.sShell.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.BACK_IMG)));
		this.buttonClose.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		this.buttonClose.addSelectionListener(new ButtonCloseListener());

		// Listeners
		this.labelImage.addMouseListener(new ImageListener());

		// to center the shell on the screen
		Monitor primary = this.sShell.getDisplay().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = this.sShell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 3;
		this.sShell.setLocation(x, y);

		this.sShell.open();

		Display display = parent.getDisplay();

		while (!this.sShell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		return (0);
	}

	class ImageListener implements MouseListener {

		@Override
		public void mouseDoubleClick(MouseEvent arg0) {

		}

		@Override
		public void mouseDown(MouseEvent arg0) {

		}

		@Override
		public void mouseUp(MouseEvent arg0) {
			DesktopUtil.openDefaultBrowser("http://www.sinadura.net");
		}

	}

	class ButtonCloseListener implements SelectionListener {

		@Override
		public void widgetDefaultSelected(SelectionEvent arg0) {
			widgetSelected(arg0);
		}

		@Override
		public void widgetSelected(SelectionEvent arg0) {
			sShell.dispose();
		}

	}
	
	class UrlListener implements MouseListener {
		
		private String url = null;
		
		public UrlListener(String url) {
			this.url = url;
		}

		@Override
		public void mouseDoubleClick(MouseEvent arg0) {

		}

		@Override
		public void mouseDown(MouseEvent arg0) {

		}

		@Override
		public void mouseUp(MouseEvent arg0) {
			DesktopUtil.openDefaultBrowser(url);
		}

	}

}
