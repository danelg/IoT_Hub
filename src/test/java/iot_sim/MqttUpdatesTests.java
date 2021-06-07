package iot_sim;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttUpdatesTests {

	@Test
	public void testUpdate_getTopic() {

		String TopicPrefix = "test";

		MqttUpdates mqttUpd = new MqttUpdates(TopicPrefix);
		String topic = mqttUpd.getTopic("test1", "test2");

		assertTrue("test/update/test1/test2".equals(topic));
	}

	@Test
	public void testUpdate_getMessage() {

		String TopicPrefix = "test";
		String value = "test";
		MqttUpdates mqttUpd = new MqttUpdates(TopicPrefix);

		MqttMessage msg1 =new MqttMessage(value.getBytes());
		msg1.setRetained(true);

		assertTrue(msg1.toString().equals(mqttUpd.getMessage(value).toString()));
	}
}
