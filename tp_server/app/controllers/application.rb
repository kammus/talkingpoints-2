# Filters added to this controller apply to all controllers in the application.
# Likewise, all the methods added will be available for all controllers.
class ApplicationController < ActionController::Base
  helper :all # include all helpers, all the time
  
  def authorize
    if session[:user_id] != nil
      logger.info('authorize = true')
      return true
    else
      logger.info('authorize = false')
      return false
    end
  end

  # See ActionController::RequestForgeryProtection for details
  # Uncomment the :secret if you're not using the cookie session store
  protect_from_forgery # :secret => '775b23339f1bec6a91f2b3a0ed2e137b'
end