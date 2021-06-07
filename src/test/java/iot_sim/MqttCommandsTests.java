package iot_sim;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttCommandsTests {

	@Test
	public void test_getTopic() {

		String TopicPrefix = "test";
		ArrayList<PlugSim> plugs = new ArrayList<>();
		plugs.add(new PlugSim("a"));
		plugs.add(new PlugSim("b"));

		MqttCommands mqttCmd = new MqttCommands(plugs, TopicPrefix);
		String topic = mqttCmd.getTopic();

		assertTrue("test/action/#".equals(topic));
	}

	@Test
	public void test_handleMessage_on() {

		ArrayList<PlugSim> plugs = new ArrayList<>();
		PlugSim pluga = new PlugSim("a");
		plugs.add(new PlugSim("a"));
		plugs.add(new PlugSim("b"));

		String topic = "prefix/action/a/on";
		MqttMessage msg = new MqttMessage("prueba2".getBytes());

		MqttCommands mqttCmd = new MqttCommands(plugs, "prefix");
		mqttCmd.handleMessage(topic, msg);;

		pluga.switchOn();//update

		assertTrue(pluga.isOn());
	}
	
	@Test
	public void test_handleMessage_off() {

		ArrayList<PlugSim> plugs = new ArrayList<>();
		PlugSim pluga = new PlugSim("a");
		plugs.add(new PlugSim("a"));
		plugs.add(new PlugSim("b"));

		String topic = "prefis/action/a/off";
		MqttMessage msg = new MqttMessage("prueba2".getBytes());

		MqttCommands mqttCmd = new MqttCommands(plugs, "prueba2");
		mqttCmd.handleMessage(topic, msg);;
		pluga.switchOff();//update

		assertFalse(pluga.isOn());
	}

	@Test
	public void test_handleMessage_toggle() {

		ArrayList<PlugSim> plugs = new ArrayList<>();
		PlugSim pluga = new PlugSim("a");
		plugs.add(new PlugSim("a"));
		plugs.add(new PlugSim("b"));

		String topic = "prefis/action/a/toggle";
		MqttMessage msg = new MqttMessage("prueba2".getBytes());

		MqttCommands mqttCmd = new MqttCommands(plugs, "prueba2");
		mqttCmd.handleMessage(topic, msg);;

		pluga.toggle();//update

		assertTrue(pluga.isOn());
	}

	@Test
	public void test_handleMessage_error() {

		ArrayList<PlugSim> plugs = new ArrayList<>();
		PlugSim pluga = new PlugSim("a");
		plugs.add(new PlugSim("a"));
		plugs.add(new PlugSim("b"));

		String topic = "prefis/action/a/random";
		MqttMessage msg = new MqttMessage("prueba2".getBytes());

		MqttCommands mqttCmd = new MqttCommands(plugs, "prueba2");
		mqttCmd.handleMessage(topic, msg);;
		assertFalse(pluga.isOn());
	}


}
