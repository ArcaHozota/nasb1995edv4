document.addEventListener("DOMContentLoaded", () => {
	const can = document.getElementById("errMsgContainer");
	const msg = can.textContent;
    const decodedMsg = base64ToUtf8(msg);
	can.textContent = decodedMsg;
});

document.getElementById("backBtn").addEventListener("click", () => {
    window.location.replace("/home/to-mainmenu");
});