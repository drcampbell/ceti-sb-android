# README #

This document details how to setup the development environment for the School2Biz Android Application.
This document was generated in Markdown. [Learn Markdown](https://bitbucket.org/tutorials/markdowndemo)

### What is this repository for? ###

* Quick summary
* This is the repository for the Android App for the Ceti School2Biz project, a collaboration between the Ceti Lab at The Ohio State University and the Delphos School District of Ohio.  
* Version 0.6.1
* [Learn Markdown](https://bitbucket.org/tutorials/markdowndemo)

### How do I get set up? ###

* Summary of set up
		* lib32stdc++6 library
		* Java 7 or 8
		* Git

### System Configuration ###
Install your favorite JDK. Using the Oracle version is recommended. But you can probably use your favorite.    [Oracle Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

Install git

`$ sudo apt-get install git-all`

#### Install Android Studio ####
Ubuntu: Run this command to get the necessary libraries to install the Android SDK.

`$ sudo apt-get install lib32stdc++6`

[Download and Install Android Studio](developer.android.com/sdk/index.html)

If you want to use the emulator, you may want to [Setup VM Acceleration on Linux](http://developer.android.com/tools/devices/emulator.html#vm-linux)
### Android Dependencies ###
The School2Biz Android App uses the Google Volley framework to send and receive RESTful HTTP/HTTPS messages.  To install enter:

```
$ git clone https://android.googlesource.com/platform/frameworks/volley
```

You may also need to install the Android SDK 22.
####Facebook SDk####
This should work out of the box...

####Twitter SDK####
This shouldn't...

* Database configuration
* How to run tests
* Deployment instructions

### Contribution guidelines ###

* Writing tests
* Code review
* Other guidelines

### Who do I talk to? ###

Email: campbell[dot][one][seven][six][zero][at]osu[dot]edu

or ramnath.6[at]osu.edu
