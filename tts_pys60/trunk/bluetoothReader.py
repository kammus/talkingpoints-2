#Bluetooth Tag Reader + UI popup window
from pyaosocket import AoResolver
import e32
import appuifw
import sys
import time
import urllib
sys.path.append("e:\\python")

import json
import ServerAPI

#global variables
macAddress = 0
count = 0
cont = None
myLock = e32.Ao_lock()

class BluetoothReader:
	
	def __init__(self, btGUI):
		self.resolver = AoResolver() # Bluetooth Reader
		self.count = 0
		self.myLock = e32.Ao_lock()
		self.cont = None
		self.myGUI = btGUI
		self.detected_locations_dic = {} #detected_locations_dictionary	
#		self.serverAPI = ServerAPI.ServerAPI()	
		self.server_host = "http://test.talking-points.org"
	def notifyMacAddress(self, macAddress):
		flag = 0
#		if(macAddress == "001ff3b01a1e"): #for test
		#see if this mac address has been detected
		for i,v in self.detected_locations_dic.iteritems():
			if v['bluetooth_mac'] == str(macAddress): #if this has been detected
				flag = 1
				break
						
		if flag == 0 :
			try:
				request_url = self.server_host + "/locations/show/" + str(1) + ".json"
				print "here"
				response = urllib.urlopen(request_url).read()
				loc = json.read(response)	
			except:
				print "fucked up"
			if loc == null:
				return
			else: #if this is valid macaddress
				myGUI.location_cache.appendLocation(loc)
				myGUI.notifyOfNewLocation(loc['name'] + " [" + loc['type'] + "]")	
								
	def __callback(self, error, mac, name, fp): #Different Thread
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
			macAddress = mac
			fp(macAddress)	
				
			cont = self.resolver.next
		myLock.signal()

	def btSearch(self):
		global macAddress
		try:
			self.resolver.open()
			print "Bluetooth Search Start"
			cont = lambda: self.resolver.discover(self.__callback, self.notifyMacAddress)
			while cont:
				cont()
				myLock.wait()

		finally:
			self.resolver.close()	