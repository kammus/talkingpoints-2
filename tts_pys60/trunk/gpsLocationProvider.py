import e32
import os, os.path
import sys
if e32.in_emulator(): # for emulator testing
	sys.path.append("c:\\python")
	sys.path.append("c:\\python\\tp")
else:
	sys.path.append("e:\\python")
	sys.path.append("e:\\python\\tp")
	
import appuifw
import thread
import positioning
import math
import time
import string

import serverAPI

keep_scanning = 1

class GpsLocProvider:
    
    # neardist = how close a POI needs to be in order to be "triggered" (in mi.)
    # fardist = how far away a POI must be to be removed from the active list (in mi.)
    def __init__(self, GUIref, neardist=25, fardist=50, logmode=0):
        self.current_location = { }
        self.nearbyPOIs = [ ] #should be a list of dictionaries
        self.nearTolerance = neardist #how close we need to be to a POI to trigger it
        self.farTolerance = fardist #how far we must be from a POI before it's removed from the recent list
        self.logmode = logmode
        if logmode:
        	if e32.in_emulator():
        		logdir = u"c:\\python\\gps_log"
        	else:
        		logdir = u"e:\\python\\gps_log"
        	self.logpath = unicode(logdir + "\\gps_log.txt")
        	if not os.path.exists(logdir):
		    	os.makedirs(logdir)
        
        self.FILE = None
        self.actives = [ ]
        self.newActives = 0
        self.removedActives = [ ]
        
        # save a local reference to the GUI object
        self.GUI = GUIref
    
    	# server field defaults to None, must be set by object that owns gpsLocationProvider
        self.server = None
   
        self.nearbyLock = thread.allocate_lock()
        self.updateThread = thread.start_new_thread(self.Update, () )
        
    
    def exit_thread(self):
        global keep_scanning
        keep_scanning = 0
    
    
    def getCurrentLocation(self):
    	previous_time = time.clock()
        tempDict = positioning.position()
        currentTime = time.clock()
        self.current_location = {"lat":tempDict["position"]["latitude"], "lng":tempDict["position"]["longitude"], "timestamp":currentTime}
        # write to the log if necessary.
        if self.logmode:
        	logtext = "GPS query completed in " + str(currentTime-previous_time) + " sec.\r"
        	self.FILE.write(logtext)
        	logtext = "New position is: " + str(self.current_location["lat"]) + ", " + str(self.current_location["lng"]) + "\r"
        	self.FILE.write(logtext)
        	
        self.GUI.current_position = self.current_location
        return self.current_location
    
    
    # incPOIs: list of new POIs (which are dictionaries)
    def addPOIs(self, incPOIs): 
        #print "Adding " + str(len(incPOIs)) + " POIs."
        # Check for duplicates
        self.nearbyLock.acquire()
        try:
            if len(incPOIs) != 0: #len generates a TypeError exception
                if self.logmode:
                	self.FILE.write("Server returned " + str(len(incPOIs)) + " POIs.\r")
                for poi in incPOIs: #poi is a key
                    newDict = { poi:incPOIs[poi] }
                    if newDict not in self.nearbyPOIs: # is this evaluating as true when it shouldn't?
                        self.nearbyPOIs.append(newDict)
                        if(self.logmode):
                    	    self.FILE.write("Adding: ")
                    	    self.FILE.write(str(newDict)) 
                    	    self.FILE.write("\r")
        
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
        
        # open log file
        if(self.logmode):
            self.FILE = open(self.logpath, "w+")
            self.FILE.write("LOGFILE START:")
            self.FILE.write("GPS Modules detected:\r")
            self.FILE.write(str(positioning.modules()))
            self.FILE.write("Using default: " + str(positioning.default_module()) + "\r")
       
        global keep_scanning
        
        while keep_scanning: 
            # get current location, compare to old current location
            newLoc = self.getCurrentLocation()
            if newLoc['lat'] != newLoc['lat']: # if we got a "NaN" response from positioning, this will abort and try again
            	continue
            # if no(or little) change, don't do anything
            #latDif = math.fabs(newLoc["lat"] - self.current_location["lat"])
            #lngDif = math.fabs(newLoc["lng"] - self.current_location["lng"])
            # if latDif < 0.0001 or lngDif < .001:
            #     print "Haven't moved far enough.  Cancelling update."
            #self.current_location = newLoc
            
            #self.nearbyLock.signal()
            self.addPOIs(self.get_nearby_locations(self.current_location["lat"], self.current_location["lng"]))
               
            self.actives = self.get_active_list()
            if len(self.actives) != 0:
                self.newActives = 1
            else:
            	self.newActives = 0
                	
       #     for poi in actives:
        #        self.GUI.location_cache.appendLocation(poi)       
         #       self.GUI.notifyOfNewLocation(poi["name"])
                
          #  print "Update in :" + str(time.clock() - initialTime) + " ms."       
                                            
            self.FILE.flush()
            e32.ao_yield()
            e32.ao_sleep(5)
        
        self.FILE.close()
        print "Update thread done."
            
    # returns list of dictionaries that contain full POI data
    # performs a check on the GUI's current location cache before submitting a request
    # for full POI info to the server.
    def get_active_list(self):
        activeList = [ ]
        new_actives = 0
        self.nearbyLock.acquire()
        for poi in self.nearbyPOIs:
            for key in poi:
                tempDict = poi[key]
                dist = self.calcDistance(self.current_location, tempDict) #poi must have "lat" and "lng" entries
                if dist <= self.nearTolerance and dist == dist: 
                    if not self.GUI.location_cache.checkLocationsForTPID(tempDict['tpid']):
                        new_active = self.server.get_location(tempDict['tpid'])
                        new_active["distance"] = dist
                        activeList.append(new_active)
                        new_actives += 1
                if dist >= self.farTolerance: # is this actually working?
                    if self.logmode:
                    	self.FILE.write("Removed " + str(key) + " due to distance.\r")
                    self.nearbyPOIs.remove(poi)
                    self.removedActives.append(poi[key]['tpid'])
        self.nearbyLock.release()
        if self.logmode:
        	self.FILE.write(str(new_actives) + " active POIs this iteration. \r")
        	self.FILE.write(str(len(activeList)) + " total are active.\r")
        return activeList
                    
                    
    def get_nearby_locations(self, lat, lng): 
        #print "Contacting remote server..."
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
        
        distance_in_meters = d * 1852
        #if self.logmode:
        #	self.FILE.write("calcDistance result for " + str(pointA['lat']) + "," + str(pointA['lng']))
        #	self.FILE.write(" and " + str(pointB['lat']) + "," + str(pointB['lng']))
        #	self.FILE.write(" is " + str(distance_in_meters) + ".\r")

        return distance_in_meters
        
        #distInMiles = d * 1.1508
        #return distInMiles
