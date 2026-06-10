document.addEventListener("DOMContentLoaded", () => {
    const can = document.getElementById("errMsgContainer");
    const msg = can.textContent;
    const binary = atob(msg);
    const bytes = Uint8Array.from(
        binary,
        c => c.charCodeAt(0)
    );
    const decodedMsg =
        new TextDecoder().decode(bytes);
    can.textContent = decodedMsg;
});

document.getElementById("backBtn").addEventListener("click", () => {
    window.location.replace("/home/to-mainmenu");
});