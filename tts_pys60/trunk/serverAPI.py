class ServerAPI:

	def __init__(self):
		import json
		import urllib
		self.server_host = "http://test.talking-points.org"
		
	def get_location(tpid):
		request_url = self.server_host + "/locations/show/" + tpid + ".json"
        response = urllib.urlopen(request_url).read()
        return json.read(response)
    
	def get_location_by_bluetooth_mac(mac): 
		request_url = self.server_host + "/locations/show_by_bluetooth_mac/" + mac + ".json"
        response = urllib.urlopen(request_url).read()
        return json.read(response)

	def get_nearby_locations(lat, lng):
		latstr = str(lat)
       	lngstr = str(lng)
        lat = string.replace(latstr, '.', ',')
        lng = string.replace(lngstr, '.', ',')
        request_url = self.server_host + "/locations/get_nearby/" + lat + ";" + lng + ".json"
        response = urllib.urlopen(request_url).read()
        return json.read(response)