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
		flag = 0
		if(macAddress == "001ff3b01a1e"): #for test
			#see if this mac address has been detected
			for i,v in detected_locations_dic.iteritems():
				if v['bluetooth_mac'] == str(macAddress): #if this has been detected
					flag = 1
					break
						
			if flag == 0 :
				#Connect the server and get the information
				json_parsed = {}
				json_parsed['name'] = 'Espresso Royale'
				json_parsed['tpid'] = '1'
				json_parsed['bluetooth_mac'] = '001ff3b01a1e'
				#Also, pass this dictionary to the UI
				#json_parsed = sAPI.get_location_by_bluetooth_mac(macAddress) Hopefully this works?
				#tp_server = "http://test.talking-points.org"
				#url = tp_server + "/locations//1.json"
				#json_src = urllib.urlopen(url).read()
				#json_parsed = json.read(json_src)
				appuifw.note(u"Place:" + str(json_parsed['name']), "info", 1) #this should be at the view class (global note)
				detected_locations_dic[json_parsed['tpid']] = json_parsed #update detected_locations list
				#.append(unicode(json_parsed['name'])) #append mac address here
				tp_no_class_test.notifyMacAddress(detected_locations_dic)
				
								
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
#sAPI = serverAPI.ServerAPI() #serverAPI start
bluetoothReader = BluetoothReader()
bluetoothReader.btSearch()