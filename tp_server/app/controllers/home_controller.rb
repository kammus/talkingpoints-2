class HomeController < ApplicationController
  def index
    @locations = Location.find(:all)
  end
  
  def login
    
  end
  
  def authenticate
    if user = User.find(:first, :conditions => ["username = ?", params[:user][:username]])
      if user.password == params[:user][:password]
        session[:user_id] = user.id
        session[:username] = user.username
        flash[:notice] = "Login successful"
        redirect_to '/home/index'
      else
        flash[:notice] = "wrong password"
        redirect_to :controller => 'home', :action => 'login'
      end
    else
      flash[:notice] = "wrong username"
      redirect_to :controller => 'home', :action => 'login'
    end
  end
  
  def logout
    session[:user_id] = nil
    session[:username] = nil
    redirect_to :back
  end
end
