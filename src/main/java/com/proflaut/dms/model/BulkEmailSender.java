package com.proflaut.dms.model;

import java.util.Date;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.exception.CustomException;

public class BulkEmailSender {

	private static final Logger logger = LogManager.getLogger(BulkEmailSender.class);
	private MailInfoRequest mailInfoRequest;
	private Properties emailConfig;

	public BulkEmailSender(MailInfoRequest mailInfoRequest, Properties emailConfig) {
		this.mailInfoRequest = mailInfoRequest;
		this.emailConfig = emailConfig;
	}

	public MailInfoRequest getEmailInfo() {
		return mailInfoRequest;
	}

	public void sendEmail(MimeMessage mimeMessage) {
		try {
			Transport.send(mimeMessage);
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
		}
	}

	public MimeMessage getMimeMessage() throws CustomException {
		MimeMessage mimeMessage;
		mimeMessage = buildMimeMessage();
		return mimeMessage;
	}

	private MimeMessage buildMimeMessage() throws CustomException {
//		final String username = "pdineshofficial@gmail.com";// change accordingly
//		final String password = "ykkekdkvlautwqex";// change accordingly
//		final String username = "sathishsjroman@gmail.com";// change accordingly
//		final String password = "heqrgvtwhjghjgmc";// change accordingly
		final String username = "sathishkumar@proflaut.com";// change accordingly
		final String pass = "igwmiafnylucedbq";
		// Get the Session object.
		Session session = Session.getInstance(emailConfig, new javax.mail.Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, pass);
			}
		});
		MimeMessage mimeMessage = new MimeMessage(session);

		try {
			setFromAddress().andThen(setToAddress()).andThen(setSubject()).apply(mimeMessage);

			mimeMessage.setSentDate(new Date());
		} catch (Exception e) {
			throw new CustomException("Error in setting from, to, cc address and subject of the email");
		}

		return mimeMessage;
	}

	private Function<MimeMessage, MimeMessage> setSubject() {
		return mimeMessage -> {
			if (!Objects.isNull(mailInfoRequest.getSubject()) && !mailInfoRequest.getSubject().isEmpty()) {
				try {
					mimeMessage.setSubject(mailInfoRequest.getSubject());
				} catch (Exception e) {
					throw new RuntimeException("Could not set subject " + mailInfoRequest.getSubject());
				}
			}
			return mimeMessage;
		};
	}

	private Function<MimeMessage, MimeMessage> setFromAddress() {
		return mimeMessage -> {
			if (!Objects.isNull(mailInfoRequest.getFrom()) && !mailInfoRequest.getFrom().isEmpty()) {
				try {
					mimeMessage.setFrom(new InternetAddress(mailInfoRequest.getFrom().trim()));
				} catch (Exception e) {
					throw new RuntimeException("Could not resolve from address " + mailInfoRequest.getFrom());
				}
			}
			return mimeMessage;
		};
	}

	private Function<MimeMessage, MimeMessage> setToAddress() {
		return mimeMessage -> {
			if (!Objects.isNull(mailInfoRequest.getToList()) && !mailInfoRequest.getToList().isEmpty()) {
				try {
					mimeMessage.setRecipients(Message.RecipientType.TO, mailInfoRequest.getToList().stream().map(to -> {
						try {
							return new InternetAddress(to);
						} catch (AddressException e) {
							throw new RuntimeException("Could not resolve to address");
						}
					}).toArray(InternetAddress[]::new));
				} catch (MessagingException e) {
					throw new RuntimeException(
							"Could not set to address in message " + mailInfoRequest.getToList().toString());
				}
			}
			return mimeMessage;
		};
	}
}
