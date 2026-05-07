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

function toggleLike(postId) {
    const button = document.querySelector(`[data-post-id="${postId}"]`);
    const icon = button.querySelector('i');
    const liked = localStorage.getItem(`liked_${postId}`) === 'true';
    
    if (liked) {
        localStorage.removeItem(`liked_${postId}`);
        icon.classList.replace('bi-hand-thumbs-up-fill', 'bi-hand-thumbs-up');
        button.classList.remove('liked');
    } else {
        localStorage.setItem(`liked_${postId}`, 'true');
        icon.classList.replace('bi-hand-thumbs-up', 'bi-hand-thumbs-up-fill');
        button.classList.add('liked');
    }
}

document.querySelectorAll('[data-post-id]').forEach(button => {
    const postId = button.dataset.postId;
    if (localStorage.getItem(`liked_${postId}`) === 'true') {
        button.querySelector('i').classList.replace('bi-hand-thumbs-up', 'bi-hand-thumbs-up-fill');
        button.classList.add('liked');
    }
});