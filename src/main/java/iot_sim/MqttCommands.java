package iot_sim;

import java.util.List;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class MqttCommands {
    private final TreeMap<String, PlugSim> plugs = new TreeMap<>();
    private final String topicPrefix;
    private static final Logger logger = LoggerFactory.getLogger(MqttCommands.class);

    public MqttCommands (List<PlugSim> plugs, String topicPrefix){
        for (PlugSim plug: plugs)
            this.plugs.put(plug.getName(), plug);
        this.topicPrefix = topicPrefix;
    }

    public String getTopic(){
        logger.info("MqttCommands - getTopic(): {}", topicPrefix);
        return topicPrefix+"/action/#";
    }
    
    public void handleMessage (String topic, MqttMessage msg){        
        
        try{

            String[] elements = topic.split("/");
            String plugName = elements[elements.length-2]; 
            String action = elements[elements.length-1];

            PlugSim plug = plugs.get(plugName);

            if(action.equals("on")){
                plug.switchOn();
            }
            else if(action.equals("off")){
                plug.switchOff();
            }
            else if(action.equals("toggle")){
                plug.toggle();
            }
            else{
                logger.info("WRONG");
            }            
        }
        catch(Exception e){

        }          
    }
}