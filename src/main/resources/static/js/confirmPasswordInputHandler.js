        const $ = elementId => document.getElementById(elementId);

        const verifyPasswordMatch = () => {
            console.log($('newPassword').value);
            if(!$('newPassword').value || $('newPassword').value !== event.target.value) {
                $('confirmNewPassMessage').innerText = "Passwords must match.";
            } else {
                $('confirmNewPassMessage').innerText = "";
            }
        }