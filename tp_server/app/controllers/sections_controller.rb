class SectionsController < ApplicationController
  layout 'locations'
  
  # GET /sections
  # GET /sections.xml
  def index
    @location = Location.find(params[:location_id])
    @sections = @location.sections
    
    @section = Section.new # for the new section form

    respond_to do |format|
      format.html # index.html.erb
      format.xml  { render :xml => @sections }
    end
  end

  # GET /sections/1
  # GET /sections/1.xml
  def show
    @section = Section.find(params[:id])
    @location = @section.location

    respond_to do |format|
      format.html # show.html.erb
      format.xml  { render :xml => @section }
    end
  end

  # GET /sections/1/edit
  def edit
    @section = Section.find(params[:id])
    @location = @section.location
  end

  # POST /sections
  # POST /sections.xml
  def create
    params[:section][:user_id] = session[:user_id]
    params[:section][:location_id] = params[:location_id]
    @section = Section.new(params[:section])

    respond_to do |format|
      if @section.save
        flash[:notice] = 'Section was successfully created.'
        format.html { redirect_to(location_sections_path(@section.location)) }
        format.xml  { render :xml => @section, :status => :created, :location => @section }
      else
        format.html { render :action => "index" }
        format.xml  { render :xml => @section.errors, :status => :unprocessable_entity }
      end
    end
  end
  
  # PUT /sections/1
  # PUT /sections/1.xml
  def update
    @section = Section.find(params[:id])

    respond_to do |format|
      if @section.update_attributes(params[:section])
        flash[:notice] = 'Section was successfully updated.'
        format.html { redirect_to(location_section_path(@section.location, @section)) }
        format.xml  { head :ok }
      else
        format.html { render :action => "edit" }
        format.xml  { render :xml => @section.errors, :status => :unprocessable_entity }
      end
    end
  end

  # DELETE /sections/1
  # DELETE /sections/1.xml
  def destroy
    @section = Section.find(params[:id])
    location = @section.location
    @section.destroy

    respond_to do |format|
      format.html { redirect_to(location_sections_path(location)) }
      format.xml  { head :ok }
    end
  end
end
