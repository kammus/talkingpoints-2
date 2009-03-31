import sys
sys.path.append("c:\\python\tp")

import e32
import appuifw
import LocationCache
import LocationStore

class GUI:
    def __init__(self, app_lock):
		appuifw.app.screen = 'normal'
		appuifw.app.title = u"Talking Points"
		
		self.location_listbox = []
		self.location_listbox_mapping = {}
		self.location_menu_list = []
		self.location_menu_listbox = []
		
		self.comment_list = []
		self.notifyable = 1
		self.terminated = 0
        
		self.current_tpid = None
		
		self.lock = app_lock
		appuifw.app.exit_key_handler = self.exit		
		self.location_cache = LocationCache.LocationCache()
		self.location_store = LocationStore.LocationStore()
		
#    def drawText(self, string):
#        text = appuifw.Text() 
#        appuifw.app.body = text
#		
#        text.font = u"LatinBold19"
#        text.style = appuifw.STYLE_BOLD
#        text.color = 0x000000
#        text.add( string )
#        return text

    def drawText(self, string):
        global text
        text = appuifw.InfoPopup() 
        text.show( unicode(string), (30,50), 7000, 0)
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
    
    #== dummy callback functions from Global menu=================================        
    def gpsNotification(self):
        appuifw.note(u"Does this need to be called from gpsLocationProvider?")

    def drawBookmarkedLocationList(self):
    	appuifw.app.exit_key_handler = self.drawLocationList
    	tmp = self.location_store.getBookmarkedLocationsList()
    	if len(tmp['list']) > 0:
			bookmarked_list = tmp['list']
			self.bookmarked_listbox_mapping = tmp['mapping']
			self.bookmarked_listbox = appuifw.Listbox(
				bookmarked_list,
				self.bookmarkedListCallback
			)
			appuifw.app.body = self.bookmarked_listbox
        else:
			appuifw.note(u"No bookmarked locations")
			
    def bookmarkedListCallback(self):
		self.notifyable = 0
		appuifw.app.exit_key_handler = self.drawBookmarkedLocationList
		
		if self.current_tpid == None:
			self.current_tpid = self.bookmarked_listbox_mapping[self.bookmarked_listbox.current()]
		
		#self.drawLocationMenu()
		tmp = self.location_cache.getLocationMenuList(self.current_tpid)
		self.location_menu_list = tmp['list']
		self.location_menu_mapping = tmp['mapping']
		
		self.location_menu_listbox = appuifw.Listbox(
			self.location_menu_list,
			self.locationMenuCallback
	    )
		appuifw.app.body = self.location_menu_listbox
        
    def drawHiddenLocationList(self):
		appuifw.app.exit_key_handler = self.drawLocationList
		tmp = self.location_store.getHiddenLocationsList()
		print str(tmp)
		if len(tmp['list']) > 0:
			hidden_list = tmp['list']
			self.hidden_listbox_mapping = tmp['mapping']
			self.hidden_listbox = appuifw.Listbox(
				hidden_list,
				self.hiddenListCallback
			)
			appuifw.app.body = self.hidden_listbox
		else:
			appuifw.note(u"No hidden locations")
			
    def hiddenListCallback(self):
		self.notifyable = 0
		appuifw.app.exit_key_handler = self.drawHiddenLocationList
		
		if self.current_tpid == None:
			self.current_tpid = self.hidden_listbox_mapping[self.hidden_listbox.current()]
		
		#self.drawLocationMenu()
		tmp = self.location_cache.getLocationMenuList(self.current_tpid)
		self.location_menu_list = tmp['list']
		self.location_menu_mapping = tmp['mapping']
		
		self.location_menu_listbox = appuifw.Listbox(
			self.location_menu_list,
			self.locationMenuCallback
	    )
		appuifw.app.body = self.location_menu_listbox
    
    # --submenus of "Settings"--    
    def talkingSpeed(self):
        appuifw.note(u"talking speed option will be implemented soon")
        
    def inputMode(self):
        appuifw.note(u"input mode won't be implemented this time. Only Key input allowed")
	
    def fontSize(self):
        appuifw.note(u"Font size option will be available soon")
    
    #---------------------------
    #=============================================================================]
    def drawLocationList(self):
        self.notifyable = 1
        self.current_tpid = None
        appuifw.app.exit_key_handler = self.exit
        appuifw.app.menu = [(u"Navigate", self.gpsNotification), 
                            (u"Bookmarked Locations", self.drawBookmarkedLocationList),
                            (u"Hidden Locations", self.drawHiddenLocationList),
                            (u"Settings", ((u"Talking Speed", self.talkingSpeed),
                                           (u"Input Mode", self.inputMode),
                                           (u"Font Size", self.fontSize)))]
                            #seleting global menu by left softkey
        
        tmp = self.location_cache.getCurrentLocationList()
        if len(tmp['list']) > 0:
			location_list = tmp['list']
			self.location_listbox_mapping = tmp['mapping']
			self.location_listbox = appuifw.Listbox(
				location_list,
				self.locationListCallback
			)
			appuifw.app.body = self.location_listbox
        else:
			appuifw.app.body = appuifw.Text( unicode("Searching ...") )
			
			
    def locationListCallback(self):
        self.notifyable = 0
        appuifw.app.exit_key_handler = self.drawLocationList
		
        if self.current_tpid == None:
			self.current_tpid = self.location_listbox_mapping[self.location_listbox.current()]
		
		#self.drawLocationMenu()
        tmp = self.location_cache.getLocationMenuList(self.current_tpid)
        self.location_menu_list = tmp['list']
        self.location_menu_mapping = tmp['mapping']
		
        self.location_menu_listbox = appuifw.Listbox(
			self.location_menu_list,
			self.locationMenuCallback
	    )
        appuifw.app.body = self.location_menu_listbox
   
   	def drawLocationMenu(self):
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
		
		menu_item_nr_selected = self.location_menu_listbox.current()
		menu_item_text_selected = self.location_menu_mapping[menu_item_nr_selected]
		
		if menu_item_text_selected == "Comments":
			appuifw.app.exit_key_handler = self.locationListCallback
			self.comment_list = self.location_cache.getLocationCommentsList(self.current_tpid)
			self.comment_listbox = appuifw.Listbox(self.comment_list, self.locationCommentCallback)
			appuifw.app.body = self.comment_listbox
			
		elif menu_item_text_selected == "Bookmark":
			msg = self.location_store.bookmark(self.current_tpid, self.location_cache.getLocationName(self.current_tpid))
			appuifw.note(unicode(msg))
			
		elif menu_item_text_selected == "Hide":
			msg = self.location_store.hide(self.current_tpid, self.location_cache.getLocationName(self.current_tpid))
			appuifw.note(unicode(msg))
			
		else:
			appuifw.app.body = appuifw.Text( menu_item_text_selected )
		
    def exit(self):
		yesno = appuifw.query(u"Do you want to exit", "query")
		if yesno == 1:
			self.terminated = 1
			self.lock.signal()