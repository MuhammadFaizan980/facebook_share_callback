# facebook_share_callback

Flutter Plugin for sharing contents to Facebook.

You can use it share to Facebook. Support Url and Text, Photo

support:

Android & iOS : Facebook
Note: This plugin is still under development, and some APIs might not be available yet.
Feedback and Pull Requests are most welcome!

## Getting Started
add share_facebook_callback as a [dependency in your pubspec.yaml file](https://flutter.io/platform-plugins/).

Please check the latest version before installation.
```
dependencies:
  flutter:
    sdk: flutter
  # add share_facebook_callback
  share_facebook_callback: ^0.0.1
```

## Setup

#### Android

Add "facebook app id" to the application tag of AndroidManifest.xml
```
    <application>
       ...
       //add this 
        <meta-data
            android:name="com.facebook.sdk.ApplicationId"
            android:value="@string/facebook_app_id" />
        <meta-data 
            android:name="com.facebook.sdk.ClientToken" 
            android:value="@string/facebook_client_token"/>    
            
        <provider
            android:name="com.facebook.FacebookContentProvider"
            android:authorities="com.facebook.app.FacebookContentProvider[facebook_app_id]"
            android:exported="true" />
    </application>

    <queries>
        <provider android:authorities="com.facebook.katana.provider.PlatformProvider" /> 
    </queries>
```

string.xml:
```
<?xml version="1.0" encoding="utf-8"?>
<resources>
<!-- Replace "12345678901234" with your Facebook App ID here. -->
    <string name="facebook_app_id">12345678901234</string>
<!-- Replace "123456789abcdefghimnl" with your Facebook Client Token here. -->
    <string name="facebook_client_token">123456789abcdefghimnl</string>
</resources>
```
#### IOS

##### setup facebook

make sure you add below deatils in your plist file.

```
<key>FacebookAppID</key>
<string>fbid</string>
<key>FacebookClientToken</key>
<string>123456789abcdefghimnl</string>
<key>CFBundleURLTypes</key>
	<array>
		<dict>
			<key>CFBundleURLSchemes</key>
			<array>
				<string>fb-your-fb-id</string>
			</array>
		</dict>
	</array>

```
Note-: Make sure you add fb in  at start of your fb Id in CFBundleURLSchemes.

Add below value in url scheme(Refer to example).

```<key>LSApplicationQueriesSchemes</key>
	<array>
        <string>fbapi</string>
        <string>fb-messenger-share-api</string>
    </array>
```