#GPS Reader
#set up a server here, and send dummy data via the port 30940
import time
import socket
import positioning
import math

class GpsLocProvider:
    
    def __init__(self):
        self.HOST = '127.0.0.1'
        self.BACKEND_PORT = 30940
        self.s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        # Initialize GPS module here
        positioning.select_module(positioning.default_module())
        positioning.set_requestors([{"type":"service", 
                             "format":"application",
                             "data":"GpsLocProvider"}])    
   
    def generateDummyGPSData(self):
        s.bind((self.HOST, BACKEND_PORT))
        s.listen(1)
        
        conn, addr = s.accept()
        while 1:
            conn.send({"latitude":-12.333, "longitude":22.333333})
            time.sleep(5)
    
    
    # pointA and pointB must be dictionaries with 'latitude' and 'longitude' entries
    def calcDistance(pointA, pointB):
        DegreesToRadians = math.pi/180
        R = 3956
        
        lat1 = pointA['latitude']
        lon1 = pointA['longitude']
        lat2 = pointB['latitude']
        lon2 = pointB['longitude']
        # convert to radians
        lat1 *= DegreesToRadians
        lon1 *= DegreesToRadians
        lat2 *= DegreesToRadians
        lon2 *= DegreesToRadians
        
        dlon = lon2 - lon1
        dlat = lat2 - lat1
 
        a = math.pow(math.sin(dlat/2), 2) + math.cos(lat1) * math.cos(lat2) * math.pow(math.sin(dlon/2), 2)
        c = 2 * math.atan2(math.sqrt(a), math.sqrt(1-a))
        d = R * c
        
        distInMiles = d * 1.1508
        return distInMiles
    
            