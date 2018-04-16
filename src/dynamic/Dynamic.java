package dynamic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Dynamic {

    public static String PID = null;
    public static String DEVICEID = null;
    public static String FILENAME = null;
    public static List<String> PACKAGE = new LinkedList<String>();
    public static String p_name = null;
    public static List<String> filename = new LinkedList<String>();

    public static void main(String[] args) throws IOException, InterruptedException {

        String line = "null";
        String cvsSplitBy = " ";
        String devicename = null;
        String pidnum = null;
        int count = 0;
        Runtime run = Runtime.getRuntime();
        Process pruninstall = null;

        /** Reading all the Files in the folder
         *  on which the Dynamic Analysis will be performed.
         * ************************************************************************************************************
         */
        BufferedReader br = null;
        File folder = new File("/users/talal.ahmed/Downloads/first_100/first10/");
        File[] listOfFiles = folder.listFiles();
        List<String> filenames = new LinkedList<String>();
        int counter = 0;

        for (int i = 0; i < listOfFiles.length; i++) {
            if (!listOfFiles[i].isHidden()) {
                if (listOfFiles[i].isFile()) {
                    filenames.add(counter, listOfFiles[i].getName());
                    counter++;
                }
            }
        }
        System.out.println("listOfFiles Length " + listOfFiles.length);
        System.out.println("Filenames Length " + filenames.size() + "\n\n");
        for (int a = 0; a < filenames.size(); a++) {
            System.out.println(a + "The APK name is: " + filenames.get(a));
            filename.add(a, filenames.get(a));
            try {

                String sCurrentLine;

                br = new BufferedReader(new FileReader("/users/talal.ahmed/DynamicAnalysis/apks/package_names/package/" 
                        + filenames.get(a) + ".txt"));

                while ((sCurrentLine = br.readLine()) != null) {
                    System.out.println("The Package name is: " + sCurrentLine);
                    PACKAGE.add(a, sCurrentLine);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println();
        }

        /**
         * ************************************************************************************************************
         */
        
        /*Changing the directory level to the
        folder in order to acess the ADB Commands*/
        
        String cmd = "/users/talal.ahmed/library/android/sdk/platform-tools/";
        String devices = cmd + "./adb devices";

        Process pr = run.exec(devices);

        pr.waitFor();
        
        //Reading Emulator ID
        
        BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));

        while ((line = buf.readLine()) != null) {
            String[] splitter = line.split(cvsSplitBy);
            if (count == 1) {
                devicename = splitter[0].substring(0, 13);
            }
            count++;
        }

        DEVICEID = devicename;

        for (int k = 0; k < filename.size(); k++) {

            FILENAME = filename.get(k);//This file name will be passed to the Thread to Install the APK in android

            //Initializing the thread. This thread installs the APK in a android
            
            Install in = new Install();
            Thread install = new Thread(in);
            install.start();
            
            int y = 0;// This section waits for the apk to be installed. The max wait time is 30 seconds.
            boolean installstatus = false;
            while (y < 30 && installstatus == false) {
                TimeUnit.SECONDS.sleep(1);
                installstatus = in.getstatus();
            }

            install.interrupt();//The installation thread is closed here.

            // The following statements will launch the app in the Android
            
            String launcher = cmd + "./adb shell monkey -p " + PACKAGE.get(k) + " -c android.intent.category.LAUNCHER 1";
            Process pr1 = run.exec(launcher);
            TimeUnit.SECONDS.sleep(5);
            p_name = PACKAGE.get(k);

            // These statements will get the PID of the app running 
            
            String pid = cmd + "./adb shell ps | grep " + PACKAGE.get(k);
            Process pr2 = run.exec(pid);

            BufferedReader buf1 = new BufferedReader(new InputStreamReader(pr2.getInputStream()));
            while ((line = buf1.readLine()) != null) {
                System.out.println(line);
                String[] splitter = line.split(cvsSplitBy);
                pidnum = splitter[4];
                System.out.println("PID  " + pidnum);
            }
            PID = pidnum;//This PID will be passed to STRACE Thread for recording system calls.

            // The Threads to run STRACE & MONKEY are initialized here. 
            
            MyThread th = new MyThread();
            Thread strace = new Thread(th);
            Monkey th1 = new Monkey();
            Thread monkey = new Thread(th1);

            //STRACE Starts, and after 5 seconds, MONKEY starts.
            
            strace.start();
            TimeUnit.SECONDS.sleep(5);
            monkey.start();
            int z = 0;
            boolean monkeystatus = false;//This section waits for Monkey to run for 30 seconds
            while (z < 30 && monkeystatus == false) {
                TimeUnit.SECONDS.sleep(1);
                monkeystatus = th1.getstatus();
            }
            
            //These statements will close the App in the android....
            
            String kill = cmd + "/adb -s emulator-5554 shell kill -9 " + pidnum;
            Process pr6 = run.exec(kill);
            TimeUnit.SECONDS.sleep(5);

            
            // These statements will copy the files/results of STRACE
            // stored in the Android Emulator and paste them in the 
            // specified folder.
            
            String pull = cmd + "./adb pull /mnt/sdcard/" + PACKAGE.get(k) + ".txt /users/talal.ahmed/Dynamic_logs/Results_Benign/";
            Process pr5 = run.exec(pull);
            int status = pr5.waitFor();
            if (status == 0) {
                strace.interrupt();//STRACE is stopped
                monkey.interrupt();//MONKEY is stopped
                TimeUnit.SECONDS.sleep(5);
            }
            
            //The App will be uninstalled from Android
            
            String uninstall = cmd + "./adb uninstall " + PACKAGE.get(k);
            pruninstall = run.exec(uninstall);
            int pruninstallstatus = pruninstall.waitFor();
            System.out.println(pruninstallstatus);
            System.out.println("UnInstalling Package: " + PACKAGE.get(k));
            System.out.println(pruninstallstatus);
            if (pruninstallstatus == 0) {
                pruninstall.destroy();
                Process deletefile = run.exec("rm /users/talal.ahmed/Downloads/first_100/first10/"+filename.get(k));
            }

        }
    }

    public static String getdeviceid() {
        return DEVICEID;
    }

    public static String getpid() {
        return PID;
    }

    public static String packagename() {
        return p_name;
    }

    public static String file() {
        return FILENAME;
    }

    public static class MyThread extends Object implements Runnable {

        String cmd = "/users/talal.ahmed/library/android/sdk/platform-tools/";
        String line = " ";
        Process pr3;
        String shell = cmd + "./adb -s " + Dynamic.getdeviceid() + " shell";
        String strace = shell + " strace -p " + Dynamic.getpid() + " -c -o /mnt/sdcard/" + packagename() + ".txt";

        @Override
        public void run() {

            try {
                pr3 = Runtime.getRuntime().exec(strace);
            } catch (IOException ex) {
                Logger.getLogger(Dynamic.class.getName()).log(Level.SEVERE, null, ex);
            }
            BufferedReader buf4 = new BufferedReader(new InputStreamReader(pr3.getInputStream()));
            while (!Thread.currentThread().isInterrupted()) {
                try {

                    while ((line = buf4.readLine()) != null) {
                        System.out.println(line);
                        Thread.sleep(1);
                    }

                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }

            }

        }
    }

    public static class Monkey extends Object implements Runnable {

        String cmd = "/users/talal.ahmed/library/android/sdk/platform-tools/";
        String line = " ";
        String monkey = cmd + "./adb -e shell monkey -p " + packagename() + " -v 500 -s 42";
        Process pr4;
        private boolean check = false;

        @Override
        public void run() {

            try {
                pr4 = Runtime.getRuntime().exec(monkey);
            } catch (IOException ex) {
                Logger.getLogger(Dynamic.class.getName()).log(Level.SEVERE, null, ex);
            }
            BufferedReader buf4 = new BufferedReader(new InputStreamReader(pr4.getInputStream()));
            while (!Thread.currentThread().isInterrupted()) {
                try {

                    while ((line = buf4.readLine()) != null) {
                        System.out.println(line);
                        if (line.equalsIgnoreCase("// Monkey finished")) {
                            check = true;
                        }
                        Thread.sleep(1);
                    }

                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        public boolean getstatus() {
            return check;
        }

    }

    public static class Install extends Object implements Runnable {

        String cmd = "/users/talal.ahmed/library/android/sdk/platform-tools/";
        String line = " ";
        String install = cmd + "./adb -s emulator-5554 -e install /users/talal.ahmed/Downloads/first_100/first10/" + file();
        Process pr3;
        private boolean check = false;

        @Override
        public void run() {

            try {
                pr3 = Runtime.getRuntime().exec(install);
            } catch (IOException ex) {
                Logger.getLogger(Dynamic.class.getName()).log(Level.SEVERE, null, ex);
            }
            BufferedReader buf4 = new BufferedReader(new InputStreamReader(pr3.getInputStream()));
            while (!Thread.currentThread().isInterrupted()) {
                try {

                    while ((line = buf4.readLine()) != null) {
                        System.out.println(line);
                        if (line.equalsIgnoreCase("Success")) {
                            check = true;
                        }
                        Thread.sleep(1);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Dynamic.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Dynamic.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        public boolean getstatus() {
            return check;
        }
    }
}
