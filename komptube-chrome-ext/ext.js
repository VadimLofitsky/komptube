document.addEventListener("DOMContentLoaded", () => {
    chrome.tabs.query({currentWindow: true, active: true}, function([tab]) {
        console.log("*".repeat(100));
        console.log(tab);
        console.log("*".repeat(100));
        let fullUrl = "http://192.168.1.3:8765/?url=" + encodeURIComponent(tab.url) + "&title=" + encodeURIComponent(tab.title);
        fetch(fullUrl, { signal: AbortSignal.timeout(15000) }).then(r => { window.close(); });
    });
});
