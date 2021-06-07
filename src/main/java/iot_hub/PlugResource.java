package iot_hub;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlugResource {
    private final PlugModel plugs;

    public PlugResource (PlugModel plugs){
        this.plugs = plugs;
    }

    protected Object makePlug(String plugName){
        HashMap<String, Object> ret = new HashMap<>();
        ret.put("name", plugName);
        ret.put("state", plugs.getPlugState(plugName));
        ret.put("power", plugs.getPlugPower(plugName));

        return ret;
    }

    @GetMapping("/api/plugs/{plugName:.+}")
    public Object getPlug( @PathVariable("plugName") String plugName , @RequestParam(value = "action" , required = false) String action){
        if (action == null){
            Object ret = makePlug(plugName);
            logger.info("Plug {}: {}", plugName, ret);
            return ret;
        }else{
            plugs.updateState(plugName, action);
            return makePlug(plugName);
        }
    }

    @GetMapping("/api/plugs")
    public Collection<Object> getAllPlugs() throws Exception{
 
        ArrayList<Object> ret = new ArrayList<>();
        ArrayList<String> allPlugNames = plugs.getAllPlugNames();
        for( String plugName : allPlugNames){
            ret.add(makePlug(plugName));
        }
        return ret;
    }

    @PostMapping("/api/groups/{group}")
    public void createGroup( @PathVariable("group") String group, @RequestBody List<String> members){
        plugs.setGroupMembers(group, members);
        logger.info("PlugResource - createGroup(): Group {}: members {}", group, members);
    }
    
    @DeleteMapping("/api/groups/{group}")
    public void removeGroup(@PathVariable("group") String group){
        plugs.removeGroup(group);
    }
    
    protected Object makeGroup(String groupName){
        HashMap<String, Object> ret = new HashMap<>();
        List<Object> members = new ArrayList<Object>();
        ret.put("name", groupName);
        for(String plugName : plugs.getGroupMembers(groupName)){
            Object a = makePlug(plugName);
            members.add(a);
        }
        ret.put("members", members);
        return ret;
    }

    @GetMapping("/api/groups/{groupName}")
    public Object getGroup( @PathVariable("groupName") String groupName , @RequestParam(value = "action" , required = false) String action){

        if (action == null){
            Object ret = makeGroup(groupName);
            return ret;
        }else{
            List<String> plugNames = plugs.getGroupMembers(groupName);
            for (String plugName : plugNames){
                plugs.updateState(plugName, action);
            }
            return makeGroup(groupName);
        }        
    }
    
    @GetMapping("/api/groups")
    public Collection<Object> getAllGroups(){
        ArrayList<Object> rets = new ArrayList<>();        
        ArrayList<String> groupNames = new ArrayList<>();
        groupNames = plugs.getAllGroupNames();
        for (String groupName : groupNames){
            Object ret = makeGroup(groupName);
            rets.add(ret);
        } 
        
        return rets;
    }

    private static final Logger logger = LoggerFactory.getLogger(PlugResource.class);
}
