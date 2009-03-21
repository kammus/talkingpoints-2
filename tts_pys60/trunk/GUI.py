import e32
import appuifw
import LocationCache

class GUI:
	def __init__(self, app_lock):
		appuifw.app.screen = 'normal'
		appuifw.app.title = u"Talking Points"
		self.location_list = []
		self.notifyable = True
		self.lock = app_lock
#		appuifw.app.exit_key_handler = self.close
		
		self.location_cache = LocationCache.LocationCache()
		
#		self.app_lock = e32.Ao_lock()
#		self.app_lock.wait()
		
	def notifyOfNewLocation(self, location_name):
		if self.notifyable == True:
			apuifw.note(unicode("You are getting close to " + location_name), 'info')
			self.drawLocationList()
	
	def drawLocationList(self):
		#global location_cache
		self.notifyable = True
		appuifw.app.body = self.location_list = appuifw.Listbox(self.location_cache.getCurrentLocationList(), self.detail_location_callback)

	def detail_location_callback(self):
		self.notifyable = False
		appuifw.note(unicode(self.location_list.current()), "info")
		
	def close(self):
		self.lock.signal()