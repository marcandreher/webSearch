document.addEventListener("DOMContentLoaded", function (event) {
    url = new URL(window.location.href.split('?')[0]);
    if (window.location.href == "https://search.scampi.me/") {
        location.href = url + "?q=";
    } else if (window.location.href == "search.scampi.me/") {
        location.href = url + "?q=";
    }
});

function addPage($page, $query) {

    url = new URL(window.location.href.split('?')[0]);
    location.href = url + "?q=" + $query + "&page=" + ($page + 1);
}

function goBack($page, $query) {
    url = new URL(window.location.href.split('?')[0]);
    location.href = url + "?q="+$query + "&page="+($page-1);
}