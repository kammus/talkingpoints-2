#GPS Reader
import appuifw
import e32
import urllib
import thread
import positioning
import math
import string
import time
import sys
sys.path.append('e:\python\libs')
import json
sys.path.append('e:\python')
sys.path.append('C:\python')
import serverAPI

keep_scanning = True

#TODO: put this in a separate thread
class GpsLocProvider:
    
    def __init__(self, neardist, fardist):
        self.currentLoc = { }
        self.nearbyPOIs = [ ] #should be a list of dictionaries
        self.nearTolerance = neardist #how close we need to be to a POI to trigger it
        self.farTolerance = fardist #how far we must be from a POI before it's removed from the recent list
        # Initialize GPS module here
        self.server = serverAPI.ServerAPI()
        #positioning.select_module(positioning.default_module())
        #positioning.set_requestors([{"type":"service", 
        #                     "format":"application",
        #                     "data":"GpsLocProvider"}])    
        
        self.nearbyLock = thread.allocate_lock()
        self.updateThread = thread.start_new_thread(self.Update, () )
        #self.updateThread = thread(group=None, target=self.Update(), name="GPS Update Thread")
        #self.updateThread.setDaemon(true)
    
    def exit_thread(self):
        global keep_scanning
        keep_scanning = False
    
    
    def getCurrentLocation(self):
        print "Querying position module..."
        tempDict = positioning.position()
        self.currentLoc = {"lat":tempDict["position"]["latitude"], "lng":tempDict["position"]["longitude"]}
        print "New loc:"
        print self.currentLoc
        return self.currentLoc
    
    
    # incPOIs: list of new POIs (which are dictionaries)
    def addPOIs(self, incPOIs): 
        # Check for duplicates
        self.nearbyLock.acquire()
        try:
            if len(incPOIs) != 0: #len generates a TypeError exception
                for poi in incPOIs: #poi is a key
                    newDict = { poi:incPOIs[poi] }
                    if newDict not in self.nearbyPOIs:
                        self.nearbyPOIs.append(newDict) 
        except TypeError:
            self.nearbyLock.release()
            return
        
        self.nearbyLock.release()
        
    # Performs a full update of the object 
    def Update(self):
        positioning.select_module(positioning.default_module())
        positioning.set_requestors([{"type":"service", 
                             "format":"application",
                             "data":"GpsLocProvider"}])  
        global keep_scanning
        
        while keep_scanning:
            initialTime = time.clock() 
            # get current location, compare to old current location
            newLoc = self.getCurrentLocation()
            # if no(or little) change, don't do anything
            latDif = math.fabs(newLoc["lat"] - self.currentLoc["lat"])
            lngDif = math.fabs(newLoc["lng"] - self.currentLoc["lng"])
            # if latDif < 0.0001 or lngDif < .001:
            #     print "Haven't moved far enough.  Cancelling update."
            #     return activeList
            # else:
            print "Updated current location."
            self.currentLoc = newLoc
            
            #self.nearbyLock.signal()
            self.addPOIs(self.get_nearby_locations(self.currentLoc["lat"], self.currentLoc["lng"]))
                
            print "Update in :" + str(time.clock() - initialTime) + " ms."       
            e32.ao_yield()
            e32.ao_sleep(8)
        
        print "Update thread done."
            
    # returns list of dictionaries that contain full POI data
    def get_active_list(self):
        activeList = [ ]
        
        self.nearbyLock.acquire()
        for poi in self.nearbyPOIs:
            for key in poi:
                tempDict = poi[key]
                dist = self.calcDistance(self.currentLoc, tempDict) #poi must have "lat" and "lng" entries
                if dist <= self.nearTolerance:
                    activeList.append(self.server.get_location(tempDict['tpid']))
                if dist >= self.farTolerance:
                    self.nearbyPOIs.remove(poi)
        self.nearbyLock.release()
        return activeList
                    
                    
    def get_nearby_locations(self, lat, lng): 
        print "Contacting remote server..."
        return self.server.get_nearby_locations(lat, lng)
  
        
    # pointA and pointB must be dictionaries with 'lat' and 'lng' entries
    def calcDistance(self, pointA, pointB):
        DegreesToRadians = math.pi/180
        R = 3956
        
        lat1 = float(pointA['lat'])
        lon1 = float(pointA['lng'])
        lat2 = float(pointB['lat'])
        lon2 = float(pointB['lng'])
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
repeat = 0
GPS = GpsLocProvider(1.1, 3.0)
while repeat < 10:
    repeat += 1
    print "repetition #" + str(repeat)
    actives = GPS.get_active_list()
    if len(actives) != 0:
        for poi in actives:
            notestring = (u"You are getting close to TPID " + str(poi['name']))
            appuifw.note(notestring, "info")
    e32.ao_yield()
    e32.ao_sleep(10)

GPS.exit_thread()

#GPS = GpsLocProvider(1.1, 0.00000001)
#while 1:
#    actives = GPS.Update()
#    print "got " + str(len(actives)) + " POIs."
#    if len(actives) != 0:
#        for poi in actives:
#            notestring = (u"You are getting close to TPID " + str(poi['name']))
#            appuifw.note(notestring, "info")
#    e32.ao_yield()
#    e32.ao_sleep(8)
