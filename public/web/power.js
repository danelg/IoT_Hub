/**
 * The Power controller holds the state of power consumption.
 * It creates its view in render().
 */
class Power extends React.Component {

	constructor(props) {
		super(props);
		this.state = {
            powerData: [],
		};
	}

	componentDidMount() {
		this.getPowerData();
		setInterval(this.getPowerData, 500);
	}

	render() {
		// we choose to implement this simple view in the controller
		var powerData = this.state.powerData;

		if (powerData.length == 0)
			return <div></div>;
		
		var last = powerData[powerData.length-1];
		return (<div>
			Last power reading: {last.power} Watts, {last.date} {last.time}
		</div>);
	}

	getPowerData = () => {
		// for simplicity, we generate some fake data here
		var now = new Date();
		var date = now.toLocaleDateString();
		var time = now.toLocaleTimeString();

        var powerData = [{date: null, time: null, power: null}];
        var plug_it = this.props.plugSelected;
        var power = 30;
		
		if (plug_it != null) {
			powerData = this.state.powerData.slice(0);

            powerData.push({date: date, time: time, power: plug_it.power});
            // discard old data
		    if (powerData.length > 20)
		    	powerData = powerData.slice(powerData.length-20);
            this.props.display.setData(powerData); // external MVC framework
            this.setState({powerData: powerData});
		}else{	
			// update state
			this.props.display.setData([]); // external MVC framework
			this.setState({powerData: []});
        }
	}
}

// export
window.Power = Power;