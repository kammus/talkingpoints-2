import sys
#sys.path.append("e:\\python")
import ServerAPI
import time

class LocationCache:
	
	def __init__(self):
		self.detected_locations = {}
	
	# check if a location with this MAC address is already detected
	def macDetected(self, mac):
		for i,v in self.detected_locations.iteritems():
			if v['bluetooth_mac'] == str(mac):
				self.seenLocation(v['tpid'])
				return true
			else:
				return false
	
	# adds a location data structure to the detected_location dictionary
	def appendPOI(self, loc):
		loc['last_seen'] = time.clock()
		self.detected_locations[ loc['tpid'] ] = loc
	
	# updates the timestamp of a location identified by tpid
	def seenLocation(self, tpid):
		self.detected_locations[ tpid ]['last_seen'] = time.clock()
		
	# ---- for the GUI ----
	
	# returns the current detected_locations as a list for the ListBox UI element
	def getLocationsList(self):
		list = []
		for i,v in self.detected_locations.iteritems():
			if (time.clock() - v['last_seen']) < 6000:
				list.append((v['name'], v['location_type']))
				
		return list
	
	# return a location identified by tpid
	def getLocation(self, tpid):
		return self.detected_locations[tpid]
			
	