class Location < ActiveRecord::Base
  acts_as_mappable
  
  has_many :comments
  has_many :sections
  belongs_to :user
  belongs_to :location_type
end
