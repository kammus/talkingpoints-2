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
			return "Location succesfully bookmarked. (" + str(tpid) + ":" + label + ")"
		else:
			return "Location was already bookmarked. (" + str(tpid) + ":" + label + ")"
		
	def hide(self, tpid, label):
		if tpid not in self.hidden_locations:
			self.hidden_locations[tpid] = label;
			return "Location succesfully hidden. (" + str(tpid) + ":" + label + ")"
		else:
			return "Location was already hidden. (" + str(tpid) + ":" + label + ")"
			
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