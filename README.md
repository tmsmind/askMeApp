Ask Me  Navigation App
===================================

Project that uses the [Google Places API for Android](https://developers.google.com/places/android/).

This project uses the following API/SDK:

1. Google Places API for Android
2. Google Directions API
3. Google Distance Matrix API
4. Facebook SDK
5. Twitter SDK (Fabric SDK)


----------


Pre-requisites
--------------

- Android SDK v23
- Android Build Tools v23.0.1
- Android Support Repository
- Compile Google APIs (Google Inc.) (API 23)


----------


Getting Started
---------------

This sample uses the Gradle build system.

First download the samples by cloning this repository or downloading an archived
snapshot.

In Android Studio, use the "Import Project" option. Next select the directories
("HumesisDirectionApp").  If prompted for a gradle configuration
accept the default settings.

Alternatively use the "gradlew build" command to build the project directly.

Don't forget to add your API key to the AndroidManifest.xml.
(See https://developers.google.com/places/android/signup)


----------


Preparing Facebook SDK
---------------

 1. Goto https://developers.facebook.com/apps and create a new application

----------


Preparing Twitter SDK
---------------

 1. Goto https://apps.twitter.com/app


----------


Getting Google Maps API Key
---------------

I have left my API keys for Facebook and Twitter but for google since it is usage based please create your own.

You can enter it in res/values/google_maps_api.xml

 1. Goto https://console.developers.google.com/project and create a new project.
 2. Open the APIs section and enable the following APIs:
	 Google Maps Android API
	 Google Maps Directions API
	 Google Maps Distance Matrix API
	 Google Maps Geocoding API
	 Google Places API for Android
	 Google Places API Web Service
	 
 3. Open Credentials section and add new credential>API keys.
 4. Click Android Key.
 5. Enter the Name for the Key.
 6. Generate the SHA1 fingerprint using the following command `keytool -list -v -keystore mystore.keystore` where mystore is your key.
 7. Paste the SHA1 fingerprint along with the package name (in this case, com.android.askme) and press create.
 8. Repeate Step 3 and click on Browser Key and press create.
 9. Copy the keys and paste it in google_maps_api.xml file for the following keys
	


----------


License
-------

Copyright 2015 Anand Sharma, Humesis, Inc.

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements.  See the NOTICE file distributed with this work for
additional information regarding copyright ownership.  The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License.  You may obtain a copy of
the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.
