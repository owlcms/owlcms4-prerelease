/***
 * Copyright (c) 2009-2019 Jean-François Lamy
 * 
 * Licensed under the Non-Profit Open Software License version 3.0  ("Non-Profit OSL" 3.0)  
 * License text at https://github.com/jflamy/owlcms4/master/License.txt
 */
package app.owlcms.ui.group;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import org.slf4j.LoggerFactory;

import com.flowingcode.vaadin.addons.ironicons.AvIcons;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.timepicker.TimePicker;

import app.owlcms.fieldofplay.FOPEvent;
import app.owlcms.init.OwlcmsSession;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

@SuppressWarnings("serial")
public class BreakDialog extends Dialog {
	final private Logger logger = (Logger) LoggerFactory.getLogger(BreakDialog.class);
	{ logger.setLevel(Level.DEBUG);}
	
	public enum BreakType {INTRODUCTION, FIRST_SNATCH, FIRST_CJ, TECHNICAL};
	public enum CountdownType {DURATION, TARGET};
	
	private Button breakStart = null;
	private Button breakPause = null;
	private Button breakEnd = null;
	private Object origin;
	private Label minutes;
	
	BreakDialog(Object origin) {
		this.setOrigin(origin);
		
		Dialog dialog = this;
		NumberField nf = new NumberField();
		TimePicker tp = new TimePicker();
		DatePicker dp = new DatePicker();
		
		RadioButtonGroup<BreakType> bt = new RadioButtonGroup<BreakType>();
		bt.setItems(BreakType.values());
		bt.setLabel("Break Type");
		
		RadioButtonGroup<CountdownType> ct = new RadioButtonGroup<CountdownType>();
		ct.setItems(CountdownType.values());
		ct.setLabel("Countdown Type");
		
		nf.addValueChangeListener(e -> setValues(CountdownType.DURATION, nf, tp, dp));
		Locale locale = new Locale("en", "SE"); // ISO 8601 style dates and time
		tp.setLocale(locale);
		tp.addValueChangeListener(e -> setValues(CountdownType.TARGET, nf, tp, dp));
		dp.setLocale(locale);
		tp.addValueChangeListener(e -> setValues(CountdownType.TARGET, nf, tp, dp));	
		minutes = new Label("minutes");
		
		ct.addComponents(CountdownType.DURATION, nf, new Label(" "), minutes, new Div());
		ct.addComponents(CountdownType.TARGET, dp, new Label(" "), tp);
		ct.addValueChangeListener(e -> {
			CountdownType cType = e.getValue();
			setValues(cType, nf, tp, dp);
			if (cType != CountdownType.TARGET) {
				switchToDuration(cType, nf, tp, dp);
			} else {
				switchToTarget(cType, nf, tp, dp);
			}
		});
		
		breakStart = new Button(AvIcons.PLAY_ARROW.create(), (e) -> {
			OwlcmsSession.withFop(fop -> {
				long breakDuration = setBreakTimeRemaining(ct.getValue(), nf, tp, dp);
				fop.getFopEventBus().post(new FOPEvent.BreakStarted(bt.getValue(), (int) breakDuration, this.getOrigin()));
			});
			e.getSource().setEnabled(false);
			breakStart.setEnabled(false);
			breakPause.setEnabled(true);
			breakEnd.setEnabled(true);
		});
		breakStart.getElement().setAttribute("theme", "primary");
		breakStart.getElement().setAttribute("title", "Start Break Countdown Timer");
		
		breakPause = new Button(AvIcons.PAUSE.create(), (e) -> {
			OwlcmsSession.withFop(fop -> fop.getFopEventBus()
				.post(new FOPEvent.BreakPaused(this.getOrigin())));
			breakStart.setEnabled(true);
			breakPause.setEnabled(false);
			breakEnd.setEnabled(true);
		});
		breakPause.getElement().setAttribute("theme", "primary");
		breakPause.getElement().setAttribute("title", "Pause Break Countdown Timer");
		
		breakEnd = new Button(AvIcons.STOP.create(), (e) -> {
			OwlcmsSession.withFop(fop -> fop.getFopEventBus()
				.post(new FOPEvent.StartLifting(this.getOrigin())));
			breakStart.setEnabled(true);
			breakPause.setEnabled(false);
			breakEnd.setEnabled(false);
			dialog.close();
		});
		breakEnd.getElement().setAttribute("theme", "primary");
		breakEnd.getElement().setAttribute("title", "End Break, Start Lifting");
		
		HorizontalLayout buttons = new HorizontalLayout();
		buttons.add(breakStart, breakPause, breakEnd);
		buttons.setWidth("100%");
		buttons.setJustifyContentMode(JustifyContentMode.AROUND);
		
		dialog.add(bt);
		dialog.add(new Hr());
		dialog.add(ct);
		dialog.add(new Hr());
		dialog.add(buttons);
		
		OwlcmsSession.withFop(fop -> {		
			switch (fop.getState()) {
			case BREAK:
				bt.setValue(BreakType.FIRST_CJ);
				break;
			case INACTIVE:
				bt.setValue(BreakType.INTRODUCTION);
				break;
			default:
				bt.setValue(BreakType.FIRST_SNATCH);
				break;
			}
			ct.setValue(CountdownType.DURATION);
			nf.setValue(10.0D);
		});
	}

