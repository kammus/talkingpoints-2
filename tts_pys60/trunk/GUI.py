import sys
sys.path.append("e:\\python")

import e32
import appuifw
import LocationCache

class GUI:
	def __init__(self, app_lock):
		appuifw.app.screen = 'normal'
		appuifw.app.title = u"Talking Points"
		
		self.location_list = []
		self.location_list_mapping = {}
		self.location_menu_list = []
		self.notifyable = True
		
		self.current_tpid = None
		
		self.lock = app_lock
		appuifw.app.exit_key_handler = self.close		
		self.location_cache = LocationCache.LocationCache()
		#print "GUI completed"
		
	def notifyOfNewLocation(self, location_name):
		if self.notifyable == True:
			appuifw.note(u"You are getting close to " + str(location_name), 'info')
			self.drawLocationList()
	
	def drawLocationList(self):
		#global location_cache
		self.notifyable = True
		appuifw.app.body = self.location_list = appuifw.Listbox(
			self.location_cache.getCurrentLocationList(),
			self.locationListCallback
		)
		self.location_list_mapping[0] = 1 # list index => tpid
		self.location_list_mapping[1] = 2

	def locationListCallback(self):
		self.notifyable = False
		
		if self.current_tpid != None:
			self.current_tpid = self.location_list_mapping[self.location_list.current()]
		
		#appuifw.note(unicode(self.location_list.current()), "info") # debugging
		appuifw.app.body = self.location_menu_list = appuifw.Listbox(
			self.location_cache.getLocationMenuList(self.current_tpid),
			self.locationMenuCallback
		)
		#TODO: unset current_tpid
	
	def locationMenuCallback(self):
		self.notifyable = False
		menu_item_selected = self.location_menu_list.current()
		
		appuifw.note(unicode(menu_item_selected), "info") # debugging
		appuifw.app.exit_key_handler = self.locationListCallback
		
		if menu_item_selected == 0: # general description
			appuifw.app.body = appuifw.Text( unicode(self.location_cache.getLocation(self.current_tpid)['description']) )
			
		elif menu_item_selected == 1: # menu
			location_menu
#	    elif menu_item_selected == 2: # hours
#	        location_hours()
#	    elif menu_item_selected == 3: # comments
#	        location_comment()
#	    elif menu_item_selected == 4: # bookmark
#	        bookmark()
#	    elif menu_item_selected == 5: # hide POI
#	        hide_POI()

	def close(self):
		self.lock.signal()