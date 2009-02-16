#Bluetooth tag reader
import socket
import time

HOST= '127.0.0.1'
BACKEND_PORT = 843
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM) 

# allow the socket to be re-use immediately after a close
#s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)

s.bind((HOST, BACKEND_PORT)) #SERVER
s.listen(1) 

#Waiting for client connecion
conn, addr = s.accept()
while 1:
	conn.send('0')
	time.sleep(3)
	conn.send('1')
	time.sleep(3)	
s.close()