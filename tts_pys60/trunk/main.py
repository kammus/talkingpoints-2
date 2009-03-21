import sys
sys.path.append("C:\\python\\tp") # for emulator testing
import e32
import appuifw

import ServerAPI
import GUI

app_lock = e32.Ao_lock()
server = ServerAPI.ServerAPI()
GUI = GUI.GUI(app_lock)
GUI.location_cache.appendLocation(server.get_location(1))
GUI.location_cache.appendLocation(server.get_location(2))

GUI.drawLocationList()

app_lock.wait()