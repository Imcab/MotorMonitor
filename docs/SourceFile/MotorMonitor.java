/*
 ===============================================
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
@@@@@@@@@@@@@@@@@///////////////@@@@@@@@@@@@@@@@
@@@@@@@@@@@@@@@@@///////////////@@@@@@@@@@@@@@@@
@@@@@@@@@@@@@@@@@////@@@@@@/////@@@@@@@@@@@@@@@@
@@@@@@@@@@@@@@@@@////@@///@/////@@@@@@@@@@@@@@@@
@@@@@@@@/////@@@@/////////@/////@@@@@@@@@@@@@@@@
@@@@@@@@/////@@@@/////////@/////@@@@@@@@@@@@@@@@
@@@@@@@@////////@@@@@@@@@@@/////@@@@@@@@@@@@@@@@
@@@@@@@@@@@@@///////////////////@@@@@@@@@@@@@@@@
@@@@@@@@@@@@@///////////////////@@@@@@@@@@@@@@@@
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 ===============================================
*/
package frc.robot.Utils; //Your Package Here

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.Timer;

public class MotorMonitor{

    private PowerDistribution pdp;
    private double currentThreshold;
    private double batteryThreshold;
    private double overCurrentStart = -1;
    private double timeForAlert;
    private boolean Enabled;
    private int channel;
    private MotorProtocol protocol;
    private boolean motorshutDown;
 
    private double lastCurrent = 0;
    private double lastTimestamp = 0;
    private double cooldownStartTime = -1;
    private double accumulatedHighCurrentTime = 0;
    private double lastBatteryVoltage = 12.0;

    private double maxCurrentChangeRate; 

    public static final double DEFAULT_CURRENT_THRESHOLD = 40.0;

    public static final double LOW_BATTERY_THRESHOLD = 8.0;

    public static final double DEFAULT_MAXCURRENTCHANGE = 50.0; 

    /**
     * All this protocols will be triggerd 
     * <p> mpNone: safety functions disabled
     * <p> mpShutdown: shutdowns the motor until the current is safe
     * <p> mpProportionalReduction: Reduce the given output proportionally
     * <p> mpBatterySaver: Triggered if the battery voltage is low, reducing the given output by half
     * <p> mpCooldown: The motor enters in a cooldown mode
     * <p> mpPredictiveOverload: If the motor's current increases rapidly, reduce the given output
     * <p> mpVoltageSpikeProtection: Analyse if the motor is taking a voltage drop, reducing it's output
     * <p> mpSmartCooldown: Predict if the motor needs to cooldown and reduce the given output
     * <p> mpDynamicPowerLimit: Limits the output based on the pdp's total current
     */
    public enum MotorProtocol{
        kNone, kShutdown, kProportionalReduction, kBatterySaver, kCooldown,
         kPredictiveOverload, kVoltageSpikeProtection, kSmartCooldown,
         kDynamicPowerLimit
    }
    /**
     * The Motor Monitor Class checks individually the state of the motor and sends alerts
     * @param pdp The power distribution system
     * @param channel The pdp's channel to check the motor
     */
    public MotorMonitor(PowerDistribution pdp, int channel){
        this.pdp = pdp;
        this.channel = channel;
        currentThreshold = DEFAULT_CURRENT_THRESHOLD;
        timeForAlert = 3;
        Enabled = false;
        motorshutDown = false;
        batteryThreshold = LOW_BATTERY_THRESHOLD;
        maxCurrentChangeRate = DEFAULT_MAXCURRENTCHANGE;
        
        //monitorLog = new ArrayList<>();
    }

    public static class MonitorConfig {
        private final double currentThreshold, batteryThreshold, maxCurrentChangeRate;
        private final MotorProtocol protocol;

        public MonitorConfig(MotorProtocol protocol,double currentThreshold, double batteryThreshold, double maxCurrentChangeRate){
            this.protocol = protocol;
            this.currentThreshold = currentThreshold;
            this.batteryThreshold = batteryThreshold;
            this.maxCurrentChangeRate = maxCurrentChangeRate;
        }

        private double getCurrent(){
            return currentThreshold;
        }
        private double getBattery(){
            return batteryThreshold;
        }
        private double getRate(){
            return maxCurrentChangeRate;
        }
        private MotorProtocol geMotorProtocol(){
            return protocol;
        }
        
    }

    /**
     * Configuration from motorMonitor
     */
    public void applyConfiguration(MonitorConfig config){
        setBatteryThreshold(config.getBattery());
        setMaxCurrentChangeRate(config.getRate());
        adjustCurrentThreshold(config.getCurrent());
        seMotorProtocol(config.geMotorProtocol());

    }
    
