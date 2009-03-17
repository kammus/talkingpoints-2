#Bluetooth Tag Reader + UI popup window
from pyaosocket import AoResolver
import e32
import appuifw
import sys
import time

sys.path.append("e:\\python")
import tp_no_class_test
#global variables
macAddress = 0
count = 0
cont = None
myLock = e32.Ao_lock()
detected_locations_list = []

class BluetoothReader:
	
	def __init__(self):
		self.resolver = AoResolver() # Bluetooth Reader
		self.count = 0
		self.cont = None

#	def detectingStart(self):
		
#	def notifyMacAddress(macAddress):
		#callback function here
				
	def __callback(self, error, mac, name, fp):
		global count
		global cont
		global detected_locations_list
		if error == -25: #KErrEof (no more devices)
			#print "query done"
			cont = None
		elif error:
			raise
		else:
			count += 1
			#print "result: "
			#print repr([mac, name, count])
			macAddress = mac
			#if this mac address is same as dummy mac address
			#tp_no_class_test.app_lock.signal()
			if(macAddress == "001ff3b01a1e"):
				try:
					detected_locations_list.index(macAddress)
				except ValueError: #if this is not in the list
					place_name = "Espresso Royale"
					appuifw.note(u"Place:" + str(place_name), "info", 1) #this should be at the view class (global note)
					detected_locations_list.append(macAddress)) #append mac address here
					tp_no_class_test.notifyMacAddress(detected_locations_list)
				
			cont = self.resolver.next
		myLock.signal()

	def btSearch(self):
		global macAddress
		try:
			self.resolver.open()
			#print "Bluetooth Initial Discovery"
			cont = lambda: self.resolver.discover(self.__callback, None)
			while cont:
				cont()
				myLock.wait()

		finally:
			self.resolver.close()	
			

tp_no_class_test.uiStart() #ui start
bluetoothReader = BluetoothReader()
bluetoothReader.btSearch()