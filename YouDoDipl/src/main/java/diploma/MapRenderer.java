package diploma;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.events.ConsoleEvent;
import com.teamdev.jxbrowser.chromium.events.ConsoleListener;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

public class MapRenderer {
	
	// Holds the complete html code which needs to be rendered
	String html;
	
	public MapRenderer() {
		
	}
	
	public void setHtml (String html) {
		this.html = html;
	}
	
	public void showMap() {
		Browser browser = new Browser();
	    BrowserView view = new BrowserView(browser);
	    
	    JFrame frame = new JFrame("JxBrowser - Hello World");
	    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(view, BorderLayout.CENTER);
        frame.setSize(1000, 800);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        browser.addConsoleListener(new ConsoleListener() {
            public void onMessage(ConsoleEvent event) {
                System.out.println("Level: " + event.getLevel());
                System.out.println("Message: " + event.getMessage());
            }
        });
        
        browser.loadHTML( html );
	}
}
