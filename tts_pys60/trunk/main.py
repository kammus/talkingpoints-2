from pyaosocket import AoResolver
import sys
sys.path.append("C:\\python\\tp") # for emulator testing
sys.path.append("e:\\python")

import e32
import appuifw
from socket import *

import urllib
import json
import ServerAPI #do we need this?
import GUI

#global variables
macAddress = 0
count = 0
cont = None
myLock = e32.Ao_lock()

class BluetoothReader:
	
	def __init__(self, btGUI):
		self.resolver = AoResolver() # Bluetooth Reader
		self.count = 0
		self.cont = None
		self.myGUI = btGUI
		self.detected_locations_dic = [] #detected_locations_dictionary	
		self.serverAPI = ServerAPI.ServerAPI()	
		self.server_host = "http://test.talking-points.org"

	def notifyMacAddress(self, macAddress):
		flag = 0
		result = self.myGUI.location_cache.checkLocationsForBluetoothMAC(macAddress)
		if result == 0:
			loc = self.serverAPI.get_location_by_bluetooth_mac(macAddress)
			if loc is not None:
				self.myGUI.location_cache.appendLocation(loc)
				self.myGUI.notifyOfNewLocation(str(loc['name']) + " [" + str(loc['location_type']) + "]")	

								
	def __callback(self, error, mac, name, fp): #Different Thread
		global count
		global cont
		global detected_locations_list
		global macAddress
		if error == -25: #KErrEof (no more devices)
			#print "query done"
			cont = None
		elif error:
			raise
		else:
			count += 1
			macAddress = mac
			fp(macAddress)	
				
			cont = self.resolver.next
		myLock.signal()

	def btSearch(self):
		global macAddress
		try:
			self.resolver.open()
			#print "Bluetooth Search Start"
			GUI.drawLocationList()
			cont = lambda: self.resolver.discover(self.__callback, self.notifyMacAddress)
			while cont:
				cont()
				myLock.wait()

		finally:
			self.resolver.close()
			



			
app_lock = e32.Ao_lock() #app_lock (Only GUI will use this one from this point)

apid = select_access_point()  #Prompts you to select the access point
apo = access_point(apid)      #apo is the access point you selected
set_default_access_point(apo) #Sets apo as the default access point

GUI = GUI.GUI(app_lock)
btReader = BluetoothReader(GUI) 

#Bluetooth Discovery Start
btReader.btSearch()

app_lock.wait() #wait for user input