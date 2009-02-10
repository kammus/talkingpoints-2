#Bluetooth tag reader
import socket
import time

HOST= '127.0.0.1'
PORT = 7067
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM) 
s.bind((HOST, PORT)) #SERVER
s.listen(1) 

#Waiting for client connecion
conn, addr = s.accept()
conn.send('0')
time.sleep(5) # wait 10 seconds
conn.send('1')
time.sleep(5)	
s.close()