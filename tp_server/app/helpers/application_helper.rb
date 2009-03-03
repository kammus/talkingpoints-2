# Methods added to this helper will be available to all templates in the application.
module ApplicationHelper
  def logged_in
    if session[:user_id] != nil
      return true
    else
      return false
    end
  end
end
