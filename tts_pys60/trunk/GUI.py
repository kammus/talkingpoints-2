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
		self.location_menu_listbox = []
		self.comment_list = []
		self.notifyable = 1
		self.terminated = 0
        
		self.current_tpid = None
		
		self.lock = app_lock
		appuifw.app.exit_key_handler = self.exit		
		self.location_cache = LocationCache.LocationCache()
		
	def drawText(self, string):
		text = appuifw.Text() 
		appuifw.app.body = text
		
		text.font = u"LatinBold19"
		text.style = appuifw.STYLE_BOLD
		text.color = 0x000000
		text.add( string )
		return text
		
	def locationCommentCallback(self):
		
		comment_selected = self.comment_listbox.current()
		appuifw.app.exit_key_handler = self.locationMenuCallback
		self.drawText (self.comment_list[comment_selected])
#		comment_text = appuifw.Text()
#		appuifw.app.body = comment_text
#		
#		comment_text.style = appuifw.STYLE_BOLD
#		comment_text.add(self.comment_list[comment_selected])
		
		
		
	def notifyOfNewLocation(self, location_name):
		if self.notifyable == 1:
			appuifw.note(unicode("You are getting close to " + location_name), 'info')
			self.drawLocationList()
	
	def drawLocationList(self):
		self.notifyable = 1
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
		self.notifyable = 0
		appuifw.app.exit_key_handler = self.drawLocationList
		
		if self.current_tpid == None:
			self.current_tpid = self.location_listbox_mapping[self.location_listbox.current()]
		
		tmp = self.location_cache.getLocationMenuList(self.current_tpid)
		self.location_menu_list = tmp['list']
		self.location_menu_mapping = tmp['mapping']
		
		self.location_menu_listbox = appuifw.Listbox(
			self.location_menu_list,
			self.locationMenuCallback
		)
		appuifw.app.body = self.location_menu_listbox
		
	
	def locationMenuCallback(self):
		self.notifyable = 0
		appuifw.app.exit_key_handler = self.locationListCallback
		
		menu_item_selected = self.location_menu_listbox.current()
		
		if self.location_menu_mapping[menu_item_selected] == "Comments":
			appuifw.app.exit_key_handler = self.locationListCallback
			self.comment_list = self.location_cache.getLocationCommentsList(self.current_tpid)
			self.comment_listbox = appuifw.Listbox(self.comment_list, self.locationCommentCallback)
			appuifw.app.body = self.comment_listbox
		else:
			appuifw.app.body = appuifw.Text( self.location_menu_mapping[ menu_item_selected ] )
		
		
	def exit(self):
		yesno = appuifw.query(u"Do you want to exit", "query")
		if yesno == 1:
			self.terminated = 1
			self.lock.signal()