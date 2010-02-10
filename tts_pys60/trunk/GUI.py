import sys
sys.path.append("c:\\python\tp")

import e32
import appuifw
import appuifw2 #enables Text_display 
import LocationCache
import LocationStore
import serverAPI
import time


class GUI:
    def __init__(self, app_lock):
        appuifw.app.screen = 'normal'
        appuifw.app.title = u"Talking Points"
        
        self.location_listbox = []
        self.location_listbox_mapping = {}
        self.location_menu_list = []
        self.location_menu_listbox = []
        self.location_details_text = []
        
        self.comment_list = []
        self.notifyable = 1
        self.terminated = 0
        
        self.redraw_flag = 0
        self.gui_state = "LocationList"
        self.gui_state_stack = []
        
        self.current_tpid = None
        self.current_position = {} # will be updated by GPS
        
        self.lock = app_lock
        appuifw.app.exit_key_handler = self.exit
        appuifw2.app.exit_key_text = u"Exit" 
        appuifw2.app.menu_key_text = u"Menu"       
        self.location_cache = LocationCache.LocationCache()
        self.location_store = LocationStore.LocationStore()
        
        self.return_gui_state = None
        self.current_gui_state = None

        self.server = None

    def drawText(self, string):
        txt = appuifw2.Text_display(skinned=True, scrollbar=True, scroll_by_line=False) 
        appuifw2.app.body = txt
        
        txt.font = u"LatinBold19"
        txt.style = appuifw.STYLE_BOLD
#        txt.color = 0x000000
        txt.add( unicode(string) )
        return txt

    # monolithic GUI update function, meant to be called by main thread
    def drawGUI(self):
        self.redraw_flag = 0
        # This menu should be on all pages of the GUI, right?
        appuifw.app.menu = [(u"Navigate", (
                                (u"Where am I?", self.whereAmI),
                                (u"Nearby", self.nearby),
                                (u"Search", self.search))
                            ), 
                            (u"Bookmarked Locations", self.drawBookmarkedLocationList),
                            (u"Hidden Locations", self.drawHiddenLocationList),
                            (u"Settings", (
                                (u"Input Mode", self.inputMode),
                                (u"Font Size", self.fontSize))
                            )]
                            #seleting global menu by left softkey
        appuifw2.app.menu_key_text = u"Menu"
        
        if self.gui_state == "LocationList":
            self.notifyable = 1
            self.current_tpid = None
            appuifw.app.exit_key_handler = self.exit
            appuifw2.app.exit_key_text = u"Exit"
        
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
        
        elif self.gui_state == "LocationMenu":
            appuifw.app.exit_key_handler = self.goBack
            appuifw2.app.exit_key_text = u"Back"
            self.location_menu_listbox = appuifw.Listbox(
            self.location_menu_list,
            self.locationMenuCallback
        )
            appuifw.app.body = self.location_menu_listbox
        elif self.gui_state == "CommentsList":
            if self.comment_list != None:
                appuifw.app.exit_key_handler = self.goBack
                appuifw2.app.exit_key_text = u"Back"
                self.comment_listbox = appuifw.Listbox(self.comment_list, self.commentMenuCallback)
                appuifw.app.body = self.comment_listbox
            else:
                appuifw.note(u"There are no comments yet.")
                self.gui_state = self.gui_state_stack.pop()
        elif self.gui_state == "CommentView":
            appuifw2.app.body = self.location_details_text
        elif self.gui_state == "Bookmark": 
            msg = self.location_store.bookmark(self.current_tpid, self.location_cache.getLocationName(self.current_tpid))
            appuifw.note(unicode(msg))
            self.gui_state = self.gui_state_stack.pop()      
        elif self.gui_state == "Hide":
            msg = self.location_store.hide(self.current_tpid, self.location_cache.getLocationName(self.current_tpid))
            appuifw.note(unicode(msg))
            self.gui_state = self.gui_state_stack.pop() 
        elif self.gui_state == "Details":
            appuifw2.app.body = self.location_details_text
        elif self.gui_state == "Bookmarks":
            appuifw.app.body = self.bookmarked_listbox
            appuifw.app.menu = [(u"Delete", self.deleteBookmark), (u"Select", self.bookmarkedListCallback)]
    
    def locationListCallback(self):
        self.notifyable = 0
        self.redraw_flag = 1
        self.gui_state_stack.append(self.gui_state)
        self.gui_state = "LocationMenu"
        
        if self.current_tpid == None:
            self.current_tpid = self.location_listbox_mapping[self.location_listbox.current()]
        
        #self.drawLocationMenu()
        tmp = self.location_cache.getLocationMenuList(self.current_tpid)
        self.location_menu_list = tmp['list']
        self.location_menu_mapping = tmp['mapping']
        self.lock.signal()
        
    # sets up gui for detailed location display
    def locationMenuCallback(self):
        self.notifyable = 0
        self.redraw_flag = 1
        self.gui_state_stack.append(self.gui_state)
        
        menu_item_nr_selected = self.location_menu_listbox.current()
        menu_item_text_selected = self.location_menu_mapping[menu_item_nr_selected]
        
        if menu_item_text_selected == "Comments":
            self.gui_state = "CommentsList"
            self.comment_list = self.location_cache.getLocationCommentsList(self.current_tpid)
            self.lock.signal()
        elif menu_item_text_selected == "Bookmark":
            self.gui_state = "Bookmark"
            self.lock.signal()
        elif menu_item_text_selected == "Hide":
            self.gui_state = "Hide"
            self.lock.signal()
        else:
            self.gui_state = "Details"
            self.location_details_text = appuifw2.Text_display(skinned=1, scrollbar=1, scroll_by_line=0)
            self.location_details_text.add(menu_item_text_selected)
            self.location_details_text.font = u"LatinBold19"
            self.location_details_text.style = appuifw.STYLE_BOLD
            self.lock.signal()
            
        #else:

            #appuifw2.app.body = appuifw2.Text_display( menu_item_text_selected )
         #   self.drawText( menu_item_text_selected )
        
    def commentMenuCallback(self):
        self.notifyable = 0
        self.redraw_flag = 1
        self.gui_state_stack.append(self.gui_state)    
        self.gui_state = "CommentView"
        
        comment_selected = self.comment_listbox.current()
        self.location_details_text = appuifw2.Text_display(skinned=1, scrollbar=1, scroll_by_line=0)
        self.location_details_text.add(self.comment_list[comment_selected])
        self.location_details_text.font = u"LatinBold19"
        self.location_details_text.style = appuifw.STYLE_BOLD
        self.lock.signal()
    
    def goBack(self):
        self.gui_state = self.gui_state_stack.pop()
        self.redraw_flag = 1
        self.lock.signal()
        
