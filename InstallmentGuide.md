# !!! Disclaimer:  This is outdated since it is about the very early TP prototype from early 2008 !!! #


# Introduction #

Talking Points is freely accessible source. It is programmed based on JAVA SE, JRE 1.6. also several open sources, BlueCove and FreeTTS, CMUSphinx. This wiki page explains how to get program sources work.


# Details #

**Easiest & Fastest way**
: Using svn, checkout our recent version, currently Ver 1.0 (Sept 20, 2008), following this command:

[RunningFromEclipse](RunningFromEclipse.md)

_svn checkout http://talkingpoints-2.googlecode.com/svn/tags/release-1.0 tts-read-only_

: Main class is located at 'TagReaderTest.Java'.

**Without SVN**
: For whatever reason, if you can't get SVN work, you can go around using this way.

  1. Download talkingpoints-1.0-sources.tar.gz
  1. Unpack it, and unpack src.tar.gz as well
  1. Create lib folder

Now your root folder should look like this
```
* tts - src      // every java file should be located here
      - lib      // every supporting library should be located here
      - sounds   // currently, we are using two sounds, 
```
4) Download libraries from the following links

  * sphinx4.jar
  * WSJ\_8gau\_13dCep\_16k\_40mel\_130Hz\_6800Hz.jar
  * jsapi.jar
  * BareBonesBrowserLaunch.jar
  * freetts.jar
  * bluecove-2.0.3.jar http://code.google.com/p/bluecove/

**If you have any question, please send email at talkingpoints@umich.edu**