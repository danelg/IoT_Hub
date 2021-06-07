package iot_sim;

import java.io.File;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import iot_sim.http_server.JHTTP;
import org.eclipse.paho.client.mqttv3.MqttClient;

public class Main implements AutoCloseable {
	public static void main(String[] args) throws Exception {
		// load configuration file
		String configFile = args.length > 0 ? args[0] : "simConfig.json";
		SimConfig config = mapper.readValue(new File(configFile), SimConfig.class);
		logger.info("{}: {}", configFile, mapper.writeValueAsString(config));

		try (Main m = new Main(config))
		{
			// loop forever
			for (;;)
			{
				Thread.sleep(60000);
			}
		}
	}

	public Main(SimConfig config) throws Exception {
		// create plugs
		ArrayList<PlugSim> plugs = new ArrayList<>();
		for (String plugName: config.getPlugNames()) {
			plugs.add(new PlugSim(plugName));
		}

		// start power measurements
		MeasurePower measurePower = new MeasurePower(plugs);
		measurePower.start();

		// start HTTP commands
		this.http = new JHTTP(config.getHttpPort(), new HTTPCommands(plugs));
		this.http.start();

		//Start MQTT client
		this.mqtt = new MqttClient(config.getMqttBroker(), config.getMqttClientId(), new MemoryPersistence());
		this.mqtt.connect();
		logger.info("{} - Server Main - MQTTCLIENT: broker: {} clientId: {}", config.getMqttClientId(), config.getMqttBroker(), config.getMqttClientId());

		//Suscribing the Topic
		MqttCommands mqttCmd = new MqttCommands(plugs, config.getMqttTopicPrefix());
			this.mqtt.subscribe(mqttCmd.getTopic(), (topic, msg)->{
			mqttCmd.handleMessage(topic, msg);
			logger.info("{} Server Main - SUSCRIBING second: topic: {}", config.getMqttClientId(), mqttCmd.getTopic());
		});

		logger.info("not executed");
		//Publishing the Updates
		MqttUpdates mqttUpd = new MqttUpdates(config.getMqttTopicPrefix());
		for (PlugSim plug: plugs){
			plug.addObserver((name, key, value)-> {
				try{
					mqtt.publish(mqttUpd.getTopic(name, key), mqttUpd.getMessage(value));
					logger.info("Server Main - to PUBLISH {} {} {} y luego el topic: {}", name, key, value, mqttUpd.getTopic(name, key));

				}catch (Exception e){

				}
			});
		}			
	}

	@Override
	public void close() throws Exception {
		http.close();
	}

	private final JHTTP http;
	private final MqttClient mqtt;
	private static final ObjectMapper mapper = new ObjectMapper();
	private static final Logger logger = LoggerFactory.getLogger(Main.class);
}
