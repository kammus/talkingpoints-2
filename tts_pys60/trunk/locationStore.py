import sys
#sys.path.append("C:\\python\\tp") # for emulator testing
#import ServerAPI
import time

class LocationStore:
	
	def __init__(self):
		self.bookmarked_locations = {}
		self.hidden_locations = {}
	
	
	def bookmark(self, tpid, label):
		if tpid not in self.bookmarked_locations:
			self.bookmarked_locations[tpid] = label;
			return "Location succesfully bookmarked"
		else:
			return "Location was already bookmarked"
		
		
	def hide(self, tpid, label):
		if tpid not in self.hidden_locations:
			self.hidden_locations[tpid] = label;
			return "Location succesfully hidden"
		else:
			return "Location was already hidden"
			
			
	def unbookmark(self, tpid):
		if tpid in self.bookmarked_locations:
			del self.bookmarked_locations[tpid];
	
	
	def unhide(self, tpid):
		if tpid in self.hidden_locations:
			del self.hidden_locations[tpid];
			
			
	def getBookmarkedLocationsList(self):
		output = {}
		output['list'] = []
		output['mapping'] = {}
		i = 0
		for key,value in self.bookmarked_locations.iteritems():
			output['list'].append( unicode(value) )
			output['mapping'][i] = key
			i += 1
		return output
	
	
	def getHiddenLocationsList(self):
		output = {}
		output['list'] = []
		output['mapping'] = {}
		i = 0
		for key,value in self.hidden_locations.iteritems():
			output['list'].append( unicode(value) )
			output['mapping'][i] = key
			i += 1
		return output