package iot_hub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.junit.Test;
import iot_hub.PlugModel.MqttController;


public class iot_hubTest {

	private static final List<String> plugNames = Arrays.asList("a", "b", "c");
	private static final List<String> plugNamesEx = Arrays.asList("d", "e", "f", "g");
    private static final List<String> allPlugNames = Arrays.asList("a", "b", "c", "d", "e", "f", "g");
    private static final List<String> groupNames = Arrays.asList("x", "y", "z");    
    private static final ObjectMapper mapper = new ObjectMapper();
    private MqttController mqtt;

    static String getSim(String pathParams) throws Exception {
		String prueba = Request.Get("http://127.0.0.1:8080" + pathParams)
		.userAgent("Mozilla/5.0").connectTimeout(1000)
		.socketTimeout(1000).execute().returnContent().asString();
		return prueba;
	}

	static String getSimEx(String pathParams) throws Exception {
		return Request.Get("http://127.0.0.1:8081" + pathParams)
			.userAgent("Mozilla/5.0").connectTimeout(1000)
			.socketTimeout(1000).execute().returnContent().asString();
	}

	static String getHub(String pathParams) throws Exception {
		return Request.Get("http://127.0.0.1:8088" + pathParams)
			.userAgent("Mozilla/5.0").connectTimeout(1000)
			.socketTimeout(1000).execute().returnContent().asString();
	}

	static String getStates1() throws Exception {
		TreeMap<String, String> states = new TreeMap<>();
		for (String name: allPlugNames)
		{
			Map<String, Object> plug = mapper.readValue(getHub("/api/plugs/" + name),
				new TypeReference<Map<String, Object>>() {});
			if (!name.equals((String)plug.get("name")))
				throw new Exception("invalid name " + name);
			states.put(name, "off".equals((String)plug.get("state"))? "0": "1");
		}
		String ret = String.join("", states.values());
		return ret;
	}

	static String getStates2() throws Exception {
		TreeMap<String, String> states = new TreeMap<>();
		HashSet<String> known = new HashSet<>(allPlugNames);
		List<Map<String, Object>> plugs = mapper.readValue(getHub("/api/plugs"),
			new TypeReference<List<Map<String, Object>>>() {});
		for (Map<String, Object> plug: plugs)
		{
			String name = (String)plug.get("name");
			String state = (String)plug.get("state");
			if (!known.contains(name)){
				throw new Exception("invalid plug " + name);
			}
			known.remove(name);
			states.put(name, "off".equals(state)? "0": "1");
		}
		if (!known.isEmpty()){
			throw new Exception("missing plugs");
		}
		String ret = String.join("", states.values());
		return ret;
	}

	static String getStates3() throws Exception {
		TreeMap<String, String> states = new TreeMap<>();
		for (String name: plugNames)
		{
			String ret = getSim("/"+name);
			if ((ret.indexOf(name+" is off") != -1) && (ret.indexOf(name+" is on") == -1))
			{
				states.put(name, "0");
			}
			else
			{
				states.put(name, "1");
			}
		}
		for (String name: plugNamesEx)
		{
			String ret = getSimEx("/"+name);
			if ((ret.indexOf(name+" is off") != -1) && (ret.indexOf(name+" is on") == -1))
			{
				states.put(name, "0");
			}
			else
			{
				states.put(name, "1");
			}
		}
		String ret = String.join("", states.values());
		return ret;
	}

	static String getStates4(MqttController mqtt) throws Exception {
		TreeMap<String, String> states = new TreeMap<>();
		for (String name: allPlugNames)
		{
			states.put(name, "off".equals(mqtt.getState(name))? "0": "1");
		}
		String ret = String.join("", states.values());
		return ret;
	}

	static boolean verifyStates(String states, MqttController mqtt) throws Exception {		
		
		return states.equals(getStates1())
			&& states.equals(getStates2())
			&& states.equals(getStates3())
			&& states.equals(getStates4(mqtt));
    }
    static void postGroup(String group, List<String> members) throws Exception {

		Request.Post("http://127.0.0.1:8088/api/groups/" + group)
			.bodyByteArray(mapper.writeValueAsBytes(members), ContentType.APPLICATION_JSON)
			.userAgent("Mozilla/5.0").connectTimeout(1000)
			.socketTimeout(1000).execute();
	}

	static void delGroup(String group) throws Exception {

		Request.Delete("http://127.0.0.1:8088/api/groups/" + group)
			.userAgent("Mozilla/5.0").connectTimeout(1000)
			.socketTimeout(1000).execute();
    }
    
