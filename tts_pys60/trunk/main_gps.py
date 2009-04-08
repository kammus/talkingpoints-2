import e32
import time
import sys
if e32.in_emulator():
	sys.path.append("C:\\python\\") # for emulator testing
import appuifw
import gpsLocationProvider
import ServerAPI
import GUI

inet_mode = "offline"

app_lock = e32.Ao_lock()
server = ServerAPI.ServerAPI(inet_mode)
gui = GUI.GUI(app_lock)
gps = gpsLocationProvider.GpsLocProvider(15, 50, gui)

gui.location_cache.appendLocation(server.get_location(1))
gui.location_cache.appendLocation(server.get_location(2))

gui.drawLocationList()

timer = e32.Ao_timer()

while not gui.terminated:
	if gps.newActives == 1:
		localActives = gps.actives
		#print localActives
		#e32.ao_sleep(30)
		for poi in localActives:
			if not gui.location_cache.checkLocationsForTPID(poi["tpid"]):
				gui.location_cache.appendLocation(poi)
				gui.notifyOfNewLocation(poi["name"] + "[" + str(int(poi["distance"])) + "m away]")
	e32.ao_yield()
	timer.after(1)
	
#app_lock.wait()