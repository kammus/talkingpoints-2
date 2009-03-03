include Geokit::Geocoders
class LocationsController < ApplicationController
  layout 'application'
  
  # GET /locations
  # GET /locations.xml
  def index
    @locations = Location.find(:all)
  end

  # GET /locations/1
  # GET /locations/1.xml
  def show
    @location = Location.find(params[:id])
    
    respond_to do |format|
      format.html { render :layout => 'locations' } # show.html.erb
      format.xml  { render :xml => @location }
      format.json { render :json => @location }
    end
  end
  
  def show_by_bluetooth_mac
    @location = Location.find(:first, :conditions => ["bluetooth_mac = ?", params[:id]])
    
    @formatted_location
    
    respond_to do |format|
      format.xml  { render :layout => false }
      format.json { render :json => @location }
    end
    #headers['Content-Type'] = 'application/xml'
    #render :layout => false
    
### this is a cleaner more HTTP standard compliant way to render if no location is present, but the client doesnt support it yet
#    unless @location == nil
#      headers['Content-Type'] = 'application/xml'
#      render :layout => false
#    else
#      render(:text => 'not found', :status => 404, :layout => false)
#    end
  end

  # GET /locations/new
  # GET /locations/new.xml
  def new
    @location = Location.new
    @location_types = LocationType.find(:all)

    respond_to do |format|
      format.html # new.html.erb
      format.xml  { render :xml => @location }
    end
  end

  # GET /locations/1/edit
  def edit
    @location = Location.find(params[:id])
    @location_types = LocationType.find(:all)
    render :layout => 'locations'
  end

  # POST /locations
  # POST /locations.xml
  def create
    @location = Location.new(params[:location])

    respond_to do |format|
      if @location.save
        flash[:notice] = 'Location was successfully created.'
        format.html { redirect_to(@location) }
        format.xml  { render :xml => @location, :status => :created, :location => @location }
      else
        format.html { render :action => "new" }
        format.xml  { render :xml => @location.errors, :status => :unprocessable_entity }
      end
    end
  end

  # PUT /locations/1
  # PUT /locations/1.xml
  def update
    @location = Location.find(params[:id])

    respond_to do |format|
      if @location.update_attributes(params[:location])
        flash[:notice] = 'Location was successfully updated.'
        format.html { redirect_to(@location) }
        format.xml  { head :ok }
      else
        format.html { render :action => "edit" }
        format.xml  { render :xml => @location.errors, :status => :unprocessable_entity }
      end
    end
  end

  # DELETE /locations/1
  # DELETE /locations/1.xml
  def destroy
    @location = Location.find(params[:id])
    @location.destroy

    respond_to do |format|
      format.html { redirect_to(locations_url) }
      format.xml  { head :ok }
    end
  end
  
  def geocode
    @location = Location.find(params[:id])
    res = MultiGeocoder.geocode(@location.street + ', ' + @location.city + ', ' + @location.state)
    
    if res.lng
      @location.lat = res.lat
      @location.lng = res.lng
      @location.save
      flash[:notice] = 'Geocoding results: ' + res.lat.to_s + ', ' + res.lng.to_s
      
    else
      flash[:notice] = 'Geocoding failed'
    end
    
    respond_to do |format|
      format.html { redirect_to(@location) }
      format.xml  { head :ok }
    end
  end
  
  def get_nearby()
    @nearbys = Location.find(:all, :origin =>[42.282736,-83.747134], :within=>10)
    
    respond_to do |format|
      format.html { render :layout => 'locations' } # show.html.erb
      format.xml  { render :xml => @nearbys }
      format.json { render :json => @nearbys }
    end
  end
end
