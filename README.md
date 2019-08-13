<div align="center">
<img src="img/icon.png" width="150"/>
<h1>Focus</h1>
</div>

<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="78" height="20"><linearGradient id="b" x2="0" y2="100%"><stop offset="0" stop-color="#bbb" stop-opacity=".1"/><stop offset="1" stop-opacity=".1"/></linearGradient><clipPath id="a"><rect width="78" height="20" rx="3" fill="#fff"/></clipPath><g clip-path="url(#a)"><path fill="#555" d="M0 0h51v20H0z"/><path fill="#4c1" d="M51 0h27v20H51z"/><path fill="url(#b)" d="M0 0h78v20H0z"/></g><g fill="#fff" text-anchor="middle" font-family="DejaVu Sans,Verdana,Geneva,sans-serif" font-size="110"> <text x="265" y="150" fill="#010101" fill-opacity=".3" transform="scale(.1)" textLength="410">version</text><text x="265" y="140" transform="scale(.1)" textLength="410">version</text><text x="635" y="150" fill="#010101" fill-opacity=".3" transform="scale(.1)" textLength="170">0.1</text><text x="635" y="140" transform="scale(.1)" textLength="170">0.1</text></g> </svg>
<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="108" height="20"><linearGradient id="b" x2="0" y2="100%"><stop offset="0" stop-color="#bbb" stop-opacity=".1"/><stop offset="1" stop-opacity=".1"/></linearGradient><clipPath id="a"><rect width="108" height="20" rx="3" fill="#fff"/></clipPath><g clip-path="url(#a)"><path fill="#555" d="M0 0h57v20H0z"/><path fill="#9f9f9f" d="M57 0h51v20H57z"/><path fill="url(#b)" d="M0 0h108v20H0z"/></g><g fill="#fff" text-anchor="middle" font-family="DejaVu Sans,Verdana,Geneva,sans-serif" font-size="110"> <text x="295" y="150" fill="#010101" fill-opacity=".3" transform="scale(.1)" textLength="470">platform</text><text x="295" y="140" transform="scale(.1)" textLength="470">platform</text><text x="815" y="150" fill="#010101" fill-opacity=".3" transform="scale(.1)" textLength="410">android</text><text x="815" y="140" transform="scale(.1)" textLength="410">android</text></g> </svg>
![](https://img.shields.io/badge/language-kotlin-orange.svg)
![](https://img.shields.io/badge/license-MIT-000000.svg)

# Introduction

A simple Android application featuring a work-time supervisor.
Get rid of your phone while working!

<div align="center">
<table>
<tr>
<th>Set up</th>
<th>Get supervised</th>
<th>View report</th>
</tr>
<tr>
<td><img src="img/set.png" width="200"/></td>
<td><img src="img/offtrack.png" width="200"/></td>
<td><img src="img/report.png" width="200"/></td>
</tr>
</table>
</div>

Guess what would happen if you do?
<div align="center">
<img src="img/cover.jpg"/>
</div>

# Requirements

- Java 1.8
- Kotlin 1.3
- Gradle 3.4
- Android SDK 26

# Usage

## Register This Application

My application credential hasn't been approved by Google, so by far you have to register your own application on [Google Cloud console flow for the Google Calendar API](https://cloud.google.com/console/start/api?id=calendar).

- If necessary, sign in to your Google Account, select or create a project, and agree to the terms of service. Click Continue.
- For Platform select "Android".
  - For "Package name" you should make up a unique name that has never been used before by anyone, e.g. using your name and date like "com.linkinpark213.focus.harper1125".
    - Note: Make sure to pick a legal Java package name since we will be using it in the Android project source code. The full package name must have at least two parts, so for example "linkinpark213" will not work.
    - Note: If you try to register a package name that has already been registered, you may get an error reading "An unexpected error has occurred." In particular, "com.linkinpark213.focus" has already been registered, so you must change the package name.
  - For "SHA1 fingerprint", use the value that is output when running this command in a Terminal:
    - keytool -exportcert -alias androiddebugkey -keystore ~/.android/debug.keystore | openssl sha1
  - When asked, use "android" (without the quotes) as the keystore password.
  - Click Register.
- Click on "APIs" under "APIs & auth" in the left sidebar and make sure "Calendar API" is enabled.

## Configure This Project

- Import this project in your Android Studio.
  - Select "Import Project..." or File > Import Project...
  - Select \[someDirectory\]/focus/build.gradle and click OK.
  - Select "Use local gradle distribution" with "Gradle home" of \[someDirectory\]/gradle-3.4.2 and click OK.
- Open AndroidManifest.xml and change the package attribute of the manifest, initially "com.linkinpark213.focus", to match the name you registered in the Google Cloud Console, e.g. "com.linkinpark213.focus.harper1125".
- Rename the Java package: right-click on app/src/main/java, click New, click Package, and then enter the name you registered in the Google Cloud Console, e.g. "com.linkinpark213.focus.harper1125". Next, select the Java classes in the package "com.linkinpark213.focus", drag them into the new package you just created, and then click Refactor.

## Run

- In Android Studio, click Run > Run 'focus'.
