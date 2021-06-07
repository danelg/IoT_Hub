/**
 * The App class is a controller holding the global state.
 * It creates all children controllers in render().
 */
class IoTHubApp extends React.Component {

	constructor(props) {
		super(props);
		this.state = {
			plugSelected: null
		};
		console.info("IoTHubApp constructor()");
	}

	updatePlugSelected(plug) {
		this.setState({ plugSelected: plug });
	}

	render() {
		console.info("IoTHubApp render()");
		return (
			<div className="container">
				<div className="row">
					<h3>Welcome to IoT Hub!</h3>
					<hr className="col-sm-12" />
				</div>
				<div className="row">
					<div className="col-sm-2">
						<Plugs
							updatePlugSelected={plug => this.updatePlugSelected(plug)}
							plugSelected={this.state.plugSelected} />
					</div>
					<div className="col-sm-5">
						<PlugDetails
							plugSelected={this.state.plugSelected} 
							onPlugAction={this.onPlugAction} />
					</div>
					<div className="col-sm-5">
						<Power 
							plugSelected={this.state.plugSelected}
							display={this.props.display} />
					</div>
					<div className="col-sm-20">
					<h2>Manage Groups and Members</h2>
					<Members/>
					</div>
				</div>
				
			</div>);
	}

	PlugAction = (plugName, action) => {
		fetch("api/plugs/"+plugName+"?action="+action)
			.then(rsp => rsp.json())
			.then(data => this.updatePlug(data))
			.catch(err => console.debug("Plugs: error " + JSON.stringify(err)));
	}

	onPlugAction = (plugName, action) => {
		this.PlugAction(plugName, action);
	}
}

window.IoTHubApp = IoTHubApp;
