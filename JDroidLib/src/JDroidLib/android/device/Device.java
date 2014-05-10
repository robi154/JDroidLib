/*
 * Copyright (C) 2014 beatsleigher.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */


package JDroidLib.android.device;


import JDroidLib.android.controllers.*;
import JDroidLib.enums.DeviceState;
import java.io.*;
import java.util.*;

/**
 *
 * @author beatsleigher
 */
@SuppressWarnings({"FieldMayBeFinal"})
public class Device {

    private SU su = null;
    private BusyBox busybox = null;
    private Battery battery = null;
    private BuildProp buildProp = null;
    private DeviceState state = null;
    private CPU cpu = null;
    private String serial = null;
    private ADBController adbController = null;

    public Device(String device, ADBController adbController) {
        String[] arr = device.split("\t");
        System.out.println("Device: " + device + ", array length: " + arr.length);
        
        this.adbController = adbController;
        this.serial = arr[0];
        su = new SU(serial, adbController);
        busybox = new BusyBox(serial, adbController);
        battery = new Battery(serial, adbController);
        cpu = new CPU(serial, adbController);
        buildProp = new BuildProp(serial, adbController);
        if (!(arr.length <= 1))
            state = DeviceState.getState(arr[1].toLowerCase());
        else
            state = DeviceState.getState(device); 
    }

    /**
     * Refreshes the device's current state.
     * @return Returns the device's current state (after the state was refreshed)
     * @throws IOException If something goes wrong (which shouldn't happen, really)
     */
    public DeviceState getState() throws IOException {
        for (Device dev : adbController.getConnectedDevices()) {
            if (dev.toString().contains(toString())) {
                String[] arr = dev.toString().split("\t");
                this.state = DeviceState.valueOf(arr[1]);
            }
        }
        return state;
    }

    public SU getSU() {
        return su;
    }

    public BusyBox getBusybox() {
        return busybox;
    }

    public Battery getBattery() {
        return battery;
    }

    public CPU getCPU() {
        return cpu;
    }

    public String getSerial() {
        return serial;
    }

    public BuildProp getBuildProp() {
        return buildProp;
    }

    @Override
    /**
     * Returns the serial number of the device currently represented by this Device object.
     * @return (See description)
     */
    public String toString() {
        return serial;
    }
    
    public PackageController getPackageController() {
        return new PackageController(adbController, this);
    }
    
    /**
     * Installs an application to this device (the device represented by this object)
     * @param apk The application to install
     * @return True, if installation was a success, false is otherwise.
     * @throws IOException If something goes wrong.
     * @throws NullPointerException ^
     */
    public boolean installApplication(String apk) throws IOException, NullPointerException {
        List<String> args = new ArrayList<>();
        args.add("install");
        args.add(apk);
        String output = adbController.executeADBCommand(false, false, this, args);
        BufferedReader reader = new BufferedReader(new StringReader(output));
        String line = null;
        while ((line = reader.readLine()) != null) {
            if (line.toLowerCase().contains("success"))
                return true;
        }
        return false;
    }

}
