#main -- GUI + SUI  etc..
import socket
import e32
import time

e32.start_server('e:\\Python\\tpBackend.py')

#wait until server is running
time.sleep(5) # wait 5 seconds

HOST = '127.0.0.1'
F_PORT = 50007
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect((HOST, F_PORT))

while 1:
	data = s.recv(1024)
	if len(data) == 0: break #This would need to be changed
	print 'You are getting close to ', data #place a code for screen display here

s.close()