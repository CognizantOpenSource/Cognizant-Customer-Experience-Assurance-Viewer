
var config = require('../configuration.json');

let validation = function (data) {
    let validity = true;
    let characters = /^[0-9a-zA-Z .\-@]+$/;
    
    if(characters.test(data)){
        validity = false;
    }
    return validity;
}



module.exports = validation;