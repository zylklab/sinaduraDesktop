/*
 * # Copyright 2008 zylk.net # # This file is part of Sinadura. # # Sinadura is free software: you can redistribute it
 * and/or modify # it under the terms of the GNU General Public License as published by # the Free Software Foundation,
 * either version 2 of the License, or # (at your option) any later version. # # Sinadura is distributed in the hope
 * that it will be useful, # but WITHOUT ANY WARRANTY; without even the implied warranty of # MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the # GNU General Public License for more details. # # You should have received a copy
 * of the GNU General Public License # along with Sinadura. If not, see <http://www.gnu.org/licenses/>. [^] # # See
 * COPYRIGHT.txt for copyright notices and details. #
 */
package net.esle.sinadura.gui.util;

import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.esle.sinadura.core.certificate.CertificateUtil;
import net.esle.sinadura.core.model.ChainInfo;
import net.esle.sinadura.core.model.PDFSignatureInfo;
import net.esle.sinadura.core.model.Status;
import net.esle.sinadura.core.model.TimestampInfo;
import net.esle.sinadura.core.model.ValidationError;
import net.esle.sinadura.core.model.XadesSignatureInfo;
import net.esle.sinadura.gui.exceptions.InterpreterInterruptedException;
import net.esle.sinadura.gui.model.MessageInfo;
import net.esle.sinadura.gui.model.SignatureInfo;
import es.mityc.firmaJava.libreria.xades.EnumFormatoFirma;
import es.mityc.firmaJava.libreria.xades.ResultadoEnum;
import es.mityc.firmaJava.trust.ConfianzaEnum;


/**
 * @author zylk.net
 */
public class ValidationInterpreterUtil {
	
	private static Log logger = LogFactory.getLog(ValidationInterpreterUtil.class);
	
	
	public static List<SignatureInfo> parseResultadoValidacion(List<XadesSignatureInfo> resultados) {

		List<SignatureInfo> signatureInfos = new ArrayList<SignatureInfo>();
		for (XadesSignatureInfo resultado : resultados) {
			signatureInfos.add(parseResultadoValidacion(resultado));
		}
		return signatureInfos;
	}
	
