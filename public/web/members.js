/**
 * A model for managing members in groups.
 */
function create_members_model(groups) {
	// create the data structure
	var all_members = new Set(); // all unique member names
	var group_names = [];
	var group_members = new Map(); // group_name to set of group members 
	var group_members_names = new Map(); // group_name to set of group members names
	for (var group of groups) {
		group_names.push(group.name);
		var members = new Set(group.members);
		for(var member of group.members){
			all_members.add(member.name);
		}

		group_members.set(group.name, members);
		group_members_names.set(group.name, all_members);

	}
	var member_names = Array.from(all_members);
	group_names.sort();
	member_names.sort();

	// create the object
	var that = {}
	that.get_group_names = () => group_names;
	that.get_member_names = () => member_names;
	that.is_member_in_group = (member_name, group_name) => {
		if (group_names.includes(group_name)){

			for(var member of group_members.get(group_name)){
				if (member.name == member_name){
					return true;
				}
			}
		}
		else{
			return false;
		}			
	}
	
	that.get_group_members = group_name => group_members.get(group_name);
	console.debug("members.js: Model",
		groups, group_names, member_names, group_members);

	return that;
}

/**
 * The Members controller holds the state of groups.
 * It creates its view in render().
 */
class Members extends React.Component {

	constructor(props) {
		super(props);
		console.info("Members constructor()");
		this.state = {
			members: create_members_model([]),
			inputName: "",
			inputMembers: "",
			plugs: [],
			plugSelected: null
		};
	}

	componentDidMount() {
		console.info("Members componentDidMount()");
		this.getGroups();
		this.getPlugs();
		setInterval(this.getGroups, 1000);
	}

	render() {

		return (
			<div className="container">
				
				<div className="col-sm-11">
					<MembersTable members={this.state.members}
						inputName={this.state.inputName} inputMembers={this.state.inputMembers}
						onMemberChange={this.onMemberChange}
						onDeleteGroup={this.onDeleteGroup}
						onInputNameChange={this.onInputNameChange}
						onInputMembersChange={this.onInputMembersChange}
						onAddGroup={this.onAddGroup}
						onAddMemberToAllGroups={this.onAddMemberToAllGroups}
						onRemoveMemberFromAllGroups={this.onRemoveMemberFromAllGroups} 
						onGetPlug={this.onGetPlug} 
						onPlugAction={this.onPlugAction} 
						onGroupAction={this.onGroupAction}
						/>
				</div>				
			</div>);
	}

	getGroups = () => {
		console.debug("RESTful: get groups");
		fetch("api/groups")
			.then(rsp => rsp.json())
			.then(groups => this.showGroups(groups))
			.catch(err => console.error("Members: getGroups", err));
	}

	showGroups = groups => {
		this.setState({
			members: create_members_model(groups)
		});
	}

	createGroup = (groupName, groupMembers) => {
		console.info("members.js: createGroup: "+groupName
			+" "+JSON.stringify(groupMembers));
		console .info("a veer: " + groupMembers);
		
		var postReq = {
			method: "POST",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify(groupMembers)
		};
		fetch("api/groups/"+groupName, postReq)
			.then(rsp => this.getGroups())
			.catch(err => console.error("members.js: createGroup() error: ", err));
	}

	createManyGroups = groups => {
		console.info("members.js: createManyGroups(): "+JSON.stringify(groups));
		var pendingReqs = groups.map(group => {
			var postReq = {
				method: "POST",
				headers: {"Content-Type": "application/json"},
				body: JSON.stringify(group.members)
			};
			return fetch("api/groups/"+group.name, postReq);
		});

		Promise.all(pendingReqs)
			.then(() => this.getGroups())
			.catch(err => console.error("Members: createManyGroup", err));
	}

	deleteGroup = groupName => {
		console.info("RESTful: delete group "+groupName);
	
		var delReq = {
			method: "DELETE"
		};
		fetch("api/groups/"+groupName, delReq)
			.then(rsp => this.getGroups())
			.catch(err => console.error("Members: deleteGroup", err));
	}

