# **Configuring the MotorMonitor**

For the correct use of the MotorMonitor, we need to configure it to our preferences and safety measurements, we can do this through the `MonitorConfig` class

## **MonitorConfig class**

The `MonitorConfig` class is constructed by this four parameters:

* Protocol: how the motor will react to risk conditions
* currentThreshold: the **MAX** current the motor should take
* batteryThreshold: the **MINUMUM** battery voltage that triggers the protocol
* maxCurrentChangeRate: the **MAX** current change rate, this to predict if the motor is experiencing a rapid current increase

### **Protocol**

We can access to the list of all the protocols through the `MotorProtocol` like this:

``` java
MotorProtocol.k<the protocols>;

//Example
MotorProtocol.kSmartCooldown;
```

The list of all the protocols can be found here:

!!! info
    A protocol will be triggered if the motor reports:
    **High current or Low Battery of the robot**

* kNone: safety functions disabled
* kShutdown: shutdowns the motor until the current is safe
* kProportionalReduction: Reduce the given output proportionally
* kBatterySaver: Triggered if the battery voltage is low, reducing the given output by half
* kCooldown: The motor enters in a cooldown mode
* mpPredictiveOverload: If the motor's current increases rapidly, reduce the given output
* kVoltageSpikeProtection: Analyse if the motor is taking a voltage drop, reducing it's output
* kSmartCooldown: Predict if the motor needs to cooldown and reduce the given output
* kDynamicPowerLimit: Limits the output based on the pdp's total current

### **Default values**

If you don't know very well how to tune the `MonitorConfig`, the `MotorMonitor` class counts with static default values that you can use from the configuration

``` java
    //Default values

    double MaxCurrent = MotorMonitor.DEFAULT_CURRENT_THRESHOLD;
    double MinBattery = MotorMonitor.LOW_BATTERY_THRESHOLD;
    double MaxCharge = MotorMonitor.DEFAULT_MAXCURRENTCHANGE;
```

### **Creating a MonitorConfig**

Now that we decide our settings, we'll create a new `MonitorConfig` saving it through a `var` or a `MonitorConfig` as you prefer:

=== "var"

    ``` java
    //Empty Configuration
    var config = new MonitorConfig(null,0,0,0);

    ```

=== "MonitorConfig"

    ``` java
    //Empty Configuration
    MonitorConfig config = new MonitorConfig(null,0,0,0);
    ```
Next, we need to pass our values to the constructor like this:

``` java

    //Proportional Reduction with default values
    double MaxCurrent = MotorMonitor.DEFAULT_CURRENT_THRESHOLD;
    double MinBattery = MotorMonitor.LOW_BATTERY_THRESHOLD;
    double MaxCharge = MotorMonitor.DEFAULT_MAXCURRENTCHANGE;

    var config = new MonitorConfig(MotorProtocol.kProportionalReduction,MaxCurrent,MinBattery,MaxCharge);

```

!!! tip
    You can use the same config to apply it to different `MotorMonitor` instances

### **Applying the configuration**

Finally we need to apply our changes to the `MotorMonitor` we created, we can do this with the `applyConfiguration(configuration)` method or manually through every specified method

``` java
    //Creation of the pdp and other environments [...]

    //Monitor
    MotorMonitor m_monitor = new MotorMonitor(pdp, m_channel);

    //Proportional Reduction with default values
    double MaxCurrent = MotorMonitor.DEFAULT_CURRENT_THRESHOLD;
    double MinBattery = MotorMonitor.LOW_BATTERY_THRESHOLD;
    double MaxCharge = MotorMonitor.DEFAULT_MAXCURRENTCHANGE;

    //Creating the configuration
    var config = new MonitorConfig(MotorProtocol.kProportionalReduction,MaxCurrent,MinBattery,MaxCharge);

    //Applying it
    m_monitor.applyConfiguration(config);

```

Manually method works too:

``` java
    //Creation of the pdp and other environments [...]

    //Monitor
    MotorMonitor m_monitor = new MotorMonitor(pdp, m_channel);

    //Proportional Reduction with default values
    double MaxCurrent = MotorMonitor.DEFAULT_CURRENT_THRESHOLD;
    double MinBattery = MotorMonitor.LOW_BATTERY_THRESHOLD;
    double MaxCharge = MotorMonitor.DEFAULT_MAXCURRENTCHANGE;

    //Applying it manually

    m_monitor.setMotorProtocoll(MotorProtocol.kProportionalReduction);

    m_monitor.adjustCurrentThreshold(MaxCurrent);

    m_monitor.setBatteryThreshold(MinBattery);

    m_monitor.setMaxCurrentChangeRate(MaxCharge);

```

Both methods for applying the desired configuration should work. If we finished applying the config, we can finally start to start using our MotorMonitor
