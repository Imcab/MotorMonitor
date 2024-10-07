# **Shutdown with Motor Monitor**

During competition it is important to manage the state of the motors all through the match and being able to **shutdown** a motor
is important for various reasons:

* **Safety:** A malfunctioning motor can pose significant risks, such as overheating, sparking, or causing damage to other components
* **Preventing Further Damage:** Running a motor with issues can lead to permanent damage, which could have been avoided by stopping it early. Continuing to operate a failing motor may also affect other systems, leading to a cascading failure within the robot
* **Performance**: A faulty motor can impair the robot's ability to perform effectively in the match. Shutting it down prevents erratic behavior that could affect game strategy

## **Shutdown Methods**

To shutdown a motor, we need a motor running with the `withSafety(motorvalue)` method ([guide here](/page5)),
we have the simple `forceShutDown()` method that we can call it, forcing the motor to shutdown

``` java
    monitor1.forceShutDown()//Shutdowns the motor
```

Also the `MotorMonitor` class provides a **toggle** method to shutdown the motor accepting a boolean value:

``` java
    monitor1.toggleShutdown(boolean);
```

We can do plenty of conditionals to trigger our toggle function, here are some examples:

### **With Dashboard**

``` java
    boolean Shutdown = SmartDashboard.getBoolean("ShutdownMotor", false);

    monitor1.toggleShutdown(Shutdown);
```

### **With JoystickButton**

``` java
    boolean Shutdown = controller.getAButton();

    monitor1.toggleShutdown(Shutdown);
```

### **With sendable Chooser**

``` java
    SendableChooser<Boolean> shutdwon = new SendableChooser<>(); //Creates the sendable chooser
```

``` java

    //adds options
    shutdwon.setDefaultOption("SHUTDOWN: OFF", false);
    shutdwon.addOption("SHUTDOWN: ON", true);

    boolean Shutdown = shutdwon.getSelected(); //Selects the option

    monitor1.toggleShutdown(Shutdown);
    
```

### **Stalling detection**

When a motor stalls, it experiences maximum current draw while providing no output motion. This high current generates excessive heat, which can damage the motor's windings and even cause failures in the motor controller or other electrical components

Keeping a stalled motor running could pose risks to the robot, the field, and team members, so it is important to shutdown inmediatelly

Fortunately, the `MotorMonitor` class provides with a method that detects if a motor is stalled, combining this method with a shutdown method will protect the motor for any permanent damages

=== "Method 1"
    Checks if the Motor is stalled by passing the speed of the motor, forcing the motor to shutdown if true

    ``` java
    
    if (monitor1.stallDetection(m_motor.get())) {
      monitor1.forceShutDown();
    }

    ```

=== "Method 2"
    Checks if the Motor is stalled and inmediately disabling the motor if true

    ``` java

    monitor1.toggleShutdown(monitor1.stallDetection(m_motor.get()));

    ```

As you can see, you can do a lot of triggers to the toggle method and also you can use conditionals with the forceShutdown method, it is up to you, your team needs and imagination
