/**
 * This is a stateless view showing details of one plug.
 */
window.MemberDetails = function (props) {
	var plug = props.plugSelected;
	if (plug == null)
		return (<div>Please select a plug from the left.</div>);
	var onPlugClickON = () => props.onPlugAction(plug.name, "on");
	var onPlugClickOFF = () => props.onPlugAction(plug.name, "off");	console.info("member_details MemberDetails ")
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
		</div>);
}
