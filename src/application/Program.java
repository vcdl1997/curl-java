package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Program {

	public static void main(String[] args) throws IOException {
		String bearerToken = obterTokenApi();
		criandoNovoRecurso(bearerToken);
		deletandoRecurso(bearerToken);
	}
	
	
	// Convertendo HashMap em JSON
	private static String converterEmJson(Map<String, String> parametros) {
		String str = "{";
		int qtdParams = parametros.size();
		int contador = 0;
		
		for (Map.Entry<String,String> param : parametros.entrySet()) {
			++contador;
			String concat = "\"" + param.getKey() + "\":\"" + param.getValue() + "\"";
			if(contador < qtdParams) concat += ",";
			str += concat;
		}
		
		str += "}";
		
		return str;
	}

	
	// Obtendo Bearer Token
	private static String obterTokenApi() {
		URL url = null;
		HttpURLConnection http = null;
		
		try {
			url = new URL("http://localhost:8080/auth");
			http = (HttpURLConnection)url.openConnection();
			
			http.setConnectTimeout(10000); // 10 segundos
			http.setRequestMethod("POST");
			http.setDoOutput(true);
			http.setRequestProperty("Content-Type", "application/json");
			
			Map<String, String> params = new HashMap<>();
			params.put("email", "aluno@email.com");
			params.put("senha", "123456");
			String json = converterEmJson(params);
			
			byte[] out = json.getBytes(StandardCharsets.UTF_8);
			OutputStream stream = http.getOutputStream();
			stream.write(out);
			BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream()));
			
			String line = br.readLine();
			String token = br.readLine(); 
			
			while(line != null) {
				String[] response = line.split(",");
				token = response[0].substring(10, response[0].length()-1);
				line = br.readLine();
			}
			
			return token;
			
		}catch (IOException e) {
			e.printStackTrace();
		}finally {
			http.disconnect();
		}
		
		return null;
	}
	
	
	// Exemplo de POST repassando token para a requisição
	public static void criandoNovoRecurso(String bearerToken) {
		URL url = null;
		HttpURLConnection http = null;
		
		try {
			url = new URL("http://localhost:8080/topicos");
			http = (HttpURLConnection)url.openConnection();
			
			http.setConnectTimeout(10000); // 10 segundos
			http.setRequestMethod("POST");
			http.setDoOutput(true);
			http.setRequestProperty("Content-Type", "application/json");
			http.setRequestProperty("Authorization", "Bearer " + bearerToken);
			
			Map<String, String> params = new HashMap<>();
				  
			params.put("mensagem", "Não consegui instalar a dependencia do Spring Security");
			params.put("nomeCurso", "Spring Boot");
			params.put("titulo", "Problema com Maven");
			String json = converterEmJson(params);
			
			byte[] out = json.getBytes(StandardCharsets.UTF_8);
			OutputStream stream = http.getOutputStream();
			stream.write(out);
			BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream()));
			
			String line = br.readLine();
			
			while(line != null) {
				System.out.println(line);
				line = br.readLine();
			}
			
		}catch (IOException e) {
			e.printStackTrace();
		}finally {
			http.disconnect();
		}
	}
	
	
	// Exemplo de DELETE repassando token para a requisição
	public static void deletandoRecurso(String bearerToken) {
		URL url = null;
		HttpURLConnection http = null;
		
		try {
			url = new URL("http://localhost:8080/topicos/4");
			http = (HttpURLConnection)url.openConnection();
			
			http.setConnectTimeout(10000); // 10 segundos
			http.setRequestMethod("DELETE");
			http.setDoOutput(true);
			http.setRequestProperty("Authorization", "Bearer " + bearerToken);
			
			System.out.println(http.getResponseCode() + " " + http.getResponseMessage());
			
		}catch (IOException e) {
			e.printStackTrace();
		}finally {
			http.disconnect();
		}
	}
}
