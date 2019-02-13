/**
 *  Lights On When Door Opens
 *
 *  Copyright 2019 David Greenwald
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "Lights On When Door Opens",
    namespace: "dlgreenwald",
    author: "David Greenwald",
    description: "Turn light(s) on when door(s) open.",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
    preferences {
    	section("Turn on when doors open:") {
        	input "doors", "capability.contactSensor", required: true, title: "Where?", multiple: true
            
    	}
    	section("Turn on this light:") {
        	input "lights", "capability.switch", required: true, multiple: true
    	}
        section("For how long:") {
        	input "minutes", "number", required: true, title: "Minutes?"
        }
	}
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	subscribe(doors, "contact.open", doorOpenHandler)
    state.dark = true;
}

def doorOpenHandler(evt) {
	log.debug "doorOpenHandler called: $evt"
	if(state.dark == true){
    	def onSwitches = lights.findAll { theswitch -> 
    		theswitch.currentValue("switch") == "off" ? true : false 
    	}
    	log.debug "while ${onSwitches.size()} switches are off"
		if (onSwitches.size() > 0){
    		onSwitches.each { theswitch ->
    	    	log.debug "Turning light ${theswitch} on and setting off for the future"
    	    	theswitch.on()
    	        theswitch.off([delay: minutes * 1000 * 60])
    	    }
    	    
    	}
    }
}

def sunsetHandler(evt) {
    log.debug "Sun has set!"
    state.dark = true
}

def sunriseHandler(evt) {
    log.debug "Sun has risen!"
    state.dark = false
}