	onMemberChange2 = (memberName, groupName) => {
		var groupMembers = new Set(this.state.members.get_group_members(groupName));
		var entra = false;
		var groupMembersNames = new Set();
		for(let member of groupMembers.keys()){
			groupMembersNames.add(member.name);
			if (member.name == memberName){
				console.info("true: ");
				entra = true
			}
		}
		if (entra == true){
			console.info("dentro: ");
			groupMembers.delete(memberName);
		}
		else{
			console.info("else: ");
			groupMembersNames.add(memberName);
		}

		this.createGroup(groupName, Array.from(groupMembersNames));
	}
	onMemberChange = (memberName, groupName) => {
		var groupMembers = new Set(this.state.members.get_group_members(groupName));
		var entra = false;
		var groupMembersNames = new Set();
		for(let member of groupMembers.keys()){
			console.info("added: " + member.name)
			groupMembersNames.add(member.name);
			
			if (member.name == memberName){
				console.info("deleted: " + member.name)
				groupMembersNames.delete(memberName);
				console.info("true: ");
				entra = true
			}
		}
		if (entra == true){
			console.info("in: ");			
		}
		else{
			console.info("out: ");
			console.info("added: " + memberName)
			groupMembersNames.add(memberName);
		}
		this.createGroup(groupName, Array.from(groupMembersNames));
	}

	onDeleteGroup = groupName => {
		this.deleteGroup(groupName);
	}

	onInputNameChange = value => {
		console.debug("Members: onInputNameChange", value);
		this.setState({inputName: value});
	}

	onInputMembersChange = value => {
		console.debug("Members: onInputMembersChange", value);
		this.setState({inputMembers: value});
	}

	onAddGroup = () => {
		var name = this.state.inputName;
		var members = this.state.inputMembers.split(',');
	
		this.createGroup(name, members);
	}

	onAddMemberToAllGroups = memberName => {
		var groups = [];
		for (var groupName of this.state.members.get_group_names()) {
			var groupMembers = new Set(this.state.members.get_group_members(groupName));
			groupMembers.add(memberName);
			groups.push({name: groupName, members: Array.from(groupMembers)});
		}
		this.createManyGroups(groups);
	}

	onRemoveMemberFromAllGroups = memberName => {
		var groupMembersNames = new Set();
		for (var groupName of this.state.members.get_group_names()) {
			var groupMembers = this.state.members.get_group_members(groupName);			
			groupMembersNames.clear();
			for(let member of groupMembers.keys()){
				if (member.name == memberName){
					groupMembers.delete(memberName);
				}else{
					groupMembersNames.add(member.name);
				}
			}				
			this.createGroup(groupName, Array.from(groupMembersNames));
		}
	}	

	showGroups3 = groups => {
		this.setState({
			members: create_members_model(groups)
		});
	}

	getPlugs() {
		console.info("plugs getPlugs()");
		fetch("../api/plugs")
			.then(rsp => rsp.json())
			.then(data => this.updatePlugs(data))
			.catch(err => console.debug("Plugs: error " + JSON.stringify(err)));
	}

	updatePlugs(plugss) {
		if (!Array.isArray(plugss)) {
			console.debug("Plugs: cannot get plugs " + JSON.stringify(plugss));
			return;
		}
		this.setState({ plugs: plugss });
	}

	getPlug = memberName => {

		for ( var plug of this.state.plugs){
			if(plug.name == memberName){
				this.showPlug(plug);
			}
		}
	}

	showPlug = plug => {
		this.setState({
			plugSelected: plug 
		});
	}

	onGetPlug = memberName => {
		this.getPlug(memberName);
	}

	updatePlug(plug) {
		this.setState({ plugSelected: plug });

		if (this.props.plugSelected == null)
			return;
		var group_members = new Map();
		var groups = this.state.members;
		for( let group of groups){
			for (let member of group.members.key()){
				if (member.name == plug.name){
					member.state = "on";
				}
			}
		}	
	}

	plugAction = (plugName, action) => {
		fetch("api/plugs/"+plugName+"?action="+action)
			.then(rsp => rsp.json())
			.then(data => this.updatePlugs(data))
			.catch(err => console.debug("Plugs: error " + JSON.stringify(err)));
	}

	onGroupAction = (groupName, action) => {
		var groupMembers = this.state.members.get_group_members(groupName);			
		for(let member of groupMembers.keys()){	
			this.plugAction(member.name, action);
		}
	}	
}
// export
window.Members = Members;