	public void setValues(CountdownType cType, NumberField nf, TimePicker tp, DatePicker dp) {
		LocalDateTime now = LocalDateTime.now();
		dp.setEnabled(true);
		tp.setEnabled(true);
		nf.setEnabled(true);
		if (cType == CountdownType.DURATION) {
			Double value = nf.getValue();
			LocalDateTime target = now.plusMinutes(value != null ? value.intValue() : 0);
			dp.setValue(target.toLocalDate());
			tp.setValue(target.toLocalTime());
		} else {
			computeRoundedTargetValues(tp, dp);
		}
	}

	public long setBreakTimeRemaining(CountdownType cType, NumberField nf, TimePicker tp, DatePicker dp) {
		LocalDateTime target;
		if (cType == CountdownType.DURATION) {
			Double value = nf.getValue();
			target = LocalDateTime.now().plusMinutes(value != null ? value.intValue() : 0);
		} else {
			LocalDate date = dp.getValue();
			LocalTime time = tp.getValue();
			target = LocalDateTime.of(date, time);
		}
		long timeRemaining = LocalDateTime.now().until(target, ChronoUnit.MILLIS);
		OwlcmsSession.withFop(fop -> fop.getBreakTimer().setTimeRemaining((int)timeRemaining));
		return timeRemaining;
	}

	private Object getOrigin() {
		return origin;
	}

	public void switchToTarget(CountdownType cType, NumberField nf, TimePicker tp, DatePicker dp) {
		nf.setEnabled(false);
		minutes.setEnabled(false);
		dp.setEnabled(true);
		tp.setEnabled(true);
		tp.focus();
	}

	public void switchToDuration(CountdownType cType, NumberField nf, TimePicker tp, DatePicker dp) {
		nf.setEnabled(true);
		minutes.setEnabled(true);
		dp.setEnabled(false);
		tp.setEnabled(false);
		nf.focus();
		nf.setAutoselect(true);
	}

	private void computeRoundedTargetValues(TimePicker tp, DatePicker dp) {
		int timeStep = 30;
		tp.setStep(Duration.ofMinutes(timeStep));
		LocalTime nowTime = LocalTime.now();
		int nowMin = nowTime.getMinute();
		int nowHr = nowTime.getHour();
		int previousStepMin = (nowMin / timeStep) * timeStep; // between 0 and 50
		int nextStepMin = (previousStepMin + timeStep) % 60;
		logger.trace("previousStepMin = {} nextStepMin = {}", previousStepMin, nextStepMin);
		int nextHr = (nextStepMin == 0 ? nowHr + 1 : nowHr);
		LocalDate nextDate = LocalDate.now();
		if (nextHr >= 24) {
			nextDate.plusDays(1);
			nextHr = nextHr % 24;
		}
		dp.setValue(nextDate);
		tp.setValue(LocalTime.of(nextHr, nextStepMin));
	}

	public void setOrigin(Object origin) {
		this.origin = origin;
	}
}
