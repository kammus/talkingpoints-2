# Get Eclipse #

**Prerequisite** is a recent version of Sun's [Java Runtime Environment](http://java.com) (JRE)

First you need to download Eclipse (it is a very popular open source Integrated Development Environment written in Java).

You basically just need some version of Eclipse that includes all the necessary tools to develop and run Java code.  This is a good choice:

http://www.eclipse.org/downloads/download.php?file=/technology/epp/downloads/release/ganymede/SR1/eclipse-java-ganymede-SR1-win32.zip

Note:  Eclipse doesn't have to be "installed", you just unzip it and run eclipse.exe


# Get Subclipse #

Subclipse is a module for Eclipse that allows you to use the Subversion (SVN) code repostoty.

  1. Start Eclipse (exlipse.exe)
  1. Pick a location for your workspace
  1. Skip the intro to the workbench
  1. Go to Help > Sofware Updates ..
  1. Go to the Available Software tab
  1. Go to 'Add Site..' and enter this URL: http://subclipse.tigris.org/update_1.4.x
  1. Expand the subclise entry you created
  1. Check 'JavaHL Adapter' and 'Subclipse' and click 'Install...'
  1. wait :)
  1. Click 'Next', 'Accept agreement', 'Finish'
  1. Restart Eclipse

If you get stuck,  you can find more documentation about installing Subclipse here: http://subclipse.tigris.org/install.html


# Check out the code #

Now you have to check out (download) the code from the code repository

  1. Go to 'Window' > 'Open perspective' > 'Other'
  1. Select 'SVN Repository Exploring'
  1. Right-click in the 'SVN Repositories' area on the right and select 'New' > 'Repository location'
  1. Enter this URL: http://talkingpoints-2.googlecode.com/svn/tts_java/tags/release-1.0
  1. Right-click on the created repository and chose 'Checkout'
  1. Select 'Finish'
  1. Wait until the code has been checked out


# Configure the application #

  1. Switch to the 'Java' or 'Java Browsing' perspective
  1. Click on 'Run' > 'Run configurations'
  1. Select 'Java Application' (double click) to create a new configuration
  1. For the Main class click on 'Search..' and select the "TagReaderTest.java" class as the Main class
  1. Go to arguments tab and add the following parameters
  1. Program arguments can be either "1" (SUI only), "2" (GUI only), or "3" (SUI+GUI)
  1. The VM arguments need to be "-Xmx200m" to allocate enough memory to the VM for the speech recognition to work
  1. Apply the configuration

Now you can run the application either through the 'Run' menu or by hitting 'Crtl+F11'

If Bluetooth is disabled it will detect 3 virtual Bluetooth tags,  otherwise it will start searching for real Bluetooth devices.
