/*******************************************************************************
 * Copyright © 2009-present Jean-François Lamy
 *
 * Licensed under the Non-Profit Open Software License version 3.0  ("NPOSL-3.0")
 * License text at https://opensource.org/licenses/NPOSL-3.0
 *******************************************************************************/
package app.owlcms.sound;

import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.Mixer;

import org.slf4j.LoggerFactory;

import app.owlcms.utils.LoggerUtils;
import app.owlcms.utils.ResourceWalker;
import ch.qos.logback.classic.Logger;

/**
 * Play a sampled sound. Requires an uncompressed format (WAV), not a compressed (MP3) format.
 *
 * @author jflamy
 */
public class Sound {
	static final String SOUND_PREFIX = "/sounds/";
	final Logger logger = (Logger) LoggerFactory.getLogger(Sound.class);
	private Mixer mixer;
	private InputStream resource;
	private String soundURL;

	public Sound(Mixer mixer, String soundRelativeURL) throws IllegalArgumentException {
		this.mixer = mixer;
		this.soundURL = SOUND_PREFIX + soundRelativeURL;
		try {
			this.resource = ResourceWalker.getResourceAsStream(this.soundURL);
		} catch (FileNotFoundException e) {
			this.logger.error("cannot find sound {}", this.soundURL);
			this.resource = null;
		}
	}

	public synchronized void emit() {
		try {
			if (this.mixer == null) {
				return;
			}

			// since we are reading from the jar, we need to avoid the mark/reset trial and
			// error from AudioSystem.getAudioInputStream
			// so we force WaveFileReader.
			WaveFileReader wfr = new WaveFileReader();
			final AudioInputStream inputStream = wfr.getAudioInputStream(this.resource);
			final Clip clip = AudioSystem.getClip(this.mixer.getMixerInfo());
			clip.open(inputStream);

			// clip.start() creates a native thread 'behind the scenes'
			// unless this is added, it never goes away
			// ref:
			// http://stackoverflow.com/questions/837974/determine-when-to-close-a-sound-playing-thread-in-java
			clip.addLineListener(new LineListener() {
				@Override
				public void update(LineEvent evt) {
					if (evt.getType() == LineEvent.Type.STOP) {
						evt.getLine().close();
					}
				}
			});
			clip.start();

		} catch (Exception e) {
			this.logger.error("could not emit {} {}", this.soundURL, LoggerUtils./**/stackTrace(e));
		}
	}

}
