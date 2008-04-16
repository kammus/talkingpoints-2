/* TODO: Put some sort of documentation/description up here
 * 
 */

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import com.centerkey.utils.BareBonesBrowserLaunch;

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
	static final int MOREINFO_CHAR_WIDTH = 54;
	static final String MAINPANE = "Front";
	static final String MOREINFO = "More information";
	static final String COMMENTS = "POI's comments";
	static final String VIEWALL = "View all";
	static final String SHOWHIDDEN = "Show hidden";
	
	// Default constructor
	TalkingPointsGUI()  {
		System.out.println("Object " + this.toString() + " created.");
		initGUI();
	}

	// adds an item to the list.  If the list is already full, 
	// the oldest item is thrown out.
	public void addItem(POIdata p) {
		// Scan the current table contents to see if this POI is already on it.
		locListModel model = (locListModel)locationList.getModel();
		if(model.isInTable(p.getTpid()))
			System.out.println(p.name() + " already exists in table.  Throwing it out.");
		else
			ourModel.addToTable(p);
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
		goBackb = new JButton(back);
		legend = new JLabel(createImageIcon("images/legend.png", "Legend for this page's icons."),
				javax.swing.SwingConstants.CENTER);
		tableTitle = new JLabel("<html><font size = 4><u>Recently Detected Points of Interest</font></u></html>", javax.swing.SwingConstants.CENTER);
		centralPane = new JPanel(new CardLayout());
		locationTitle = new JLabel("Empty");
		coreInfo = new JEditorPane("text/html", "blahblahblah");
		viewingHistory = new Stack<String>();
		menuItems = new Vector<JRadioButton>();
		
		// Pre-calculate our desired component sizes to save time/computation
		int seenWidth = seen.getIconWidth() ;
		int seenHeight = seen.getIconHeight();
	
		int tableHeight = (seenHeight * 5) + tableTitle.getHeight() + 15;
		
		int legendWidth = legend.getIcon().getIconWidth();
		int legendHeight = legend.getIcon().getIconHeight();
		
		int logoWidth = logoButton.getIcon().getIconWidth();
		int logoHeight = logoButton.getIcon().getIconHeight();	

		int windowWidth = 800; // legend.getIcon().getIconWidth() + logoButton.getIcon().getIconWidth() + TOP_SPACER_X + COMP_SPACER_X + COMP_SPACER_X;
		int tallest = (legendHeight > logoHeight) ? legendHeight : logoHeight;
		int windowHeight = 480; //COMP_SPACER_Y + tallest + COMP_SPACER_Y + tableHeight + (COMP_SPACER_Y / 2) 
//		+ VIEWALL_Y + COMP_SPACER_Y + 60;
		
		int infoFieldsWidth = windowWidth - seen.getIconWidth() - (COMP_SPACER_X * 2);
		
		// Configure main frame
		mainFrame.setResizable(true);
		mainFrame.setPreferredSize(new Dimension(windowWidth, windowHeight));
		
		// Configure table title
		tableTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		// Configure table's scroll bar
		frontScroll.setColumnHeader(new JViewport());
		frontScroll.getColumnHeader().setVisible(false);
		frontScroll.setPreferredSize(new Dimension((windowWidth - COMP_SPACER_X - COMP_SPACER_X), tableHeight+20));
		frontScroll.setMinimumSize(new Dimension((windowWidth - COMP_SPACER_X - COMP_SPACER_X), tableHeight+20));
		frontScroll.setMaximumSize(new Dimension((windowWidth - COMP_SPACER_X - COMP_SPACER_X), tableHeight+20));
		
		// Configure location list table
		ourModel.addTableModelListener(this);
		locationList.setModel(ourModel);
		locationList.getColumnModel().getColumn(0).setCellRenderer(new ButtonRenderer());
		locationList.getColumnModel().getColumn(0).setCellEditor(new ButtonEditor(new JCheckBox()));
		locationList.getColumnModel().getColumn(0).setPreferredWidth(seenWidth+2);
		locationList.getColumnModel().getColumn(0).setMinWidth(seenWidth+2);
		locationList.getColumnModel().getColumn(0).setMaxWidth(seenWidth+2);
		locationList.getColumnModel().getColumn(1).setPreferredWidth((int)Math.floor(infoFieldsWidth * 0.25));
		locationList.getColumnModel().getColumn(1).setMinWidth((int)Math.floor(infoFieldsWidth * 0.25));
		locationList.getColumnModel().getColumn(1).setMaxWidth((int)Math.floor(infoFieldsWidth * 0.25));
		locationList.getColumnModel().getColumn(2).setPreferredWidth((int)Math.ceil(infoFieldsWidth * 0.75) - 5);
		locationList.getColumnModel().getColumn(2).setMinWidth((int)Math.ceil(infoFieldsWidth * 0.75) - 5);
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
		viewAll.setActionCommand("viewall");
		viewAll.addActionListener(this);
		
		// Configure back button for Show All and View Hidden screens
		goBackb.setActionCommand("back");
		goBackb.setContentAreaFilled(false);
		goBackb.setForeground(new Color(COMP_BG_COLOR_R, COMP_BG_COLOR_G, COMP_BG_COLOR_B));
		goBackb.setBorder(BorderFactory.createEmptyBorder());
		goBackb.setVisible(false);
		goBackb.addActionListener(this);
		
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
		bottomButton.add(goBackb);
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
		title.setPreferredSize(new Dimension(585, 40));
		title.setMinimumSize(new Dimension(585, 40));
		
		// Configure Core Information text pane
		coreInfo.setBackground(new Color(COMP_BG_COLOR_R, COMP_BG_COLOR_G, COMP_BG_COLOR_B));
		coreInfo.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
		coreInfo.setEditable(false);
		coreInfo.setMargin(new Insets(5, 5, 5, 5));
		
		infoScroll = new JScrollPane(coreInfo);
		infoScroll.setColumnHeader(null);
		infoScroll.setMinimumSize(new Dimension(420, 230));
		infoScroll.setPreferredSize(new Dimension(420, 230));
		
		// Configure radio buttons for more info menu
		core = new JRadioButton("Home", forwardsm);
		comments = new JRadioButton("Comments", forwardsm);
		core.setActionCommand("lochome");
		comments.setActionCommand("comments");
		core.addActionListener(this);
		comments.addActionListener(this);
		
		// Configure panel of radio buttons
		moreInfoMenu = new JPanel();
		
		moreInfoMenu.setBackground(new Color(COMP_BG_COLOR_R, COMP_BG_COLOR_G, COMP_BG_COLOR_B));
		moreInfoMenu.setLayout(new BoxLayout(moreInfoMenu, BoxLayout.Y_AXIS));
		moreInfoMenu.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
		moreInfoMenu.setBackground(new Color(237,241,238));
		moreInfoMenu.setMinimumSize(new Dimension(110,255));
		moreInfoMenu.setPreferredSize(new Dimension(110,255));
		buttonScroll = new JScrollPane(moreInfoMenu);
		buttonScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		buttonScroll.setMinimumSize(new Dimension(110,255));
		buttonScroll.setPreferredSize(new Dimension(110,255));
		
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
		recentlyPassedPanel.setPreferredSize(new Dimension(135, 50));
		recentlyPassedPanel.setMinimumSize(new Dimension(135, 50));
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
		locationListB.setPreferredSize(new Dimension(135,140));
		locationListB.setMinimumSize(new Dimension(135,140));
		locationListB.getTableHeader().setResizingAllowed(false);
		locationListB.getTableHeader().setReorderingAllowed(false);
		locationListB.getTableHeader().setVisible(false); 
		
		ListSelectionModel selectionModelB = locationListB.getSelectionModel();
		selectionModelB.addListSelectionListener(this);
		
		// Configure back button
		JButton goBack = new JButton(back);
		goBack.setActionCommand("home");
		goBack.setContentAreaFilled(false);
		goBack.setForeground(new Color(COMP_BG_COLOR_R, COMP_BG_COLOR_G, COMP_BG_COLOR_B));
		goBack.setBorder(BorderFactory.createEmptyBorder());
		goBack.addActionListener(this);
		
		// Configure Contribute button
		JButton contribute = new JButton("Contribute");
		contribute.setActionCommand("contribute");
		contribute.addActionListener(this);
		
		// Add components to More Info pane
		moreInfo.setLayout(new GridBagLayout());
		moreInfo.setBackground(new Color(BG_COLOR_R,BG_COLOR_G,BG_COLOR_B));
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 6;
		c.gridheight = 1;
		c.insets = new Insets(0, COMP_SPACER_X, 0, COMP_SPACER_X);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		moreInfo.add(title, c);
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		c.gridheight = 4;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(COMP_SPACER_Y, COMP_SPACER_X, COMP_SPACER_Y, COMP_SPACER_X);
		moreInfo.add(buttonScroll, c);
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 1;
		c.gridwidth = 4;
		c.gridheight = 5;
		c.insets = new Insets(COMP_SPACER_Y, 0, COMP_SPACER_Y, COMP_SPACER_X);
		c.fill = GridBagConstraints.BOTH;
		moreInfo.add(infoScroll, c);
		c = new GridBagConstraints();
		c.gridx = 6;
		c.gridy = 0;
		c.gridwidth = 2;
		c.gridheight = 2;
		c.insets = new Insets(0, 0, COMP_SPACER_Y, COMP_SPACER_X);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.FIRST_LINE_END;
		moreInfo.add(recentlyPassedPanel, c);
		c = new GridBagConstraints();
		c.gridx = 6;
		c.gridy = 2;
		c.gridwidth = 2;
		c.gridheight = 2;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 0, 0, COMP_SPACER_X);
		moreInfo.add(locationListB, c);
		c = new GridBagConstraints();
		c.gridx = 7;
		c.gridy = 4;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.LAST_LINE_END;
		c.insets = new Insets(0, 0, COMP_SPACER_Y, COMP_SPACER_X);
		moreInfo.add(goBack, c);
		c = new GridBagConstraints();
		c.gridx = 6;
		c.gridy = 4;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.LAST_LINE_END;
		c.insets = new Insets(0, 0, COMP_SPACER_Y+4, COMP_SPACER_X);
		moreInfo.add(contribute, c); 
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
		
		ButtonEditor editor = (ButtonEditor)locationList.getCellEditor(0, 0);
		
		CardLayout cl = (CardLayout)centralPane.getLayout();
		locListModel model = (locListModel)locationList.getModel();
		String s;
		
			// Changing to Main Pane
		if(newstate.compareTo(MAINPANE) == 0) {
			cl.show(centralPane, MAINPANE);
			model.tableState = MAINPANE;
			tableTitle.setText("<html><font size = 4><u>Recently Detected Points of Interest</font></u></html>");
			currentState = MAINPANE;
			viewAll.setVisible(true);
			viewAll.setText("View All");
			viewAll.setActionCommand("viewall");
			viewAll.setPreferredSize(new Dimension(VIEWALL_X, VIEWALL_Y));
			goBackb.setVisible(false);
			commentsString = null;
			model.fireTableDataChanged();
		}
		// Changing to More Info pane
		else if(newstate.compareTo(MOREINFO) == 0) {
			locationTitle.setText("<html><font size = 5><b>" + cachedData.name() + "</font><font size = 5 color = #B04C1B> [" 
						+ cachedData.location_type() + "]</font></b></html>");
				s = createCoreInfoString(cachedData);
				String prevstate = viewingHistory.peek();
				System.out.println("Previous state was: " +  prevstate);
				if(prevstate.compareTo(MAINPANE) == 0)
					configureMenuPanel();
				coreInfo.setText(s);
				cl.show(centralPane, MOREINFO);
				model.tableState = MOREINFO;
				currentState = newstate;
		}
		if(currentState.compareTo(newstate) == 0)
			return;
		else if(newstate.compareTo(COMMENTS) == 0) {
			Hashtable<String,Object> hash = cachedData.getComment();
			StringBuffer sb = new StringBuffer();
			
			if(commentsString == null) {
				if(hash != null) 
					sb = createCommentsString(hash);
			
				sb.insert(0, "<font size = 5>&nbsp;Comments</font><hr><pre>");
				commentsString = new String(sb);
			}
			
			coreInfo.setText(commentsString);
			model.tableState = COMMENTS;
			currentState = COMMENTS;
		} 
		else if(newstate.compareTo(VIEWALL) == 0) {
			model.tableState = VIEWALL;
			currentState = VIEWALL;
			tableTitle.setText("<html><font size = 4><u>All Locations</u></font></html>");
			viewAll.setVisible(true);
			viewAll.setText("Show Hidden Locations");
			viewAll.setActionCommand("showhidden");
			viewAll.setPreferredSize(new Dimension((int)Math.floor(VIEWALL_X * 2.5) , VIEWALL_Y));
			goBackb.setVisible(true);
			model.fireTableDataChanged();
		}
		else if(newstate.compareTo(SHOWHIDDEN) == 0) {
			model.tableState = SHOWHIDDEN;
			currentState = SHOWHIDDEN;
			tableTitle.setText("<html><font size = 4><u>Hidden Points of Interest</u></font></html>");
			viewAll.setVisible(false);
			model.fireTableDataChanged();
		}
		// Check to see if our new state is among the types of extended information available
		if(cachedData != null) {
			Hashtable <String,String> hash = cachedData.getHash();
			if(hash != null) {
				if(hash.containsKey(newstate)) {
					StringBuffer sb = new StringBuffer();
					sb = copyStringWithWordWrap(hash.get(newstate), sb, 0);
					sb.insert(0, "<font size = 5>&nbsp;" + newstate + "</font><hr><pre>&nbsp;");
					sb.append("</pre>");
					coreInfo.setText(new String(sb));
					currentState = new String(newstate);
					model.tableState = new String(newstate);
				}
			}
		}	
		
			
		
	}
	
	// Listener for the table model that is assigned to the main pane table
	// This is used to detect when the button in column 0 is "edited", ie clicked.
	public void tableChanged(TableModelEvent e) {
		
		locListModel model;
		if(e.getColumn() == 123) {
			
			if(currentState.compareTo(MAINPANE) == 0) {
				System.out.println("Moving row " + e.getFirstRow() + " to hidden list.");
				model = (locListModel)locationList.getModel();
				model.moveToHidden(e.getFirstRow());
				model.fireTableDataChanged();
			
			}
			
			if(currentState.compareTo(VIEWALL) == 0) {
				model = (locListModel)locationList.getModel();
				if(model.isHidden(e.getFirstRow())) {
					System.out.println("Removing " + e.getFirstRow() + " from hidden list.");
					model.removeFromHidden(e.getFirstRow());
					model.fireTableDataChanged();
				}
				else {
					System.out.println("Moving row " + e.getFirstRow() + " to hidden list.");
					model.moveToHidden(e.getFirstRow());
					model.fireTableDataChanged();
				}
					
			}
			
			if(currentState.compareTo(SHOWHIDDEN) == 0) {
				System.out.println("Removing row " + e.getFirstRow() + " from hidden list.");
				model = (locListModel)locationList.getModel();
				model.removeFromHidden(e.getFirstRow());
				model.fireTableDataChanged();
			}
			
		}
		
		
	}
		
		
	/* Listener for user selection of a table cell.  Always sends user to More Information screen.
	   For some reason, getFirstIndex() and getLastIndex() are inconsistent in the values they return when selecting rows,
	   so we need to find the row selected by converting the output of getSource() to a string, and then
	   the character after the first '{' to int, before subtracting the unicode integer value of '0'.  */
	public void valueChanged(ListSelectionEvent e) {
		locListModel model = (locListModel)locationList.getModel();
			
		if(locationList.getSelectedColumn() != 0) {
			if(!(e.getValueIsAdjusting())) {
				if(currentState.compareTo(SHOWHIDDEN) != 0) {
					String eventString = e.getSource().toString();
					int index = eventString.lastIndexOf('{');
					index++;
					if(eventString.charAt(index) == '}')
						return;
					int row = (int)eventString.charAt(index) - (int)'0';
					System.out.println("Row " + row + " selected. ");
					if(model.getValueAt(row, 1) != null) {
						cachedData = (POIdata)model.getValueAt(row, 3);
						if(cachedData != null) {
							viewingHistory.push(currentState);
							locationList.clearSelection();
							centralPane.firePropertyChange(MOREINFO, true, false);
						}
					}	
				}	
			}
		}
			
	}
		
	// Listener for TalkingPointsGUI buttons
	public void actionPerformed(ActionEvent e) {
		
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
		if(e.getActionCommand() == "lochome") {
			viewingHistory.push(currentState);
			centralPane.firePropertyChange(MOREINFO, true, false);
		}
		if(e.getActionCommand() == "comments")  {
			viewingHistory.push(currentState);
			centralPane.firePropertyChange(COMMENTS, true, false);
		}
		if(e.getActionCommand() == "viewall") {
			viewingHistory.push(currentState);
			centralPane.firePropertyChange(VIEWALL, true, false);
		}
		if(e.getActionCommand() == "showhidden") {
			viewingHistory.push(currentState);
			centralPane.firePropertyChange(SHOWHIDDEN, true, false);
		}
		if(e.getActionCommand() == "contribute") {
			BareBonesBrowserLaunch.openURL(new String("http://grocs.dmc.dc.umich.edu:3000/locations/edit/" + cachedData.getTpid()));
		}
		
		
		if(cachedData != null) {
			Hashtable <String,String>hash = cachedData.getHash();
			if(hash != null) 
				if(hash.containsKey(e.getActionCommand())) {
					viewingHistory.push(currentState);
					centralPane.firePropertyChange(e.getActionCommand(), true, false);
				}
		}
	}
	
	// Configures menu panel for More Information screens
	private void configureMenuPanel() {
		
		Hashtable <String, String> extrainfo = cachedData.getHash();
		int buttonsAdded = 1;
		
		moreInfoMenu.removeAll();
		menuItems.removeAllElements();
		
		moreInfoMenu.add(core);
		ImageIcon forwardsm = (ImageIcon)core.getIcon();
		
		for(Enumeration<String> e = extrainfo.keys() ; e.hasMoreElements() ; ) {
			String s = e.nextElement();
			menuItems.add(new JRadioButton(s, forwardsm));
			JRadioButton newbutton = menuItems.lastElement();
			newbutton.setActionCommand(s);
			newbutton.addActionListener(this);
			moreInfoMenu.add(newbutton);
			buttonsAdded++;
		}
			
		moreInfoMenu.add(comments);
		buttonsAdded++;
		if(buttonsAdded > 7)
			moreInfoMenu.setPreferredSize(new Dimension(110, 30*buttonsAdded));
		else
			moreInfoMenu.setPreferredSize(new Dimension(110,230));
		
	}
	
	/**
	 * @param args is unused
	 */  /*
	public static void main(String[] args) throws InterruptedException {
		TalkingPointsGUI ourGUI = new TalkingPointsGUI();
			
		POIdata p = new POIdata("Stucchi's", "Ice Cream Parlour", "empty", "stuff", "words", "bleh", "duder", "blah", "schmelding", "etc", "010");
		
		Hashtable <String,String> hash = new Hashtable<String,String>();
		
		hash.put("Etc", "LAFFO!!!!");
		hash.put("More", "Things!");
		hash.put("David Byrne", "Guitarist");
		hash.put("Tina Weymouth", "Bassist");
		hash.put("Chris Frantz", "Drummer");
		hash.put("Jerry Harrison", "Keyboardist");
		hash.put("Some Guy", "Who knows?");
		hash.put("David Bowie", "Ziggy played guitar!");
		p.addHash(hash);
		
		Hashtable <String,Object> otherhash = new Hashtable<String,Object>();
		POIcomment one = new POIcomment("000012", "Bob Schmelding ", "ABCEEF ", "Sept 15, 2006 at 5:30PM ", "This place be da bomb, yo! ");
		POIcomment two = new POIcomment("000013", "Alan Smithee and his intrepid spaniel steig amongst the giant pygmies off bristol, volume 8", "ABCEEG ", "Oct 2, 2007 at 1:15PM ", "This place totally sucks.  For sure.  I mean, seriously.  It sucks.  Don't even think of going here.  I'm out.  Peace, yo.");
		otherhash.put("Bob", one);
		otherhash.put("Rich", two);
		p.addComment(otherhash); 
		
		
		ourGUI.addItem(p); 
				
	//	ourGUI.addItem(new POIdata("Alan Smithee's", "Pseudonym", "empty", "stuff", "words", "bleh", "duder", "blah", "schmelding", "etc", "1-3 MWF","junk","junk","junk","The quick brown fox jumped over the lazy dog.", "012"));
	//	ourGUI.addItem(new POIdata("Bob Schmelding's", "Some guy", "empty", "stuff", "words", "bleh", "duder", "blah", "schmelding", "etc", "2-5 Sat","junk","junk","junk","junk", "013"));
	//	ourGUI.addItem(new POIdata("Shemp's", "Not Curly", "empty", "stuff", "words", "bleh", "duder", "blah", "schmelding", "etc", "2-5 Sat","junk","junk","junk","junk", "014"));
	//	ourGUI.addItem(new POIdata("Blip's Arkaid", "Arcade", "empty", "stuff", "words", "bleh", "duder", "blah", "schmelding", "etc", "2-5 Sat","junk","junk","junk","junk", "015"));
	//	ourGUI.addItem(new POIdata("Kwik-e-Mart", "Who needs it?", "empty", "stuff", "words", "bleh", "duder", "blah", "schmelding", "etc", "2-5 Sat","junk","junk","junk","junk", "016"));
	}   */

	/* Custom table model for locationList.
	 * Implements the data as a vector that is truncated if it exceeds 10 entries.
	 */
	class locListModel extends AbstractTableModel {
			
		locListModel(ImageIcon seen_t, ImageIcon notseen_t, ImageIcon bulletpoint_t) {
			data = new Vector<POIdata>();
			hidden = new Vector<POIdata>();
			seen = seen_t;
			notseen = notseen_t;
			bulletpoint = bulletpoint_t;
			tableState = new String(MAINPANE);
			mappings = new int[5];
		
			for(int i = 0 ; i < 5 ; i++)
				mappings[i] = i;
		}
		
		// Required method getRowCount()
		public int getRowCount() {
			if(tableState.compareTo(VIEWALL) == 0)
				return data.size();
			
			if(tableState.compareTo(SHOWHIDDEN) == 0)
				return hidden.size();
			
			return 5;
			
		}
		
		// Required method getColumnCount()
		public int getColumnCount() {
			return 3;
		}
		
		// Cells in column 0 must be "editable" in order to be clickable; other cells are not
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if(columnIndex == 0)
				if((tableState.compareTo(MAINPANE) == 0) || (tableState.compareTo(VIEWALL) == 0) || (tableState.compareTo(SHOWHIDDEN) == 0))
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
		public Object getValueAt(int row, int column) {
			
			boolean mainpane = false;
			boolean viewall = false;
			boolean showhidden = false;
	
			if(tableState.compareTo(MAINPANE) == 0)
				mainpane = true;
			else if(tableState.compareTo(VIEWALL) == 0)
				viewall = true;
			else if(tableState.compareTo(SHOWHIDDEN) == 0)
				showhidden = true;
			
			if((row == 0) && (column == 1) && (data.isEmpty()))
				return(new String("No locations have been detected."));
			
			if(!viewall && !showhidden) {
				if((row+1) > data.size())
					return null;
				
				try {
					data.get(mappings[row]);
				}
				catch(ArrayIndexOutOfBoundsException e) {
					return null;
				}
			}
			
			switch(column) {
			
			case(0):
				if(mainpane)
					return seen;
				if(viewall) {
					if(isHidden(row))
						return notseen;
					else
						return seen;
				}
				if(showhidden)
					return notseen;
				
				return bulletpoint;
			
			case(1):
				if(viewall)
					return data.get(row).name();
				if(showhidden)
					return hidden.get(row).name();
				// If we're not on either of those pages, we need to use the mappings
				return data.get(mappings[row]).name();
				
			case(2):
				if(viewall)
					return data.get(row).location_type();
				if(showhidden)
					return hidden.get(row).location_type();
				// If we're not on either of those pages, we need to use the mappings
				return data.get(mappings[row]).location_type();
			
			case(3):
				if(viewall)
					return data.get(row);
				if(showhidden)
					return hidden.get(row);
				
				return data.get(mappings[row]);
			
			default:
				return null;     
			}
		
		}

		// Test to see if a given TPID is already represented in the table.
		public boolean isInTable(String tpid) {
			Enumeration<POIdata> e = data.elements();
			while(e.hasMoreElements()) 
				if(e.nextElement().getTpid() == tpid)
					return true;
			
			return false;
		}
		
		// Add a new POI data to the vector.  If
		// the vector is larger than 10, the 11th item is thrown out.
		public void addToTable(POIdata p) {
			
			data.add(0, p);
			
			
			mappings[0] = 0;
			// Starting at 1, update all mappings
			int i;
			int k = 1;
			for(i = 1 ; i < 5 ; i++) {
				while(isHidden(k))
					k++;
				mappings[i] = k++;
			}
			
			if(data.size() > 10)
				data.removeElementAt(11);
			
			fireTableDataChanged();
						
		}
		
		// Moves the given row of the table to the list of hidden POIs.
		public void moveToHidden(int row) {
			
			POIdata p;
			
			try { 
				if(tableState.compareTo(MAINPANE) == 0)	
					p = data.elementAt(mappings[row]); 
				else
					p = data.elementAt(row);
			}
			catch(ArrayIndexOutOfBoundsException e) {
				System.out.println("Attempted to move nonexistent table entry at row " + row);
				return;
			}
			// To save memory, we'll make a new POIdata to put on the hidden list, with only the name, type,
			// and TPID addresses as valid fields.
			POIdata dummy = new POIdata(p.name(), p.location_type(), null, null, null, null, null, null, null, null, p.getTpid());
			hidden.add(dummy);
			
			// Now we need to remap this row of the table, and update all the mappings below it.
			int i;
			if(row < 5) {  // If the row is higher than 4, there are no mappings to update.
				for(i = mappings[row] ; i < 10 ; i++) 
					if(!(isHidden(i)))
						break;
			
				mappings[row] = i;
				i = row+1;
				int k;
				// Update all mappings below the row we changed
				while(i < 5) {
					for(k = (mappings[i]+1) ; k < 10 ; k++)
						if(!(isHidden(k)))
							break;
					mappings[i] = k;
					i++;
				}
			}
			System.out.println("[0]:" + mappings[0] + " [1]:" + mappings[1] + " [2]:" + mappings[2]);
			System.out.println("[3]:" + mappings[3] + " [4]:" + mappings[4]);
			
		}
		
		// Removes the entry in data at the given index from the hidden list, then updates
		// the row mappings.
		public void removeFromHidden(int index) {
			
			int i;
			
			// If we're on a page other than the Show Hidden page, index is a position in the "data" vector,
			// and we need to look up that position's tpid and find it in the hidden list.  Otherwise, we
			// must be on the Show Hidden page, and index represents a position in the "hidden" vector.
			if(tableState.compareTo(SHOWHIDDEN) != 0) {
				String removetpid;
				try {
					removetpid = data.get(index).getTpid();
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("Tried to remove an invalid index from hidden list: " + index);
					return;
				}
			
				int hiddensize = hidden.size();
				if(hiddensize == 0) {
					System.out.println("Error: hidden list is empty!");
					return;
				}
			
				
				for(i = 0 ; i < hiddensize ; i++) 
					if(removetpid.compareTo(hidden.get(i).getTpid()) == 0)
						break;
			
				if(i == hiddensize) {
					System.out.println("Couldn't find " + removetpid + " in hidden list.");
					return;
				}
			
				System.out.println("Found " + removetpid + " at entry " + i + " in hidden list.");
			}
			else
				i = index;
			
			hidden.remove(i);
			
			int k = 0;
			for(i = 0 ; i < 5 ; i++) {
				while(isHidden(k)) 
					k++;
				mappings[i] = k++;
			}
			
			System.out.println("[0]:" + mappings[0] + " [1]:" + mappings[1] + " [2]:" + mappings[2]);
			System.out.println("[3]:" + mappings[3] + " [4]:" + mappings[4]);
		}
		
		// Compares the given POIdata's TPID against the list of hidden locations' TPIDs, and returns true if it matches any of them
		public boolean isHidden(int index) {
			String tpid;
			
			try {
				tpid = data.get(index).getTpid();
			}
			catch(ArrayIndexOutOfBoundsException e) {
				return false;
			}
						
			for(int i = 0 ; i < 10 ; i++) 
			try{
				if(tpid.compareTo(hidden.get(i).getTpid()) == 0) 
					return true;
			}
			catch(ArrayIndexOutOfBoundsException e) {
				return false;
			}
			System.out.println(index + " is hidden.");
			return false;
			
		}
		
		
		// variable definitions
		private Vector<POIdata> data;
		private Vector<POIdata> hidden;
		private ImageIcon bulletpoint;
		private ImageIcon seen;
		private ImageIcon notseen;
		public String tableState;
		// Indicates the relationship between rows of the table and entries in the data Vector.
		// ie, "mappings[0]" returns the index in Vector "data" that corresponds to the 0th row of the table.
		// This will be used on every screen but "View All" and "Show Hidden"
		private int[] mappings;
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
			
		//	ListSelectionModel selectmodel = table.getSelectionModel();
		//	selectmodel.clearSelection();
			
			isPushed = true;
			locListModel model = (locListModel)table.getModel();
			model.fireTableChanged(new TableModelEvent(model, row, row, 123));
			button.setIcon((ImageIcon)value);
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
			System.out.println("Editing stopped.");
			super.fireEditingStopped();
		}
		
		// Variable declarations
		protected JButton button;
		public boolean isPushed;
	}
		
	// Fills a stringbuffer with the necessary text for the Core Info pane, then returns it as a string.
	private String createCoreInfoString(POIdata p) {
		
		String street, city, state, postalcode, phone, url;
		
		street = p.street();
		city = p.city();
		state = p.state();
		postalcode = p.postalCode();
		phone = p.phone();
		url = p.url();
		
		if((street == null) && (city == null) && (state == null) && (postalcode == null)) {
			street = "Not Available";
			city = " ";
			state = " ";
			postalcode = " ";
		}
		else {
			if(street == null)
				street = " ";
			if(city == null)
				city = " ";
			if(state == null)
				state = " ";
			if(postalcode == null)
				postalcode = " ";
		}
		
		if(phone == null)
			phone = "Not available";
		
		if(url == null)
			url = "Not available";
			
		StringBuffer sb = new StringBuffer();
		sb.append("<font size = 5>&nbsp;Core Information</font><hr><pre>");
		sb.append(" Name:          " + p.name() + " <br>");
		sb.append(" Location Type: " + p.location_type() + " <br>");
		sb.append(" Description:   " + p.description() + " <br>");
		sb.append(" Address:       " + street + " <br>");
		sb.append("                " + city + " " + state + " " + postalcode + " <br>");   
		sb.append(" Phone:         " + phone + " <br>");
		sb.append(" URL:           <a href=" + url + ">" + url + "</a> </pre>");
		
		return new String(sb);
		
	}

	// Creates a stringbuffer containing the comments for a particular POI.
	private StringBuffer createCommentsString(Hashtable <String,Object> hash) {
		
		StringBuffer finalstring = new StringBuffer();
		
		Enumeration <String> e = hash.keys();
		
		while(e.hasMoreElements()) {
			POIcomment p = (POIcomment)hash.get(e.nextElement());
			StringBuffer sb = new StringBuffer();
			
			
			finalstring.append("&nbsp;<u>Comment #</u>" + p.getID() + "<br>");
			finalstring.append("&nbsp;<u>User ID</u>: " + p.getUserID() + "<br>");
			sb = copyStringWithWordWrap(p.getUsername(), sb, 7);
			finalstring.append("&nbsp;<u>User name</u>: " + sb + "<br>");
			finalstring.append("&nbsp;<u>Posted at</u>: " + p.getTimestamp() + "<br>");
			sb = new StringBuffer();
			sb = copyStringWithWordWrap(p.getCommentText(), sb, 0);
			finalstring.append("&nbsp;<u>Comment</u>:<br>&nbsp;" + sb + "<br><br>");
		}
		
		finalstring.append("</pre>");
		
		return finalstring;
	}
	
	// Copies a string to a stringbuffer character by character, adding <br> tags where needed.
	// DOES NOT account for empty space within HTML tags; if the source string contains HTML tags
	// with blank space inside them, it will not be word-wrapped correctly.
	/* @param source The string to copy from
	 * @param dest The destination stringbuffer.
	 * @param early The number of characters before MOREINFO_CHAR_WIDTH that the first line should break.
	*/
	// TODO: Add support for HTML tags containing blank space?
	private StringBuffer copyStringWithWordWrap(String source, StringBuffer dest, int early) {
		
		int lastlinepos = 0;
		int charscopied = 0;
		int breakspaceinserted = 0;
		
		for(int i = 0 ; i < source.length(); i++) {
			if(charscopied == (MOREINFO_CHAR_WIDTH-early)) {
				System.out.println("Hit EOL at " + source.charAt(i));
				int j = i;
				while(source.charAt(j) != ' ') {
					j--;
					if(j == lastlinepos) 
						break;
				} 
				System.out.println("Rewound to " +  source.charAt(j) + source.charAt(j+1));
				if((j != lastlinepos) && (i != j)) {
					dest.delete(j + breakspaceinserted, i + breakspaceinserted );
					lastlinepos = j;
					i = j;
				}
				else if(j == lastlinepos) {
					dest.append(source.charAt(i));
					lastlinepos = i;
				}
				else if(i == j)
					breakspaceinserted--;
				
				dest.append("<br>&nbsp;");
				early = 0;
				breakspaceinserted += 6;
				charscopied = 0;
			}
			else {
				if(!((charscopied == 0) && (source.charAt(i) == ' '))) {
					dest.append(source.charAt(i));
					charscopied++; 
				}
				else 
					breakspaceinserted--;
				
				
				//System.out.println(dest.toString());
			}
		}
		return dest;
	}
	
	// Variable definitions
	private locListModel ourModel;
	private JFrame mainFrame;
	private JPanel mainContentPane;
	private JPanel centralPane;
	private JScrollPane frontScroll;
	private JScrollPane infoScroll;
	private JScrollPane buttonScroll;
	private JTable locationList;
	private JTable locationListB;
	private JButton logoButton;
	private JButton viewAll;
	private JButton goBackb;
	private JButton contribute;
	private JLabel legend;
	private JLabel tableTitle;
	private JLabel locationTitle;
	private JEditorPane coreInfo;
	private Stack<String> viewingHistory;
	private JPanel moreInfoMenu;
	private JRadioButton core;
	private JRadioButton comments;
	private Vector<JRadioButton> menuItems;
	// A copy of the POIdata currently being viewed
	private POIdata cachedData;
	// String that describes GUI's current state
	private String currentState;
	private String commentsString;
}
