package org.sdkj.thermal.wechat.utils;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
public class DateUtil {
	private static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public static String timeStamp2Date(String seconds, String format) {
		if (seconds == null || seconds.isEmpty() || seconds.equals("null")) {
			return "";
		}
		if (format == null || format.isEmpty()) {
			format = DEFAULT_FORMAT;
		}
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
		return dtf.format(LocalDateTime.ofInstant(
			Instant.ofEpochMilli(Long.parseLong(seconds) * 1000), ZoneId.systemDefault()));
	}

	public static String date2TimeStamp(String dateStr, String format) {
		if (format == null || format.isEmpty()) {
			format = DEFAULT_FORMAT;
		}
		try {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
			LocalDateTime ldt = LocalDateTime.parse(dateStr, dtf);
			return String.valueOf(ldt.atZone(ZoneId.systemDefault()).toEpochSecond());
		} catch (Exception e) {
			log.warn("日期转换失败: dateStr={}, format={}", dateStr, format, e);
		}
		return "";
	}

	public static String timeStamp() {
		return String.valueOf(System.currentTimeMillis() / 1000);
	}
}
