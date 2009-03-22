import e32
import sys
if e32.in_emulator():
	sys.path.append("C:\\python\\") # for emulator testing
import appuifw
import gpsLocationProvider
import ServerAPI
import GUI

app_lock = e32.Ao_lock()
server = ServerAPI.ServerAPI()
gui = GUI.GUI(app_lock)
gps = gpsLocationProvider.GpsLocProvider(1, 1, gui)

gui.location_cache.appendLocation(server.get_location(1))
gui.location_cache.appendLocation(server.get_location(2))

gui.drawLocationList()

timer = e32.Ao_timer()

while not gui.terminated:
	if gps.newActives == 1:
		localActives = gps.actives
		for poi in localActives:
			if not gui.location_cache.checkLocationsForTPID(poi["tpid"]):
				gui.location_cache.appendLocation(poi)
				gui.notifyOfNewLocation(poi["name"])
	e32.ao_yield()
	timer.after(1)
	
#app_lock.wait()