#    def drawText(self, string):
#        global text
#        text = appuifw.InfoPopup() 
#        text.show( unicode(string), (30,50), 20000, 0)
#        return text

        
    def locationCommentCallback(self):
		self.current_gui_state = locationCommentCallback
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
            self.redraw_flag = 1
            self.lock.signal()
            #self.drawLocationList()
	
    def drawBookmarkedLocationList(self):
        self.notifyable = 0
        self.redraw_flag = 1
        self.gui_state_stack.append(self.gui_state)
        self.gui_state = "Bookmarks"
        
        appuifw.app.exit_key_handler = self.goBack()
        appuifw2.app.exit_key_text = u"Back"        
        tmp = self.location_store.getBookmarkedLocationsList()
        if len(tmp['list']) > 0:
            bookmarked_list = tmp['list']
            self.bookmarked_listbox_mapping = tmp['mapping']
            self.bookmarked_listbox = appuifw.Listbox(
                bookmarked_list,
                self.bookmarkedListCallback
            )  
        else:
            appuifw.note(u"No bookmarked locations")
            self.gui_state = self.gui_state_stack.pop()
        self.lock.signal()
            
#    def deleteBookmark(self):
#        appuifw.note(u"delete bookmark")
##        if self.current_tpid == None:
##            self.current_tpid = self.bookmarked_listbox_mapping[self.bookmarked_listbox.current()]
##        
##        self.location_store.unbookmark(self.current_tpid)
##        #self.drawBookmarkedLocationList()
            
    def bookmarkedListCallback(self):
        self.notifyable = 0
        appuifw.app.exit_key_handler = self.drawBookmarkedLocationList
        appuifw2.app.exit_key_text = u"Back"
        appuifw.app.menu = [(u"Navigate", (
                                (u"Where am I?", self.whereAmI),
                                (u"Nearby", self.nearby),
                                (u"Search", self.search))
                            ), 
                            (u"Bookmarked Locations", self.drawBookmarkedLocationList),
                            (u"Hidden Locations", self.drawHiddenLocationList),
                            (u"Settings", (
                                (u"Input Mode", self.inputMode),
                                (u"Font Size", self.fontSize))
                            )]
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
        self.notifyable = 0
        appuifw.app.exit_key_handler = self.drawLocationList
        appuifw2.app.exit_key_text = u"Back"
        tmp = self.location_store.getHiddenLocationsList()
        appuifw.app.menu = [(u"Navigate", (
                                (u"Where am I?", self.whereAmI),
                                (u"Nearby", self.nearby),
                                (u"Search", self.search))
                            ), 
                            (u"Bookmarked Locations", self.drawBookmarkedLocationList),
                            (u"Hidden Locations", self.drawHiddenLocationList),
                            (u"Settings", (
                                (u"Input Mode", self.inputMode),
                                (u"Font Size", self.fontSize))
                            )]
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
        appuifw2.app.exit_key_text = u"Back"
        appuifw.app.menu = [(u"Navigate", (
                                (u"Where am I?", self.whereAmI),
                                (u"Nearby", self.nearby),
                                (u"Search", self.search))
                            ), 
                            (u"Bookmarked Locations", self.drawBookmarkedLocationList),
                            (u"Hidden Locations", self.drawHiddenLocationList),
                            (u"Settings", (
                                (u"Input Mode", self.inputMode),
                                (u"Font Size", self.fontSize))
                            )]
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
    def whereAmI(self):
        self.notifyable = 0
        appuifw.app.exit_key_handler = self.drawLocationList
        appuifw2.app.exit_key_text = u"Back"
        appuifw.app.body = appuifw.Text( unicode("Retrieving the current address ...") )
        
        if self.server == None:  self.server = serverAPI.ServerAPI("offline")
        
        if len(self.current_position) > 0:
            address = self.server.getCurrentAddress(self.current_position['lat'], self.current_position['lng'])
            freshness = int(time.clock() - self.current_position['timestamp'])
        else: #dummy data
            address = self.server.getCurrentAddress(42.2749661, -83.736541)
            freshness = 1
        
        if address != None:
            appuifw.app.body = appuifw.Text(u"You were at: " + address + ", " + str(freshness) + " seconds ago")
        else:
            appuifw.app.body = appuifw.Text(u"Sorry, we could not get the current address")
        
    def nearby(self):
        appuifw.note(u"get nearby locations of a certain type")
        
    def search(self):
        appuifw.note(u"search for locations")
        
    def inputMode(self):
        appuifw.note(u"input mode isn't implemented at this time. Only Key input allowed")
    
    def deleteBookmark(self):
        appuifw.note(u"bookmarking isn't implemented at this time. Only Key input allowed")
    
    def fontSize(self):
        appuifw.note(u"Font size option will be available soon")
    
    # ---------------------------
    
    def drawLocationList(self):
        self.notifyable = 1
        self.current_tpid = None
        appuifw.app.exit_key_handler = self.exit
        appuifw2.app.exit_key_text = u"Exit"
        appuifw.app.menu = [(u"Navigate", (
                                (u"Where am I?", self.whereAmI),
                                (u"Nearby", self.nearby),
                                (u"Search", self.search))
                            ), 
                            (u"Bookmarked Locations", self.drawBookmarkedLocationList),
                            (u"Hidden Locations", self.drawHiddenLocationList),
                            (u"Settings", (
                                (u"Input Mode", self.inputMode),
                                (u"Font Size", self.fontSize))
                            )]
                            #seleting global menu by left softkey
        appuifw2.app.menu_key_text = u"Menu"
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
            
   
    def drawLocationMenu(self):
        self.notiyfyable = 0
        tmp = self.location_cache.getLocationMenuList(self.current_tpid)
        self.location_menu_list = tmp['list']
        self.location_menu_mapping = tmp['mapping']
        appuifw.app.menu = [(u"Navigate", (
                                (u"Where am I?", self.whereAmI),
                                (u"Nearby", self.nearby),
                                (u"Search", self.search))
                            ), 
                            (u"Bookmarked Locations", self.drawBookmarkedLocationList),
                            (u"Hidden Locations", self.drawHiddenLocationList),
                            (u"Settings", (
                                (u"Input Mode", self.inputMode),
                                (u"Font Size", self.fontSize))
                            )]
        
        self.location_menu_listbox = appuifw.Listbox(
            self.location_menu_list,
            self.locationMenuCallback
        )
        appuifw.app.body = self.location_menu_listbox
    	
    
    
    def exit(self):
        yesno = appuifw.query(u"Do you want to exit", "query")
        if yesno == 1:
            self.terminated = 1
            self.lock.signal()