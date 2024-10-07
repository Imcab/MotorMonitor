# **Using Motor Monitor -Controlling Outputs**

## **Controlling the MAX% Output**

 `MotorMonitor` class counts with an static method: `setMaxOutputpercentage(double output, double %)`, this method allows us to
 reduce the given output before sending to the motor by the % reprecenting **how** much percentage we want of the output (0-100) value.
 This number is highly customisable and you can implemment it in different ways, lets see them:

### **Normal % reduction**

Here's a basic implementation of the method considering we have a motor `m_motor`:

``` java
    double axis = controller.getLeftY(); //get the value of the axis

    double outputPercentage = 85; // Only get the 85% of the value

    m_motor.set(MotorMonitor.setMaxOutputpercentage(axis, outputPercentage)); //Move the motor with 85% of the axis

```

## **Conditional reduction**

Fine! but we can make simple conditions to trigger this reduction:

``` java
    double axis = controller.getLeftY(); //get the value of the axis

    double outputPercentage = 100; // get 100% of the value

    //If the motor is overcharged, reduce the output by 30%
    if (monitor1.isOvercharged(0)) { 
      outputPercentage = 70;
    }

    m_motor.set(MotorMonitor.setMaxOutputpercentage(axis, outputPercentage)); 

```

Or we can trigger the reduction by a joystick button:

``` java
    double axis = controller.getLeftY(); //get the value of the axis

    double outputPercentage = 100; // get 100% of the value

    //If the button is true, reduce the output by 30%
    if (controller.getaButton()) { 
      outputPercentage = 70;
    }

    m_motor.set(MotorMonitor.setMaxOutputpercentage(axis, outputPercentage)); 

```

Using the Driver's dashboard could be util too:

``` java
    double axis = controller.getLeftY(); //get the value of the axis

    double outputPercentage = 100; // get 100% of the value

    boolean ShouldReduce = SmartDashboard.getBoolean("M1Reduction", false); //Checks the value of the key "M1Reduction" returning false for default

    //If "M1Reduction" is true, reduce the output by 30%
    if (ShouldReduce) { 
      outputPercentage = 70;
    }

    m_motor.set(MotorMonitor.setMaxOutputpercentage(axis, outputPercentage)); 

```

### **Using Dashboard**

We can implement the `SmartDashboard` feature to tune more our reduction

### **Input from user**

We can use the input given in the Dashboard to reduce our motors

``` java
    double axis = controller.getLeftY(); //get the value of the axis

    double DashboardSet = SmartDashboard.getNumber("Motor1Output%", 100); //Gets the number input from "Motor1Output%" (returning 100 if default)

    m_motor.set(MotorMonitor.setMaxOutputpercentage(axis, DashboardSet)); //Sets the motor with the % output given by the Dashboard
```

### **SendableChooser**

In addition, we can set 3 output % states and add it to a sendable chooser:

* **MAX** = 100% of the output
* **MEDIUM** = 85% of the output
* **MIN** = 70% of the output

First, we need to create a `SendableChooser` object:

``` java
    //Creates a sendable chooser with the data type being double values
    SendableChooser<Double> outputChooser = new SendableChooser<>();
```

Now we can define our 3 states in the code and send it to the output:

``` java
    double axis = controller.getLeftY(); //get the value of the axis

    //Set options for the Sendable Chooser with the default option being 100%
    outputChooser.setDefaultOption("MAX", 100.0);
    outputChooser.addOption("MEDIUM", 85.0);
    outputChooser.addOption("MIN", 70.0);
    
    //Controlls the given output with the 3 states of the Sendable Chooser
    m_motor.set(MotorMonitor.setMaxOutputpercentage(axis, outputChooser.getSelected()));

```