    /**
     * Proportionally reduce an output from a vaalue
     * @param output the output to reduce
     * @param percentage the % of the output (0-100)
     * @return the output fixed
     */
    public static double setMaxOutputpercentage(double output, double percentage){
        double fix = (percentage / 100);
        return output * fix;
    }
    /**
    * Set the current limit AMPS for the motor monitor to check
    * @param AMPS The limit current AMPS (40.0 for default) 
    */
    public void adjustCurrentThreshold(double AMPS){
        this.currentThreshold = AMPS;
    }
    /**
     * Sets the minimum value the battery requires to trigger low energy state
     * @param min_value the minimum volts of the battery
     */
    public void setBatteryThreshold(double min_value){
        this.batteryThreshold = min_value;
    }
    /**
    * Set the time limit for sending alerts
    * @param time The time (in secs) that has to pass for sending a report (3,000 for default) 
    */
    public void setTimeForAlert(double time){
        this.timeForAlert = time;
    }
    /**
     * Set the maximun change of the robot 
     * @param rate the maximum current change the robot can take
     */
    public void setMaxCurrentChangeRate(double rate){
        this.maxCurrentChangeRate = rate;
    }
    /**
     * Sets the motor Porotocol
     * @param p The protocol
     */
    public void seMotorProtocol(MotorProtocol p){
        this.protocol = p;
    }
    /**
     * Gets the configuration of the current limit
     * @return The current limit in AMPS
     */
    public double getCurrentLimit(){
        return currentThreshold;
    }
    /**
     * Gets the configuration of the time
     * @return The time (in milis) that need to pass for an alert
     */
    public double getTimeForAlert(){
        return timeForAlert;
    }
    /**
     * Gets the current of the motor in an specified channel
     * @return The motor current in AMPS
     */
    public double getMotorcurrent(){
        return pdp.getCurrent(channel);
    }
    private final double alertCooldown = 3;  // seconds
    private double lastAlertTime = 0;

    private void sendAlert(String warning) {
        double currentTime = Timer.getFPGATimestamp();
        if (currentTime - lastAlertTime > alertCooldown) {
            DriverStation.reportWarning(warning, false);
            lastAlertTime = currentTime;
        }
    }

    /**
     * Periodically checks for the state of the motor and sends alerts to the driver station if the motor is Overcharged
     */
    public void monitorMotor(){

        if (isOvercharged(0)) {
            if (overCurrentStart == -1) {
                overCurrentStart = Timer.getFPGATimestamp();
            }else if (Timer.getFPGATimestamp() - overCurrentStart > timeForAlert) {
                System.out.println("ALERT! MOTOR IN CHANNEL[ "+ channel + "] IS OVERCHARGED");
                sendAlert("ALERT! MOTOR IN CHANNEL[ "+ channel + "] IS OVERCHARGED");
            }
        
        }else{
            overCurrentStart = -1;
        }
    }
    /**
     * Gets the motor state:
     * <p> -Overcharged
     * <p> -Normal state
     * @return the diagnostic that the motor monitor does (string)
     */
    public String getMotorState(){
        if (isOvercharged(0)) {
            if (overCurrentStart == 0) {
                overCurrentStart = Timer.getFPGATimestamp();
            }else if (Timer.getFPGATimestamp() - overCurrentStart > timeForAlert) {
                return ("ALERT! MOTOR IN CHANNEL[ "+ channel + "] IS OVERCHARGED");
            }
            
        }else{
            overCurrentStart = 0;
            
        }
        return ("Current State: Good (Motor in channel[ "+ channel + "])");
    }
    private double getBatteryVoltage(){
        return RobotController.getBatteryVoltage();
    }
    private boolean isLowBattery(){
        return getBatteryVoltage() < batteryThreshold;
    }
    /**
     * Check if the motor is OverCharged
     * @param additionalMargin additional amps the function will consider to return the state
     * @return True if OverCharged
     */
    public boolean isOvercharged(double additionalMargin){
        return getMotorcurrent() > currentThreshold + additionalMargin;
    }
    /**
     * Enable/Disable safet mode from motors
     */
    public void disableSafety(boolean toggle){
        Enabled = toggle;
    }
    /**
     * Shutdowns the motor
     */
    public void forceShutDown(){
        toggleShutdown(true);
    }
    /**
     * Toggles the shutdown by a boolean trigger (value)
     * @param ToggleShutdow the trigger
     */
    public void toggleShutdown(boolean toggleShutdow){
        motorshutDown = toggleShutdow;
    }
    /**
     * Chcek if the motor is shutdown
     * @return if the motor current state is shutdown
     */
    public boolean isShutdown(){
        return motorshutDown;
    }
    private double shutdown(double v){
        if (isOvercharged(20)) {
                    sendAlert("MOTOR IN CHANNEL["+ channel+ "] SHUTDOWN");
                    return 0.0;
                }
        return v;               
    }
    private double batterysave(double v){
        if (isLowBattery()) {
                    sendAlert("MOTOR IN CHANNEL["+ channel + "] ENTERING IN LOW ENERGY MODE");
                    return v * 0.75 ;
            }

        return v;
    }
    private double proportionalreduct(double v){
        if (isOvercharged(0)) {
                if (v != 0) {
                    sendAlert("MOTOR IN CHANNEL[" + channel + "] PROPORTIONALLY REDUCING ENERGY");
                    return v * (currentThreshold / v);
                } else {
                    return 0.0;
                }
            }

        return v;
    }
    double accumulatedCurrent = 0.0;

