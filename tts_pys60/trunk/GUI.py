import e32
import appuifw
from main import location_cache

class GUI:
	def __init__(self):
		appuifw.app.screen = 'normal'
		appuifw.app.title = u"Talking Points"
		self.location_list = []
		self.notifyable = True
		appuifw.app.exit_key_handler = self.uiClose
		
		#self.location_cache = LocationCache()
		
	def notifyOfNewLocation(self, location_name):
		if self.notifyable == True:
			apuifw.note(unicode("You are getting close to " + locaiton_name), 'info')
			self.drawLocationList()
	
	def drawLocationList(self):
		global location_cache
		self.notifyable = True
		self.location_list = location_cache.getCurrentLocationList()
		appuifw.app.body = appuifw.Listbox(self.location_list, self.detail_location_callback)

	def detail_location_callback(self):
		self.notifyable = False
		appuifw.note(unicode(self.location_list.current()), "info")
		
	def uiClose():
		self.app_lock.signal()