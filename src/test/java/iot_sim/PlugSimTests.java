package iot_sim;

import static org.junit.Assert.*;
import org.junit.Test;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class PlugSimTests{

	private static MqttClient mqtt;

	@Test
	public void testInit() {
		PlugSim plug = new PlugSim("a");

		assertFalse(plug.isOn());
	}

	@Test
	public void testSwitchOn() {
		PlugSim plug = new PlugSim("a");
		plug.switchOn();
		assertTrue(plug.isOn());
	}

	@Test
	public void testSwitchOnOff() {
		PlugSim plug = new PlugSim("a");
		plug.switchOn();
		plug.switchOff();
		assertFalse(plug.isOn());
	}

	@Test
	public void testSwitchOnOffOnOff() {
		PlugSim plug = new PlugSim("a");
		plug.switchOn();
		plug.switchOff();
		plug.switchOn();
		plug.switchOff();

		assertFalse(plug.isOn());
	}

	@Test
	public void testSwitchOnToggle() {
		PlugSim plug = new PlugSim("a");
		plug.switchOn();
		plug.toggle();

		assertFalse(plug.isOn());
	}

	@Test
	public void testSwitchOnToggle2() {
		PlugSim plug = new PlugSim("a");

		plug.switchOn();
		plug.toggle();
		plug.toggle();

		assertTrue(plug.isOn());
	}

	@Test
	public void testMeasurePower() {
        
        PlugSim plug = new PlugSim("a.10");
		plug.switchOn();
		plug.measurePower();

		assertTrue(plug.getPower() == 10);
    }

    @Test
	public void testSwitchOffMeasure() {
        
        PlugSim plug = new PlugSim("a.10");
		plug.switchOn();
		plug.measurePower();
		plug.switchOff();
		plug.measurePower();

		assertTrue(plug.getPower() == 0);
	}
	
	@Test
	public void testSwitchOffMeasure2() {
        
        PlugSim plug = new PlugSim("a.10");
		plug.switchOn();
		plug.measurePower();
		plug.switchOff();
		plug.measurePower();
		plug.toggle();
		plug.measurePower();

		assertTrue(plug.getPower() == 10);
    }
    
    @Test
	public void testSwitchName() {
        
        PlugSim plug = new PlugSim("name.10");
		plug.switchOn();
		plug.measurePower();
		plug.switchOff();
		plug.measurePower();

		assertTrue(plug.getName().equals("name.10")
        && !plug.isOn());
	}

	@Test
	public void testPower2() {
        
        PlugSim plug = new PlugSim("500");
		plug.switchOn();
		plug.updatePower(500);
		plug.measurePower();

		assertTrue(plug.getPower() < 500);
	}

	@Test
	public void testPower3() {
        
        PlugSim plug = new PlugSim("200");
		plug.switchOn();
		plug.updatePower(200);
		plug.measurePower();

		assertTrue(plug.getPower() < 400);
	}

	@Test
	public void test_updateState() {
		PlugSim plug = new PlugSim("a");

		plug.updateState(true);

		assertTrue(plug.isOn());
	}

	@Test
	public void test_updatePower() {
		PlugSim plug = new PlugSim("a");

		plug.updatePower(123);

		assertTrue(123 == plug.getPower());
	}
	
	@Test
	public void test_observer() throws Exception{
		PlugSim plug = new PlugSim("a");

		String topicPrefix = "prefix";

		MqttUpdates mqttUpd = new MqttUpdates(topicPrefix);

		// Create client
		mqtt = new MqttClient("tcp://127.0.0.1", "iot_sim", new MemoryPersistence());
		mqtt.connect();

		plug.addObserver((name, key, value)-> {
			try{
				mqtt.publish(mqttUpd.getTopic(name, key), mqttUpd.getMessage(value));
				System.out.println("update getTopic:   " + mqttUpd.getTopic(name, key));
			}catch (Exception e){
			}
		});
		plug.updateState(true);
		plug.updatePower(666);
		plug.updateState(false);

		assertFalse(plug.isOn());
	}
}
