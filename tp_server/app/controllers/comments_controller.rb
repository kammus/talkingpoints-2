class CommentsController < ApplicationController
  layout 'locations'
  before_filter :authorize, :only => [:edit, :create, :update, :destroy]
  
  # GET /comments
  # GET /comments.xml
  def index
    @location = Location.find(params[:location_id])
    @comments = @location.comments
    
    @comment = Comment.new # empty comment object for the new commment form

    respond_to do |format|
      format.html # index.html.erb
      format.xml  { render :xml => @comments }
    end
  end

  # GET /comments/1
  # GET /comments/1.xml
  def show
    @comment = Comment.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.xml  { render :xml => @comment }
    end
  end

  # GET /comments/1/edit
  def edit
    @comment = Comment.find(params[:id])
    @location = @comment.location
  end

  # POST /comments
  # POST /comments.xml
  def create
    params[:comment][:user_id] = session[:user_id]
    params[:comment][:location_id] = params[:location_id]
    @comment = Comment.new(params[:comment])

    respond_to do |format|
      if @comment.save
        flash[:notice] = 'Comment was successfully created.'
        format.html { redirect_to(location_comments_path(@comment.location)) }
        format.xml  { render :xml => @comment, :status => :created, :location => @comment }
      else
        format.html { render :action => "index" }
        format.xml  { render :xml => @comment.errors, :status => :unprocessable_entity }
      end
    end
  end

  # PUT /comments/1
  # PUT /comments/1.xml
  def update
    @comment = Comment.find(params[:id])

    respond_to do |format|
      if @comment.update_attributes(params[:comment])
        flash[:notice] = 'Comment was successfully updated.'
        format.html { redirect_to(location_comments_path(@comment.location)) }
        format.xml  { head :ok }
      else
        format.html { render :action => "edit" }
        format.xml  { render :xml => @comment.errors, :status => :unprocessable_entity }
      end
    end
  end

  # DELETE /comments/1
  # DELETE /comments/1.xml
  def destroy
    @comment = Comment.find(params[:id])
    location = @comment.location
    @comment.destroy
    
    redirect_to(location_comments_path(location))
  end
end
