package com.github.yogurt.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.Formatter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @author Administrator
 */
@Configuration
public class LocalDateConverter {
	@Bean
	public Formatter<LocalDateTime> localDateTimeFormatter() {
		return new Formatter<LocalDateTime>() {
			@Override
			public LocalDateTime parse(String text, Locale locale) {
				return LocalDateTime.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			}

			@Override
			public String print(LocalDateTime localDateTime, Locale locale) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				return formatter.format(localDateTime);
			}


		};
	}

	@Bean
	public Formatter<LocalDate> localDateFormatter() {
		return new Formatter<LocalDate>() {
			@Override
			public LocalDate parse(String text, Locale locale) {
				return LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE);
			}

			@Override
			public String print(LocalDate object, Locale locale) {
				return DateTimeFormatter.ISO_LOCAL_DATE.format(object);
			}
		};
	}


	@Bean(name = "mapperObject")
	public ObjectMapper getObjectMapper() {
		ObjectMapper om = new ObjectMapper();
		JavaTimeModule javaTimeModule = new JavaTimeModule();
		javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
		om.registerModule(javaTimeModule);
		return om;
	}


}
