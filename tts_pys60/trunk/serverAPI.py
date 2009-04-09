import sys
sys.path.append("c:\\python\\tp\\lib")
sys.path.append("e:\\python\\tp\\lib")

import e32
import urllib
import os, os.path

import string # do we need this?

import json



class ServerAPI:

	def __init__(self, inet_mode):
		self.server_host = "http://test.talking-points.org"
		self.inet_mode = inet_mode
		self.mac_tpid_mapping = {'00194fa4e262': 11, '001c623fa0b8': 14, '0010c65e9224': 15}
		
		# make sure path exists
		if e32.in_emulator():  self.dir = u"c:\\python\\tp_offline"
		else:                  self.dir = u"e:\\python\\tp_offline"
		if not os.path.exists(self.dir):
			os.makedirs(self.dir)
		
		
	def get_location(self, tpid):
		filepath = unicode(self.dir + "\\" + str(tpid) + ".txt")
		
		if self.inet_mode == "online":
			request_url = self.server_host + "/locations/show/" + str(tpid) + ".json"
			response = urllib.urlopen(request_url).read()
		elif self.inet_mode == "offline":
			response = self.readFile(filepath)

		if response == "error":
			return None
		else:
			if self.inet_mode == "online":  self.writeFile(filepath, response)
			return json.read(response)
		
    
	def get_location_by_bluetooth_mac(self, mac):
		if self.inet_mode == "online":
			request_url = self.server_host + "/locations/show_by_bluetooth_mac/" + str(mac) + ".json"
			response = urllib.urlopen(request_url).read()
			if response == "error":
				return None
			else:
				return json.read(response)
			
		elif self.inet_mode == "offline":
			print self.mac_tpid_mapping[mac]
			return self.get_location(self.mac_tpid_mapping[mac])
			

	def get_nearby_locations(self, lat, lng):
		filepath = unicode(self.dir + "\\nearby.txt")
		
		if self.inet_mode == "online":
			# convert commas into dots in order to not screw up the Rails URL
			latstr = str(lat)
			lngstr = str(lng)
			lat = string.replace(latstr, '.', ',')
			lng = string.replace(lngstr, '.', ',')
			request_url = self.server_host + "/locations/get_nearby/" + lat + ";" + lng + ".json"
			response = urllib.urlopen(request_url).read()
		
		elif self.inet_mode == "offline":
			response = self.readFile(filepath)
			
		if response == "error":
			return None
		else:
			if self.inet_mode == "online":  self.writeFile(filepath, response)	
			return json.read(response)
		
		
	def getCurrentAddress(self, lat, lng):
		filepath = unicode(self.dir + "\\current_address.txt")
		
		if self.inet_mode == "online":
			# convert commas into dots in order to not screw up the Rails URL
			latstr = str(lat)
			lngstr = str(lng)
			lat = string.replace(latstr, '.', ',')
			lng = string.replace(lngstr, '.', ',')
			request_url = self.server_host + "/locations/get_address/" + lat + ";" + lng
			response = urllib.urlopen(request_url).read()
		else:
			response = self.readFile(filepath)
	
		if response == "error":
			return None
		else:
			if self.inet_mode == "online":  self.writeFile(filepath, response)
			return response
		
	
	def readFile(self, filepath):
		if not os.path.exists(filepath):
			return "error"
		else:
			f = file(filepath, "r")
			response = f.read()
			f.close()
			return response
		
	def writeFile(self, filepath, response):
		f = file(filepath, "w+")
		f.write(response)
		f.close()
		