	public static SignatureInfo parseResultadoValidacion(XadesSignatureInfo result) {
		
		SignatureInfo signatureInfo = new SignatureInfo();
		
		signatureInfo.setDate(result.getDatosFirma().getFechaFirma());
		
		List<MessageInfo> messages = new ArrayList<MessageInfo>();
		signatureInfo.setMessages(messages);
		
		ChainInfo chainInfo = result.getChainInfo();
		if (chainInfo != null && chainInfo.getChain() != null) {
			signatureInfo.setChain(chainInfo.getChain());
		} else {
			List<X509Certificate> chain = (List<X509Certificate>)result.getDatosFirma().getCadenaFirma().getCertificates();
			signatureInfo.setChain(chain);
		}
		
		try {

			// ERRORES DE LA CHAIN
			if (chainInfo != null && chainInfo.getError() != null && chainInfo.getError().equals(ChainInfo.Error.REVOKED)) {
				
				signatureInfo.setStatus(Status.INVALID);
				MessageInfo messageInfo = new MessageInfo();
				messageInfo.setSimpleStatus(Status.INVALID);
				SimpleDateFormat dateFormat = LanguageUtil.getFullFormater();
				String m = MessageFormat.format(LanguageUtil.getLanguage().getString("info.validation.chain.revoked"),
								CertificateUtil.getFormattedName(chainInfo.getChain().get(chainInfo.getIndex())),
								dateFormat.format(chainInfo.getDate()));
				messageInfo.setText(m);
				messages.add(messageInfo);
				throw new InterpreterInterruptedException();
				
			} else if (chainInfo != null && chainInfo.getError() != null && chainInfo.getError().equals(ChainInfo.Error.EXPIRED)) {
				
				signatureInfo.setStatus(Status.INVALID);
				MessageInfo messageInfo = new MessageInfo();
				messageInfo.setSimpleStatus(Status.INVALID);
				SimpleDateFormat dateFormat = LanguageUtil.getFullFormater();
				String m = MessageFormat.format(LanguageUtil.getLanguage().getString("info.validation.chain.expired"),
								CertificateUtil.getFormattedName(chainInfo.getChain().get(chainInfo.getIndex())),
								dateFormat.format(chainInfo.getDate()));
				messageInfo.setText(m);
				messages.add(messageInfo);
				throw new InterpreterInterruptedException();
				
			} else if (chainInfo != null && chainInfo.getError() != null && chainInfo.getError().equals(ChainInfo.Error.NOTYETVALID)) {
				
				signatureInfo.setStatus(Status.INVALID);
				MessageInfo messageInfo = new MessageInfo();
				messageInfo.setSimpleStatus(Status.INVALID);
				SimpleDateFormat dateFormat = LanguageUtil.getFullFormater();
				String m = MessageFormat.format(LanguageUtil.getLanguage().getString("info.validation.chain.notyetvalid"),
								CertificateUtil.getFormattedName(chainInfo.getChain().get(chainInfo.getIndex())),
								dateFormat.format(chainInfo.getDate()));
				messageInfo.setText(m);
				messages.add(messageInfo);
				throw new InterpreterInterruptedException();

			} else if (chainInfo != null && chainInfo.getError() != null && chainInfo.getError().equals(ChainInfo.Error.GENERIC)) {
				
				signatureInfo.setStatus(Status.INVALID);
				MessageInfo messageInfo = new MessageInfo();
				messageInfo.setSimpleStatus(Status.INVALID);
				String m = MessageFormat.format(LanguageUtil.getLanguage().getString("info.validation.chain.uknown"), CertificateUtil
						.getFormattedName(chainInfo.getChain().get(chainInfo.getIndex())),
						chainInfo.getLog());
				messageInfo.setText(m);
				messages.add(messageInfo);
				throw new InterpreterInterruptedException();
			}
			
			// ERROR
			if (!result.isValidate()) {
				
				if (result.getResultado().equals(ResultadoEnum.UNKNOWN)) {
					signatureInfo.setStatus(Status.UNKNOWN);
				} else if (result.getResultado().equals(ResultadoEnum.INVALID)) {
					signatureInfo.setStatus(Status.INVALID);
				} else {
					signatureInfo.setStatus(Status.INVALID);
				}
				if (result.getLog() != null) {
					MessageInfo messageInfo = new MessageInfo(Status.INVALID, result.getLog());
					messages.add(messageInfo);
				}
				throw new InterpreterInterruptedException();
				
			} else { // FIRMA VALIDA

				// main
				signatureInfo.setStatus(Status.VALID); // comienza en valido
				MessageInfo messageInfo = new MessageInfo();
				messageInfo.setSimpleStatus(Status.VALID);
				messageInfo.setText(LanguageUtil.getLanguage().getString("info.validation.signature.valid"));
				messages.add(messageInfo);
				
				// ts
				if (result.getEnumNivel().equals(EnumFormatoFirma.XMLSignature) || result.getEnumNivel().equals(EnumFormatoFirma.XAdES_BES)) {
					signatureInfo.setStatus(Status.VALID_WARNING); // baja a warning
					messageInfo = new MessageInfo();
					messageInfo.setSimpleStatus(Status.UNKNOWN);
					messageInfo.setText(LanguageUtil.getLanguage().getString("info.validation.date.local"));
					messages.add(messageInfo);
					
				} else {
					messageInfo = new MessageInfo();
					messageInfo.setSimpleStatus(Status.VALID);	
					messageInfo.setText(LanguageUtil.getLanguage().getString("info.validation.date.timestamp.valid"));
					messages.add(messageInfo);
				}
				
				// WARNINGS CHAIN
				if (chainInfo != null && chainInfo.getStatus().equals(Status.VALID)) {
					
					messageInfo = new MessageInfo();
					messageInfo.setSimpleStatus(Status.VALID);
					messageInfo.setText(LanguageUtil.getLanguage().getString("info.validation.chain.trust"));
					messages.add(messageInfo);
					throw new InterpreterInterruptedException();
					
				} else if (chainInfo != null && chainInfo.getError() != null && chainInfo.getError().equals(ChainInfo.Error.INCOMPLETE)) {
					
					signatureInfo.setStatus(Status.UNKNOWN); // baja a unknown
					messageInfo = new MessageInfo();
					messageInfo.setSimpleStatus(Status.UNKNOWN);
					messageInfo.setText(LanguageUtil.getLanguage().getString("info.validation.chain.incomplete"));
					messages.add(messageInfo);
					throw new InterpreterInterruptedException();
					
				} else if (chainInfo != null && chainInfo.getError() != null && chainInfo.getError().equals(ChainInfo.Error.UNTRUST)) {
					
					signatureInfo.setStatus(Status.VALID_WARNING); // baja a warning
					messageInfo = new MessageInfo();
					messageInfo.setSimpleStatus(Status.UNKNOWN);
					messageInfo.setText(LanguageUtil.getLanguage().getString("info.validation.chain.untrust"));
					messages.add(messageInfo);
					throw new InterpreterInterruptedException();
					
				} else if (chainInfo != null && chainInfo.getError() != null
						&& chainInfo.getError().equals(ChainInfo.Error.REVOCATION_UNKNOWN)) {
					
					signatureInfo.setStatus(Status.UNKNOWN);  // baja a unknown
					messageInfo = new MessageInfo();
					messageInfo.setSimpleStatus(Status.UNKNOWN);
					messageInfo.setText(LanguageUtil.getLanguage().getString("info.validation.chain.revocation.uknown"));
					messages.add(messageInfo);
					throw new InterpreterInterruptedException();
				}
				
				// xades xl validos
				if (result.getDatosFirma().esCadenaConfianza().equals(ConfianzaEnum.CON_CONFIANZA)) {
					messageInfo = new MessageInfo(Status.VALID, LanguageUtil.getLanguage().getString("info.validation.chain.trust"));
					messages.add(messageInfo);
				} else if (result.getDatosFirma().esCadenaConfianza().equals(ConfianzaEnum.SIN_CONFIANZA)) {
					signatureInfo.setStatus(Status.VALID_WARNING); // baja a warning
					messageInfo = new MessageInfo(Status.UNKNOWN, LanguageUtil.getLanguage().getString("info.validation.chain.untrust"));
					messages.add(messageInfo);
				}
				
			}
			
		} catch (InterpreterInterruptedException e) {
			// solo es para interrumpir el proceso
		}
		
		return signatureInfo;
	}
	
	
	public static List<SignatureInfo> parsePdfSignatureInfo(List<PDFSignatureInfo> pdfSignatureInfos) {
		
		List<SignatureInfo> signatureInfos = new ArrayList<SignatureInfo>();
		for (PDFSignatureInfo pdfSignatureInfo : pdfSignatureInfos) {
			signatureInfos.add(parsePdfSignatureInfo(pdfSignatureInfo));
		}
		return signatureInfos;
	}
		
