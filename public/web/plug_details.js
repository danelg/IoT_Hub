function plugActionn(name, action) {
	var url = "../api/plugs/" + name + "?action=" + action;
	fetch(url)
		.then(rsp => rsp.json())
		.catch(err => console.error("Members: switchOn????", err));
}

/**
 * This is a stateless view showing details of one plug.
 */
window.PlugDetails = function (props) {
	var plug = props.plugSelected;
	if (plug == null)
		return (<div>Please select a plug from the left.</div>);
	var onPlugClickON = () => props.onPlugAction(plug.name, "on");
	var onPlugClickOFF = () => props.onPlugAction(plug.name, "off");	
	var onPlugClickTOGGLE = () => props.onPlugAction(plug.name, "toggle");	
	
	return (
		<div>
			<p>Plug {plug.name}</p>
			<p>State {plug.state}</p>
			<p>Power {plug.power}</p>
			<button className="btn-primary" onClick={onPlugClickON}>
				Switch On
			</button>
			<button className="btn-primary" onClick={onPlugClickOFF}>
				Switch Off
			</button>
			<button className="btn-primary" onClick={onPlugClickTOGGLE}>
				Toggle
			</button>
		</div>);
}
