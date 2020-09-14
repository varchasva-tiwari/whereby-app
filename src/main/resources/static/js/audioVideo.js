
    export let myAudioEnabled = true;
	export let allAudioEnabled = true;
	export let myVideoEnabled = true;
	export let screenShareEnabled = false;
	export let publisher = '';
	export let memberCount = 0;
	export let hoursLabel = document.getElementById("hours");
	export let minutesLabel = document.getElementById("minutes");
	export let secondsLabel = document.getElementById("seconds");
	export let totalSeconds = 0;
    const ov = new OpenVidu();
    let session = ov.initSession();


session.connect(token,{clientData:nickName})
			.then(()=>{
				$("#session-title").text(sessionName);
				$("#join").hide();
				$("#session").show();
				publisher = ov.initPublisher("video-container",{
					videoSource:undefined,
					audioSource:undefined,
					publishAudio:myAudioEnabled,
					publishVideo:true,
					resolution:'640x480',
					frameRate:30,
					insertMode:'APPEND',
					mirror:true
				})
				publisher.on("videoElementCreated",(event)=>{
					addPannel(document.querySelector(".memberListContent"),publisher.stream);
					updateMemberCount(++memberCount);
					// Init the main video with ours and append our data
					var userData = {
						nickName: nickName,
						userName: userName
					};
					initMainVideo(event.element, userData);
					appendUserData(event.element, userData);
				})
				session.publish(publisher);
				// starting timer
				let d = new Date();
				totalSeconds = Math.round((d.getTime() - startTime)/1000);
				setInterval(setTime, 1000);
			})
			.catch()

	/** audio logic starts **/
	function muteMyself() {
		myAudioEnabled = !myAudioEnabled;
		if(allAudioEnabled != myAudioEnabled) {
			allAudioEnabled = myAudioEnabled;
		}

		if(myAudioEnabled == true) {
			if(confirm("turn microphone on")){
				publisher.publishAudio(myAudioEnabled);
			}
		} else {
			publisher.publishAudio(myAudioEnabled);
		}
	}

	function muteAll() {
	console.log("mute signal generated")
		const signal = {
			type:"muteAll",
			to:[]
		}
		session.streamManagers.forEach(member=>{
			if(member.stream.connection.connectionId !== session.connection.connectionId){
				signal.to.push(member.stream.connection);
			}
		})
		session.signal(signal);
	}

	session.on("signal:muteAll",(event)=>{
	console.log("mute signal recieved")
		allAudioEnabled = !allAudioEnabled;
		if(myAudioEnabled != allAudioEnabled) {
			myAudioEnabled = allAudioEnabled;
		}

		if(allAudioEnabled == true) {
			if(confirm("turn microphone on")){
				publisher.publishAudio(allAudioEnabled);
			}
		} else {
			publisher.publishAudio(allAudioEnabled);
		}
	});
	/** audio logic ends **/


	session.on("streamCreated",(event)=>{
		let subscriber = session.subscribe(event.stream,"video-container");
		console.log("joined....")
		subscriber.on("videoElementCreated",(subEvent)=>{
			addPannel(document.querySelector(".memberListContent"),event.stream);
			// Add a new HTML element for the user's name and nickname over its video
			appendUserData(subEvent.element, subscriber.stream.connection);
			updateMemberCount(++memberCount);
		})
	});

	session.on("streamDestroyed",(event)=>{
		console.log("stream destroyed.....")
		document.querySelector(".memberListContent").querySelector("#"+"container-"+event.stream.connection.connectionId).remove();
		// Delete the HTML element with the user's name and nickname
		removeUserData(event.stream.connection);
		updateMemberCount(--memberCount);
	});

	function leaveSession() {
		session.disconnect();
	}

	/** making video clickable and init main screen starts **/

	/** making video clickable and init main screen stops **/

	/** video on/off logic starts **/
	if(document.getElementById("xx")){
        document.getElementById("xx").addEventListener("click",(e)=>{
            const signal = {
                type:"turnOffVideo",
                to:[]
            }
            session.streamManagers.forEach(member=>{
                if(member.stream.connection.connectionId !== session.connection.connectionId){
                    signal.to.push(member.stream.connection);
                }
            })
            session.signal(signal)
        })
	}

	if(document.getElementById("yy")){
        document.getElementById("yy").addEventListener("click",(e)=>{
            const signal = {
                type:"turnOnVideo",
                to:[]
            }
            session.streamManagers.forEach(member=>{
                if(member.stream.connection.connectionId !== session.connection.connectionId){
                    signal.to.push(member.stream.connection);
                }
            })
            session.signal(signal)
        })
	}



	if(document.getElementById("muteAll")){
        document.getElementById("muteAll").addEventListener("click",(e)=>{
        console.log("clicked")
            const signal = {
                type:"muteAll",
                to:[]
            }
            session.streamManagers.forEach(member=>{
                if(member.stream.connection.connectionId !== session.connection.connectionId){
                    signal.to.push(member.stream.connection);
                }
            })
            session.signal(signal)
        })
	}



	session.on("signal:turnOffVideo",()=>{
		publisher.publishVideo(false);
	});

	session.on("signal:turnOnVideo",()=>{
		if(! publisher.stream.videoActive){
			if(confirm("turn camera on")){
				publisher.publishVideo(true);
			}
		}
	});

	function addPannel(container,stream){
		const div = createElements("div");
		div.id = "container-"+stream.connection.connectionId;
		const name = stream.connection.nickName ? stream.connection.nickName : createElements("p",null,JSON.parse(stream.connection.data.split("%/%")[0]).clientData);


		const videoOnOffToggle = createElements("div",null,null,"<i class='fa fa-video-camera' aria-hidden='false'></i>")
		const audioOnOffToggle = createElements("div",null,null,"<i class='fa fa-microphone-slash'></i>")

		const subUnsubOptnsContainer = createElements("div",null,null,"<span class='subUnsubBtn'>....</span>")
		const subUnsubOptns = createElements("div","subUnsubOptns")

		const subUnsubVideo = createElements("div",null,"unsubscribe to video");
		const subUnsubAudio = createElements("div",null,"unsubscribe to audio");

		subUnsubOptns.append(subUnsubVideo);
		subUnsubOptns.append(subUnsubAudio);
		subUnsubOptnsContainer.append(subUnsubOptns);

		videoOnOffToggle.id=stream.connection.connectionId+"-"+1;
		audioOnOffToggle.id=stream.connection.connectionId+"-"+2;
		subUnsubVideo.id = stream.connection.connectionId+"-"+3;
		subUnsubAudio.id = stream.connection.connectionId+"-"+4;


		div.append(name);
		div.append(videoOnOffToggle);
		div.append(audioOnOffToggle);
		if(owner)div.append(subUnsubOptnsContainer);

		container.append(div);
        if(owner){
        		videoOnOffToggle.addEventListener("click",(e)=>turnOnOffVideo(e))
                audioOnOffToggle.addEventListener("click",(e)=>turnOnOffAudio(e))
                subUnsubVideo.addEventListener("click",(e)=>toggleVideoOfSubscriber(e))
                subUnsubAudio.addEventListener("click",(e)=>toggleAudioOfSubscriber(e))
        }else{
                videoOnOffToggle.addEventListener("click",(e)=>toggleVideoOfSubscriber(e))
                audioOnOffToggle.addEventListener("click",(e)=>toggleAudioOfSubscriber(e))
        }

	}

	function createElements(type,css=null,content=null,innerhtml=null){
		const ele = document.createElement(type);
		if(css!=null) ele.className = css;
		if(content!=null) ele.innerText = content;
		if(innerhtml!=null)ele.innerHTML = innerhtml;
		return ele;
	}

	function turnOffVideo(e){
		const signal = {
			type:"turnOffVideo",
			to:[]
		}
		const id = e.target.id.split("-")[0]
		console.log(id)
		signal.to.push(session.streamManagers.find(member=>member.stream.connection.connectionId === id).stream.connection)
		session.signal(signal);
	}

	function turnOnVideo(e){
		const signal = {
			type:"turnOnVideo",
			to:[]
		}
		const id = e.target.id.split("-")[0]
		signal.to.push(session.streamManagers.find(member=>member.stream.connection.connectionId == id).stream.connection)
		session.signal(signal);
	}


	function turnOnOffVideo(e){
		const id = e.target.closest("div").id.split("-")[0]
		let member = session.streamManagers.find(member=>member.stream.connection.connectionId == id)
		e = {
			target:{
				id
			}
		}
		if(member.stream.videoActive){
			turnOffVideo(e)
		}else{
			turnOnVideo(e)
		}
	}


	function turnOffAudio(e){
		const signal = {
			type:"turnOffAudio",
			to:[]
		}
		const id = e.target.id.split("-")[0]
		console.log(id)
		signal.to.push(session.streamManagers.find(member=>member.stream.connection.connectionId === id).stream.connection)
		session.signal(signal);
	}

	function turnOnAudio(e){
		const signal = {
			type:"turnOnAudio",
			to:[]
		}
		const id = e.target.id.split("-")[0]
		console.log(id)
		signal.to.push(session.streamManagers.find(member=>member.stream.connection.connectionId === id).stream.connection)
		session.signal(signal);
	}


	function turnOnOffAudio(e){
		const id = e.target.closest("div").id.split("-")[0]
		let member = session.streamManagers.find(member=>member.stream.connection.connectionId == id)
		e = {
			target:{
				id
			}
		}
		if(member.stream.audioActive){
			turnOffAudio(e)
		}else{
			turnOnAudio(e)
		}
	}



	session.on("signal:turnOffAudio",()=>{
		publisher.publishAudio(false);
		myAudioEnabled = false;
		allAudioEnabled = false;
	});

	session.on("signal:turnOnAudio",()=>{
		if(! publisher.stream.audioActive){
			if(confirm("turn microphone on")){
				publisher.publishAudio(true);
			}
		}
		myAudioEnabled = true;
		allAudioEnabled = true;
	});

	function toggleVideoOfSubscriber(e){
		let id = owner ? e.target.id.split("-")[0] : e.target.closest("div").id.split("-")[0];
		let subscriber = session.streamManagers.find(member=>member.stream.connection.connectionId === id)
		if("subscribeToAudio" in subscriber.properties){
			subscriber.subscribeToVideo(! subscriber.stream.videoActive)
		}else if("publishVideo" in subscriber.properties){
			subscriber.publishVideo(! subscriber.stream.videoActive)
		}
	}

	function toggleAudioOfSubscriber(e){
		let id = owner ? e.target.id.split("-")[0] : e.target.closest("div").id.split("-")[0];
		let subscriber = session.streamManagers.find(member=>member.stream.connection.connectionId === id)
		if("subscribeToAudio" in subscriber.properties){
			subscriber.subscribeToAudio(! subscriber.stream.audioActive)
		}else if("publishVideo" in subscriber.properties){
			subscriber.publishAudio(! subscriber.stream.audioActive)
		}
	}

	/** video on/off logic ends **/

	/** screen share logic starts **/
	function shareScreen() {
		publisher = ov.initPublisher("video-container", {
			audioSource: undefined, // The source of audio. If undefined default microphone
			videoSource: "screen",
			publishAudio: myAudioEnabled, // Whether you want to start publishing with your audio unmuted or not
			publishVideo: true, // Whether you want to start publishing with your video enabled or not
			resolution: '640x480', // The resolution of your video
			frameRate: 30, // The frame rate of your video
			insertMode: 'APPEND', // How the video is inserted in the target element 'video-container'
			mirror: false // Whether to mirror your local video or not
		});

		publisher.on('videoElementCreated', (event) => {
			// Init the main video with this user's data
			var userData = {
				nickName: nickName,
				userName: userName
			};
			initMainVideo(event.element, userData);
			appendUserData(event.element, userData);
			$(event.element).prop('muted', true); // Mute local video
		});

		publisher.once('accessAllowed', (event) => {
			publisher.stream.getMediaStream().getVideoTracks()[0].addEventListener('ended', () => {
				console.log('User pressed the "Stop sharing" button');
				backToOldScreen(publisher);
			});

			// Unpublishing the old publisher (sharing camera)
			session.unpublish(session.openvidu.publishers[0]);
			// publishing the new publisher (sharing screen)
			session.publish(publisher);

			// sending signal to subscribers regarding screen share started
			session.signal({
				// data: session.openvidu.publishers[0].videos[0].video,
				data: nickName, // notifying everyone that who shared
				to: [], // Broadcast to everyone
				type: 'start-screenshare' // The type of message (optional)
			}).then(() => {
				console.log('Notified successfully sent');
			}).catch(error => {
				console.error(error);
			});

		});

		publisher.once('accessDenied', (event) => {
			console.warn('ScreenShare: Access Denied');
		});
	}

	function backToOldScreen(oldPublisher){
		publisher = ov.initPublisher('video-container', {
			videoSource:undefined,
			audioSource:undefined,
			publishAudio:myAudioEnabled,
			publishVideo:true,
			resolution:'640x480',
			framRate:30,
			insertMode:'APPEND',
			mirror:true
		});

		publisher.on('videoElementCreated', (event) => {
			// Init the main video with ours and append our data
			var userData = {
				nickName: nickName,
				userName: userName
			};
			initMainVideo(event.element, userData);
			appendUserData(event.element, userData);
			$(event.element).prop('muted', true); // Mute local video
		});

		// Unpublishing the old publisher (sharing screen)
		session.unpublish(oldPublisher);
		// publishing the new publisher (sharing camera)
		session.publish(publisher);

		// sending signal to subscribers regarding screen share stoped
		session.signal({
			data: nickName, // notifying everyone that who shared
			to: [], // Broadcast to everyone
			type: 'stop-screenshare' // The type of message (optional)
		}).then(() => {
			console.log('Notified successfully sent');
		}).catch(error => {
			console.error(error);
		});
	}

	// showing notification when screen shared
	session.on('signal:start-screenshare', (event) => {
		// console.log(event.data); // Message
		var x = document.getElementById("start-share");
		x.hidden = false;
		setTimeout(function(){ x.hidden = true; }, 3000);
	});
	session.on('signal:stop-screenshare', (event) => {
		// console.log(event.data); // Message
		var x = document.getElementById("stop-share");
		x.hidden = false;
		setTimeout(function(){ x.hidden = true; }, 3000);
	});

	/** screen share logic ends **/

	document.getElementById("messengerForm").addEventListener("submit",submitFormHandler);

	function submitFormHandler(e){
		e.preventDefault();
		let text = document.getElementById("messengerForm").msg.value;
		const signal = {
			data:JSON.stringify({
				message : text,
				from : nickName
			}),
			type:"chatMessage"
		}
		session.signal(signal);
		document.getElementById("messengerForm").msg.value = "";
		document.getElementById("messengerForm").msg.focus();
		document.getElementById("emojis-div").hidden = true;
	}

	session.on("signal:chatMessage",(e)=>{
		putMessageInBox(JSON.parse(e.data))
	})

	function putMessageInBox(data){
		const container = createElements("div","chatmsg");
		const nameAndMsg = createElements("div","nameAndMsg");
		const initialContainer = createElements("div","initials");
		const initials = createElements("div");
		const time = createElements("div","time");
		initials.textContent = data.from.slice(0,2).toLocaleUpperCase()
		const name = createElements("div","name");
		name.textContent = data.from
		const message = createElements("div","msg");
		message.textContent = data.message
		nameAndMsg.append(name);
		nameAndMsg.append(message);
		initialContainer.append(initials);
		const date = new Date();
		let h = date.getHours()
		let m = date.getMinutes()
		if(h>12){
			h = Math.abs(h-12)
		}if(h == 0){
			h = 12;
		}
		time.textContent = h+":"+m;
		container.append(initialContainer);
		container.append(nameAndMsg);
		container.append(time);
		document.getElementById("messageBox").append(container);
	}

	/** emoji logic start**/
	function addEmojiToChat(elem) {
		document.getElementById("messengerForm").msg.value += elem.value;
	}

	function showHideEmojis() {
		document.getElementById("toggleEmoji").hidden = ! document.getElementById("toggleEmoji").hidden
	}
	/** emoji logic end**/


	function toggleChat(){
		const chatBox = document.querySelector(".chat");
		const main = document.querySelector("#main");
		chatBox.hidden = ! chatBox.hidden;
		main.classList.toggle("mainWithChat");
		main.classList.toggle("mainFullWindow");
	}

	function toggleMembers(){

	}

	function showMembers(){
		if(! document.querySelector("#main").classList.contains("mainWithChat")){
			toggleChat();
		}
		document.querySelector("#memberListButton").classList.add("clickChatHeaderButton")
		document.querySelector("#chatButton").classList.remove("clickChatHeaderButton")
		document.querySelector("#messageBox").hidden = true;
		document.querySelector("#members").hidden = false;
		document.querySelector("#messengerForm").hidden = true;

	}

	function showChat(){
		if(! document.querySelector("#main").classList.contains("mainWithChat")){
			toggleChat();
		}
		document.querySelector("#memberListButton").classList.remove("clickChatHeaderButton")
		document.querySelector("#chatButton").classList.add("clickChatHeaderButton");
		document.querySelector("#messageBox").hidden = false;
		document.querySelector("#members").hidden = true;
		document.querySelector("#messengerForm").hidden = false;

	}

	/** footer Btn functions start*/
	function footerAudioToggle() {
		myAudioEnabled = !myAudioEnabled;
		if(allAudioEnabled != myAudioEnabled) {
			allAudioEnabled = myAudioEnabled;
		}

		if(myAudioEnabled == true) {
			if(confirm("turn microphone on")){
				publisher.publishAudio(myAudioEnabled);
			}
		} else {
			publisher.publishAudio(myAudioEnabled);
		}
	}

	function footerVideoToggle() {
		myVideoEnabled = !myVideoEnabled;

		if(myVideoEnabled == true) {
			if(confirm("turn camera on")){
				publisher.publishVideo(myVideoEnabled);
			}
		} else {
			publisher.publishVideo(myVideoEnabled);
		}
	}

	function footerScreenToggle() {
		shareScreen();
	}

	function footerChatToggle() {
		const chatBox = document.querySelector(".chat");
		const main = document.querySelector("#main");
		chatBox.hidden = ! chatBox.hidden;
		main.classList.toggle("mainWithChat");
		main.classList.toggle("mainFullWindow");
		document.querySelector("#memberListButton").classList.remove("clickChatHeaderButton")
		document.querySelector("#chatButton").classList.add("clickChatHeaderButton");
		document.querySelector("#messageBox").hidden = false;
		document.querySelector("#members").hidden = true;
		document.querySelector("#messengerForm").hidden = false;
	}

