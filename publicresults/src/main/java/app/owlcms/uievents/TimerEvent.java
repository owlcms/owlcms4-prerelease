/*******************************************************************************
 * Copyright © 2009-present Jean-François Lamy
 *
 * Licensed under the Non-Profit Open Software License version 3.0  ("NPOSL-3.0")
 * License text at https://opensource.org/licenses/NPOSL-3.0
 *******************************************************************************/
package app.owlcms.uievents;

public class TimerEvent {
    /**
     * Class SetTime.
     */
    static public class SetTime extends TimerEvent {

        private Integer timeRemaining;

        /**
         * Instantiates a new sets the time.
         *
         * @param timeRemaining the time remaining
         */
        public SetTime(Integer timeRemaining) {
            this.timeRemaining = timeRemaining;
        }

        /**
         * Gets the time remaining.
         *
         * @return the time remaining
         */
        public Integer getTimeRemaining() {
            return this.timeRemaining;
        }

    }

    /**
     * Class StartTime.
     */
    static public class StartTime extends TimerEvent {

        private Integer timeRemaining;
        private boolean silent;

        /**
         * Instantiates a new start time.
         *
         * @param timeRemaining the time remaining
         * @param silent
         */
        public StartTime(Integer timeRemaining, boolean silent) {
            this.timeRemaining = timeRemaining;
            this.silent = silent;
        }

        /**
         * Gets the time remaining.
         *
         * @return the time remaining
         */
        public Integer getTimeRemaining() {
            return this.timeRemaining;
        }

        public boolean isSilent() {
            return this.silent;
        }

    }

    /**
     * Class StopTime.
     */
    static public class StopTime extends TimerEvent {

        private int timeRemaining;

        /**
         * Instantiates a new stop time.
         *
         * @param timeRemaining the time remaining
         */
        public StopTime(int timeRemaining) {
            this.timeRemaining = timeRemaining;
        }

        /**
         * Gets the time remaining.
         *
         * @return the time remaining
         */
        public Integer getTimeRemaining() {
            return this.timeRemaining;
        }
    }

    private String fopName;

    public TimerEvent() {
    }

    public String getFopName() {
        return this.fopName;
    }

    public void setFopName(String fopName) {
        this.fopName = fopName;
    }

}
