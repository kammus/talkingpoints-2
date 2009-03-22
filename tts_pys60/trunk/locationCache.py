import sys
#sys.path.append("C:\\python\\tp") # for emulator testing
#import ServerAPI
import time

class LocationCache:
	
	def __init__(self):
		self.detected_locations = {}
	
	# ---- backend methods ----
	
	# check if a location with this MAC address is already detected
	def checkLocationsForBluetoothMAC(self, mac):
		for key,value in self.detected_locations.iteritems():
			if value['bluetooth_mac'] == str(mac):
				self.seenLocation(value['tpid'])
				return 1

		return 0
	
	def checkLocationsForTPID(self, TPID):
		for key,value in self.detected_locations.iteritems():
			if str(value['tpid']) == str(TPID):
				self.seenLocation(value['tpid'])
				return value['tpid']
		
		return 0
	
	# adds a location data structure to the detected_location dictionary
	def appendLocation(self, loc):
		loc['last_seen'] = time.clock()
		self.detected_locations[ loc['tpid'] ] = loc
		
		#GUI.notifyOfNewLocation(loc['name'] + " [" + loc['type'] + "]")
	
	# updates the timestamp of a location identified by tpid
	def seenLocation(self, tpid):
		self.detected_locations[ tpid ]['last_seen'] = time.clock()
	
	# ---- GUI methods ----
	
	# returns the current detected_locations as a list for the ListBox UI element
	def getCurrentLocationList(self):
		output = {}
		output['list'] = []
		output['mapping'] = {}
		i = 0
		for key,value in self.detected_locations.iteritems():
			if (time.clock() - value['last_seen']) < 30: # location has been seen no longer than 60 seconds ago
				# append (name, location_type) tupel to the list
				output['list'].append( ( unicode(value['name']), unicode(value['location_type']) ) )
				output['mapping'][i] = value['tpid']
				i += 1
		return output
	
	# return a location identified by tpid
	def getLocation(self, tpid):
		return self.detected_locations[tpid]
	
	# returns main menu options (sections) for a location
	def getLocationMenuList(self, tpid):
		output = {}
		
		output['list'] = [u"General Description"]
		output['mapping'] = {0: unicode(self.detected_locations[tpid]['description'])}
		
		i = 1
		#list = [u"General Description", u"Menu", u"Hours", u"Comments", u"Bookmark" , u"Hide"]
		for key in self.detected_locations[tpid]['sections']:
			if key != "Comments":
				output['list'].append( unicode(key) )
				output['mapping'][i] = unicode(self.detected_locations[tpid]['sections'][key])
				i += 1
		
		output['list'] += [u"Comments", u"Bookmark" , u"Hide"]
		output['mapping'][i] = "Comments"
		output['mapping'][i+1] = "Bookmark"
		output['mapping'][i+2] = "Hide location"
		
		return output
	
	# returns list of comments for a certain location
	def getLocationCommentsList(self, tpid):
		list = []
		for key in self.detected_locations[tpid]['sections']['Comments']:
			list.append( unicode(self.detected_locations[tpid]['sections']['Comments'][key]['text']) )
		return list
			

# ---------------- TEST code --------------------------

#server = ServerAPI.ServerAPI()
#location_cache = LocationCache()
#location_cache.appendLocation(server.get_location(1))
#location_cache.appendLocation(server.get_location(2))
#
#print "* loc 1: " + str(location_cache.detected_locations[1]['last_seen']) + " seconds\n"
#print "* loc 2: " + str(location_cache.detected_locations[2]['last_seen']) + " seconds\n"
#print "* since loc 1: " + str(time.clock() - location_cache.detected_locations[1]['last_seen']) + " seconds\n"
#
#location_cache.checkLocationsForBluetoothMAC('1234567890ab')
#
#print "* loc 1: " + str(location_cache.detected_locations[1]['last_seen']) + " seconds\n"
#
#location_cache.seenLocation(1)
#
#print "* loc 1: " + str(location_cache.detected_locations[1]['last_seen']) + " seconds\n"



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