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
		
		appuifw.app.screen='normal'
		self.tp_title = appuifw.app.title
		appuifw.app.title = u"Talking Points"
	   
		self.exit_flag=False
		appuifw.app.exit_key_handler = self.clientClose
	   
		canvas=appuifw.Canvas(event_callback = keys)
		appuifw.app.body = canvas
			
	def clientStart(self):
		self.s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		self.s.connect((self.HOST, self.F_PORT))
		while 1:
			self.data = self.s.recv(1024)
			if len(self.data) == 0: break #This would need to be changed
			loc_out = (u"you are getting close to" + self.data)
			appuifw.note(loc_out, "info")
			breakdown = loc_out.split(u" ")
			audio.say(breakdown)
			#e32.ao_sleep(5)
			#print 'You are getting close to ', self.data #place a code for screen display here
			self.help()
			self.keys()
			self.lock.wait()
	
	def keys(event):
   	    if event['key_code'] == key_codes.EKeyLeftArrow:
   	   	   appuifw.note(u"back")
   	   	   self.back()
   	    elif event['key_code'] == key_codes.EKeyRightArrow:
   	   	   appuifw.note(u"more")
   	   	   self.more()
   	    elif event['key_code'] == key_codes.EKeyUpArrow:
   	   	   appuifw.note(u"help")
   	   	   self.help() #help function
   	    else:
   	   	   self.lock.wait()	
			
	def more(self):
		
		appuifw.note(u"Espresso Royal. Enjoy your coffee")
		
	def back(self):
		#back function goes here
		clientStart(self)
		
	def help(self):
		#help function goes here
		help_out = (u"""To go back to previous page, push Left Arrow Key.
		For detailed information of your point of interest, push Right Arrow key.
		For instruction, use Up arrow key.""")
		appuifw.Text(help_out)
		breakdown_help = help_out.split(u" ")
		audio.say(breakdown_help)
		
	def clientClose(self):
		self.exit_flag=True
 		print "Exiting Talking Points"
   		self.lock.signal()
		self.s.close()	

frontendClient = FrontendClient()
frontendClient.clientStart()
frontendClient.clientClose()

#separate server side and UI side in class level
#location_cache => store object for previous, current, nest location data