    static String getGroups1() throws Exception {
		TreeMap<String, String> fields = new TreeMap<>();

		for (String name: groupNames)
		{
			Map<String, Object> group = mapper.readValue(getHub("/api/groups/"+name),
				new TypeReference<Map<String, Object>>() {});

			System.out.println((String)group.get("name"));
			System.out.println(name);
			if (!name.equals((String)group.get("name")))
				throw new Exception("invalid name " + name);

			StringBuilder field = new StringBuilder(name+".");
			
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> members = (List<Map<String, Object>>)group.get("members");
			for (Map<String, Object> member: members)
			{
				field.append(member.get("name"));
				field.append("off".equals(member.get("state"))? "0": "1");
			}
			if (!members.isEmpty())
				fields.put(name, field.toString());
		}
		String ret = String.join("|", fields.values());
		return ret;
	}

	static String getGroups2() throws Exception {

		TreeMap<String, String> fields = new TreeMap<>();
		List<Map<String, Object>> groups = mapper.readValue(getHub("/api/groups"),
			new TypeReference<List<Map<String, Object>>>() {});
		for (Map<String, Object> group: groups)
		{
			String name = (String)group.get("name");
			StringBuilder field = new StringBuilder(name+".");
			
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> members = (List<Map<String, Object>>)group.get("members");
			for (Map<String, Object> member: members)
			{
				field.append(member.get("name"));
				field.append("off".equals(member.get("state"))? "0": "1");
			}
			fields.put(name, field.toString());
		}
		String ret = String.join("|", fields.values());
		return ret;
	}

	static boolean verifyGroups(String groups) throws Exception {
		return groups.equals(getGroups1())
			&& groups.equals(getGroups2());
	}
    
    @Test
	public void test00() {
        try{  
            
            mqtt.close();
            mqtt.publishAction("ee", "e");
            assertTrue("0000000".equals(getStates1()));       
            
        }catch(Exception e){
            System.out.println("error");
        }		
    }
    
    @Test
	public void test01() {
        try{
            getHub("/api/plugs/a?action=on");
            getHub("/api/plugs/c?action=toggle");
            Thread.sleep(1000);
            assertTrue("1010000".equals(getStates1()));
        }catch(Exception e){
            System.out.println("error");
        }		
    }
        
    @Test
	public void test02() {
        try{
            getHub("/api/plugs/a?action=toggle");
            getHub("/api/plugs/c?action=off");
            getHub("/api/plugs/e?action=on");
            getHub("/api/plugs/g?action=toggle");

            Thread.sleep(1000);            
            assertTrue("0000101".equals(getStates1()));
        }catch(Exception e){
            System.out.println("error");
        }		
    }
    
    @Test
	public void test03() {
        try{
            getHub("/api/plugs/a?action=off");
            getHub("/api/plugs/b?action=on");
            getHub("/api/plugs/c?action=off");
            getHub("/api/plugs/d?action=toggle");
            getHub("/api/plugs/e?action=on");
            getHub("/api/plugs/f?action=off");
            getHub("/api/plugs/g?action=toggle");
            Thread.sleep(1000);
            assertTrue("0101100".equals(getStates2()));
        }catch(Exception e){
            System.out.println("error");
        }		
    }
    @Test
	public void test04() {
        try{
            getHub("/api/plugs/b?action=off");
            getHub("/api/plugs/d?action=on");
            getHub("/api/plugs/f?action=on");
            Thread.sleep(1000);           
            assertTrue("0001110".equals(getStates2()));
        }catch(Exception e){
            System.out.println("error");
        }		
    }
    @Test
	public void test05() {
        try{
            getSim("/b?action=on");
            Thread.sleep(1000);          
            assertTrue(verifyStates("0101110", mqtt));
        }catch(Exception e){
            System.out.println("error");
        }		
    }
    @Test
	public void test06() {
        try{
            getSimEx("/d?action=off");
            Thread.sleep(1000);         
            assertTrue(verifyStates("0100110", mqtt));
        }catch(Exception e){
            System.out.println("error");
        }		
    }
    @Test
	public void test07() {
        try{
            mqtt.publishAction("c", "on");
            mqtt.publishAction("e", "off");
            Thread.sleep(1000);     
            assertTrue(verifyStates("0110010", mqtt));
        }catch(Exception e){
            System.out.println("error");
        }		
    }
    @Test
	public void test08() {
        try{
            getSim("/a?action=toggle");
            mqtt.publishAction("d", "toggle");
            getSimEx("/e?action=toggle");
            mqtt.publishAction("g", "toggle");
            Thread.sleep(1000);
            assertTrue(verifyStates("1111111", mqtt));
        }catch(Exception e){
            System.out.println("error");
        }		
    }
    @Test
	public void test09() {
        try{
            getHub("/api/plugs/a?action=off");
            mqtt.publishAction("b", "toggle");
            getSim("/c?action=off");
            getSimEx("/d?action=toggle");
            getHub("/api/plugs/e?action=toggle");
            mqtt.publishAction("f", "off");
            getSimEx("/g?action=off");
            Thread.sleep(1000);       
            assertTrue(verifyStates("0000000", mqtt));
        }catch(Exception e){
            System.out.println("error");
        }		
    }

