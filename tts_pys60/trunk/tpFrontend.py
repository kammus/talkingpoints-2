#main -- GUI + SUI  etc..
import socket
import e32
import time

e32.start_server('e:\\python\\tpBackend.py')
e32.start_server('e:\\python\\bluetoothReader.py')
e32.start_server('e:\\python\\gpsLocationProvider.py')

#wait until server is running
time.sleep(5)

class FrontendClient:
	
	def __init__(self):
		self.HOST = '127.0.0.1'
		self.F_PORT = 2190
			
	def clientStart(self):
		self.s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		self.s.connect((self.HOST, self.F_PORT))
		while 1:
			self.data = self.s.recv(1024)
			if len(self.data) == 0: break #This would need to be changed
			print 'You are getting close to ', self.data #place a code for screen display here
			
	def clientClose(self):
		self.s.close()	

frontendClient = FrontendClient()
frontendClient.clientStart()
frontendClient.clientClose()