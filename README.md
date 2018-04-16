This Java Application performs the Dynamic Analysis on the Andoird Apps by collecting the System calls that an app calls during its exectuion.

This analysis was performed on the 750 beningn and 750 malware apps as a part of my Final Project Research. 


## Dynamic Analysis Steps

(i) Open AVD, and run the device.
(ii) Use ./adb devices command to check the emulators running.
(iii) Install the apk by using ‘./adb install <package name>’. 
(iv) Run the application using command ‘./adb shell monkey -p <package.name> -c android.intent.category.LAUNCHER 1’.
(v) Check the process id by using ‘./adb shell ps | grep <package name>’
(vi) Use the Strace command ‘strace -p <process id> -c -o <path in emulator><Filename.txt>’.
(vii) Start Monkey using command ‘./adb -e shell monkey -p <package name> -v 500 -s 42’
(viii) After money runner stops pull the Strace txt file using ‘./adb pull <path in emulator> <path in destination>’.
(ix) Uninstall the application using ‘./adb uninstall <package name>’

![screen shot 2018-04-16 at 4 22 32 pm](https://user-images.githubusercontent.com/17535963/38833073-7d31229e-4192-11e8-8c45-cba23ab76dae.png)
