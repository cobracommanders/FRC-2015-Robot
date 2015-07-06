package org.usfirst.frc.team498.robot;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This is a demo program showing the use of the RobotDrive class. The
 * SampleRobot class is the base of a robot application that will automatically
 * call your Autonomous and OperatorControl methods at the right time as
 * controlled by the switches on the driver station or the field controls.
 *
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SampleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 *
 * WARNING: While it may look like a good choice to use for your code if you're
 * inexperienced, don't. Unless you know what you are doing, complex code will
 * be much more difficult under this system. Use IterativeRobot or Command-Based
 * instead if you're new.
 */
public class Robot extends SampleRobot {
	LightManager lightManager = new LightManager(this);
	LightPatterns patterns = new LightPatterns();
	RobotDrive drive = new RobotDrive(4, 2);
	Victor wacker = new Victor(8);
	Joystick thisStick = new Joystick(0);
	Joystick thatStick = new Joystick(1);
	Timer mainClock = new Timer();
	Victor rightW = new Victor(1);
	Victor leftW = new Victor(3);
	Timer ultrasonicClock = new Timer();
	Victor pulley = new Victor(5);
	Relay light0 = new Relay(0);
	Relay light1 = new Relay(1);
	Relay extenderLight = new Relay(2);
	Relay light3 = new Relay(3);
	DigitalInput autonomousToggle = new DigitalInput(0);
	DigitalInput switch1 = new DigitalInput(4); // whoever decided Limit Switch
												// 1 goes at the top of the
												// robot is cancer
	DigitalInput switch2 = new DigitalInput(5);
	DigitalInput switch3 = new DigitalInput(6);
	DigitalInput switch4 = new DigitalInput(7);
	DigitalInput switch5 = new DigitalInput(8); // the bottom
	DigitalInput wackerStopperOut = new DigitalInput(9);
	DigitalInput wackerStopperIn = new DigitalInput(10);
	DoubleSolenoid armExtender = new DoubleSolenoid(0, 1);
	DoubleSolenoid canBurglar = new DoubleSolenoid(2, 3);
	// Ultrasonic eye0 = new Ultrasonic(0, 1);// (Input, Output) Slots on the
	// sensor
	Ultrasonic eye1 = new Ultrasonic(2, 3);
	PowerDistributionPanel PDP = new PowerDistributionPanel();
	Gyro gyro = new Gyro(1);
	GyroCalibration calibratedGyro = new GyroCalibration(gyro);
	Timer rampClock = new Timer();
	boolean lightsFirstTime = false;
	boolean partnerSlowDown = false;
	boolean usingLights = false;
	boolean Initialized = false;
	double gyroOffset;
	int phase = -1; // Current Autonomous Phase
	double u = -.1; // Debug value used to find drift constant in teleop
	double i = .65;// Debug value used to find drift constant in teleop
	/*
	 * Constants
	 */
	// double driftConstant = -0.1; // The number that needs to be added to
	// RotateValue to drive straight
	// double armLength = 27; // Length in inches of the robot's arms
	// double scalingValue = (1 / 2); // For Ultrasonic Drive to Tote
	double rampTime = .3; // Time in seconds before drive motors reach 100%
	// double robotToToteWhenSetDistance = 9; // Distance in inches from
	// ultrasonic
	// to the tote for optimal lifeting
	// double turnReduction = .82;
	// double beginWait = 2.5;
	/*
	 * double pistonDelay = .36; double pulleyDownTime = 1; double pulleyUpValue
	 * = 1; double pulleyDownValue = -1; double turnValue = -.9;
	 */
	double wackerSmackValue = -.8;
	double wackerRetractValue = .6;
	double wackerSmackTime = .85;
	double wackerRetractTime = .44;
	double preWackDriveTime = .7;
	double preWackSpeed = .85;
	// VALUES FROM GITUB//
	// ------------------------------------------------------------------
	double driftConstant = -0.138; // The number that needs to be added to
	// RotateValue to drive straight
	double armLength = 29; // Length in inches of the robot's arms
	double armLength2 = 31; // length of the wheels arms. 31 is an aprx
	double scalingValue = (1 / 2); // For Ultrasonic Drive to Tote
	double robotToToteWhenSetDistance = 9; // Distance in inches from ultrasonic
	// to the tote for optimal lifeting
	double robotToTrashCan = 28; // Distance in inches from ultrasonic to move
									// trashcan. 69 is a place holder
	double pistonDelay = .4;
	double pulleyDownTime = .1;
	double bigPistonDelay = 3;
	double pulleyUpValue = 1;
	double pulleyDownValue = -0.83;
	double turnValue = -0.9; // Has been adjusted to two motors
	// --------------------------------------------------------------------------
	// was double turnValue = -0.6;
	/*
	 * Autonomous Public Variables
	 */
	boolean haveArrived = false;
	double moveValue;
	double moveValue2;
	double rotateValue;
	double rotateValue2;
	double canMoving = 3;
	public void initilize() {
		if (!Initialized) {
			eye1.setAutomaticMode(true);
			LightPatterns lightPatterns = new LightPatterns();
			lightManager.setLightParser(lightPatterns.lightPattern1);
			mainClock.start();
			CameraServer server = CameraServer.getInstance();
			server.setQuality(50);
			server.startAutomaticCapture("cam0");
			rampClock.start();
			calibratedGyro.reset();
			Initialized = true;
		}
	}
	// The TEST VERSION
	/*
	 * private void driveStraight() { drive.arcadeDrive(moveValue, rotateValue);
	 * rotateValue = driftConstant + (calibratedGyro.getCalibratedAngle() *
	 * .03);
	 * 
	 * if (eye1.getRangeInches() < armLength && eye1.getRangeInches() >
	 * robotToToteWhenSetDistance) { moveValue = .35; haveArrived = false; }
	 * else if (eye1.getRangeInches() > armLength) { moveValue = .85;
	 * haveArrived = false; } else { moveValue = 0; haveArrived = true; } }
	 */
	// From GITHUB
	private void driveStraight() {
		drive.arcadeDrive(moveValue, rotateValue);
		rotateValue = (calibratedGyro.getCalibratedAngle() * .03)
				+ driftConstant;
		if (eye1.getRangeInches() < armLength
				&& eye1.getRangeInches() > robotToToteWhenSetDistance) {
			moveValue = .25;
			haveArrived = false;
		} else if (eye1.getRangeInches() > armLength) {
			moveValue = .75;
			haveArrived = false;
		} else {
			moveValue = 0;
			haveArrived = true;
		}
	}
	private void tannerDrive() {
		leftW.set(-1);
		rightW.set(1);
		drive.arcadeDrive(moveValue2, rotateValue2);
		rotateValue2 = (calibratedGyro.getCalibratedAngle() * .03)
				+ driftConstant;
		if (eye1.getRangeInches() < armLength2
				&& eye1.getRangeInches() > robotToTrashCan) { // look this isn't
																// going to
																// work, but I
																// have an idea
			moveValue2 = .75;
			haveArrived = false;
		} else if (eye1.getRangeInches() > armLength2) {
			moveValue2 = .75;
			haveArrived = false;
		} else {
			moveValue2 = 0;
			haveArrived = true;
		}
	}
	private void extender() {
		if (thisStick.getRawButton(6) && !thisStick.getRawButton(5)) {
			armExtender.set(DoubleSolenoid.Value.kReverse);
			extenderLight.set(Relay.Value.kOff);
		} else if (thisStick.getRawButton(5) && !thisStick.getRawButton(6)) {
			armExtender.set(DoubleSolenoid.Value.kForward);
			extenderLight.set(Relay.Value.kForward);
		}
	}
	private void lights() {
		/*
		 * light0.set(Relay.Value.kReverse); 1 light0.set(Relay.Value.kForward);
		 * 2 light1.set(Relay.Value.kForward); 3
		 * light1.set(Relay.Value.kReverse); 4
		 */
		if (!usingLights) {
			if (!switch5.get()) {
				light0.set(Relay.Value.kOff);
				light1.set(Relay.Value.kOff);
			}
			if (!switch4.get()) {
				light0.set(Relay.Value.kReverse);
				light1.set(Relay.Value.kOff);
			}
			if (!switch3.get()) {
				light0.set(Relay.Value.kOn);
				light1.set(Relay.Value.kOff);
			}
			if (!switch2.get()) {
				light0.set(Relay.Value.kOn);
				light1.set(Relay.Value.kForward);
			}
			if (!switch1.get()) {
				light0.set(Relay.Value.kOn);
				light1.set(Relay.Value.kOn);
			}
		}
	}
	private void pulley() {
		if (!switch5.get()) {
			if (thisStick.getRawAxis(5) < -.1) {
				pulley.set(thisStick.getRawAxis(5) * -1);
			} else {
				pulley.set(0);
			}
		}
		if (!switch1.get()) {
			if (thisStick.getRawAxis(5) > .1) {
				pulley.set(thisStick.getRawAxis(5) * -1);
			} else {
				pulley.set(0);
			}
		}
		if (switch1.get() & switch5.get()) {
			if (thisStick.getRawAxis(5) > 0.1 || thisStick.getRawAxis(5) < -0.1) {
				pulley.set(thisStick.getRawAxis(5) * -1);
			} else {
				pulley.set(0);
			}
		}
	}
	private void dashboard() {
		// SmartDashboard.putNumber("Turn Reduction", turnReduction);
		SmartDashboard.putNumber("1Channel 1,LFR", PDP.getCurrent(1));
		SmartDashboard.putNumber("1Channel 2, BRD", PDP.getCurrent(2));
		SmartDashboard.putNumber("1Channel 3, FRD", PDP.getCurrent(3));
		SmartDashboard.putNumber("Channel 4", PDP.getCurrent(4));
		SmartDashboard.putNumber("Channel 5", PDP.getCurrent(5));
		SmartDashboard.putNumber("Channel 6", PDP.getCurrent(6));
		SmartDashboard.putNumber("Channel 7", PDP.getCurrent(7));
		SmartDashboard.putNumber("Channel 8", PDP.getCurrent(8));
		SmartDashboard.putNumber("Channel 9", PDP.getCurrent(9));
		SmartDashboard.putNumber("Channel 10", PDP.getCurrent(10));
		SmartDashboard.putNumber("Channel 11", PDP.getCurrent(11));
		SmartDashboard.putNumber("1Channel 12, LBD", PDP.getCurrent(12));
		SmartDashboard.putNumber("Channel 13", PDP.getCurrent(13));
		SmartDashboard.putNumber("Channel 14", PDP.getCurrent(14));
		SmartDashboard.putNumber("Channel 15", PDP.getCurrent(15));
		SmartDashboard.putNumber("Eye1", eye1.getRangeInches());
		SmartDashboard.putNumber("Clock Value", mainClock.get());
		SmartDashboard.putNumber("rampClock", rampClock.get());
		SmartDashboard.putNumber("_U", u);
		SmartDashboard.putNumber("_i", i);
		SmartDashboard.putNumber("Move Value", moveValue);
		SmartDashboard.putNumber("Rotate Value", rotateValue);
		SmartDashboard.putNumber("Gyro Angle", gyro.getAngle());
		SmartDashboard.putNumber("Gyro Calibrated Angle",
				calibratedGyro.getCalibratedAngle());
		SmartDashboard.putNumber("Gyro Offset", calibratedGyro.gyroOffset);
		SmartDashboard.putNumber("Gyro Clock", calibratedGyro.GyroClock.get());
		SmartDashboard.putBoolean("Arrived?", haveArrived);
		SmartDashboard.putBoolean("Initilized", Initialized);
		SmartDashboard.putNumber("Auto Phase", phase);
		SmartDashboard.putBoolean("AutonomousSwitch", autonomousToggle.get());
		SmartDashboard.putNumber("Current Phase", lightManager.currentPhase);
		SmartDashboard.putBoolean("Bottom Switch", switch5.get());
	}
	private void driveCode() {
		if (thisStick.getRawButton(3)) {
			wacker.set(.1);
		} else if (thisStick.getRawButton(4)) {
			wacker.set(-1);
		} else {
			wacker.set(0);
		}
		if (thisStick.getRawButton(1)) {
			u = +.01;
		} else if (thisStick.getRawButton(2)) {
			u = -.01;
		}
		if (thisStick.getRawButton(7)) {
			drive.arcadeDrive(i, u);
		} else {
			if (thisStick.getRawAxis(3) > 0) {
				drive.arcadeDrive(rampUp(thisStick, 3), thisStick.getX() * -1);
			} else {
				drive.arcadeDrive(rampUp(thisStick, 2) * -1, thisStick.getX()
						* -1);
			}
		}
	}
	private double rampUp(Joystick stick, int axis) {
		if (rampClock.get() < rampTime) {
			return stick.getRawAxis(axis) / rampTime * rampClock.get();
		} else {
			return stick.getRawAxis(axis);
		}
	}
	private void rampManager() {
		if (thisStick.getRawAxis(2) < .1 && thisStick.getRawAxis(3) < .1) {
			rampClock.reset();
		}
	}
	private void operatorOperations() {
		/*
		 * if (usingLights) { lightManager.Tick(); } if
		 * (thatStick.getRawButton(1)) { mainClock.reset();
		 * lightManager.setLightParser(patterns.strobe); usingLights = true; }
		 * if (thatStick.getRawButton(2)) { mainClock.reset(); usingLights =
		 * true; lightManager.setLightParser(patterns.lightPattern1); } if
		 * (thatStick.getRawButton(3)) { mainClock.reset();
		 * lightManager.setLightParser(patterns.lightPattern2); usingLights =
		 * true; } if (thatStick.getRawButton(4)) { mainClock.reset();
		 * lightManager.setLightParser(patterns.lightPattern3); usingLights =
		 * true; } if (thatStick.getRawButton(5)) { mainClock.reset();
		 * lightManager.setLightParser(patterns.lightPattern4); usingLights =
		 * true; } if (thatStick.getRawButton(6)) { mainClock.reset(); } if
		 * (thatStick.getRawButton(8)) { mainClock.reset(); usingLights = false;
		 * lightManager.setLightParser(patterns.off); }
		 */
		if (thatStick.getRawButton(1)) {
			partnerSlowDown = true;
		} else {
			partnerSlowDown = false;
		}
	}
	public void autonomous() {
		if (autonomousToggle.get()) {
			canMoving = 2;
			autonomous6(); // Down
			//2 is too small 3.5 is too far
		} else {
			canMoving = 1.4;
			autonomous6(); // Up
		}
	}
	public void autonomous1() {
		initilize();
		calibratedGyro.reset();
		mainClock.reset();
		phase = -1;
		while (isEnabled() && isAutonomous()) {
			dashboard();
			switch (phase) {
			case 40:
				break;
			case -1:
				armExtender.set(DoubleSolenoid.Value.kForward);
				Timer.delay(pistonDelay);
				phase++;
				break;
			case 0:
				pulley.set(pulleyUpValue);
				if (!switch1.get()) {
					pulley.set(0);
					mainClock.reset();
					phase++;
					break;
				}
				if (!switch2.get()) {
					pulley.set(0);
					mainClock.reset();
					phase++;
					break;
				}
				break;
			// Delay was here R.I.P. Delay
			case 1:
				drive.arcadeDrive(preWackSpeed, +driftConstant);
				if (mainClock.get() > preWackDriveTime) {
					mainClock.reset();
					phase++;
					break;
				}
				break;
			case 2:
				wacker.set(wackerSmackValue);
				if (!wackerStopperOut.get()) {
					wacker.set(0);
					mainClock.reset();
					phase++;
					break;
				}
				break;
			/*
			 * case 1: drive.arcadeDrive(preWackSpeed, + driftConstant);
			 * 
			 * if (eye1.getRangeInches() < 7) { mainClock.reset(); phase++;
			 * break; } break; case 2:
			 * 
			 * drive.arcadeDrive(preWackSpeed, + driftConstant);
			 * wacker.set(wackerSmackValue); if (mainClock.get() >
			 * wackerSmackTime) { wacker.set(0); mainClock.reset(); phase=45;
			 * break; } break; case 45: drive.arcadeDrive(preWackSpeed, +
			 * driftConstant); wacker.set(wackerRetractValue); if
			 * (mainClock.get() > wackerRetractTime) { wacker.set(0);
			 * mainClock.reset(); phase = 46; break; } break; case 46:
			 * drive.arcadeDrive(preWackSpeed, + driftConstant); if
			 * (mainClock.get() > preWackDriveTime - wackerSmackTime -
			 * wackerRetractTime) { wacker.set(0); mainClock.reset(); phase = 3;
			 * break; } break;
			 */
			case 3:
				wacker.set(wackerRetractValue);
				if (mainClock.get() > wackerRetractTime) {
					wacker.set(0);
					phase++;
					break;
				}
				break;
			case 4:
				driveStraight();
				if (haveArrived) {
					haveArrived = false;
					phase++;
					mainClock.reset();
					break;
				}
				break;
			case 5:
				pulley.set(pulleyDownValue);
				if (mainClock.get() > pulleyDownTime) {
					pulley.set(0);
					phase++;
					break;
				}
				break;
			case 6:
				armExtender.set(DoubleSolenoid.Value.kReverse);
				Timer.delay(pistonDelay);
				phase++;
				mainClock.reset();
				break;
			case 7:
				pulley.set(pulleyDownValue);
				if (!switch5.get()) {
					pulley.set(0);
					phase++;
					break;
				}
				break;
			case 8:
				armExtender.set(DoubleSolenoid.Value.kForward);
				Timer.delay(pistonDelay);
				phase++;
				mainClock.reset();
				break;
			case 9:
				pulley.set(pulleyUpValue);
				if (!switch2.get()) {
					pulley.set(0);
					phase++;
					phase++;
					mainClock.reset();
					break;
				}
				break;
			// Wacker goes here
			/*
			 * case 10: pulley.set(pulleyUpValue); driveStraight(); if
			 * (haveArrived) { haveArrived = false; } if (!switch2.get()) {
			 * phase++; pulley.set(0); mainClock.reset(); break; } break;
			 */
			// Maybe here
			case 11:
				drive.arcadeDrive(preWackSpeed, driftConstant);
				if (mainClock.get() > preWackDriveTime) {
					mainClock.reset();
					phase++;
					break;
				}
				break;
			case 12:
				wacker.set(wackerSmackValue);
				if (!wackerStopperOut.get()) {
					wacker.set(0);
					mainClock.reset();
					phase++;
					break;
				}
				break;
			case 13:
				wacker.set(wackerRetractValue);
				if (mainClock.get() > wackerRetractTime) {
					wacker.set(0);
					mainClock.reset();
					phase++;
					break;
				}
				break;
			case 14:
				driveStraight();
				if (haveArrived) {
					phase++;
					haveArrived = false;
					mainClock.reset();
					break;
				}
				break;
			case 15:
				pulley.set(pulleyDownValue);
				if (mainClock.get() > pulleyDownTime) {
					pulley.set(0);
					phase++;
					break;
				}
				break;
			case 16:
				armExtender.set(DoubleSolenoid.Value.kReverse);
				Timer.delay(pistonDelay);
				phase++;
				mainClock.reset();
				break;
			case 17:
				pulley.set(pulleyDownValue);
				if (!switch5.get()) {
					pulley.set(0);
					phase++;
					break;
				}
				break;
			case 19:
				armExtender.set(DoubleSolenoid.Value.kForward);
				Timer.delay(pistonDelay);
				phase++;
				mainClock.reset();
				break;
			case 20:
				pulley.set(pulleyUpValue);
				if (mainClock.get() > .5) {
					pulley.set(0);
					phase++;
					break;
				}
				break;
			case 21:
				drive.arcadeDrive(0, turnValue);
				if (gyro.getAngle() > 75) {
					drive.arcadeDrive(0, 0);
					mainClock.reset();
					phase++;
					break;
				}
				break;
			case 22:
				drive.arcadeDrive(.8, driftConstant);
				if (mainClock.get() > 1) {
					haveArrived = false;
					phase++;
					mainClock.reset();
					break;
				}
				break;
			case 23:
				drive.arcadeDrive(.35, driftConstant);
				if (mainClock.get() > .3) {
					haveArrived = false;
					phase++;
					mainClock.reset();
					break;
				}
				break;
			case 24:
				pulley.set(pulleyDownValue);
				if (!switch5.get()) {
					phase++;
					mainClock.reset();
					break;
				}
				break;
			case 25:
				armExtender.set(DoubleSolenoid.Value.kReverse);
				Timer.delay(pistonDelay);
				phase = 40;
				mainClock.reset();
				break;
			}
		}
	}
	public void autonomous2() {
		initilize();
		calibratedGyro.reset();
		mainClock.reset();
		phase = -1;
		while (isEnabled() && isAutonomous()) {
			dashboard();
			switch (phase) {
			case 40:
				break;
			case -1:
				armExtender.set(DoubleSolenoid.Value.kForward);
				Timer.delay(pistonDelay);
				mainClock.reset();
				phase++;
				break;
			case 0:
				pulley.set(pulleyUpValue);
				if (mainClock.get() > .45) {
					pulley.set(0);
					phase++;
					break;
				}
				break;
			case 1:
				drive.arcadeDrive(0, turnValue);
				if (gyro.getAngle() > 75) {
					drive.arcadeDrive(0, 0);
					mainClock.reset();
					phase++;
					break;
				}
				break;
			case 2:
				drive.arcadeDrive(.75, driftConstant);
				if (mainClock.get() > 3.1) {
					haveArrived = false;
					phase++;
					mainClock.reset();
					break;
				}
				break;
			case 3:
				drive.arcadeDrive(.55, driftConstant);
				if (mainClock.get() > .15) {
					haveArrived = false;
					phase = 40;
					mainClock.reset();
					break;
				}
				break;
			case 4:
				pulley.set(pulleyDownValue);
				if (!switch5.get()) {
					phase++;
					pulley.set(0);
					mainClock.reset();
					break;
				}
				break;
			case 5:
				armExtender.set(DoubleSolenoid.Value.kReverse);
				Timer.delay(pistonDelay);
				phase = 40;
				mainClock.reset();
				break;
			}
		}
	}
	public void autonomous4() {
		initilize();
		calibratedGyro.reset();
		mainClock.reset();
		phase = -1;
		while (isEnabled() && isAutonomous()) {
			dashboard();
			switch (phase) {
			case 40:
				break;
			case -1:
				armExtender.set(DoubleSolenoid.Value.kForward);
				Timer.delay(pistonDelay);
				mainClock.reset();
				phase++;
				break;
			case 0:
				pulley.set(pulleyUpValue);
				if (mainClock.get() > .45) {
					pulley.set(0);
					phase++;
					phase++;
					break;
				}
				break;
			case 2:
				drive.arcadeDrive(-.75, -driftConstant);
				if (mainClock.get() > 3.1) {
					haveArrived = false;
					phase++;
					mainClock.reset();
					break;
				}
				break;
			case 3:
				drive.arcadeDrive(0, turnValue);
				if (calibratedGyro.getCalibratedAngle() > 110) {
					haveArrived = false;
					phase++;
					mainClock.reset();
					break;
				}
				break;
			case 4:
				pulley.set(pulleyDownValue);
				if (!switch5.get()) {
					phase++;
					pulley.set(0);
					mainClock.reset();
					break;
				}
				break;
			case 5:
				armExtender.set(DoubleSolenoid.Value.kReverse);
				Timer.delay(pistonDelay);
				phase = 40;
				mainClock.reset();
				break;
			}
		}
	}
	public void autonomous3() {
		initilize();
		calibratedGyro.reset();
		mainClock.reset();
		phase = 0;
		while (isEnabled() && isAutonomous()) {
			dashboard();
			switch (phase) {
			case 40:
				break;
			case 0:
				armExtender.set(DoubleSolenoid.Value.kForward);
				Timer.delay(pistonDelay);
				phase++;
				break;
			case 1:
				pulley.set(pulleyUpValue);
				if (!switch2.get()) {
					pulley.set(0);
					Timer.delay(1.75);
					phase++;
					break;
				}
				break;
			case 2:
				driveStraight();
				if (haveArrived) {
					haveArrived = false;
					phase++;
					mainClock.reset();
					break;
				}
				break;
			case 3:
				pulley.set(pulleyDownValue);
				if (mainClock.get() > pulleyDownTime) {
					pulley.set(0);
					phase++;
					break;
				}
				phase++;
				break;
			case 4:
				armExtender.set(DoubleSolenoid.Value.kReverse);
				Timer.delay(pistonDelay);
				phase++;
				mainClock.reset();
				break;
			case 5:
				pulley.set(-1);
				if (!switch5.get()) {
					pulley.set(0);
					phase++;
					break;
				}
				break;
			case 6:
				armExtender.set(DoubleSolenoid.Value.kForward);
				Timer.delay(pistonDelay);
				phase++;
				break;
			case 7:
				pulley.set(pulleyUpValue);
				if (!switch2.get()) {
					pulley.set(0);
					phase++;
					break;
				}
				break;
			case 8:
				driveStraight();
				if (haveArrived) {
					haveArrived = false;
					phase++;
					mainClock.reset();
					break;
				}
				break;
			case 9:
				pulley.set(pulleyDownValue);
				if (mainClock.get() > pulleyDownTime + .2) {
					pulley.set(0);
					phase++;
					break;
				}
				break;
			case 10:
				armExtender.set(DoubleSolenoid.Value.kReverse);
				Timer.delay(pistonDelay);
				phase++;
				mainClock.reset();
				break;
			case 11:
				pulley.set(-1);
				if (!switch5.get()) {
					pulley.set(0);
					phase++;
					break;
				}
				break;
			case 12:
				armExtender.set(DoubleSolenoid.Value.kForward);
				Timer.delay(pistonDelay);
				phase++;
				mainClock.reset();
				break;
			case 13:
				pulley.set(pulleyUpValue);
				if (mainClock.get() > .3) {
					pulley.set(0);
					gyro.reset();
					phase++;
					break;
				}
				break;
			case 14:
				drive.arcadeDrive(0, turnValue);
				if (gyro.getAngle() > 60) {
					drive.arcadeDrive(0, 0);
					mainClock.reset();
					phase++;
					break;
				}
				break;
			case 15:
				drive.arcadeDrive(.8, driftConstant);
				if (mainClock.get() > 1) {
					haveArrived = false;
					phase++;
					mainClock.reset();
					break;
				}
				break;
			case 16:
				drive.arcadeDrive(.5, driftConstant);
				if (mainClock.get() > .3) {
					haveArrived = false;
					phase++;
					mainClock.reset();
					break;
				}
				break;
			case 17:
				pulley.set(pulleyDownValue);
				if (!switch5.get()) {
					phase++;
					mainClock.reset();
					break;
				}
				break;
			case 18:
				armExtender.set(DoubleSolenoid.Value.kReverse);
				Timer.delay(pistonDelay);
				phase++;
				mainClock.reset();
				break;
			case 19:
				drive.arcadeDrive(-1, 1);
				phase = 40;
				mainClock.reset();
				break;
			}
		}
	}
	public void autonomous5() {
		initilize();
		calibratedGyro.reset();
		mainClock.reset();
		phase = 0;
		while (isEnabled() && isAutonomous()) {
			dashboard();
			switch (phase) {
			case 40:
				break;
			case 0:
				armExtender.set(DoubleSolenoid.Value.kForward);
				Timer.delay(pistonDelay);
				phase++;
				break;
			case 1:
				pulley.set(pulleyUpValue);
				if (!switch2.get()) {
					pulley.set(0);
					phase++;
					break;
				}
				break;
			case 2:
				tannerDrive();
				if (haveArrived) {
					haveArrived = false;
					phase++;
					mainClock.reset();
					break;
				}
				break;
			case 3:
				leftW.set(1);
				rightW.set(-1);
				driveStraight();
				if (haveArrived) {
					haveArrived = false;
					phase++;
					mainClock.reset();
					break;
				}
				break;
			case 4:
				pulley.set(pulleyDownValue);
				if (mainClock.get() > pulleyDownTime) {
					pulley.set(0);
					phase++;
					break;
				}
				phase++;
				break;
			case 5:
				armExtender.set(DoubleSolenoid.Value.kReverse);
				Timer.delay(pistonDelay);
				phase++;
				mainClock.reset();
				break;
			case 6:
				pulley.set(-1);
				if (!switch5.get()) {
					pulley.set(0);
					phase++;
					break;
				}
				break;
			case 7:
				armExtender.set(DoubleSolenoid.Value.kForward);
				Timer.delay(pistonDelay);
				phase++;
				break;
			case 8:
				pulley.set(pulleyUpValue);
				if (!switch2.get()) {
					pulley.set(0);
					phase++;
					break;
				}
				break;
			case 9:
				tannerDrive();
				if (haveArrived) {
					haveArrived = false;
					phase++;
					mainClock.reset();
					break;
				}
				break;
			case 10:
				leftW.set(1);
				rightW.set(-1);
				driveStraight();
				if (haveArrived) {
					haveArrived = false;
					phase++;
					mainClock.reset();
					break;
				}
				break;
			case 11:
				pulley.set(pulleyDownValue);
				if (mainClock.get() > pulleyDownTime + .2) {
					pulley.set(0);
					phase++;
					break;
				}
				break;
			case 12:
				armExtender.set(DoubleSolenoid.Value.kReverse);
				Timer.delay(pistonDelay);
				phase++;
				mainClock.reset();
				break;
			case 13:
				pulley.set(-1);
				if (!switch5.get()) {
					pulley.set(0);
					phase++;
					break;
				}
				break;
			case 14:
				armExtender.set(DoubleSolenoid.Value.kForward);
				Timer.delay(pistonDelay);
				phase++;
				mainClock.reset();
				break;
			case 15:
				pulley.set(pulleyUpValue);
				if (mainClock.get() > .3) {
					pulley.set(0);
					gyro.reset();
					phase++;
					break;
				}
				break;
			case 16:
				drive.arcadeDrive(0, turnValue);
				if (gyro.getAngle() > 60) {
					drive.arcadeDrive(0, 0);
					mainClock.reset();
					phase++;
					break;
				}
				break;
			case 17:
				drive.arcadeDrive(.8, driftConstant);
				if (mainClock.get() > 1) {
					haveArrived = false;
					phase++;
					mainClock.reset();
					break;
				}
				break;
			case 18:
				drive.arcadeDrive(.5, driftConstant);
				if (mainClock.get() > .3) {
					haveArrived = false;
					phase++;
					mainClock.reset();
					break;
				}
				break;
			case 19:
				pulley.set(pulleyDownValue);
				if (!switch5.get()) {
					phase++;
					mainClock.reset();
					break;
				}
				break;
			case 20:
				armExtender.set(DoubleSolenoid.Value.kReverse);
				Timer.delay(pistonDelay);
				phase++;
				mainClock.reset();
				break;
			case 21:
				leftW.set(-1);
				rightW.set(1);
				drive.arcadeDrive(-1, 1);
				Timer.delay(1.25);
				phase = 40;
				mainClock.reset();
				break;
			}
		}
	}
	public void autonomous6() {
		initilize();
		calibratedGyro.reset();
		mainClock.reset();
		phase = -1;
		while (isEnabled() && isAutonomous()) {
			dashboard();
			switch (phase) {
			case 40:
				break;
			case -1:
				drive.arcadeDrive(-.63, -driftConstant);
				if (mainClock.get() > 1.5) {
					mainClock.reset();
					phase++;
					drive.arcadeDrive(0, driftConstant);
					break;
				}
				break;
			case 0:
				canBurglar.set(DoubleSolenoid.Value.kForward);
				Timer.delay(bigPistonDelay);
				mainClock.reset();
				phase++;
				break;
			case 1:
				drive.arcadeDrive(1, driftConstant);
				if (mainClock.get() > canMoving) {
					phase++;
					mainClock.reset();
					break;
				}
				break;
			case 2:
				canBurglar.set(DoubleSolenoid.Value.kReverse);
				Timer.delay(bigPistonDelay);
				mainClock.reset();
				phase = 40;
				break;
			}
		}
	}
	public void operatorControl() {
		initilize();
		calibratedGyro.reset();
		light0.set(Relay.Value.kOn);
		light1.set(Relay.Value.kOn);
		while (isOperatorControl() && isEnabled()) {
			if (eye1.getRangeInches() < robotToToteWhenSetDistance) {
				light3.set(Relay.Value.kForward);
			} else {
				light3.set(Relay.Value.kOff);
			}
			if (thisStick.getRawButton(4)) {
				u += .0001;
			}
			if (thisStick.getRawButton(3)) {
				u -= .0001;
			}
			if (thisStick.getRawButton(1)) {
				// i += .0001;
				canBurglar.set(DoubleSolenoid.Value.kForward);
			} else if (thisStick.getRawButton(2)) {
				// i -= .0001;
				canBurglar.set(DoubleSolenoid.Value.kReverse);
			} else {
				// leftW.set(0);
				// rightW.set(0);
			}
			if (thisStick.getRawButton(8)) {
				calibratedGyro.reset();
			}
			pulley();
			driveCode();
			extender();
			operatorOperations();
			dashboard();
			rampManager();
			lights();
		}
	}
	public void disabled() {
		initilize();
		while (isDisabled()) {
			dashboard();
			if (!calibratedGyro.finishedCalibration) {
				calibratedGyro.calibrateGyro();
			}
		}
	}
}
