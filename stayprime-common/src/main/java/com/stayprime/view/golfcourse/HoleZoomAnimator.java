/*
 * 
 */
package com.stayprime.view.golfcourse;

import com.stayprime.view.control.ViewControl;
import com.stayprime.view.control.ZoomAnimator;

/**
 *
 * @author benjamin
 */
public class HoleZoomAnimator extends ZoomAnimator {
    private double currentAngle;
    private double targetAngle;
    private double angleDelta = 0.1;

    public HoleZoomAnimator(ViewControl control) {
        super(control);
    }

    public void setTargetAngle(double targetAngle) {
        this.targetAngle = targetAngle;
    }

    @Override
    public void begin() {
        super.begin();

        Double angle = transformView.getRotation();
        currentAngle = angle == null? 0 : angle;
        angleDelta = targetAngle < currentAngle? -0.001 : 0.001;
    }

    @Override
    public void timingEvent(float fraction) {
        setScaleAndCenter(fraction);

        if (Math.abs(targetAngle - currentAngle) > Math.abs(angleDelta)) {
            testNextAngleStep();
        }

        control.repaintContainer(true);
    }

    private void testNextAngleStep() {
        boolean redoAngle = true;
        while (redoAngle) {
            if (Math.abs(targetAngle - currentAngle) <= Math.abs(angleDelta)) {
                control.setRotation(targetAngle);
//                System.out.println("Case 1: " + control.getRotation());
                redoAngle = false;
            }
            else {
                control.setRotation(currentAngle + angleDelta);
//                System.out.println("Case 2: " + control.getRotation());
                if (control.testBoundsInsideMap()) {
                    if (!forward) {
                        redoAngle = false;
                        control.setRotation(currentAngle);
//                        System.out.println("Case 3: " + control.getRotation());
                    }
                    else
                        redoAngle = true;
                }
                else {
                    if (!forward)
                        redoAngle = true;
                    else {
                        redoAngle = false;
                        control.setRotation(currentAngle);
//                        System.out.println("Case 4: " + control.getRotation());
                    }
                }

                currentAngle = transformView.getRotation();
            }
        }
//        System.out.println();
    }
}
