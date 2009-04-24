import e32
import sys
if e32.in_emulator(): # for emulator testing
	sys.path.append("c:\\python\\")
	sys.path.append("c:\\python\\tp")
else:
	sys.path.append("e:\\python\\")
	sys.path.append("e:\\python\\tp")
	

import string
import time
import appuifw

import gpsLocationProvider
import serverAPI
import GUI

inet_mode = "online"

app_lock = e32.Ao_lock()
server = serverAPI.ServerAPI(inet_mode)
gui = GUI.GUI(app_lock)
gps = gpsLocationProvider.GpsLocProvider(gui, 25, 50, 1)
gps.server = server

#gui.location_cache.appendLocation(server.get_location(1))
#gui.location_cache.appendLocation(server.get_location(2))

gui.drawLocationList()

timer = e32.Ao_timer()

draw_time = time.clock() # time the UI has last been updated

while not gui.terminated:
	gps.nearbyLock.acquire()
	
	if len(gps.removedActives) > 0:
		for tpid in gps.removedActives:
			gui.location_cache.removeLocation(tpid)
		gps.removedActives = [ ]
		
	gps.nearbyLock.release()
	
	if gps.newActives:
		localActives = gps.actives
		for poi in localActives:
			if not gui.location_cache.checkLocationsForTPID(poi["tpid"]):
				gui.location_cache.appendLocation(poi)
				distance = string.split(str(poi["distance"]), ".")[0]
				gui.notifyOfNewLocation(poi["name"] + " [" + distance + "m away]")
				draw_time = time.clock()
		
	elif (time.clock() - draw_time) > 60:
		if gui.notifyable == 1:
			gui.drawLocationList()
		draw_time = time.clock()
		
	e32.ao_yield()
	timer.after(1)
	
#app_lock.wait()