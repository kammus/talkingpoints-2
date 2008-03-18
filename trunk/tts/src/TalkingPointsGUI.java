/* TODO: Put some sort of documentation/description up here
 * 
 */

//package talkingPoints;

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
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Component;
import java.awt.BorderLayout;

public class TalkingPointsGUI  {

	// Default constructor
	TalkingPointsGUI()  {
		System.out.println("Object " + this.toString() + " created.");
		initGUI();
	}

	public void addItem(POIdata p) {
		ourModel.Push(p);
	}
	
	// Does what it says; initialize the GUI and its components
	private void initGUI() {
		
		// Allocate components for the GUI's front page
		mainFrame = new JFrame("Talking Points");
		mainContentPane = new JPanel(new BorderLayout());
		locationList = new JTable(5, 2);
		ourModel = new locListModel();
		frontScroll = new JScrollPane(locationList);
		logoButton = new JButton();
		viewAll = new JButton("View All");
		legend = new JLabel();
			
		// Configure main frame
		mainFrame.setResizable(false);
		mainFrame.setPreferredSize(new Dimension(400, 305));
		
		// Configure table's scroll bar
		frontScroll.setColumnHeader(new JViewport());
		frontScroll.getColumnHeader().setVisible(false);
		frontScroll.setPreferredSize(new Dimension(350, 130));
		frontScroll.setMinimumSize(new Dimension(350,130));
		frontScroll.setMaximumSize(new Dimension(350,160));
		
		// Configure location list table
		locationList.setModel(ourModel);
		locationList.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		locationList.setFillsViewportHeight(true);
		locationList.setShowHorizontalLines(false);
		locationList.setShowVerticalLines(false);
		locationList.getTableHeader().setResizingAllowed(false);
		locationList.getTableHeader().setReorderingAllowed(false);
		locationList.getTableHeader().setVisible(false);
		
		// Configure Legend label
		legend.setPreferredSize(new Dimension(100, 50));
		legend.setMinimumSize(new Dimension(100,50));
		legend.setMaximumSize(new Dimension(100,50));
		legend.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		legend.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		// Configure logo button
		logoButton.setPreferredSize(new Dimension(100, 50));
		logoButton.setMinimumSize(new Dimension(100,50));
		logoButton.setMaximumSize(new Dimension(100,50));

		// Configure View All button
		viewAll.setPreferredSize(new Dimension(80, 45));		
		
		// Set up horizontal layer at top of window
		JPanel topButtons = new JPanel();
		topButtons.setLayout(new BoxLayout(topButtons, BoxLayout.LINE_AXIS));
		topButtons.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		topButtons.add(Box.createRigidArea(new Dimension(10,10)));
		topButtons.add(legend);
		topButtons.add(Box.createGlue());
		topButtons.add(logoButton);
		topButtons.add(Box.createRigidArea(new Dimension(10,10)));
		
		// Set up vertical layer consisting of only scroll pane + location list table
		JPanel middlePane = new JPanel();
		middlePane.setLayout(new BoxLayout(middlePane, BoxLayout.PAGE_AXIS));
		middlePane.add(Box.createRigidArea(new Dimension(10,5)));
		middlePane.add(frontScroll);
		middlePane.add(Box.createRigidArea(new Dimension(10,5)));
						
		/// Set up final horizontal layer of only the view all button
		JPanel bottomButton = new JPanel();
		bottomButton.setLayout(new BoxLayout(bottomButton, BoxLayout.LINE_AXIS));
		bottomButton.add(Box.createGlue());
		bottomButton.add(viewAll);
		bottomButton.add(Box.createRigidArea(new Dimension(10,10)));
		
		// Add components to main content pane
		mainContentPane.add(topButtons, BorderLayout.PAGE_START);
		mainContentPane.add(middlePane, BorderLayout.CENTER);
		mainContentPane.add(bottomButton, BorderLayout.PAGE_END);
		mainFrame.setContentPane(mainContentPane);
		mainFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		
		mainFrame.pack();
		mainFrame.setVisible(true);
	}
	
	
	/**
	 * @param args is unused
	 */
/*	public static void main(String[] args) {
		TalkingPointsGUI ourGUI = new TalkingPointsGUI();
		
		POIdata test = new POIdata("things", "place", "stuff");
		System.out.println("Adding item");
		POIdata test2 = new POIdata("what", "huh", "okay");
		ourGUI.addItem(test);
		ourGUI.addItem(test2);
		ourGUI.addItem(test);
		ourGUI.addItem(test);
		ourGUI.addItem(test);
		ourGUI.addItem(test2);
	} */

	/* Custom table model for locationList.
	 * Implements the table data as a sort of ersatz-queue, 
	 * where random access is possible.
	 */
	class locListModel extends AbstractTableModel {
			
		locListModel() {
			data = new TableEntry[5];
			for(int i = 0 ; i < 5 ; i++) {			
				data[i] = new TableEntry();
			}
			System.out.println("Location list model created.");
		}
		
		// Required method getRowCount()
		public int getRowCount() {
			return 5;
		}
		
		// Required method getColumnCount()
		public int getColumnCount() {
			return 2;
		}
		
		// Required method getValueAt()
		// TODO: Update if number of columns changes
		public Object getValueAt(int row, int column) {
			
			if(column == 0)
				return data[row].getName();
			else
				return data[row].getLoc();
		}
		
		// Push a new data entry onto the list.  The
		// current last element on the list is thrown away.
		public void Push(POIdata p) {
			System.out.println("Trying push onto location queue.");
			
			for(int i = 4 ; i > 0 ; i--) {
				System.out.println("Copying data at " + (i-1) + " to " + i);
				if(data[i] != null)
					data[i].setValues(new POIdata(data[i-1].getName(), "nothing", data[i-1].getLoc()));
				else
					System.out.println("object #" + i + " is unallocated.");
			}
		if(data[0] != null)
			data[0].setValues(p);
		else
			System.out.println("data[0] is unallocated.");
		
		fireTableDataChanged();
			
		}
		
		// Queue to store location data; this will consist of two strings contained in the class TableEntry.
		private TableEntry[] data;
	} 
	
	// Variable definitions
	private locListModel ourModel;
	private JFrame mainFrame;
	private JPanel mainContentPane;
	private JScrollPane frontScroll;
	private JTable locationList;
	private JButton logoButton;
	private JButton viewAll;
	private JLabel legend;
	
}
