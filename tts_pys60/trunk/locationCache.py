import sys
sys.path.append("C:\\python\\tp") # for emulator testing
import ServerAPI
import time

class LocationCache:
	
	def __init__(self):
		self.detected_locations = {}
	
	# check if a location with this MAC address is already detected
	def checkLocationsForBluetoothMAC(self, mac):
		for i,v in self.detected_locations.iteritems():
			if v['bluetooth_mac'] == str(mac):
				self.seenLocation(v['tpid'])
				return v['tpid']
			else:
				return false
	
	# adds a location data structure to the detected_location dictionary
	def appendLocation(self, loc):
		loc['last_seen'] = time.clock()
		self.detected_locations[ loc['tpid'] ] = loc
	
	# updates the timestamp of a location identified by tpid
	def seenLocation(self, tpid):
		self.detected_locations[ tpid ]['last_seen'] = time.clock()
		
	# ---- for the GUI ----
	
	# returns the current detected_locations as a list for the ListBox UI element
	def getCurrentLocationList(self):
		list = []
		for i,v in self.detected_locations.iteritems():
			if (time.clock() - v['last_seen']) < 60:
				# append (name, location_type) tupel to the list
				list.append( ( unicode(v['name']), unicode(v['location_type']) ) )
				
		return list
	
	# return a location identified by tpid
	def getLocation(self, tpid):
		return self.detected_locations[tpid]
			

# ---------------- TEST code --------------------------

server = ServerAPI.ServerAPI()
location_cache = LocationCache()
location_cache.appendLocation(server.get_location(1))
location_cache.appendLocation(server.get_location(2))

print "* loc 1: " + str(location_cache.detected_locations[1]['last_seen']) + " seconds\n"
print "* loc 2: " + str(location_cache.detected_locations[2]['last_seen']) + " seconds\n"
print "* since loc 1: " + str(time.clock() - location_cache.detected_locations[1]['last_seen']) + " seconds\n"

location_cache.checkLocationsForBluetoothMAC('1234567890ab')

print "* loc 1: " + str(location_cache.detected_locations[1]['last_seen']) + " seconds\n"

location_cache.seenLocation(1)

print "* loc 1: " + str(location_cache.detected_locations[1]['last_seen']) + " seconds\n"



#import e32
#import appuifw
#
#def detail_location_callback():
#	appuifw.note(unicode(location_list.current()), "info")
#	
#appuifw.app.screen = 'normal'
#appuifw.app.title = u"Talking Points"
#location_list = location_cache.getCurrentLocationList()
#appuifw.app.body = appuifw.Listbox(location_list, detail_location_callback)
#
#app_lock = e32.Ao_lock()
#app_lock.wait()