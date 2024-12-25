package app.owlcms.data.jpa;

import java.lang.reflect.Field;

import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;

import app.owlcms.utils.LoggerUtils;
import ch.qos.logback.classic.Logger;

public class HikariDataSourcePoolDetail {
	private final static Logger logger = (Logger) LoggerFactory.getLogger(HikariDataSourcePoolDetail.class);
	private final HikariDataSource dataSource;

	public HikariDataSourcePoolDetail(HikariDataSource dataSource) {
		this.dataSource = dataSource;
	}

	public int getActive() {
		try {
			return getHikariPool().getActiveConnections();
		} catch (Exception ex) {
			return -1;
		}
	}

	public HikariPool getHikariPool() {
		Field field;
		try {
			field = this.dataSource.getClass().getDeclaredField("pool");
			field.setAccessible(true);
			HikariPool hikariPool = (HikariPool) field.get(this.dataSource);
			return hikariPool;
		} catch (Exception e) {
			LoggerUtils.logError(logger, e);
		}
		return null;
	}

	public int getMax() {
		return this.dataSource.getMaximumPoolSize();
	}
}