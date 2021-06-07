package iot_hub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PlugModel {

    public static class MqttController {
        private final String clientId;
        private final String topicPrefix;
        private final MqttClient client;
        private HashMap<String, String> states = new HashMap<>();
        private HashMap<String, String> powers = new HashMap<>();    
	
		public MqttController(String broker, String clientId,
			String topicPrefix) throws Exception {
			this.clientId = clientId;
			this.topicPrefix = topicPrefix;
			this.client = new MqttClient(broker, clientId, new MemoryPersistence());
		}
	
		public void start() throws Exception {
            MqttConnectOptions opt = new MqttConnectOptions();
			opt.setCleanSession(true);
			client.connect(opt);
            client.subscribe(topicPrefix+"/update/#", this::handleUpdate);            
		}

		public void close() throws Exception {
			client.disconnect();
            logger.info(" MqttCtl {}: disconnected", clientId);            
		}
	
		synchronized public void publishAction(String plugName, String action) {
            String topic = topicPrefix+"/action/"+plugName+"/"+action;                        
			try
			{
                client.publish(topic, new MqttMessage());
            }
			catch (Exception e)
			{
            //PONERLO PROJECT 6
                logger.error("MqttCtl {}: {} fail to publish", clientId, topic);
			}
		}
	
		synchronized public String getState(String plugName) {
			return states.get(plugName);
		}
	
		synchronized public String getPower(String plugName) {
			return powers.get(plugName);
		}
	
		synchronized public Map<String, String> getStates() {
			return new TreeMap<>(states);
		}
	
		synchronized public Map<String, String> getPowers() {
			return new TreeMap<>(powers);
		}
	
		synchronized protected void handleUpdate(String topic, MqttMessage msg) {
            
			String[] nameUpdate = topic.substring(topicPrefix.length()+1).split("/");
            //PONERLO PROJECT 6
            if ((nameUpdate.length != 3) || !nameUpdate[0].equals("update"))
				return; // ignore unknown format
	
			switch (nameUpdate[2])
			{
            case "state":
				states.put(nameUpdate[1], msg.toString());
				break;
            case "power":
				powers.put(nameUpdate[1], msg.toString());
                break;
            //PONERLO PROJECT 6
			default:
				return;
			}
		}
	
		private static final Logger logger = LoggerFactory.getLogger(MqttController.class);
    }
    
    private static final Logger logger = LoggerFactory.getLogger(PlugModel.class);
    private static final String broker = "tcp://127.0.0.1";
    private static String topicPrefix = "topic";
    private static String clientId = "client";
    private MqttController mqtt;
    private HashMap<String, HashSet<String>> groups = new HashMap<>();
    
    public PlugModel() throws Exception {
        this.mqtt = new MqttController(broker, clientId, topicPrefix);
        mqtt.start();        
    }

    public void close() throws Exception {
        mqtt.close();
        logger.info(" MqttCtl {}: disconnected", clientId);        
    }

    public void enviar_topic (String topicPrefixxx, String clientIddd){
        topicPrefix = topicPrefixxx;
        clientId = "graderrr/iot_hub";
    }        

    synchronized public String getPlugState(String plugName){
     
        for(String plug_it : mqtt.getStates().keySet()){
            if(plug_it.equals(plugName)){
                return mqtt.getState(plugName); 
            }
        }
        return "off";
    }

    synchronized public String getPlugPower(String plugName){
        
        for(String plug_it : mqtt.getPowers().keySet()){
            if(plug_it.equals(plugName)){
                return mqtt.getPower(plugName); 
            }
        }
        return "0";
    }

    synchronized public ArrayList<String> getAllPlugNames(){
        ArrayList<String> plugNames = new ArrayList<>();

        for(String plug_it : mqtt.getStates().keySet()){
            plugNames.add(plug_it);
        } 
    
        return plugNames;
    }          

    synchronized public void updateState(String plugName, String action){

        mqtt.publishAction(plugName, action);
    }
 
    synchronized public ArrayList<String> getAllGroupNames(){

        return new ArrayList<>(groups.keySet());
    }

    synchronized public List<String> getGroupMembers(String groupName){

        HashSet<String> members = groups.get(groupName);
        return (members == null) ? new ArrayList<>(): new ArrayList<>(members);
    }
    
    synchronized public void setGroupMembers(String groupName, List<String> members){
        groups.put(groupName, new HashSet<>(members));
    }
    
    synchronized public void removeGroup(String groupName){
        groups.remove(groupName);
    }    
}
