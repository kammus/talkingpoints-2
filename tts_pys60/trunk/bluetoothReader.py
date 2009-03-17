#Bluetooth Tag Reader + UI popup window
from pyaosocket import AoResolver
import e32
import appuifw
import sys
import time
from socket import *
import urllib

sys.path.append("e:\\python")
import json
import tp_no_class_test
#global variables
macAddress = 0
count = 0
cont = None
myLock = e32.Ao_lock()
detected_locations_dic = {} #detected_locations_dictionary

class BluetoothReader:
	
	def __init__(self):
		self.resolver = AoResolver() # Bluetooth Reader
		self.count = 0
		self.cont = None
		
	def notifyMacAddress(self, macAddress):
		global detected_locations_dic
		#callback function here
		if(macAddress == "001ff3b01a1e"):
			try:
				#see if this mac address has been detected macaddress
				#This needs to be modified
				detected_locations_dic.index(macAddress)
			except ValueError: #if this is not in the dictionary
				#Connect the server and get the information
				#Also, pass this dictionary to the UI
				place_name = "Espresso Royale"
				tp_server = "http://test.talking-points.org"
				url = tp_server + "/locations/show/1.json"
				json_src = urllib.urlopen(url).read()
				json_parsed = json.read(json_src)
				appuifw.note(u"Place:" + str(json_parsed['name']), "info", 1) #this should be at the view class (global note)
				detected_locations_dic.append(unicode(json_parsed['name'])) #append mac address here
				tp_no_class_test.notifyMacAddress(detected_locations_list)
				
								
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
			fp(macAddress)	
				
			cont = self.resolver.next
		myLock.signal()

	def btSearch(self):
		global macAddress
		try:
			self.resolver.open()
			#print "Bluetooth Initial Discovery"
			#apid = select_access_point()  #Prompts you to select the access point
			#apo = access_point(apid)      #apo is the access point you selected
			#set_default_access_point(apo) #Sets apo as the default access point
			
			cont = lambda: self.resolver.discover(self.__callback, self.notifyMacAddress)
			while cont:
				cont()
				myLock.wait()

		finally:
			self.resolver.close()	
			

tp_no_class_test.uiStart() #ui start
bluetoothReader = BluetoothReader()
bluetoothReader.btSearch()