    private double coolingdown(double v){
        double currentMotorCurrent = getMotorcurrent();

        double timeInterval = 0.02; 

        accumulatedCurrent += currentMotorCurrent * timeInterval;

        double coolingLimit = 1200;

        if (accumulatedCurrent > coolingLimit) {
                sendAlert("MOTOR IN CHANNEL["+ channel+ "] COOLING DOWN");

                return (v/2);
            }
        
        return v;
            
    }
    private double predictiveOverload(double v, double currentTime) {
        double currentMotorCurrent = getMotorcurrent();
        double currentChangeRate = (currentMotorCurrent - lastCurrent) / (currentTime - lastTimestamp);

        lastCurrent = getMotorcurrent();
        lastTimestamp = currentTime;

        if (currentChangeRate > maxCurrentChangeRate) {
            sendAlert("MOTOR IN CHANNEL[" + channel + "] EXPERIENCING RAPID CURRENT INCREASE");
            return v * 0.8; 
        }

        return v;
    }
    private double voltageSpikeSafety(double v) {
        double voltageDrop = lastBatteryVoltage - getBatteryVoltage();
        lastBatteryVoltage = getBatteryVoltage();

        if (voltageDrop > 2.0 && getBatteryVoltage() < 8.0) {
            sendAlert("MOTOR IN CHANNEL[" + channel + "] EXPERIENCING VOLTAGE DROP");
            return v * 0.7;
        }

        return v;
    }
    private double smartCooldown(double v, double currentTime) {
        if (isOvercharged(0)) {
            if (cooldownStartTime == -1) {
                cooldownStartTime = currentTime;
            }
            accumulatedHighCurrentTime += (currentTime - cooldownStartTime);
        } else {
            accumulatedHighCurrentTime = Math.max(0, accumulatedHighCurrentTime - (currentTime - cooldownStartTime));
            cooldownStartTime = -1;
        }
    
        if (accumulatedHighCurrentTime > 10) {
            sendAlert("MOTOR IN CHANNEL[" + channel + "] NEEDS COOLDOWN");
            return v * 0.5; 
        } else if (accumulatedHighCurrentTime > 5) {
            return v * 0.75; 
        }
    
        return v;
    }
    
    private double dynamicPowerLimit(double v) {

        double totalCurrent = pdp.getTotalCurrent();  
        double maxSafeCurrent = 100;  

        if (totalCurrent > maxSafeCurrent) {
            sendAlert("TOTAL CURRENT TOO HIGH. REDUCING MOTOR ["+ channel +"]POWER.");
            return v * 0.7; 
        }

        return v; 
    }
    /**
     * Detects if the motor is stalled, if it is true you should stop the motors inmmediately to prevent damage
     * @param motorVelocity
     * @return
     */
    public boolean stallDetection(double motorVelocity) {
        if (isOvercharged(0) && Math.abs(motorVelocity) < 0.1) {
            sendAlert("MOTOR IN CHANNEL[" + channel + "] IS STALLED");
            return true; 
        }
        return false;
    }
    /**
     * 
     * Let the Monitor take actions based on high current levels or low battery voltage to preserve the motor conditions
     * @param motorvalue the output given to the motor
     * @return the motor output with safety measurements
     */
    public double withSafety(double motorvalue){ 

        if (isShutdown()) {
            return 0;
        }
        if (!isShutdown()){
            if (!Enabled) {
               switch (protocol) {
                case kNone:
                    return motorvalue;
                case kBatterySaver:
                    return batterysave(motorvalue);
                case kShutdown:
                    return shutdown(motorvalue);
                case kDynamicPowerLimit:
                    return dynamicPowerLimit(motorvalue);
                case kSmartCooldown:
                    return smartCooldown(motorvalue, Timer.getFPGATimestamp());
                case kCooldown:
                    return coolingdown(motorvalue);
                case kPredictiveOverload:
                    return predictiveOverload(motorvalue, Timer.getFPGATimestamp());
                case kProportionalReduction:
                    return proportionalreduct(motorvalue);
                case kVoltageSpikeProtection:
                    return voltageSpikeSafety(motorvalue);
                default:
                    return motorvalue;
               } 
            }
        }

        return motorvalue;

    }
}