/*******************************************************************************
 * Copyright © 2009-present Jean-François Lamy
 *
 * Licensed under the Non-Profit Open Software License version 3.0  ("NPOSL-3.0")
 * License text at https://opensource.org/licenses/NPOSL-3.0
 *******************************************************************************/
package app.owlcms.tests;

import java.time.LocalDateTime;

import org.slf4j.LoggerFactory;

import app.owlcms.fieldofplay.FieldOfPlay;
import app.owlcms.fieldofplay.IBreakTimer;
import app.owlcms.fieldofplay.IProxyTimer;
import app.owlcms.utils.LoggerUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class MockCountdownTimer implements IBreakTimer, IProxyTimer {

    final private static Logger logger = (Logger) LoggerFactory.getLogger(MockCountdownTimer.class);

    private Integer breakDuration;

    private boolean indefinite;

    private int timeRemaining;

    private int timeRemainingAtLastStop;

    public MockCountdownTimer() {
        logger.setLevel(Level.INFO);
    }

    @Override
    public void finalWarning(Object origin) {
        // ignored
    }

    @Override
    public Integer getBreakDuration() {
        return this.breakDuration;
    }

    /*
     * (non-Javadoc)
     *
     * @see app.owlcms.fieldofplay.IProxyTimer#getTimeRemaining()
     */
    @Override
    public int getTimeRemaining() {
        return timeRemaining;
    }

    /**
     * @return the timeRemainingAtLastStop
     */
    @Override
    public int getTimeRemainingAtLastStop() {
        return timeRemainingAtLastStop;
    }

    @Override
    public void initialWarning(Object origin) {
        // ignored
    }

    @Override
    public boolean isIndefinite() {
        return this.indefinite;
    }

    @Override
    public boolean isRunning() {
        return timeRemaining != 0;
    }

    @Override
    public int liveTimeRemaining() {
        return getTimeRemaining() - 1000;
    }

    @Override
    public void setBreakDuration(Integer breakDuration) {
        this.breakDuration = breakDuration;
    }

    @Override
    public void setEnd(LocalDateTime targetTime) {
    }

    @Override
    public void setFop(FieldOfPlay fieldOfPlay) {
    }

    @Override
    public void setIndefinite() {
        this.indefinite = true;
    }

    @Override
    public void setOrigin(Object origin) {
    }

    /*
     * (non-Javadoc)
     *
     * @see app.owlcms.fieldofplay.IProxyTimer#setTimeRemaining(int)
     */
    @Override
    public void setTimeRemaining(int timeRemaining, boolean indefinite) {
        logger.debug("setting Time -- timeRemaining = {}\t[{}]", timeRemaining, LoggerUtils.whereFrom());
        this.timeRemaining = timeRemaining;
    }

    /*
     * (non-Javadoc)
     *
     * @see app.owlcms.tests.ICountDownTimer#start()
     */
    @Override
    public void start() {
        logger.debug("starting Time -- timeRemaining = {} \t[{}]", timeRemaining, LoggerUtils.whereFrom());
        timeRemainingAtLastStop = timeRemaining;
    }

    /*
     * (non-Javadoc)
     *
     * @see app.owlcms.tests.ICountDownTimer#stop()
     */
    @Override
    public void stop() {
        logger.debug("stopping Time -- timeRemaining = {} \t[{}]", timeRemaining, LoggerUtils.whereFrom());
        timeRemaining = (getTimeRemaining() - 2000);
        timeRemainingAtLastStop = timeRemaining;
    }

    /*
     * (non-Javadoc)
     *
     * @see app.owlcms.fieldofplay.IProxyTimer#timeOut(java.lang.Object)
     */
    @Override
    public void timeOver(Object origin) {
        stop();
        timeRemaining = 0;
    }

}
