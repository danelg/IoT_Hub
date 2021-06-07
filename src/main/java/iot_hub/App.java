package iot_hub;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class App {
	@Autowired
	public App(Environment env) throws Exception {
	}

	@Bean(destroyMethod = "disconnect")
	public MqttClient mqttClient (Environment env) throws Exception{
		String broker = env.getProperty("mqtt.broker");
		String clientId = env.getProperty("mqtt.clientId");
		MqttClient mqtt = new MqttClient(broker, clientId, new MemoryPersistence());
		mqtt.connect();
		return mqtt;
		
	}
}