/* dropdown logic starts */
	//toggle between hiding and showing the dropdown content
	function profileToggle() {
		document.getElementById("profileDropdown").classList.toggle("show");
	}

	// Close the dropdown if the user clicks outside of it
	window.onclick = function(event) {
		if (!event.target.matches('.avatar-button')) {
			var dropdowns = document.getElementsByClassName("dropdown-content");
			var i;
			for (i = 0; i < dropdowns.length; i++) {
				var openDropdown = dropdowns[i];
				if (openDropdown.classList.contains('show')) {
					openDropdown.classList.remove('show');
				}
			}
		}
	}
/* dropdown logic ends */



function initMainVideo(videoElement, userData) {
		$('#main-video video').get(0).srcObject = videoElement.srcObject;
		$('#main-video p.nickName').html(userData.nickName);
		$('#main-video video').prop('muted', true);
	}


function appendUserData(videoElement, connection) {
		var clientData;
		var serverData;
		var nodeId;
		if (connection.nickName) { // Appending local video data
			clientData = connection.nickName;
			// serverData = connection.userName;
			nodeId = 'main-videodata';
		} else {
			clientData = JSON.parse(connection.data.split('%/%')[0]).clientData;
			// serverData = JSON.parse(connection.data.split('%/%')[1]).serverData;
			nodeId = connection.connectionId;
		}
		var dataNode = document.createElement('div');
		dataNode.className = "data-node";
		dataNode.id = "data-" + nodeId;
		dataNode.innerHTML = '<p class="nickName">' + clientData + '</p>';
		videoElement.parentNode.insertBefore(dataNode, videoElement.nextSibling);
		addClickListener(videoElement, clientData, serverData);
	}

	function removeUserData(connection) {
		var userNameRemoved = $("#data-" + connection.connectionId);
		if ($(userNameRemoved).find('p.userName').html() === $('#main-video p.userName').html()) {
			cleanMainVideo(); // The participant focused in the main video has left
		}
		$("#data-" + connection.connectionId).remove();
	}

	function removeAllUserData() {
		$(".data-node").remove();
	}

	function cleanMainVideo() {
		$('#main-video video').get(0).srcObject = null;
		$('#main-video p').each(function () {
			$(this).html('');
		});
	}

	function addClickListener(videoElement, clientData, serverData) {
		videoElement.addEventListener('click', function () {
			var mainVideo = $('#main-video video').get(0);
			if (mainVideo.srcObject !== videoElement.srcObject) {
				$('#main-video').fadeOut("fast", () => {
					$('#main-video p.nickName').html(clientData);
					mainVideo.srcObject = videoElement.srcObject;
					$('#main-video').fadeIn("fast");
				});
			}
		});
	}

	function footerPeopleToggle() {
		const chatBox = document.querySelector(".chat");
		const main = document.querySelector("#main");
		chatBox.hidden = ! chatBox.hidden;
		main.classList.toggle("mainWithChat");
		main.classList.toggle("mainFullWindow");
		document.querySelector("#memberListButton").classList.add("clickChatHeaderButton")
		document.querySelector("#chatButton").classList.remove("clickChatHeaderButton")
		document.querySelector("#messageBox").hidden = true;
		document.querySelector("#members").hidden = false;
		document.querySelector("#messengerForm").hidden = true;
	}
	/**footer Btn functions end*/

	/** update member count starts **/
	function updateMemberCount(count){
		console.log("updateMemberCount called...")
	   document.querySelector(".participant-counter").innerText = count;
	}
	/** update member count ends **/

	/** show timer starts **/
	function showTimer(){
		// toggle timer
		if(!document.getElementById("timer").hidden){
			document.getElementById("timer").hidden=true;
		}else{
			document.getElementById("timer").hidden=false;
		}
	}
	function setTime() {

  		++totalSeconds;
  		secondsLabel.innerHTML = pad(totalSeconds % 60);
  		minutesLabel.innerHTML = pad(parseInt(totalSeconds / 60));
		hoursLabel.innerHTML = pad(parseInt(totalSeconds / 3600));
	}

	function pad(val) {
		var valString = val + "";
		if (valString.length < 2) {
			return "0" + valString;
		} else {
			return valString;
		}
	}
	/** show timer ends **/

	/** open check connectivity starts **/
	function openInNewTab(url) {
  		var win = window.open(url, '_blank');
  		win.focus();
	}
	/** open check connectivity ends **/

	if(document.getElementById("meetingLink")){
	    document.getElementById("meetingLink").value = window.location.origin+"/join-meeting/"+sessionName;
	    document.getElementById("meetingLink").readOnly = true;
	    document.getElementById("meetingLink").addEventListener("click",(e)=>{
	          document.getElementById("meetingLink").select();
              document.getElementById("meetingLink").setSelectionRange(0, 99999);
              document.execCommand("copy")
	    })
	}
    document.getElementById("leaveForm").addEventListener("submit",leaveSession)