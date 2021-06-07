package iot_sim;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

public class HTTPCommandsTests {

	@Test
	public void testInit() {
		PlugSim a = new PlugSim("a");
		PlugSim x = new PlugSim("x");

		ArrayList<PlugSim> list = new ArrayList<>();
		list.add(a);
		list.add(x);

		HTTPCommands cmds = new HTTPCommands(list);

		assertEquals(cmds.handleGet("/b", new HashMap<>()), null);
	}
	@Test
	public void test02() {
		PlugSim a = new PlugSim("a");
		PlugSim x = new PlugSim("x");

		ArrayList<PlugSim> list = new ArrayList<>();
		list.add(a);
		list.add(x);

		HTTPCommands cmds = new HTTPCommands(list);

		assertTrue(cmds.handleGet("/a", new HashMap<>()) != null);
	}

	@Test
	public void test03() {
		PlugSim a = new PlugSim("a");
		PlugSim x = new PlugSim("x");

		ArrayList<PlugSim> list = new ArrayList<>();
		list.add(a);
		list.add(x);

		HTTPCommands cmds = new HTTPCommands(list);

		HashMap<String, String> params = new HashMap<>();
		params.put("action", "on");

		assertFalse(cmds.handleGet("/a", params) == null);
	}

	@Test
	public void test04() {
		PlugSim a = new PlugSim("a");
		PlugSim x = new PlugSim("x");

		ArrayList<PlugSim> list = new ArrayList<>();
		list.add(a);
		list.add(x);

		HTTPCommands cmds = new HTTPCommands(list);

		HashMap<String, String> params = new HashMap<>();
		params.put("action", "off");

		assertFalse(cmds.handleGet("/a", params) == null);
	}

	@Test
	public void test05() {
		PlugSim a = new PlugSim("a");
		PlugSim x = new PlugSim("x");

		ArrayList<PlugSim> list = new ArrayList<>();
		list.add(a);
		list.add(x);

		HTTPCommands cmds = new HTTPCommands(list);

		HashMap<String, String> params = new HashMap<>();
		params.put("action", "toggle");

		assertFalse(cmds.handleGet("/a", params) == null);
	}

	@Test
	public void test06() {
		PlugSim a = new PlugSim("a");
		PlugSim x = new PlugSim("x");

		ArrayList<PlugSim> list = new ArrayList<>();
		list.add(a);
		list.add(x);

		HTTPCommands cmds = new HTTPCommands(list);

		HashMap<String, String> params = new HashMap<>();
		params.put("action", "toggle");

		assertFalse(cmds.handleGet("/", params) == null);
	}

	@Test
	public void test07() {
		PlugSim a = new PlugSim("a");
		PlugSim x = new PlugSim("x");

		ArrayList<PlugSim> list = new ArrayList<>();
		list.add(a);
		list.add(x);

		HTTPCommands cmds = new HTTPCommands(list);

		HashMap<String, String> params = new HashMap<>();
		params.put("action", "toggle");

		assertFalse(cmds.handleGet("/a", params) == null && cmds.handleGet("/x", params) == null);
	}

	@Test
	public void test08() {
		PlugSim a = new PlugSim("a");
		PlugSim x = new PlugSim("x");

		ArrayList<PlugSim> list = new ArrayList<>();
		list.add(a);
		list.add(x);

		HTTPCommands cmds = new HTTPCommands(list);

		HashMap<String, String> params1 = new HashMap<>();
		params1.put("action", "on");
		HashMap<String, String> params2 = new HashMap<>();
		params2.put("action", "off");

		assertFalse(cmds.handleGet("/a", params1) == null && cmds.handleGet("/x", params2) == null);
	}
	@Test
	public void test09() {
		PlugSim a = new PlugSim("a");
		PlugSim x = new PlugSim("x");

		ArrayList<PlugSim> list = new ArrayList<>();
		list.add(a);
		list.add(x);

		HTTPCommands cmds = new HTTPCommands(list);

		HashMap<String, String> params1 = new HashMap<>();
		params1.put("action", "on");
		HashMap<String, String> params2 = new HashMap<>();
		params2.put("action", "on");

		assertFalse(cmds.handleGet("/a", params1) == null && cmds.handleGet("/x", params2) == null);
	}

	@Test
	public void test10() {
		PlugSim a = new PlugSim("a");
		PlugSim x = new PlugSim("x");

		ArrayList<PlugSim> list = new ArrayList<>();
		list.add(a);
		list.add(x);

		HTTPCommands cmds = new HTTPCommands(list);

		HashMap<String, String> params1 = new HashMap<>();
		params1.put("action", "off");
		HashMap<String, String> params2 = new HashMap<>();
		params2.put("action", "off");

		assertFalse(cmds.handleGet("/a", params1) == null && cmds.handleGet("/x", params2) == null);
	}


}
