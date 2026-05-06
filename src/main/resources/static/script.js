function toggleNewPost() {
    const popup = document.getElementById('newPostPopup');
    popup.classList.toggle('open');
}

function darkMode() {
    document.body.classList.toggle("darkmode");
    localStorage.setItem("darkmode", document.body.classList.contains("darkmode"));
}

if (localStorage.getItem("darkmode") === "true") {
    document.body.classList.add("darkmode")
}