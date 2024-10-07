# **Using Motor Monitor - WithSafety**

!!! warning
    Before using the `withSafety(motorvalue)` method, make sure you already have [configured](/MotorMonitor/page2/) the `MotorMonitor` and already have a
    **[protocol](/MotorMonitor/page2/#protocol)**

Remember the configuration we set on [Configuring the MotorMonitor](/MotorMonitor/page2/) ?
With that configuration we can run a given output with the [safety protocols](/MotorMonitor/page2/#protocol) without worrying of the overcurrent/states of the motor or manually setting up, the `withSafety(motorvalue)` method will do all that for you:

`withSafety(motorvalue)`
Let the Monitor take actions based on high current levels or low battery voltage to preserve the motor conditions

**Parameters:**

**motorvalue** the output given to the motor

**Returns:**
the motor output with safety measurements

## **Implementation**

Here's some basic implementation of how to run a motor with safety protocols:

``` java
    double axis = controller.getLeftY(); //get the value of the axis

    m_motor.set(monitor1.withSafety(axis)); //Runs the motor with safety protocols enabled

```

## **Disable**

It is as simpliest as you can see to run this method, also you can temporally disable the safety protocols if you are having issues:

``` java
    monitor1.disableSafety(true); //Disables the safety protocols of the motors
```

Or you can toggle the safety state on the dashboard like this:

``` java
    boolean getButton = SmartDashboard.getBoolean("ToggleSafety", false); //Get the value of the key "ToggleSafety", returning false on default

    monitor1.disableSafety(getButton); //Toggles the safety by the Dashboard
```
