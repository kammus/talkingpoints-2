#the place where everything comes together
import socket
import e32
import time

e32.start_server('e:\\python\\bluetoothReader.py')

#wait until server is running
time.sleep(3)

#HOST, PORT configuration
HOST = '127.0.0.1'
B_PORT = 7067
F_PORT = 50007

#Socket Initialization
b_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
front_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
b_socket.connect((HOST, B_PORT))
front_socket.bind((HOST, F_PORT))
front_socket.listen(1)

#Dummy location information
a = ['Espresso Royale', 'EECS Building']
conn, addr = front_socket.accept()
#Receive data from bluetoothReader.py
while 1:
	data = b_socket.recv(1024)
	#Waiting for client connecion
	#send object here
	conn.send(a[int(data)])	

#Close
b_socket.close()
front_socket.close()

