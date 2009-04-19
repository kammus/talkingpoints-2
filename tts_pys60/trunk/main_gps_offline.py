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

inet_mode = "offline"

app_lock = e32.Ao_lock()
server = serverAPI.ServerAPI(inet_mode)
gui = GUI.GUI(app_lock)
gps = gpsLocationProvider.GpsLocProvider(25, 50, gui)
gps.server = server

#gui.location_cache.appendLocation(server.get_location(1))
#gui.location_cache.appendLocation(server.get_location(2))

gui.drawLocationList()

timer = e32.Ao_timer()

draw_time = time.clock() # time the UI has last been updated

while not gui.terminated:
	if gps.newActives == 1:
		localActives = gps.actives
		#print localActives
		#e32.ao_sleep(30)
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