    @Test
	public void test010() {
        try{
            PlugModel plug = new PlugModel();
            plug.getPlugState("wrong_plug");
            plug.getPlugPower("wrong_plug");
            getHub("/api/plugs/v?action=on");
            assertTrue("1000000".equals(getStates1()));
        }catch(Exception e){
            System.out.println("error");
        }		
    }
    
    @Test
    public void testCase11() throws Exception {
        try{
            assertEquals(verifyGroups(""), true);
        }catch(Exception e){
            System.out.println("error");
        }
    }
    
    @Test
    public void testCase12() throws Exception {
        try{
            getHub("/api/plugs/a?action=off");
            getHub("/api/plugs/b?action=on");
            getHub("/api/plugs/c?action=off");
            getHub("/api/plugs/d?action=toggle");
            getHub("/api/plugs/e?action=on");
            getHub("/api/plugs/f?action=off");
            getHub("/api/plugs/g?action=off");
            postGroup("z", Arrays.asList("a", "d"));
            Thread.sleep(1000);
            assertTrue(verifyStates("0101100", mqtt) && verifyGroups("z.a0d1"));
        }catch(Exception e){
            System.out.println("error");
        }
    }

    @Test
    public void testCase13() throws Exception {
        try{
            postGroup("x", Arrays.asList("b", "e"));
            postGroup("y", Arrays.asList("c", "f"));
            getHub("/api/groups/y?action=on");
            Thread.sleep(1000);
            assertTrue(verifyStates("0111110", mqtt) && verifyGroups("x.b1e1"));
        }catch(Exception e){
            System.out.println("error");
        }
    }

    @Test
    public void testCase14() throws Exception {
        try{
            getHub("/api/plugs/y?action=on");
            Thread.sleep(1000);
            assertEquals(verifyStates("0101100", mqtt) , verifyGroups("x.b1e1"));
        }catch(Exception e){
            System.out.println("error");
        }
    }

    @Test
    public void testCase15() throws Exception {
        try{
            Map<String, Object> group = mapper.readValue(getHub("/api/groups"),
				new TypeReference<Map<String, Object>>() {});

            Thread.sleep(1000);
            assertTrue(group.size()==3);
        }catch(Exception e){
            System.out.println("error");
        }
    }

    @Test
    public void testCase16() throws Exception {
        try{
            delGroup("z");
            Thread.sleep(1000);
            assertFalse(verifyStates("0101100", mqtt) && verifyGroups("z.a0d1"));
        }catch(Exception e){
            System.out.println("error");
        }
    }

    @Test
    public void testCase17() throws Exception {
        try{
            postGroup("w", Arrays.asList("b", "e"));
            delGroup("w");
            Thread.sleep(1000);
            assertFalse(verifyStates("0101100", mqtt) && verifyGroups("w.a0d1"));
        }catch(Exception e){
            System.out.println("error");
        }
    }

    @Test
    public void testCase18() throws Exception {
        try{
            postGroup("w", Arrays.asList("b", "e"));
            postGroup("w", Arrays.asList("a", "c"));
            Thread.sleep(1000);
            assertEquals(verifyStates("0101100", mqtt), verifyGroups("w.a0c0"));
        }catch(Exception e){
            System.out.println("error");
        }
    }

    @Test
    public void testCase19() throws Exception {
        try{
            postGroup("s", Arrays.asList("b", "e"));
            postGroup("t", Arrays.asList("a", "b"));
            Thread.sleep(1000);
            assertEquals(verifyStates("0101100", mqtt), verifyGroups("t.a0b1"));
        }catch(Exception e){
            System.out.println("error");
        }
    }
    @Test
    public void testCase20() throws Exception {
        try{
            postGroup("s", Arrays.asList("b", "e"));
            delGroup("w");
            postGroup("t", Arrays.asList("a", "b"));

            Thread.sleep(1000);
            assertEquals(verifyStates("0101100", mqtt), verifyGroups("t.a0b1"));
        }catch(Exception e){
            System.out.println("error");
        }
    }
}

