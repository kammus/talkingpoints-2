/* TODO: Put some sort of documentation/description up here
 * 
 */



import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.JButton;
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
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Component;
import java.awt.BorderLayout;

public class TalkingPointsGUI  {

	// Constants
	static final int COMP_SPACER_X = 10;
	static final int COMP_SPACER_Y = 10;
	static final int TOP_SPACER_X = 50;
	static final int VIEWALL_X = 80;
	static final int VIEWALL_Y = 45;
		
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
			
		// Allocate components for the GUI's front page
		ImageIcon forward = createImageIcon("images/forward.png", "Button that advances to a screen with more information about this point of interest.");
		
		mainFrame = new JFrame("Talking Points");
		mainContentPane = new JPanel(new BorderLayout());
		locationList = new JTable(5, 3);
		ourModel = new locListModel(forward);
		frontScroll = new JScrollPane(locationList);
		logoButton = new JButton(createImageIcon("images/logo.jpg", "TalkingPoints Logo"));
		viewAll = new JButton("View All");
		legend = new JLabel(createImageIcon("images/legend.png", "Legend for this page's icons."),
				javax.swing.SwingConstants.CENTER);
		tableTitle = new JLabel("<html><font size = 4><u>Detected Points of Interest</font></u></html>", javax.swing.SwingConstants.CENTER);
				
		// Pre-calculate our desired component sizes to save time/computation
		int forwardWidth = forward.getIconWidth() ;
		int forwardHeight = forward.getIconHeight();
	
		int tableHeight = (forwardHeight * 5) + tableTitle.getHeight() + 15;
		
		int legendWidth = legend.getIcon().getIconWidth();
		int legendHeight = legend.getIcon().getIconHeight();
		
		int logoWidth = logoButton.getIcon().getIconWidth();
		int logoHeight = logoButton.getIcon().getIconHeight();	

		int windowWidth = legend.getIcon().getIconWidth() + logoButton.getIcon().getIconWidth() + TOP_SPACER_X;
		int tallest = (legendHeight > logoHeight) ? legendHeight : logoHeight;
		int windowHeight = COMP_SPACER_Y + tallest + COMP_SPACER_Y + tableHeight + (COMP_SPACER_Y / 2) 
		+ VIEWALL_Y + COMP_SPACER_Y + 38;
		
		int infoFieldsWidth = windowWidth - forward.getIconWidth() - (COMP_SPACER_X * 2);
		
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
		locationList.setModel(ourModel);
		locationList.getColumnModel().getColumn(0).setCellRenderer(new ButtonRenderer());
		locationList.getColumnModel().getColumn(0).setPreferredWidth(forwardWidth);
		locationList.getColumnModel().getColumn(0).setMinWidth(forwardWidth);
		locationList.getColumnModel().getColumn(0).setMaxWidth(forwardWidth);
		locationList.getColumnModel().getColumn(1).setPreferredWidth((int)Math.floor(infoFieldsWidth * 0.25));
		locationList.getColumnModel().getColumn(1).setMinWidth((int)Math.floor(infoFieldsWidth * 0.25));
		locationList.getColumnModel().getColumn(1).setMaxWidth((int)Math.floor(infoFieldsWidth * 0.25));
		locationList.getColumnModel().getColumn(2).setPreferredWidth((int)Math.ceil(infoFieldsWidth * 0.75) - 3);
		locationList.getColumnModel().getColumn(2).setMinWidth((int)Math.ceil(infoFieldsWidth * 0.75) - 3);
		locationList.getColumnModel().getColumn(2).setMaxWidth(infoFieldsWidth * 2);
		locationList.setRowHeight(forwardHeight);
		locationList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		locationList.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		locationList.setFillsViewportHeight(true);
		locationList.setShowHorizontalLines(false);
		locationList.setShowVerticalLines(false);
		
		locationList.getTableHeader().setResizingAllowed(false);
		locationList.getTableHeader().setReorderingAllowed(false);
		locationList.getTableHeader().setVisible(false);
		
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

		// Configure View All button
		viewAll.setPreferredSize(new Dimension(VIEWALL_X, VIEWALL_Y));		
		
