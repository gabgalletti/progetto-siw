document.addEventListener("DOMContentLoaded", function () {
    const playPauseBtn = document.getElementById("playPause");
    const audio = document.getElementById("audioPlayer");
    const progress = document.getElementById("progress");
    const currentTimeLabel = document.getElementById("currentTime");
    const durationLabel = document.getElementById("duration");

    if (!audio || !playPauseBtn || !progress || !currentTimeLabel || !durationLabel) {
        console.error("Alcuni elementi del player audio sono mancanti nel DOM!");
        return;
    }

    // Funzione helper per formattare il tempo
    function formatTime(time) {
        const minutes = Math.floor(time / 60);
        const seconds = Math.floor(time % 60);
        return `${minutes}:${seconds < 10 ? "0" + seconds : seconds}`;
    }

    // Carica durata quando un file audio è pronto
    audio.addEventListener("loadedmetadata", () => {
        durationLabel.textContent = formatTime(audio.duration);
    });

    // Aggiorna barra di progresso e tempo attuale
    audio.addEventListener("timeupdate", () => {
        if (audio.duration) {
            progress.value = (audio.currentTime / audio.duration) * 100;
            currentTimeLabel.textContent = formatTime(audio.currentTime);
        }
    });

    // Gestisci clic su Play e Pause
    playPauseBtn.addEventListener("click", () => {
        const icon = playPauseBtn.querySelector("i");
        if (audio.paused) {
            audio.play();
            icon.classList.remove("fa-play");
            icon.classList.add("fa-pause");
        } else {
            audio.pause();
            icon.classList.remove("fa-pause");
            icon.classList.add("fa-play");
        }
    });
    function updateProgressBar() {
        const value = (progress.value / progress.max) * 100; // Percentuale correntemente riprodotta
        progress.style.background = `linear-gradient(to right, #1E90FF ${value}%, #ddd ${value}%)`;
    }

    // Aggiorna lo slider dinamicamente mentre l'audio viene riprodotto
    audio.addEventListener("timeupdate", () => {
        const currentTime = audio.currentTime;
        const duration = audio.duration;

        if (!isNaN(duration)) {
            progress.value = (currentTime / duration) * 100; // Aggiorna il valore dello slider
            updateProgressBar(); // Aggiorna il colore dinamico
        }
    });



    // Cambia posizione dell'audio tramite la barra di progresso
    progress.addEventListener("input", () => {
        audio.currentTime = (progress.value / 100) * audio.duration;
    });
});