/* TODO: Put some sort of documentation/description up here
 * 
 */

import javax.swing.SwingConstants;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.border.LineBorder;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JViewport;
import javax.swing.ImageIcon;
import javax.swing.table.TableCellRenderer;
import javax.swing.UIManager;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.lang.StringBuffer;
import java.util.Stack;

public class TalkingPointsGUI implements ActionListener, TableModelListener, ListSelectionListener,
							   PropertyChangeListener   {
	
	// Constants
	static final int BG_COLOR_R = 241;
	static final int BG_COLOR_G = 215;
	static final int BG_COLOR_B = 161;
	static final int COMP_BG_COLOR_R = 255;
	static final int COMP_BG_COLOR_G = 255;
	static final int COMP_BG_COLOR_B = 255;
	static final int COMP_SPACER_X = 10;
	static final int COMP_SPACER_Y = 10;
	static final int TOP_SPACER_X = 50;
	static final int VIEWALL_X = 80;
	static final int VIEWALL_Y = 45;
	static final String MAINPANE = "Front pane of TPGUI";
	static final String MOREINFO = "More information pane of TPGUI";
	static final String MENU = "POI's menu";
	static final String HOURS = "POI's hours";
	static final String HISTORY = "POI's history";
	static final String ACCESSIBILITY = "POI's accessibility";
	static final String SPECIALS = "POI's specials";
	static final String COMMENTS = "POI's comments";
	
	
	// Default constructor
	TalkingPointsGUI()  {
		System.out.println("Object " + this.toString() + " created.");
		initGUI();
	}

	// adds an item to the list.  If the list is already full, 
	// the oldest item is thrown out.
	public void addItem(POIdata p) {
		ourModel.Push(p);
	}
	
	// Does what it says; initialize the GUI and its components
	private void initGUI() {
			
		// Allocate components for the GUI's pages
		ImageIcon forward = createImageIcon("images/forward.png", "Button that advances to a screen with more information about this point of interest.");
		ImageIcon seen = createImageIcon("images/seen.png", "icon that represents a visible location.");
		ImageIcon notseen = createImageIcon("images/notseen.png", "icon that represents a hidden location.");
		ImageIcon forwardsm = createImageIcon("images/forwardsm.png", "small version of forward.png.");
		ImageIcon bulletpoint = createImageIcon("images/bulletpoint.png", "bullet point");
		ImageIcon back = createImageIcon("images/back.png", "Back button");
		
		mainFrame = new JFrame("Talking Points");
		mainContentPane = new JPanel(new BorderLayout());
		locationList = new JTable(5, 3);
		locationListB = new JTable(5, 3);
		ourModel = new locListModel(seen, notseen, bulletpoint);
		frontScroll = new JScrollPane(locationList);
		logoButton = new JButton(createImageIcon("images/logo.jpg", "TalkingPoints Logo"));
		viewAll = new JButton("View All");
		legend = new JLabel(createImageIcon("images/legend.png", "Legend for this page's icons."),
				javax.swing.SwingConstants.CENTER);
		tableTitle = new JLabel("<html><font size = 4><u>Detected Points of Interest</font></u></html>", javax.swing.SwingConstants.CENTER);
		centralPane = new JPanel(new CardLayout());
		locationTitle = new JLabel("Empty");
		coreInfo = new JEditorPane("text/html", "blahblahblah");
		viewingHistory = new Stack<String>();
		
		// Pre-calculate our desired component sizes to save time/computation
		int seenWidth = seen.getIconWidth() ;
		int seenHeight = seen.getIconHeight();
	
		int tableHeight = (seenHeight * 5) + tableTitle.getHeight() + 15;
		
		int legendWidth = legend.getIcon().getIconWidth();
		int legendHeight = legend.getIcon().getIconHeight();
		
		int logoWidth = logoButton.getIcon().getIconWidth();
		int logoHeight = logoButton.getIcon().getIconHeight();	

		int windowWidth = legend.getIcon().getIconWidth() + logoButton.getIcon().getIconWidth() + TOP_SPACER_X + COMP_SPACER_X + COMP_SPACER_X;
		int tallest = (legendHeight > logoHeight) ? legendHeight : logoHeight;
		int windowHeight = COMP_SPACER_Y + tallest + COMP_SPACER_Y + tableHeight + (COMP_SPACER_Y / 2) 
		+ VIEWALL_Y + COMP_SPACER_Y + 60;
		
		int infoFieldsWidth = windowWidth - seen.getIconWidth() - (COMP_SPACER_X * 2);
		
		// Configure main frame
		mainFrame.setResizable(false);
		mainFrame.setPreferredSize(new Dimension(windowWidth, windowHeight));
		
		// Configure table title
		tableTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		// Configure table's scroll bar
		frontScroll.setColumnHeader(new JViewport());
		frontScroll.getColumnHeader().setVisible(false);
		frontScroll.setPreferredSize(new Dimension((windowWidth - COMP_SPACER_X - COMP_SPACER_X), tableHeight));
		frontScroll.setMinimumSize(new Dimension((windowWidth - COMP_SPACER_X - COMP_SPACER_X), tableHeight));
		frontScroll.setMaximumSize(new Dimension((windowWidth - COMP_SPACER_X - COMP_SPACER_X), tableHeight));
		
		// Configure location list table
		ourModel.addTableModelListener(this);
		locationList.setModel(ourModel);
		locationList.getColumnModel().getColumn(0).setCellRenderer(new ButtonRenderer());
		locationList.getColumnModel().getColumn(0).setCellEditor(new ButtonEditor(new JCheckBox()));
		locationList.getColumnModel().getColumn(0).setPreferredWidth(seenWidth);
		locationList.getColumnModel().getColumn(0).setMinWidth(seenWidth);
		locationList.getColumnModel().getColumn(0).setMaxWidth(seenWidth);
		locationList.getColumnModel().getColumn(1).setPreferredWidth((int)Math.floor(infoFieldsWidth * 0.25));
		locationList.getColumnModel().getColumn(1).setMinWidth((int)Math.floor(infoFieldsWidth * 0.25));
		locationList.getColumnModel().getColumn(1).setMaxWidth((int)Math.floor(infoFieldsWidth * 0.25));
		locationList.getColumnModel().getColumn(2).setPreferredWidth((int)Math.ceil(infoFieldsWidth * 0.75) - 3);
		locationList.getColumnModel().getColumn(2).setMinWidth((int)Math.ceil(infoFieldsWidth * 0.75) - 3);
		locationList.getColumnModel().getColumn(2).setMaxWidth(infoFieldsWidth * 2);
		locationList.setBackground(new Color(COMP_BG_COLOR_R, COMP_BG_COLOR_G, COMP_BG_COLOR_B));
		locationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		locationList.setCellSelectionEnabled(true);
		locationList.setRowSelectionAllowed(false);
		locationList.setColumnSelectionAllowed(false);
		locationList.setRowHeight(seenHeight);
		locationList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		locationList.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		locationList.setFillsViewportHeight(true);
		locationList.setShowHorizontalLines(false);
		locationList.setShowVerticalLines(false);
		locationList.getTableHeader().setResizingAllowed(false);
		locationList.getTableHeader().setReorderingAllowed(false);
		locationList.getTableHeader().setVisible(false);
		// Add selection listener to table
		ListSelectionModel selectionModel = locationList.getSelectionModel();
		selectionModel.addListSelectionListener(this);
		
		// Configure Legend label
		legend.setPreferredSize(new Dimension(legendWidth, legendHeight));
		legend.setMinimumSize(new Dimension(legendWidth, legendHeight));
		legend.setMaximumSize(new Dimension(legendWidth, legendHeight));
		legend.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		legend.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		// Configure logo button
		logoButton.setPreferredSize(new Dimension(logoWidth, logoHeight));
		logoButton.setMinimumSize(new Dimension(logoWidth, logoHeight));
		logoButton.setMaximumSize(new Dimension(logoWidth, logoHeight));
		logoButton.setActionCommand("home");
		logoButton.addActionListener(this);

		// Configure View All button
		viewAll.setPreferredSize(new Dimension(VIEWALL_X, VIEWALL_Y));		
		viewAll.setAlignmentX(Component.RIGHT_ALIGNMENT);
		viewAll.setAlignmentY(Component.CENTER_ALIGNMENT);
		
		// Set up horizontal layer at top of window
		JPanel topButtons = new JPanel();
		topButtons.setBackground(new Color(BG_COLOR_R, BG_COLOR_G, BG_COLOR_B));
		topButtons.setLayout(new BoxLayout(topButtons, BoxLayout.LINE_AXIS));
		topButtons.setBorder(BorderFactory.createEmptyBorder(COMP_SPACER_X, 2, COMP_SPACER_Y, 2));
		topButtons.add(Box.createRigidArea(new Dimension(COMP_SPACER_X, COMP_SPACER_Y)));
		topButtons.add(legend);
		topButtons.add(Box.createGlue());
		topButtons.add(logoButton);
		topButtons.add(Box.createRigidArea(new Dimension(COMP_SPACER_X, COMP_SPACER_Y)));
		
		// Set up vertical layer consisting of title + scroll pane + location list table
		JPanel middlePane = new JPanel();
		middlePane.setBackground(new Color(BG_COLOR_R, BG_COLOR_G, BG_COLOR_B));
		middlePane.setLayout(new BoxLayout(middlePane, BoxLayout.Y_AXIS));
		middlePane.add(tableTitle);
		middlePane.add(Box.createRigidArea(new Dimension(COMP_SPACER_X, (COMP_SPACER_Y / 2))));
		middlePane.add(frontScroll);
		middlePane.add(Box.createRigidArea(new Dimension(COMP_SPACER_X, (COMP_SPACER_Y / 2))));
		middlePane.add(viewAll);
		middlePane.add(Box.createRigidArea(new Dimension(COMP_SPACER_X, COMP_SPACER_Y)));
		
		/// Set up final horizontal layer of only the view all button
		JPanel bottomButton = new JPanel();
		bottomButton.setBackground(new Color(BG_COLOR_R, BG_COLOR_G, BG_COLOR_B));
		bottomButton.setLayout(new BoxLayout(bottomButton, BoxLayout.LINE_AXIS));
		bottomButton.add(Box.createGlue());
		bottomButton.add(viewAll);
		bottomButton.add(Box.createRigidArea(new Dimension(COMP_SPACER_X, COMP_SPACER_Y))); 
		middlePane.add(bottomButton);
	
		centralPane.add(middlePane, MAINPANE);
		
		// CONFIGURE MORE INFORMATION PANEL
		// Set up More Information pane
		JPanel moreInfo = new JPanel();
		JPanel recentlyPassedPanel = new JPanel();
		
		// Configure title 
		JPanel title = new JPanel();
		locationTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
		title.setBackground(new Color(COMP_BG_COLOR_R,COMP_BG_COLOR_G,COMP_BG_COLOR_B));
		title.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
		title.add(locationTitle, BorderLayout.PAGE_START);
		title.setAlignmentX(Component.LEFT_ALIGNMENT);
//		title.setPreferredSize(new Dimension(400, 35));
//		title.setMinimumSize(new Dimension(400, 35));
//		title.setMaximumSize(new Dimension(400, 35));
		
		// Configure Core Information text pane
		coreInfo.setBackground(new Color(COMP_BG_COLOR_R, COMP_BG_COLOR_G, COMP_BG_COLOR_B));
		coreInfo.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
		coreInfo.setEditable(false);
		coreInfo.setMargin(new Insets(5, 5, 5, 5));
		infoScroll = new JScrollPane(coreInfo);
		infoScroll.setColumnHeader(null);
		
		// Configure radio buttons for more info menu
		menu = new JRadioButton("Menu", forwardsm);
		hours = new JRadioButton("Hours", forwardsm);
		history = new JRadioButton("History", forwardsm);
		comments = new JRadioButton("Comments", forwardsm);
		specials = new JRadioButton("Contact", forwardsm);
		accessibility = new JRadioButton("Accessibility", forwardsm);
		menu.setActionCommand("menu");
		hours.setActionCommand("hours");
		history.setActionCommand("history");
		comments.setActionCommand("comments");
		specials.setActionCommand("specials");
		accessibility.setActionCommand("accessibility");
		menu.addActionListener(this);
		hours.addActionListener(this);
		history.addActionListener(this);
		comments.addActionListener(this);
		specials.addActionListener(this);
		accessibility.addActionListener(this);
		
		// Configure panel of radio buttons
		moreInfoMenu = new JPanel();
		moreInfoMenu.setBackground(new Color(COMP_BG_COLOR_R, COMP_BG_COLOR_G, COMP_BG_COLOR_B));
		moreInfoMenu.setLayout(new BoxLayout(moreInfoMenu, BoxLayout.Y_AXIS));
		moreInfoMenu.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
		moreInfoMenu.setBackground(new Color(237,241,238));
		moreInfoMenu.add(menu);
		moreInfoMenu.add(hours);
		moreInfoMenu.add(history);
		moreInfoMenu.add(accessibility);
		moreInfoMenu.add(specials);
		moreInfoMenu.add(comments);
		
		// Configure recentlypassed label
		JLabel recentlyPassed = new JLabel("<html><font size = 4>Points You Have <u>Recently Passed</u></font></html>");
//		recentlyPassed.setBackground(new Color(COMP_BG_COLOR_R,COMP_BG_COLOR_G,COMP_BG_COLOR_B));
//		recentlyPassed.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
		recentlyPassed.setVerticalAlignment(SwingConstants.NORTH);
		recentlyPassed.setAlignmentX(Component.CENTER_ALIGNMENT);
//		recentlyPassed.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		recentlyPassed.setPreferredSize(new Dimension(120, 50));
		recentlyPassed.setMinimumSize(new Dimension(120, 50));
		recentlyPassed.setMaximumSize(new Dimension(120, 50));
		
		recentlyPassedPanel.setBackground(new Color(COMP_BG_COLOR_R,COMP_BG_COLOR_G,COMP_BG_COLOR_B));
		recentlyPassedPanel.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
		recentlyPassedPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		recentlyPassedPanel.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		recentlyPassedPanel.setPreferredSize(new Dimension(125, 50));
		recentlyPassedPanel.setMinimumSize(new Dimension(150, 50));
		recentlyPassedPanel.setMaximumSize(new Dimension(150, 50));
		recentlyPassedPanel.add(recentlyPassed);
		
		// Configure location list table B
		locationListB.setModel(ourModel);
		locationListB.getColumnModel().getColumn(0).setCellRenderer(new ButtonRenderer());
		locationListB.getColumnModel().getColumn(0).setPreferredWidth(12);
		locationListB.getColumnModel().getColumn(0).setMinWidth(12);
		locationListB.getColumnModel().getColumn(0).setMaxWidth(12);
		locationListB.getColumnModel().getColumn(1).setPreferredWidth((int)Math.floor(infoFieldsWidth * 0.2));
		locationListB.getColumnModel().getColumn(1).setMinWidth((int)Math.floor(infoFieldsWidth * 0.2));
		locationListB.getColumnModel().getColumn(1).setMaxWidth((int)Math.floor(infoFieldsWidth * 0.2));
		locationListB.getColumnModel().getColumn(2).setPreferredWidth(0);
		locationListB.getColumnModel().getColumn(2).setMinWidth(0);
		locationListB.getColumnModel().getColumn(2).setMaxWidth(0);
		locationListB.setBackground(new Color(COMP_BG_COLOR_R, COMP_BG_COLOR_G, COMP_BG_COLOR_B));
		locationListB.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		locationListB.setRowSelectionAllowed(false);
		locationListB.setColumnSelectionAllowed(false);
		locationListB.setRowHeight(14);
		locationListB.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		locationListB.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		locationListB.setFillsViewportHeight(true);
		locationListB.setShowHorizontalLines(false);
		locationListB.setShowVerticalLines(false);
		locationListB.getTableHeader().setResizingAllowed(false);
		locationListB.getTableHeader().setReorderingAllowed(false);
		locationListB.getTableHeader().setVisible(false); 
		
		// Configure back button
		JButton goBack = new JButton(back);
		goBack.setActionCommand("back");
		goBack.setContentAreaFilled(false);
		goBack.setForeground(new Color(COMP_BG_COLOR_R, COMP_BG_COLOR_G, COMP_BG_COLOR_B));
		goBack.setBorder(BorderFactory.createEmptyBorder());
		goBack.addActionListener(this);
		
		// Add components to More Info pane
		moreInfo.setLayout(new GridBagLayout());
		moreInfo.setBackground(new Color(BG_COLOR_R,BG_COLOR_G,BG_COLOR_B));
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 6;
		c.gridheight = 1;
		c.weightx = 0.65;
		c.weighty = 0.65;
		c.insets = new Insets(0, COMP_SPACER_X, 0, COMP_SPACER_X);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		moreInfo.add(title, c);
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		c.gridheight = 4;
		c.weightx = 1;
		c.weighty = 0.75;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(COMP_SPACER_Y, COMP_SPACER_X, COMP_SPACER_Y, COMP_SPACER_X);
		moreInfo.add(moreInfoMenu, c);
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 1;
		c.gridwidth = 4;
		c.gridheight = 5;
		c.weightx = 0.75;
		c.weighty = 0.75;
		c.insets = new Insets(0, 0, COMP_SPACER_Y, COMP_SPACER_X);
		c.fill = GridBagConstraints.BOTH;
		moreInfo.add(infoScroll, c);
		c = new GridBagConstraints();
		c.gridx = 6;
		c.gridy = 0;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.weightx = 0.15;
		c.weighty = 0.15;
		c.insets = new Insets(0, 0, 0, COMP_SPACER_X);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.FIRST_LINE_END;
		moreInfo.add(recentlyPassedPanel, c);
		c = new GridBagConstraints();
		c.gridx = 6;
		c.gridy = 1;
		c.gridwidth = 2;
		c.gridheight = 3;
		c.weightx = 0.75;
		c.weighty = 0.75;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 0, 0, COMP_SPACER_X);
		moreInfo.add(locationListB, c);
		c = new GridBagConstraints();
		c.gridx = 7;
		c.gridy = 4;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.LAST_LINE_END;
		c.weightx = 0.75;
		c.weighty = 0.75;
		c.insets = new Insets(0, 0, COMP_SPACER_Y, COMP_SPACER_X);
		moreInfo.add(goBack, c);
		centralPane.add(moreInfo, MOREINFO);
	
		// Register listener for central pane property change events
		centralPane.addPropertyChangeListener(this);
		
		// Set GUI's state to MAINPANE
		currentState = MAINPANE;
		
		// Add components to main content pane
		mainContentPane.add(topButtons, BorderLayout.PAGE_START);
		mainContentPane.add(centralPane, BorderLayout.CENTER);
		mainFrame.setContentPane(mainContentPane);
		mainFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		
		mainFrame.pack();
		mainFrame.setVisible(true);
	}
	
	// Creates an icon from an image, or returns null if image cannot be found.
	protected ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = getClass().getResource(path);
		if(imgURL != null)
			return new ImageIcon(imgURL, description);
		else {
			System.out.println("Couldn't load file " + path);
			return null;
		}
	}
	
	// Property change listener for central pane of GUI
	// Checks currentState and configures the central pane accordingly
	// All state switching is handled in this method.
	public void propertyChange(PropertyChangeEvent e) {
		String newstate = e.getPropertyName();
		System.out.println("Propertychange event received:" + newstate);
		
		CardLayout cl = (CardLayout)centralPane.getLayout();
		locListModel model = (locListModel)locationList.getModel();
		String s;
		
		// Changing to Main Pane
		if(newstate.compareTo(MAINPANE) == 0) {
			cl.show(centralPane, MAINPANE);
			model.tableState = MAINPANE;
			currentState = MAINPANE;
		}
		// Changing to More Info pane
		else if(newstate.compareTo(MOREINFO) == 0) {
			locationTitle.setText("<html><font size = 5><b>" + cachedData.name() + "</font><font size = 5 color = #B04C1B> [" 
						+ cachedData.description() + "]</font></b></html>");
				s = createCoreInfoString(cachedData);
				configureMenuPanel();
				coreInfo.setText(s);
				cl.show(centralPane, MOREINFO);
				model.tableState = MOREINFO;
				currentState = newstate;
		}
		// Switch central info pane to Menu info for current POI
		else if(newstate.compareTo(MENU) == 0) {
			s = createMenuString(cachedData);
			coreInfo.setText(s);
			model.tableState = MENU;
			currentState = MENU;
		}
		else if(newstate.compareTo(HOURS) == 0) {
			s = createHoursString(cachedData);
			coreInfo.setText(s);
			model.tableState = HOURS;
			currentState = HOURS;
		}
	else if(newstate.compareTo(HISTORY) == 0) {
			s = createHistoryString(cachedData);
			coreInfo.setText(s);
			model.tableState = HISTORY;
			currentState = HISTORY;
		} 
		else if(newstate.compareTo(ACCESSIBILITY) == 0) {
			s = createAccessString(cachedData);
			coreInfo.setText(s);
			model.tableState = ACCESSIBILITY;
			currentState = ACCESSIBILITY;
		} 
		else if(newstate.compareTo(SPECIALS) == 0) {
			s = createSpecialsString(cachedData);
			coreInfo.setText(s);
			model.tableState = SPECIALS;
			currentState = SPECIALS;
		} 
		else if(newstate.compareTo(COMMENTS) == 0) {
			s = createCommentsString(cachedData);
			coreInfo.setText(s);
			model.tableState = COMMENTS;
			currentState = COMMENTS;
		} 
		else
			System.out.println("Property change message contained an invalid state:" + newstate);
		
	}
	
	// Listener for the table model that is assigned to the main pane table
	// This is used to detect when the button in column 0 is "edited", ie clicked.
	public void tableChanged(TableModelEvent e) {
			
		if(e.getColumn() == 123) {
			System.out.println("Source: " + e.getSource());
			int row = e.getFirstRow();
			locListModel model = (locListModel)locationList.getModel();
				if(model.getValueAt(row, 1) != null) {
	//			locationName.setText((String)model.getValueAt(row, 1));
				CardLayout cl = (CardLayout)centralPane.getLayout();
				cl.show(centralPane, MOREINFO);
			}
			
		}
	}
		
		
	/* Listener for user selection of a table cell.  Always sends user to More Information screen.
	   For some reason, getFirstIndex() and getLastIndex() are inconsistent in the values they return when selecting rows,
	   so we need to find the row selected by converting the output of getSource() to a string, and then
	   the character after the first '{' to int, before subtracting the unicode integer value of '0'.  */
	public void valueChanged(ListSelectionEvent e) {
		locListModel model = (locListModel)locationList.getModel();
		
		if(!(e.getValueIsAdjusting())) {
			String eventString = e.getSource().toString();
			int index = eventString.lastIndexOf('{');
			index++;
			if(eventString.charAt(index) == '}')
				return;
			int row = (int)eventString.charAt(index) - (int)'0';
			System.out.println("Row " + row + " selected. ");
			if(model.getValueAt(row, 1) != null) {
				cachedData = (POIdata)model.getValueAt(row, 3);
				viewingHistory.push(currentState);
				locationList.clearSelection();
				centralPane.firePropertyChange(MOREINFO, true, false);
			}	
				
		}
			
	}
		
	// Listener for TalkingPointsGUI buttons
	public void actionPerformed(ActionEvent e) {
		
		locListModel model = (locListModel)locationList.getModel();
		StringBuffer sb = new StringBuffer();
		
		if(e.getActionCommand() == "home")  {
			viewingHistory.clear();
			centralPane.firePropertyChange(MAINPANE, true, false);
			}	
		
		if(e.getActionCommand() == "back") {
			if(!(viewingHistory.empty())) {
				String s = new String(viewingHistory.pop());
				centralPane.firePropertyChange(s, true, false);
			}
		}
		if(e.getActionCommand() == "menu")  {
			viewingHistory.push(currentState);
			centralPane.firePropertyChange(MENU, true, false);
		}
		if(e.getActionCommand() == "hours")  {
			viewingHistory.push(currentState);
			centralPane.firePropertyChange(HOURS, true, false);
		}
		if(e.getActionCommand() == "history")  {
			viewingHistory.push(currentState);
			centralPane.firePropertyChange(HISTORY, true, false);
		}
		if(e.getActionCommand() == "accessibility")  {
			viewingHistory.push(currentState);
			centralPane.firePropertyChange(ACCESSIBILITY, true, false);
		}
		if(e.getActionCommand() == "specials")  {
			viewingHistory.push(currentState);
			centralPane.firePropertyChange(SPECIALS, true, false);
		}
		if(e.getActionCommand() == "comments")  {
			viewingHistory.push(currentState);
			centralPane.firePropertyChange(COMMENTS, true, false);
		}
		
	}
	
	// Configures menu panel for More Information screens
	private void configureMenuPanel() {
		moreInfoMenu.removeAll();
		if(cachedData.getMenu() != null)
			moreInfoMenu.add(menu);
		if(cachedData.hours_array() != null) 
			moreInfoMenu.add(hours);
	//	if(cachedData.getHistory()) != null
		 // moreInfoMenu.add(history);
		if(cachedData.getAccess() != null)
			moreInfoMenu.add(accessibility);
		if(cachedData.getSpecials() != null)
			moreInfoMenu.add(specials);
		if(cachedData.comments() != null)
			moreInfoMenu.add(comments);
	}
	
	/**
	 * @param args is unused
	 */   /*
	public static void main(String[] args) throws InterruptedException {
		TalkingPointsGUI ourGUI = new TalkingPointsGUI();
		
		String [] s = {"1-3 MWF", "2-5 Sat", "3-5 Sun", " ", " ", " ", " "};
		
		ourGUI.addItem(new POIdata("Stucchi's", "Ice Cream Parlour", "empty", "stuff", "words", "bleh", "duder", "blah", "schmelding", "etc", s));

	} */

	/* Custom table model for locationList.
	 * Implements the table data as a sort of ersatz-queue, 
	 * where random access is possible.
	 */
	class locListModel extends AbstractTableModel {
			
		locListModel(ImageIcon seen_t, ImageIcon notseen_t, ImageIcon bulletpoint_t) {
			data = new POIdata[10];
			seen = seen_t;
			notseen = notseen_t;
			bulletpoint = bulletpoint_t;
			tableState = new String(MAINPANE);
		}
		
		// Required method getRowCount()
		public int getRowCount() {
			return 5;
		}
		
		// Required method getColumnCount()
		public int getColumnCount() {
			return 3;
		}
		
		// Cells in column 0 must be "editable" in order to be clickable; other cells are not
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if(columnIndex == 0)
				if(tableState.compareTo(MAINPANE) == 0)
					return true;
		
			return false;
	
		}
		
		public String getColumnName(int col) {
			if(col == 0)
				return("Icon");
			if(col == 1)
				return("Location");
			if(col == 2)
				return("Description");
			else
				return("We should never get here.");
		}
			
		// Required method getValueAt()
		// If column is 3, returns entire POIdata object.
		// TODO: Alter to filter out hidden locations
		public Object getValueAt(int row, int column) {
			
			if(data[row] == null)
				return null;
			
			switch(column) {
			case(0):
				if(tableState.compareTo(MAINPANE) == 0)
					return seen;
				else
					return bulletpoint;
			case(1):
				return data[row].name();
			case(2):
				return data[row].location_type();
			case(3):
				return data[row];
			default:
				return null;     }
		
		}
		
		// Push a new data entry onto the list.  The
		// current last element on the list is thrown away.
		public void Push(POIdata p) {
			System.out.println("Trying push onto location queue.");
			
			for(int i = 9 ; i > 0 ; i--) {
				if(data[i-1] != null)
					data[i] = data[i-1];
				else
					System.out.println("object #" + i + " is unallocated.");
			}
		/* does this work instead of creating a new POIdata object every time*/ 
		data[0] = p;
		//data[0] = new POIdata(p.name(), p.location_type(), p.description()); ;
		}
		
		
		// variable definitions
		private POIdata[] data;
		private ImageIcon bulletpoint;
		private ImageIcon seen;
		private ImageIcon notseen;
		public String tableState;
	} 
	
	// Custom renderer for the button fields that will appear in the tables
	class ButtonRenderer extends JButton implements TableCellRenderer {
		
		ButtonRenderer() {
			setBorderPainted(false);
			setContentAreaFilled(false);
			setOpaque(true);
			setEnabled(false);
			setBackground(new Color(255, 255, 255));
			setForeground(new Color(255, 255, 255));
		}
		
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
				boolean hasFocus, int row, int column) {
								
			if(value != null) {
				if(value.getClass().getName() == "javax.swing.ImageIcon") {
					setIcon((ImageIcon)value);
					setEnabled(true);
					}
			}
			else {
				setIcon(null);
				setEnabled(false);
			}
			return this;
			
		}
	}
	
	// Custom editor that allows buttons in table to be clicked
	class ButtonEditor extends DefaultCellEditor
					   implements ActionListener {
		
		ButtonEditor(JCheckBox checkBox) {
			super(checkBox);
			System.out.println(this + "created.");
			button = new JButton();
			button.setOpaque(true);
			button.addActionListener(this);
		}
		
		public void actionPerformed(ActionEvent e) {
			fireEditingStopped();
		}
		
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			isPushed = true;
			locListModel model = (locListModel)table.getModel();
			System.out.println(value);
			model.fireTableChanged(new TableModelEvent(model, row, row, 123));
			return button;
		}
		
		public Object GetCellEditorValue() {
			if(isPushed)
				System.out.println("Button pushed.");
			isPushed = false;
			return new String("Empty");
		}
		
		public boolean StopCellEditing() {
			System.out.println("Editing stopped.");
			isPushed = false;
			return super.stopCellEditing();
		}
		
		protected void fireEditingStopped() {
			super.fireEditingStopped();
		}
		
		// Variable declarations
		protected JButton button;
		private boolean isPushed;
	}
		
	// Fills a stringbuffer with the necessary text for the Core Info pane, then returns it as a string.
	private String createCoreInfoString(POIdata p) {
		StringBuffer sb = new StringBuffer();
		sb.append("<font size = 5>&nbsp;Core Information</font><hr><pre>");
		sb.append(" Name:          " + p.name() + " <br>");
		sb.append(" Location Type: " + p.location_type() + " <br>");
		sb.append(" Description:   " + p.description() + " <br>");
		sb.append(" Address:       " + p.street() + " <br>");
		sb.append("                " + p.city() + " " + p.state() + " " + p.postalCode() + " <br>");
		sb.append(" Phone:         " + p.phone() + " <br>");
		sb.append(" URL:           <a href=" + p.url() + ">" + p.url() + "</a> </pre>");
		
		String s = new String(sb);
		return s;
	}
	
	// Stringbuffer-filling methods for each of the possible More Info menu items.
	// This is ugly, but it gets the job done (presumably).  May want to streamline later.
	// They all assume that <br>'s are placed in the proper places in the source strings to keep the horizontal
	// scroll bar from appearing.
	
	// Fills a stringbuffer with the necessary text for a Menu pane about our current POI, then returns it as a string.
	private String createMenuString(POIdata p) {
		StringBuffer sb = new StringBuffer();
		sb.append("<font size = 5>&nbsp;Menu</font><hr><pre>");
		sb.append("&nbsp;" + p.getMenu() + "</pre>");
		return(new String(sb));
		
	} 
	
	// Fills a stringbuffer with the necessary text for a Hours pane about our current POI, then returns it as a string.
	// If hours_array is ever changed to have more or less entries, we need to alter the for loop.
	private String createHoursString(POIdata p) {
		StringBuffer sb = new StringBuffer();
		sb.append("<font size = 5>&nbsp;Hours</font><hr><pre>");
		String [] s = p.hours_array();
		for(int i = 0 ; i < 7 ; i++)
			sb.append("&nbsp;" + s[i] + "<br>");
		sb.append("</pre>");
		return(new String(sb));
		
	} 
	
	// Fills a stringbuffer with the necessary text for a History pane about our current POI, then returns it as a string.
	// TODO: Change p.getMenu() to whatever returns the History string when it gets implemented.
	private String createHistoryString(POIdata p) {
		StringBuffer sb = new StringBuffer();
		sb.append("<font size = 5>&nbsp;History</font><hr><pre>");
		sb.append("&nbsp;" + p.getMenu() + "</pre>");
		return(new String(sb));
	}
 	
	// Fills a stringbuffer with the necessary text for a Accessibility pane about our current POI, then returns it as a string.
	private String createAccessString(POIdata p) {
		StringBuffer sb = new StringBuffer();
		sb.append("<font size = 5>&nbsp;Accessibility Information</font><hr><pre>");
		sb.append("&nbsp;" + p.getAccess() + "</pre>");
		return(new String(sb));
		
	} 
	
	// Fills a stringbuffer with the necessary text for a Specials pane about our current POI, then returns it as a string.
	private String createSpecialsString(POIdata p) {
		StringBuffer sb = new StringBuffer();
		sb.append("<font size = 5>&nbsp;Specials</font><hr><pre>");
		sb.append("&nbsp;" + p.getSpecials() + "</pre>");
		return(new String(sb));
		
	} 
	
	// Fills a stringbuffer with the necessary text for a Comments pane about our current POI, then returns it as a string.
	private String createCommentsString(POIdata p) {
		StringBuffer sb = new StringBuffer();
		sb.append("<font size = 5>&nbsp;User Comments</font><hr><pre>");
		sb.append("&nbsp;" + p.comments() + "</pre>");
		return(new String(sb));
		
	} 
	
	// Variable definitions
	private locListModel ourModel;
	private JFrame mainFrame;
	private JPanel mainContentPane;
	private JPanel centralPane;
	private JScrollPane frontScroll;
	private JScrollPane infoScroll;
	private JTable locationList;
	private JTable locationListB;
	private JButton logoButton;
	private JButton viewAll;
	private JLabel legend;
	private JLabel tableTitle;
	private JLabel locationTitle;
	private JEditorPane coreInfo;
	private Stack<String> viewingHistory;
	private JPanel moreInfoMenu;
	private JRadioButton menu;
	private JRadioButton hours;
	private JRadioButton history;
	private JRadioButton comments;
	private JRadioButton specials;
	private JRadioButton accessibility;
	// A copy of the POIdata currently being viewed
	private POIdata cachedData;
	// String that describes GUI's current state
	private String currentState;
	
}
