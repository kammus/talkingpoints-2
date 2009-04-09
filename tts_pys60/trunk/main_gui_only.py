import sys
sys.path.append("c:\\python\\tp")
sys.path.append("e:\\python\\tp")

import e32
import appuifw

import serverAPI
import GUI

inet_mode = "offline"

app_lock = e32.Ao_lock()
server = serverAPI.ServerAPI(inet_mode)
GUI = GUI.GUI(app_lock)
GUI.server = server

GUI.location_cache.appendLocation(server.get_location(1))
GUI.location_cache.appendLocation(server.get_location(2))

GUI.drawLocationList()

app_lock.wait()