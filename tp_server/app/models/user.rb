class User < ActiveRecord::Base
  has_many :comments
  has_many :locations
  has_many :sections
end
