# SystemUI Tuner
Replacement/extension for the stock Android System UI Tuner.

Since some manufacturers disable System UI Tuner in their skins, a lot of people miss out on the options provided there.
SystemUI Tuner (this app -- no space in SystemUI) provides a replacement for any of these people looking for advanced customization options.
If you're one of the lucky people who still has access to the stock System UI Tuner, this app gives you a few extra things as well.

Most settings are available without the need to root (ADB is required).

# Features
 - Status Bar
   - Hide icons in the status bar that you don't want to see
 - Quick Settings
   - Toggle the fancy pulldown animation
   - Change how many settings are available in the header (Nougat+)
 - Demo Mode
   - Specify time shown in status bar
   - Select battery level shown in status bar
   - Select WiFi signal strength
   - Select Mobile signal strength
   - Select Mobile signal type
   - Select status bar style
   - Toggle showing notification icons
   - Toggle battery charging state
   - Toggle Airplane Mode indicator
 - TouchWiz
   - Toggle High Brightness Warning
   - Toggle Samsung-specific status bar icons
 - Miscellaneous
   - Show a Do Not Disturb switch in the volume slider window (does not work on TouchWiz)
   - Toggle Heads-Up notifications
   - Toggle headphone volume warning
   - Toggle battery percentage inside battery (only works on close-to-stock ROMs; requires root)
   - Toggle clock seconds
   - Toggle Night Mode/Night Display (Night Mode only works on 7.0; Night Display only works on Pixel devices)
   - Change system animation speeds
   - Custom settings values (enable in Settings)
   - Enable Power Notification Controls (7.0+; http://www.phonearena.com/news/Android-Ns-Power-notification-controls-explored-heres-what-those-are---how-to-benefit-from-them_id81530)

# Notes

 - TouchWiz users:
   - KEEP SAFE MODE TURNED ON. There is an issue in TouchWiz Nougat where modifying what icons show on the status bar causes System UI to break upon reboot or resolution change.
   - Unfortunately, I can't account for System UI stopping by itself (ie resolution/DPI changes), but I can account for a normal reboot. If you do happen to crash System UI, just reboot.
     - If, for whatever reason, you disabled Safe Mode and ran into crashes, read the troubleshooting section.
 - If you disable the Quick Settings fancy pulldown animation, a reboot tends to cause the QS header icons to disappear until the animation is toggled on and off.
   - Work around this by enabling Safe Mode.
   - This happens on all Nougat devices.
   

# Troubleshooting

 - If System UI is crashing and you've tried rebooting normally:
   - If you have fingerprints set up and ADB working:
     - Use your fingerprint to unlock your device and plug it in. Run this command:
     `adb shell settings delete secure icon_blacklist`
   - If you don't have ADB:
     - TWRP method:
       - Boot into TWRP and use ADB there:
         `adb pull /system/data/settings_secure.xml`
       - Open that file in a text editor and find the `icon_blacklist` section; delete it.
       - Alternatively, send the file to me and I can do it for you.
     - Factory reset:
       - It shouldn't be possible to get to this point
