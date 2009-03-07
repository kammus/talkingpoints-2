#main -- GUI + SUI  etc..
import socket
import e32
import time
import appuifw
import audio
e32.start_server('e:\\python\\tpBackend.py')
e32.start_server('e:\\python\\bluetoothReader.py')
e32.start_server('e:\\python\\gpsLocationProvider.py')

#wait until server is running
time.sleep(5)

class FrontendClient:
	
	def __init__(self):
		self.HOST = '127.0.0.1'
		self.F_PORT = 2190
		self.lock=e32.Ao_lock()
				
	def clientStart(self):
		self.s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		self.s.connect((self.HOST, self.F_PORT))
#		while 1:
#			self.data = self.s.recv(1024)
#			if len(self.data) == 0: break #This would need to be changed
#			loc_out = (u"you are getting close to" + self.data)
#			appuifw.note(loc_out, "info")
#			breakdown = loc_out.split(u" ")
#			audio.say(breakdown)
#			#e32.ao_sleep(5)
#			#print 'You are getting close to ', self.data #place a code for screen display here
#			self.help()
#			self.keys()
#			self.lock.wait()
	
	def clientClose(self):
		
 		print "Exiting Talking Points"
   		self.lock.signal()
		self.s.close()	

class FrontUI(FrontendClient):
	
	def __init__(self):
		appuifw.app.screen='normal'
		self.tp_title = appuifw.app.title
		appuifw.app.title = u"Talking Points"
	   
		self.exit_flag=False
		appuifw.app.exit_key_handler = self.uiClose
		
		location_cache = {} #dictionary to save previous, current, next location data
		
		menu_options=[u"More Information", u"Previous Location", u"Next Location", u"Help"]
	   
		
	def uiStart(self):
		canvas=appuifw.Canvas(event_callback = keys)
		appuifw.app.body = canvas
		while clientStart():
			self.data = self.s.recv(1024)
			location_cache['currentVal'] = self.data
			if len(self.data) == 0: break #This would need to be changed
			loc_out = (u"you are getting close to" + self.data)
			appuifw.note(loc_out, "info")

#			breakdown = loc_out.split(u" ")
#			audio.say(breakdown)
#			e32.ao_sleep(5)
#			print 'You are getting close to ', self.data #place a code for screen display here
			self.help()
			self.keys()
			self.lock.wait()
			
	def show_menu_screen(self):
		appuifw.app.body=menu
		appuifw.app.menu=[(u"Select", menu_callback),(u"Exit", uiClose)]
		appuifw.app.exit_key_handler = self.uiClose	
		menu=appuifw.Listbox(menu_options, menu_callback)	
	
	def menu_callback(self):
		ltem_selected=menu.current()
		if item_selected==0:
			self.more()
		elif item_selected==1:
			self.back()
		elif item_selected==2:
			self.next_loc()
		else:
			self.help()
				
	def keys(event):
   	    if event['key_code'] == key_codes.EKeyLeftArrow:
   	   	   appuifw.note(u"back")
 #  	   	   audio.say(u"back")
   	   	   self.back()
   	    elif event['key_code'] == key_codes.EKeyRightArrow:
   	   	   appuifw.note(u"more")
 #  	   	   audio.say(u"more")
   	   	   self.more()
   	    elif event['key_code'] == key_codes.EKey5:
   	       self.show_menu_screen()
   	    elif event['key_code'] == key_codes.EKeyUpArrow:
   	   	   appuifw.note(u"help")
 #  	   	   audio.say(u"help")
   	   	   self.help()
   	    else:
   	   	   self.lock.wait()	
			
	def more(self):
		#get detail of self.data
		appuifw.note(u"Espresso Royal. Enjoy your coffee")
		
	def back(self):
		#location_cache['preVal']
		appuifw.note(u"previous location")
	def next_loc(self):
		#locaion_cache['nextVal']
		appuifw.note(u"next location")
	
	def help(self):
		#help function goes here
		help_out = (u"""To go back to previous page, push Left Arrow Key.
		For detailed information of your point of interest, push Right Arrow key.
		For instruction, use Up arrow key.""")
		appuifw.Text(help_out)
#		breakdown_help = help_out.split(u" ")
#		audio.say(breakdown_help)

	def uiClose(self):
		self.exit_flag=True
		self.lock.signal()
		
frontendClient = FrontendClient()
frontUI = FrontUI()
frontendClient.clientStart()
frontUI.uiStart()
frontendClient.clientClose()
frontUI.uiClose()


#separate server side and UI side in class level
#location_cache => store object for previous, current, nest location data
