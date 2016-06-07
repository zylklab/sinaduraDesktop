package net.esle.sinadura.protocol.services;


import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.esle.sinadura.protocol.exceptions.RestServiceException;
import net.esle.sinadura.protocol.model.ConfigVO;
import net.esle.sinadura.protocol.model.InputVO;
import net.esle.sinadura.protocol.utils.HttpUtils;


public class ServiceManager {

	private static Log log = LogFactory.getLog(ServiceManager.class);
	
	private final static String ENCODING_REST_SERVICE = "UTF-8";
	private final static String UniverseTransaction = "transactions";
	
	
	private static enum Services {
		
		GetVersionInfo("version/get"),
		GetConfiguration("config/get"),
		SetStatus("status/set"),
		AddInput("input/add"),
		GetInputs("inputs/get"),
		AddSignatureFile("signaturefile-upload/{transaction-id}/add"),
		PutError("error/add")
		;
		
		private String path;
		
		private Services(String path){
			this.path = path;
		}
		public String getServiceName(){
			return path;
		}
	}
	
	private String transactionServerURL;
	private String token;
	private String serverURL;
	
	
	public ServiceManager(String serverURL, String token) {
		
		this.transactionServerURL = serverURL + '/' + UniverseTransaction;
		this.serverURL = serverURL;
		this.token = token;
	}

	
	public ConfigVO getConfig() throws RestServiceException {
		
		try {
			String fullURL = transactionServerURL + "/" + token + "/" + Services.GetConfiguration.getServiceName();
			log.info("desktop-protocol | getConfig: " + fullURL);

			InputStream is = HttpUtils.getHttp(fullURL);
			byte[] res = IOUtils.toByteArray(is);
			String s = new String(res, ENCODING_REST_SERVICE);
			
			ObjectMapper mapper = new ObjectMapper();
			ConfigVO config = mapper.readValue(s, ConfigVO.class);

			return config;
		
		} catch (MalformedURLException e) {
			throw new RestServiceException(e);
		} catch (IOException e) {
			throw new RestServiceException(e);
		}
	}
	
	
	public List<InputVO> getInputs() throws RestServiceException {
		
		try {
			String fullURL = transactionServerURL + "/" + token + "/" + Services.GetInputs.getServiceName();
		
			InputStream is = HttpUtils.getHttp(fullURL);
			byte[] res = IOUtils.toByteArray(is);
			String s = new String(res, ENCODING_REST_SERVICE);
			
			ObjectMapper mapper = new ObjectMapper();
			
			List<InputVO> inputs = Arrays.asList(mapper.readValue(s, InputVO[].class));
			
			return inputs;
		
		} catch (MalformedURLException e) {
			throw new RestServiceException(e);
		} catch (IOException e) {
			throw new RestServiceException(e);
		}
	}

	public void addSignatureFile(String idInput, byte[] result) throws RestServiceException {
		
		try {
			String fullURL = transactionServerURL + "/" + Services.AddSignatureFile.getServiceName().replace("{transaction-id}", token);
		
			Map<String, String> formObjects = new HashMap<String, String>();
			formObjects.put("idInput", idInput);
			
			HttpUtils.postHttpMultipart(fullURL, formObjects, result);
		
		} catch (IOException e) {
			throw new RestServiceException(e);
		}
	}
	
	public void setStatus(String status) throws RestServiceException {
		
		try {
			String fullURL = transactionServerURL + "/" + token + "/" + Services.SetStatus.getServiceName();
		
			Map<String, String> params = new HashMap<String, String>();
			params.put("status", status);
		
			HttpUtils.postFormHttp(fullURL, params);
			
		} catch (MalformedURLException e) {
			throw new RestServiceException(e);
		} catch (IOException e) {
			throw new RestServiceException(e);
		}
	}
	
	public void setError(String code, String message) throws RestServiceException {
		
		try {
			String fullURL = transactionServerURL + "/" + token + "/" + Services.PutError.getServiceName();
		
			Map<String, String> params = new HashMap<String, String>();
			params.put("code", code);
			params.put("message", message);
		
			HttpUtils.postFormHttp(fullURL, params);
		
		} catch (MalformedURLException e) {
			throw new RestServiceException(e);
		} catch (IOException e) {
			throw new RestServiceException(e);
		}
	}
	
}
