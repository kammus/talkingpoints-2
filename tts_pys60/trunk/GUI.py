import sys
sys.path.append("e:\\python")

import e32
import appuifw
import LocationCache

class GUI:
	def __init__(self, app_lock):
		appuifw.app.screen = 'normal'
		appuifw.app.title = u"Talking Points"
		
		self.location_listbox = []
		self.location_listbox_mapping = {}
#		self.location_list_mapping[0] = 1 # list index => tpid
#		self.location_list_mapping[1] = 2
		self.location_menu_list = []
		self.comment_list = []
		self.notifyable = True
		
		self.current_tpid = None
		
		self.lock = app_lock
		appuifw.app.exit_key_handler = self.exit		
		self.location_cache = LocationCache.LocationCache()
		
	def locationCommentCallback(self):
		comment_selected = self.comment_listbox.current()
		appuifw.app.body = appuifw.Text( self.comment_list[comment_selected] )
		
	def notifyOfNewLocation(self, location_name):
		if self.notifyable == True:
			appuifw.note(unicode("You are getting close to " + location_name), 'info')
			self.drawLocationList()
	
	def drawLocationList(self):
		self.notifyable = True
		self.current_tpid = None
		appuifw.app.exit_key_handler = self.exit
		
		tmp = self.location_cache.getCurrentLocationList()
		if len(tmp['list']) > 0:
			location_list = tmp['list']
			self.location_listbox_mapping = tmp['mapping']
			self.location_listbox = appuifw.Listbox(
				location_list,
				self.locationListCallback
			)
			appuifw.app.body = self.location_listbox
			
#			e32.ao_sleep(30)
#			for every 30 sec:
#				if loc_cache.check == expired
#					self.drawLocationList
		else:
			appuifw.app.body = appuifw.Text( unicode("Searching ...") )
			
	

	def locationListCallback(self):
		self.notifyable = False
		appuifw.app.exit_key_handler = self.drawLocationList
		
		if self.current_tpid == None:
			self.current_tpid = self.location_listbox_mapping[self.location_listbox.current()]
		
		self.location_menu_list = appuifw.Listbox(
			self.location_cache.getLocationMenuList(self.current_tpid),
			self.locationMenuCallback
		)
		appuifw.app.body = self.location_menu_list
		#TODO: unset current_tpid
	
	def locationMenuCallback(self):
		self.notifyable = False
		
		#appuifw.note(unicode(menu_item_selected), "info") # debugging
		appuifw.app.exit_key_handler = self.locationListCallback
		
		loc = self.location_cache.getLocation(self.current_tpid)
		menu_item_selected = self.location_menu_list.current()
		
		if menu_item_selected == 0: # general description
			appuifw.app.body = appuifw.Text( unicode(loc['description']) )
			
		elif menu_item_selected == 1: # menu
			appuifw.app.body = appuifw.Text( unicode(loc['sections']['Menu']) )
		elif menu_item_selected == 2: # hours
			appuifw.app.body = appuifw.Text( unicode(loc['sections']['Hours']) )
		elif menu_item_selected == 3: # comments
			#self.locationComment()
			appuifw.app.exit_key_handler = self.locationListCallback
			self.comment_list = self.location_cache.getLocationCommentsList(self.current_tpid)
			self.comment_listbox = appuifw.Listbox(self.comment_list, self.locationCommentCallback)
			appuifw.app.body = self.comment_listbox
		elif menu_item_selected == 4: # bookmark
			bookmark()
		elif menu_item_selected == 5: # hide POI
			hide_POI()
		
	def exit(self):
		yesno = appuifw.query(u"Do you want to exit", "query")
		if yesno == 1:
			self.lock.signal()