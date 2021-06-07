const btnClassAdd = "btn btn-primary btn-block";
const btnClassDel = "btn btn-danger btn-block";

/**
 * This is a stateless view showing the table header.
 */
function Header(props) {
	var groupName2;
	var ths = [];
	var ths = props.groupNames.map(groupName => {
		var onGroupClickON = () => props.onGroupAction(groupName, "on");
		var onGroupClickOFF = () => props.onGroupAction(groupName, "off");
		var onGroupClickToggle = () => props.onGroupAction(groupName, "toggle");

		return <th key={groupName}>
				<button className={btnClassAdd}>{groupName}</button>

				<th>
					<button width="10%" className={btnClassAdd} onClick={onGroupClickON}>Switch ON</button>
				</th>				
				<th>
					<button width="10%" className={btnClassAdd} onClick={onGroupClickOFF}>Switch OFF</button>
				</th>
				<th>
					<button width="10%" className={btnClassAdd} onClick={onGroupClickToggle}>Toggle</button>
				</th>
			
			</th>
	});

	return (
		<thead>
			
			<tr>
				<th rowSpan="2" width="10%">Members</th>
				<th colSpan={props.groupNames.length}>Groups</th>
				<th rowSpan="2" width="10%">Remove from All Groups</th>
			</tr>
			<tr>
				{ths}
			</tr>
		</thead>
	);
}

/**
 * This is a stateless view showing one row.
 */
function Row(props) {
	var members = props.members;
	var tds = members.get_group_names().map(groupName => {
		var onChange = () => props.onMemberChange(props.memberName, groupName);
		var checked = members.is_member_in_group(props.memberName, groupName);
		console.info("boton como esta?: " + checked);
		return (<td key={groupName}>
			<input type="checkbox" onChange={onChange} checked={checked}/></td>);
	});
	
    var onStateClick = () => props.onGetPlug(props.memberName);
	var onRemoveClick = () => props.onRemoveMemberFromAllGroups(props.memberName);
	return (
		<tr>
			<td><button className={btnClassAdd} onClick={onStateClick}>{props.memberName}</button></td>
			{tds}
			<td><button className={btnClassDel} onClick={onRemoveClick}>X</button></td>
		</tr>
	);
}

/**
 * This is a stateless view showing the row for delete groups.
 */
function DeleteGroupsRow(props) {
	var tds = props.groupNames.map(groupName => {
		var onClick = () => props.onDeleteGroup(groupName);
		return <td key={groupName}>
			<button className={btnClassDel} onClick={onClick}>X</button></td>;
	});

	return (
		<tr>
			<td></td>
			{tds}
			<td></td>
		</tr>
	);
}

/**
 * This is a stateless view showing inputs for add/replace groups.
 */
function AddGroup(props) {
	var onChangeName = event => props.onInputNameChange(event.target.value);
	var onChangeMembers = event => props.onInputMembersChange(event.target.value);

	return (
		<div>
			<label>Group Name</label>
			<input type="text" onChange={onChangeName} value={props.inputName}/>
			<label>Members</label>
			<input type="text" onChange={onChangeMembers} value={props.inputMembers}
				size="60" placeholder="e.g. a,b,c"/>
			<button className="btn btn-primary" onClick={props.onAddGroup}>
				Add/Replace</button>
		</div>
	);
}

/**
 * This is a stateless view showing the table body.
 */
function Body(props) {
	var rows = props.members.get_member_names().map(memberName =>
		<Row key={memberName} memberName={memberName} members={props.members}
			onMemberChange={props.onMemberChange}
			onAddMemberToAllGroups={props.onAddMemberToAllGroups}
			onRemoveMemberFromAllGroups={props.onRemoveMemberFromAllGroups}
			onGetPlug={props.onGetPlug}
			onPlugAction={props.onPlugAction}
			//plugSelected={props.plugSelected}
			//selectPlug={props.selectPlug}
			/>);

	return (
		<tbody>
			{rows}
			<DeleteGroupsRow groupNames={props.members.get_group_names()}
				onDeleteGroup={props.onDeleteGroup} />
			<tr><td colSpan="7">
				<AddGroup inputName={props.inputName} inputMembers={props.inputMembers}
					onInputNameChange={props.onInputNameChange}
					onInputMembersChange={props.onInputMembersChange}
					onAddGroup={props.onAddGroup} />
			</td></tr>
		</tbody>
	);
}

/**
 * This is a stateless view showing the whole members table.
 */
function MembersTable(props) {
	//console.info("MembersTable()");
	if (props.members.get_group_names().length == 0)
		return (
			<div>
				<div>There are no groups.</div>
				<AddGroup inputName={props.inputName} inputMembers={props.inputMembers}
					onInputNameChange={props.onInputNameChange}
					onInputMembersChange={props.onInputMembersChange}
					onAddGroup={props.onAddGroup} />
			</div>);
	var header = props.members.get_group_names().map(groupName =>
		<Header key={groupName} groupName={groupName} members={props.members}
			groupNames={props.members.get_group_names()} 
			onGroupAction={props.onGroupAction}
			/>);
			//<Header groupNames={props.members.get_group_names()} 
			//	onGroupAction={props.onGroupAction}/>
	return (
		<table className="table table-striped table-bordered">
			 
			 <Header groupNames={props.members.get_group_names()} 
				onGroupAction={props.onGroupAction}/>
			<Body members={props.members}
				inputName={props.inputName} inputMembers={props.inputMembers}
				onMemberChange={props.onMemberChange}
				onDeleteGroup={props.onDeleteGroup}
				onInputNameChange={props.onInputNameChange}
				onInputMembersChange={props.onInputMembersChange}
				onAddGroup={props.onAddGroup}
				onAddMemberToAllGroups={props.onAddMemberToAllGroups}
				onRemoveMemberFromAllGroups={props.onRemoveMemberFromAllGroups} 
				onGetPlug={props.onGetPlug} 
				onPlugAction={props.onPlugAction}
				//plugSelected={props.plugSelected}
				//selectPlug={props.selectPlug} 
				/>
		</table>);
}

//export
window.MembersTable = MembersTable;