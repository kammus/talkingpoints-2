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
