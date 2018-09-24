# Intro
SystemUI Tuner is a replacement and extension of AOSP's included System UI Tuner.

With SystemUI Tuner you can, among other things:
 - Hide status bar icons
 - Enable system-wide Immersive Mode and tweak its behavior per-app
 - Modify the behavior and appearance of the Quick Settings shade
 - Enable and customize Demo Mode
 - Change system-wide animation speeds
 - Enter and read your own custom settings values
 
# User Support
I make no guarantee with this app that:
 - it will work on your particular device
 - it will not not cause your device to malfunction
 - every feature will work on your device

SystemUI has no warranty. If you choose to use this app, I am not responsible for anything that goes wrong.
I will do my best to help you provided that:
 - I can
 - you have read this document and its warnings
 - you don't ask a question already answered here
 
# Permissions
SystemUI Tuner asks for some sensitive permissions:
 - android.permission.WRITE_SECURE_SETTINGS
 - android.permission.DUMP
 - android.permission.PACKAGE_USAGE_STATS
 
These permissions are not used to collect any sort of data, cause harm to your device (except by your own volition), or for any other malicious intent. SystemUI Tuner is open source (you are currently on the source page), and you are welcome to verify the validity of this claim.

Below I have explained the reason(s) each permission is needed:

WRITE_SECURE_SETTINGS
 - Without this, SystemUI Tuner just won't work. Hiding status bar icons, modifying Immersive Mode, changing Quick Settings, etc, all need this permission to function.

DUMP
 - This permission is needed for Demo Mode to function.
 - This permission is needed for the Status Bar icon auto-detect feature.
 
PACKAGE_USAGE_STATS
 - This permission is needed for the Status Bar icon auto-detect feature.
 
I have attempted to modularize when these permissions are requested. All three are requested during setup, but *only* WRITE_SECURE_SETTINGS is required at this point. If you skip the other permissions
