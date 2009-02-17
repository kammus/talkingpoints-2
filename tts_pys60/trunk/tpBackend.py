#the place where everything comes together
import socket
import time

class BackendConnection:
	
	def __init__(self):
		self.HOST = '127.0.0.1'
		self.BLUETOOTH_PORT = 843
		self.FRONT_PORT = 2190
		#Dummy location information
		self.a = ['Espresso Royale', 'EECS Building']
		
			
	def connectionStart(self):
		self.b_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		self.front_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		self.b_socket.connect((self.HOST, self.BLUETOOTH_PORT))
		self.front_socket.bind((self.HOST, self.FRONT_PORT))
		self.front_socket.listen(1)
		self.conn, self.addr = self.front_socket.accept()
		#Receive data from bluetoothReader.py
		while 1:
			data = self.b_socket.recv(1024)
			#Waiting for client connecion
			#send object here
			self.conn.send(self.a[int(data)])	
			
	def connectionClose(self):
		self.b_socket.close()
		self.front_socket.close()	

backendConnection = BackendConnection()
backendConnection.connectionStart()
backendConnection.connectionClose()

class ServerAPI:
	
	def __init__(self):
		self.server_host = "http://grocs.dmc.dc.umich.edu:3000"
    
	def get_location_by_bluetooth_mac(mac): 
		request_url = self.server_host + "/locations/show_by_bluetooth_mac/" + mac + ".json"
        response = urllib.urlopen(request_url).read()
        return json.read(response)

	def get_nearby_locations(lat, lng):
		request_url = self.server_host + "/locations/get_nearby/" + lat + ";" + lng + ".json"
        response = urllib.urlopen(request_url).read()
        return json.read(response)
