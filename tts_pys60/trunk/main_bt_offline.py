import sys
sys.path.append("c:\\python\\tp")
sys.path.append("e:\\python\\tp")

from pyaosocket import AoResolver
from socket import *

import e32
import appuifw

import serverAPI #do we need this?
import GUI

#global variables
macAddress = 0
inet_mode = "offline"
count = 0
cont = None
myLock = e32.Ao_lock()

class BluetoothReader:
	
	def __init__(self, btGUI):
		self.resolver = AoResolver() # Bluetooth Reader
		self.sleepTimer = e32.Ao_timer()
		self.count = 0
		self.cont = None
		self.bt_scan_completed = 0
		self.myGUI = btGUI
		self.detected_locations_dic = [] #detected_locations_dictionary
		global inet_mode
		self.serverAPI = serverAPI.ServerAPI(inet_mode)

	def notifyMacAddress(self, macAddress): #For exposition, put filtering here
		flag = 0
		result = self.myGUI.location_cache.checkLocationsForBluetoothMAC(macAddress)
		if result == 0:
			loc = self.serverAPI.get_location_by_bluetooth_mac(macAddress)
			if loc is not None:
				if not self.myGUI.location_cache.checkLocationsForBluetoothMAC(macAddress):
					self.myGUI.location_cache.appendLocation(loc)
					self.myGUI.notifyOfNewLocation(str(loc['name']) + " [" + str(loc['location_type']) + "]")	
								
	def __callback(self, error, mac, name, fp): #Different Thread
		global count
		global cont
		global detected_locations_list
		global macAddress
		global inet_mode
		if error == -25: #KErrEof (no more devices)
			#print "query done"
			cont = None
		elif error:
			raise
		else:
			count += 1
			macAddress = mac
			# filtering       #Pete's USB BT dongle		    #Nokia Tablet                   #Dell Axim
			if inet_mode == "offline": 
				if macAddress == '000272c008cb' or macAddress == '00194fa4e262' or macAddress == '0010c65e9224':
					self.notifyMacAddress(macAddress)
			else:
				self.notifyMacAddress(macAddress)
				#fp(macAddress)						
			cont = self.resolver.next
		
		self.bt_scan_completed = 1
		myLock.signal()

	def btSearch(self):
		global macAddress
		self.bt_scan_completed = 1
		try:
			self.resolver.open()
			self.myGUI.drawGUI()
			cont = lambda: self.resolver.discover(self.__callback, self.notifyMacAddress)
			while 1:
				if self.bt_scan_completed:
					self.bt_scan_completed = 0
					cont()
#					if cont == None:
#						cont = lambda: self.resolver.discover(self.__callback, self.notifyMacAddress)
				
				myLock.wait()
				#if macAddress == '000272c008cb' or macAddress == '00194fa4e262' or macAddress == '0010c65e9224':
				#	self.notifyMacAddress(macAddress)		
				if(self.myGUI.terminated == 1): #if this lock is resolved by the GUI
					break
				if(self.myGUI.redraw_flag != 0):
					self.myGUI.drawGUI()
				e32.ao_yield()

		finally:
			self.resolver.close()
			



			
app_lock = e32.Ao_lock() #app_lock (Only GUI will use this one from this point)

#apid = select_access_point()  #Prompts you to select the access point
#if apid != 0:
#	apo = access_point(apid)      #apo is the access point you selected
#	set_default_access_point(apo) #Sets apo as the default access point
#	inet_mode = "online"

mainGUI = GUI.GUI(myLock)
btReader = BluetoothReader(mainGUI)

#Bluetooth Discovery Start
btReader.btSearch()