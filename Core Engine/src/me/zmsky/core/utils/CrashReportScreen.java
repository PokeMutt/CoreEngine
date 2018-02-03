package me.zmsky.core.utils;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class CrashReportScreen implements Runnable, WindowListener{
	private JFrame frame;
	private JTextArea display;
	
	public CrashReportScreen(){
	    frame = new JFrame ();
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(this);
		
		JPanel middlePanel = new JPanel ();
	    middlePanel.setBorder ( new TitledBorder ( new EtchedBorder (), "Error Details" ) );

	    // create the middle panel components

	    display = new JTextArea ( 16, 58 );
	    display.setEditable ( false ); // set textArea non-editable
	    JScrollPane scroll = new JScrollPane ( display );
	    scroll.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
	    
	    //Add Textarea in to middle panel
	    middlePanel.add ( scroll );
	    frame.add ( middlePanel );
	    frame.pack ();
	    frame.setResizable(false);
	    frame.setLocationRelativeTo ( null );
	}
	public void run(){
		frame.setVisible ( true );
	}
	public void setError(String error){ display.setText(error); display.setCaretPosition(0);}
	public void windowActivated(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowClosing(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
}
