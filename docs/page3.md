# **Using Motor Monitor -Logging**

`MotorMonitor` provides a lot of methods to provide safety and logging to know what is actually happening to the motor

## **Logging**

We can obtain information fields for feedback with `MotorMonitor` to keep an observation of each motor performance through the match

### **State**

We can log the state of the `MotorMonitor` by two methods:

* **void** `monitorMotor()` sends alert to the driver station reporting anomalies in the motor

* **string** `getMotorState()` returns the "state" (alert) of the motor for you to use for display

!!! info
    All this methods **should** be called periodically, every **X** seconds or being triggered for optimization

``` java
    m_monitor.monitorMotor(); //Logs the state of the motor
    
    String motor1State = m_monitor.getMotorState();

    SmartDashboard.putString("Motor 1 state:", motor1State); //Logs the state and shows it to your Dashboard

```

The alert it will show is the following:

    "ALERT! MOTOR IN CHANNEL[ "+ channel + "] IS OVERCHARGED"

If you're using the `getMotorState()` method, it will show this text if there's no errors:

    "Current State: Good (Motor in channel[ "+ channel + "])"

### **Individual Fields**

Additionally we can log more detailed information of the motor

``` java
    m_monitor.monitorMotor(); //Logs the state of the motor
    
    String motor1State = m_monitor.getMotorState();

    double motor1Current = m_monitor.getMotorCurrent();

    boolean isOverCharged = m_monitor.isOverCharged(0); //Checks if the motor is OverCharged with additional margin of 0

    SmartDashboard.putString("Motor 1 state:", motor1State); //Logs the state and shows it to your Dashboard

    SmartDashboard.putNumber("Motor 1 current:", motor1Current); //Logs the Current

    SmartDashboard.putBoolean("Motor 1 Overcharged", isOverCharged); //Logs overcharged state on the Dashboard

```

### **Logging multiple mechanisms**

You can log detailed information for as many mechanisms as you want, lets log the current and boolean states of a 4 `MotorMonitor` Modules swerve Drivebase

``` java

    //Logs all the detailed information of the 4 Modules
    double flcurrent = monitor1.getMotorcurrent();
    boolean flstate = monitor1.isOvercharged(0);

    double frcurrent = monitor2.getMotorcurrent();
    boolean frstate = monitor2.isOvercharged(0);
    
    double blcurrent = monitor3.getMotorcurrent();
    boolean blstate = monitor3.isOvercharged(0);

    double brcurrent = monitor4.getMotorcurrent();
    boolean brstate = monitor4.isOvercharged(0);

    //Group all this data into lists
    Double[]SwerveCurrent = {flcurrent,frcurrent,blcurrent,brcurrent};

    Boolean[]SwerveStates = {flstate,frstate,blstate,brstate};

    //Display it 
    SmartDashboard.putNumberArray("SwerveCurrent", SwerveCurrent);

    SmartDashboard.putBooleanArray("ModulesOvercharged", SwerveStates);
    
```