		// Set up horizontal layer at top of window
		JPanel topButtons = new JPanel();
		topButtons.setLayout(new BoxLayout(topButtons, BoxLayout.LINE_AXIS));
		topButtons.setBorder(BorderFactory.createEmptyBorder(COMP_SPACER_X, 2, COMP_SPACER_Y, 2));
		topButtons.add(Box.createRigidArea(new Dimension(COMP_SPACER_X, COMP_SPACER_Y)));
		topButtons.add(legend);
		topButtons.add(Box.createGlue());
		topButtons.add(logoButton);
		
		// Set up vertical layer consisting of title + scroll pane + location list table
		JPanel middlePane = new JPanel();
		middlePane.setLayout(new BoxLayout(middlePane, BoxLayout.Y_AXIS));
		middlePane.add(tableTitle);
		middlePane.add(Box.createRigidArea(new Dimension(COMP_SPACER_X, (COMP_SPACER_Y / 2))));
		middlePane.add(frontScroll);
		middlePane.add(Box.createRigidArea(new Dimension(COMP_SPACER_X, (COMP_SPACER_Y / 2))));
						
		/// Set up final horizontal layer of only the view all button
		JPanel bottomButton = new JPanel();
		bottomButton.setLayout(new BoxLayout(bottomButton, BoxLayout.LINE_AXIS));
		bottomButton.add(Box.createGlue());
		bottomButton.add(viewAll);
		bottomButton.add(Box.createRigidArea(new Dimension(COMP_SPACER_X, COMP_SPACER_Y)));
		
		// Add components to main content pane
		mainContentPane.add(topButtons, BorderLayout.PAGE_START);
		mainContentPane.add(middlePane, BorderLayout.CENTER);
		mainContentPane.add(bottomButton, BorderLayout.PAGE_END);
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
	
	/**
	 * @param args is unused
	 */ /*
	public static void main(String[] args) {
		TalkingPointsGUI ourGUI = new TalkingPointsGUI();
		
		POIdata test = new POIdata("things", "place", "stuff");
		System.out.println("Adding item");
		POIdata test2 = new POIdata("what", "huh", "This is a really long field.  I mean REALLY loooooooong.  We need to be able to read all of it.");
		ourGUI.addItem(test);
		ourGUI.addItem(test2);
		ourGUI.addItem(test);
		ourGUI.addItem(test);
		ourGUI.addItem(test);
		ourGUI.addItem(test2);
	}  */

	/* Custom table model for locationList.
	 * Implements the table data as a sort of ersatz-queue, 
	 * where random access is possible.
	 */
	class locListModel extends AbstractTableModel {
			
		locListModel(ImageIcon icon) {
			data = new POIdata[5];
			forward = icon;
			System.out.println("Location list model created.");
		}
		
		// Required method getRowCount()
		public int getRowCount() {
			return 5;
		}
		
		// Required method getColumnCount()
		public int getColumnCount() {
			return 3;
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
		// TODO: Update if number of columns changes
		public Object getValueAt(int row, int column) {
			
			if(data[row] == null)
				return null;
			
			if(column == 1)
				return data[row].name();
			else if (column == 2)
				return data[row].description();
			else 
				return forward;
		}
		
		// Push a new data entry onto the list.  The
		// current last element on the list is thrown away.
		public void Push(POIdata p) {
			System.out.println("Trying push onto location queue.");
			
			for(int i = 4 ; i > 0 ; i--) {
				if(data[i-1] != null)
					data[i] = data[i-1];
				else
					System.out.println("object #" + i + " is unallocated.");
			}
		/* does this work instead of creating a new POIdata object every time*/ 
		data[0] = p;
		//data[0] = new POIdata(p.name(), p.location_type(), p.description()); ;
		
		fireTableDataChanged();
			
		}
		
		// Queue to store location data; this will consist of two strings contained in the class TableEntry.
		private POIdata[] data;
		private ImageIcon forward;
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
	
	// TODO: Add editor that will allow user to click on forward buttons
	
	// Variable definitions
	private locListModel ourModel;
	private JFrame mainFrame;
	private JPanel mainContentPane;
	private JScrollPane frontScroll;
	private JTable locationList;
	private JButton logoButton;
	private JButton viewAll;
	private JLabel legend;
	private JLabel tableTitle;
}
