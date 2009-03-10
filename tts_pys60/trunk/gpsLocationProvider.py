#GPS Reader
#set up a server here, and send dummy data via the port 30940
import appuifw
import urllib
import time
import socket
import positioning
import math
import string
import json

class GpsLocProvider:
    
    def __init__(self, neardist, fardist):
        self.HOST = '127.0.0.1'
        self.BACKEND_PORT = 30940
        self.s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.currentLoc = { }
        self.nearbyPOIs = [ ]
        self.nearTolerance = neardist #how close we need to be to a POI to trigger it
        self.farTolerance = fardist #how far we must be from a POI before it's removed from the recent list
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
            conn.send({"lat":-12.333, "lng":22.333333})
            time.sleep(5)
    
    def getCurrentLocation(self):
        tempDict = positioning.position()
        self.currentLoc = {"lat":tempDict["position"]["latitude"], "lng":tempDict["position"]["longitude"]}
        return self.currentLoc
    
    # incPOIs: list of new POIs (which are dictionaries)
    def addPOIs(self, incPOIs): 
        # Check for duplicates
        for poi in incPOIs:
            if poi not in nearbyPOIs:
                self.nearbyPOIs.add(poi)
    
    # 
    def Update(self):
        initialTime = time.clock()
        activeList = [ ] 
        # get current location, compare to old current location
        newLoc = self.getCurrentLocation()
        # if no(or little) change, don't do anything
        latDif = math.fabs(newLoc["lat"] - self.currentLoc["lat"])
        lngDif = math.fabs(newLoc["lng"] - self.currentLoc["lng"])
        if latDif < 0.0001 or lngDif < .001:
            print "Haven't moved far enough.  Cancelling update."
            return activeList
        else:
            self.currentLoc = newLoc
        
        for poi in nearbyPOIs:
            dist = calcDistance(self.currentLoc, poi) #poi must have "lat" and "lng" entries
            if dist <= nearTolerance:
                activelist.append(poi)
            if dist >= farTolerance:
                recentPOIs.remove(poi)
        print "Update in :", time.clock() - initialTime       
        return activeList
    
    def get_nearby_locations(self, lat, lng):
        self.server_host = "http://test.talking-points.org"
        latstr = str(lat)
        lngstr = str(lng)
        lat = string.replace(latstr, '.', ',')
        lng = string.replace(lngstr, '.', ',') 
        request_url = self.server_host + "/locations/get_nearby/" + lat + ";" + lng + ".json"
        response = urllib.urlopen(request_url).read()
        return json.read(response)    
        
        
    # pointA and pointB must be dictionaries with 'lat' and 'lng' entries
    def calcDistance(pointA, pointB):
        DegreesToRadians = math.pi/180
        R = 3956
        
        lat1 = pointA['lat']
        lon1 = pointA['lng']
        lat2 = pointB['lat']
        lon2 = pointB['lng']
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
    
# test code begins here    
GPS = GpsLocProvider(1.1, 2)
GPS.getCurrentLocation()
while 1:
    newPOIs = GPS.get_nearby_locations(GPS.currentLoc["lat"], GPS.currentLoc["lng"])
    time.sleep(8)
    actives = GPS.Update()
    if len(actives) != 0:
        for poi in actives:
            notestring = (u"You are getting close to TPID " + poi["tpid"])
            appuifw.note(notestring, "info")