	public static SignatureInfo parsePdfSignatureInfo(PDFSignatureInfo pdfSignatureInfo) {

		SignatureInfo signatureInfo = new SignatureInfo();
		signatureInfo.setChain(pdfSignatureInfo.getChainInfo().getChain());
		signatureInfo.setDate(pdfSignatureInfo.getDate());
		signatureInfo.setStatus(pdfSignatureInfo.getStatus());

		signatureInfo.setMessages(getMessages(pdfSignatureInfo));
		
		return signatureInfo;
	}
	
	private static List<MessageInfo> getMessages(PDFSignatureInfo pdfSignatureInfo) {

		List<MessageInfo> messages = new ArrayList<MessageInfo>();
		MessageInfo messageInfo;
		
		try {
			// ERRORES
			if (pdfSignatureInfo.getError() != null && pdfSignatureInfo.getError().equals(ValidationError.CORRUPT)) {
				
				messageInfo = new MessageInfo();
				messageInfo.setSimpleStatus(Status.INVALID);
				String m = LanguageUtil.getLanguage().getString("info.validation.signature.error.corrupt");
				messageInfo.setText(m);
				messages.add(messageInfo);
				
				throw new InterpreterInterruptedException();
			}
			
			ChainInfo chainInfo = pdfSignatureInfo.getChainInfo();
			if (chainInfo.getError() != null && chainInfo.getError().equals(ChainInfo.Error.REVOKED)) {
				
				messageInfo = new MessageInfo();
				messageInfo.setSimpleStatus(Status.INVALID);
				SimpleDateFormat dateFormat = LanguageUtil.getFullFormater();
				String m = MessageFormat.format(LanguageUtil.getLanguage().getString("info.validation.chain.revoked"),
								CertificateUtil.getFormattedName(chainInfo.getChain().get(chainInfo.getIndex())),
								dateFormat.format(chainInfo.getDate()));
				messageInfo.setText(m);
				messages.add(messageInfo);
				
				throw new InterpreterInterruptedException();
				
			} else if (chainInfo.getError() != null && chainInfo.getError().equals(ChainInfo.Error.EXPIRED)) {
				
				messageInfo = new MessageInfo();
				messageInfo.setSimpleStatus(Status.INVALID);
				SimpleDateFormat dateFormat = LanguageUtil.getFullFormater();
				String m = MessageFormat.format(LanguageUtil.getLanguage().getString("info.validation.chain.expired"),
								CertificateUtil.getFormattedName(chainInfo.getChain().get(chainInfo.getIndex())),
								dateFormat.format(chainInfo.getDate()));
				messageInfo.setText(m);
				messages.add(messageInfo);
				
				throw new InterpreterInterruptedException();
				
			} else if (chainInfo.getError() != null && chainInfo.getError().equals(ChainInfo.Error.NOTYETVALID)) {
				
				messageInfo = new MessageInfo();
				messageInfo.setSimpleStatus(Status.INVALID);
				SimpleDateFormat dateFormat = LanguageUtil.getFullFormater();
				String m = MessageFormat.format(LanguageUtil.getLanguage().getString("info.validation.chain.notyetvalid"),
								CertificateUtil.getFormattedName(chainInfo.getChain().get(chainInfo.getIndex())),
								dateFormat.format(chainInfo.getDate()));
				messageInfo.setText(m);
				messages.add(messageInfo);
				
				throw new InterpreterInterruptedException();
				
			} else if (chainInfo.getError() != null && chainInfo.getError().equals(ChainInfo.Error.GENERIC)) {
				
				messageInfo = new MessageInfo();
				messageInfo.setSimpleStatus(Status.INVALID); 
				String m = MessageFormat.format(LanguageUtil.getLanguage().getString("info.validation.chain.uknown"), CertificateUtil
						.getFormattedName(chainInfo.getChain().get(chainInfo.getIndex())),
						chainInfo.getLog());
				messageInfo.setText(m);
				messages.add(messageInfo);
				
				throw new InterpreterInterruptedException();
			}
			
			// MAIN
			messageInfo = new MessageInfo();
			messageInfo.setSimpleStatus(Status.VALID);
			String m = LanguageUtil.getLanguage().getString("info.validation.signature.valid");
			messageInfo.setText(m);
			messages.add(messageInfo);
			
			// FECHA
			messages.add(getDateMessage(pdfSignatureInfo));
			
			// CHAIN
			if (chainInfo.getStatus().equals(Status.VALID)) {
				
				messageInfo = new MessageInfo();
				messageInfo.setSimpleStatus(Status.VALID);
				messageInfo.setText(LanguageUtil.getLanguage().getString("info.validation.chain.trust"));
				messages.add(messageInfo);
				
			} else if (chainInfo.getError().equals(ChainInfo.Error.INCOMPLETE)) {
				
				messageInfo = new MessageInfo();
				messageInfo.setSimpleStatus(Status.UNKNOWN);
				messageInfo.setText(LanguageUtil.getLanguage().getString("info.validation.chain.incomplete"));
				messages.add(messageInfo);
				
			} else if (chainInfo.getError().equals(ChainInfo.Error.UNTRUST)) {
				
				messageInfo = new MessageInfo();
				messageInfo.setSimpleStatus(Status.UNKNOWN);
				messageInfo.setText(LanguageUtil.getLanguage().getString("info.validation.chain.untrust"));
				messages.add(messageInfo);
				
			} else if (chainInfo.getError().equals(ChainInfo.Error.REVOCATION_UNKNOWN)) {
				
				messageInfo = new MessageInfo();
				messageInfo.setSimpleStatus(Status.UNKNOWN);
				messageInfo.setText(LanguageUtil.getLanguage().getString("info.validation.chain.revocation.uknown"));
				messages.add(messageInfo);
			}

		} catch (InterpreterInterruptedException e) {
			// solo es para interrumpir el proceso
		} 
		
		return messages;
	}

	private static MessageInfo getDateMessage(PDFSignatureInfo pdfSignatureInfo) {

		MessageInfo messageInfo = new MessageInfo();
		TimestampInfo timestampInfo = pdfSignatureInfo.getTimestampInfo();
		
		if (timestampInfo == null) {
		
			messageInfo.setSimpleStatus(Status.UNKNOWN);
			String m = LanguageUtil.getLanguage().getString("info.validation.date.local");
			messageInfo.setText(m);
			
		} else if (timestampInfo.getStatus().equals(Status.VALID)) {
			
			messageInfo.setSimpleStatus(Status.VALID);
			String m = LanguageUtil.getLanguage().getString("info.validation.date.timestamp.valid");
			messageInfo.setText(m);
			
		} else {
			messageInfo.setSimpleStatus(Status.UNKNOWN);
			String m = LanguageUtil.getLanguage().getString("info.validation.date.timestamp.error.generic");
			messageInfo.setText(m);
		}
		
		return messageInfo;
	}
	
}






