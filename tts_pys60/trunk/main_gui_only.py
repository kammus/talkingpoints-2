import sys
sys.path.append("C:\\python\\tp") # for emulator testing
import e32
import appuifw

import ServerAPI
import GUI

app_lock = e32.Ao_lock()
server = ServerAPI.ServerAPI()
GUI = GUI.GUI(app_lock)

## check if alreay detected
#if GUI.location_cache.checkLocationsForTPID(tpid) == False
#	loc = server.get_location(tpid)
#	GUI.location_cache.appendLocation(loc)
#	GUI.notify(loc['name'])

GUI.location_cache.appendLocation(server.get_location(1))
GUI.location_cache.appendLocation(server.get_location(2))

GUI.drawLocationList()

#print str(GUI.location_cache.getLocationCommentsList(1))

app_lock.wait()